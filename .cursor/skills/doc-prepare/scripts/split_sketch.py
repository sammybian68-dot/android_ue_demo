#!/usr/bin/env python3
"""
Split Sketch MeaXure annotation data into per-artboard files.

Usage:
    python split_sketch.py <input_html_or_json> <output_dir>

Input:  index.html (Sketch MeaXure export) or extracted JSON file
Output: A directory structure ready for AI consumption:
    output_dir/
    ├── colors.json
    ├── slices.json
    ├── artboard_index.json    (lightweight index of all artboards)
    └── artboards/
        ├── 01-联系人-按下.json
        ├── 02-联系人-检索-yz固定位置.json
        └── ...
"""

import json
import os
import re
import sys
import unicodedata


def sanitize_filename(name: str, max_len: int = 80) -> str:
    name = unicodedata.normalize("NFC", name)
    name = re.sub(r'[\\/:*?"<>|]', "_", name)
    name = re.sub(r"\s+", "_", name).strip("_")
    return name[:max_len] if len(name) > max_len else name


def extract_data_from_html(html_path: str) -> dict:
    with open(html_path, "r", encoding="utf-8") as f:
        content = f.read()
    match = re.search(r"let\s+data\s*=\s*(\{.+\})\s*;", content, re.DOTALL)
    if not match:
        raise ValueError("Cannot find 'let data = {...};' in HTML file")
    return json.loads(match.group(1))


def extract_data_from_json(json_path: str) -> dict:
    with open(json_path, "r", encoding="utf-8") as f:
        content = f.read().strip().rstrip(";").strip()
    return json.loads(content)


def load_data(input_path: str) -> dict:
    if input_path.endswith(".html") or input_path.endswith(".htm"):
        return extract_data_from_html(input_path)
    return extract_data_from_json(input_path)


def collect_referenced_slices(artboard: dict) -> list:
    slices = []
    for layer in artboard.get("layers", []):
        for exp in layer.get("exportable", []):
            slices.append(exp.get("path", exp.get("name", "")))
    return slices


def build_artboard_summary(idx: int, ab: dict) -> dict:
    layer_types = {}
    for layer in ab.get("layers", []):
        t = layer.get("type", "unknown")
        layer_types[t] = layer_types.get(t, 0) + 1

    return {
        "index": idx,
        "name": ab.get("name", ""),
        "slug": ab.get("slug", ""),
        "width": ab.get("width"),
        "height": ab.get("height"),
        "layer_count": len(ab.get("layers", [])),
        "layer_types": layer_types,
        "referenced_slices": collect_referenced_slices(ab),
        "preview": ab.get("imagePath", ""),
        "preview_icon": ab.get("imageIconPath", ""),
        "file": None,
    }


def run(input_path: str, output_dir: str):
    data = load_data(input_path)

    os.makedirs(os.path.join(output_dir, "artboards"), exist_ok=True)

    colors = data.get("colors", [])
    with open(os.path.join(output_dir, "colors.json"), "w", encoding="utf-8") as f:
        json.dump(colors, f, ensure_ascii=False, indent=2)
    print(f"  colors.json  ({len(colors)} colors)")

    slices = data.get("slices", [])
    with open(os.path.join(output_dir, "slices.json"), "w", encoding="utf-8") as f:
        json.dump(slices, f, ensure_ascii=False, indent=2)
    print(f"  slices.json  ({len(slices)} slices)")

    artboard_index = []
    artboards = data.get("artboards", [])
    for i, ab in enumerate(artboards):
        fname = f"{i + 1:02d}-{sanitize_filename(ab.get('name', str(i)))}.json"
        fpath = os.path.join(output_dir, "artboards", fname)

        with open(fpath, "w", encoding="utf-8") as f:
            json.dump(ab, f, ensure_ascii=False, indent=2)

        summary = build_artboard_summary(i + 1, ab)
        summary["file"] = f"artboards/{fname}"
        artboard_index.append(summary)
        print(f"  artboards/{fname}  ({summary['layer_count']} layers)")

    index_data = {
        "resolution": data.get("resolution"),
        "unit": data.get("unit"),
        "colorFormat": data.get("colorFormat"),
        "artboard_count": len(artboards),
        "color_count": len(colors),
        "slice_count": len(slices),
        "artboards": artboard_index,
    }
    with open(
        os.path.join(output_dir, "artboard_index.json"), "w", encoding="utf-8"
    ) as f:
        json.dump(index_data, f, ensure_ascii=False, indent=2)
    print(f"  artboard_index.json  ({len(artboards)} artboards)")

    print(f"\nDone. Output: {output_dir}")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(f"Usage: {sys.argv[0]} <input_html_or_json> <output_dir>")
        sys.exit(1)
    run(sys.argv[1], sys.argv[2])
