#!/usr/bin/env python3
"""
Convert PDF pages to PNG images + extract text, producing an LLM-friendly output.

Usage:
    python pdf_to_images.py <input.pdf> <output_dir> [--dpi 200] [--max-width 2000]

Output structure:
    <output_dir>/
    ├── page_index.json      # Index with page metadata
    ├── pages/
    │   ├── page_01.png      # Page images
    │   ├── page_02.png
    │   └── ...
    └── text/
        ├── page_01.txt      # Extracted text per page
        ├── page_02.txt
        └── ...
"""

import argparse
import json
import os
import sys

try:
    import pymupdf
except ImportError:
    print("ERROR: pymupdf not installed. Run: pip3 install --user pymupdf", file=sys.stderr)
    sys.exit(1)


def convert_pdf(input_path, output_dir, dpi=200, max_width=2000):
    if not os.path.isfile(input_path):
        print(f"ERROR: File not found: {input_path}", file=sys.stderr)
        sys.exit(1)

    pages_dir = os.path.join(output_dir, "pages")
    text_dir = os.path.join(output_dir, "text")
    os.makedirs(pages_dir, exist_ok=True)
    os.makedirs(text_dir, exist_ok=True)

    doc = pymupdf.open(input_path)
    total = len(doc)
    print(f"Processing {total} pages from: {input_path}")

    zoom = dpi / 72.0
    mat = pymupdf.Matrix(zoom, zoom)

    page_index = {
        "source": os.path.abspath(input_path),
        "total_pages": total,
        "dpi": dpi,
        "pages": [],
    }

    for i, page in enumerate(doc):
        num = i + 1
        pad = str(num).zfill(len(str(total)))

        pix = page.get_pixmap(matrix=mat, alpha=False)
        if pix.width > max_width:
            scale = max_width / pix.width
            new_mat = pymupdf.Matrix(zoom * scale, zoom * scale)
            pix = page.get_pixmap(matrix=new_mat, alpha=False)

        img_name = f"page_{pad}.png"
        img_path = os.path.join(pages_dir, img_name)
        pix.save(img_path)

        text = page.get_text("text").strip()
        txt_name = f"page_{pad}.txt"
        txt_path = os.path.join(text_dir, txt_name)
        with open(txt_path, "w", encoding="utf-8") as f:
            f.write(text)

        text_preview = text[:120].replace("\n", " ") + ("..." if len(text) > 120 else "")

        page_index["pages"].append({
            "page": num,
            "image": f"pages/{img_name}",
            "text": f"text/{txt_name}",
            "width": pix.width,
            "height": pix.height,
            "text_length": len(text),
            "preview": text_preview,
        })

        print(f"  [{num}/{total}] {img_name} ({pix.width}x{pix.height})")

    index_path = os.path.join(output_dir, "page_index.json")
    with open(index_path, "w", encoding="utf-8") as f:
        json.dump(page_index, f, ensure_ascii=False, indent=2)

    doc.close()
    print(f"\nDone! Output: {output_dir}")
    print(f"  Images:  {pages_dir}/ ({total} files)")
    print(f"  Text:    {text_dir}/ ({total} files)")
    print(f"  Index:   {index_path}")


def main():
    parser = argparse.ArgumentParser(description="Convert PDF to LLM-friendly images + text")
    parser.add_argument("input", help="Input PDF file path")
    parser.add_argument("output", help="Output directory")
    parser.add_argument("--dpi", type=int, default=200, help="Render DPI (default: 200)")
    parser.add_argument("--max-width", type=int, default=2000, help="Max image width in px (default: 2000)")
    args = parser.parse_args()
    convert_pdf(args.input, args.output, args.dpi, args.max_width)


if __name__ == "__main__":
    main()
