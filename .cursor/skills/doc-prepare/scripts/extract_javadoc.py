#!/usr/bin/env python3
"""
Extract API summaries from Javadoc HTML documentation.

Scans a Javadoc HTML root directory, parses each class page,
and outputs a consolidated api_summary.md + class_index.json.

Usage:
    python3 extract_javadoc.py <javadoc_root> <output_dir> --sdk-name "<display name>"

Example:
    python3 extract_javadoc.py \
        "输入/依赖/my_sdk/doc/reference" \
        "docs/prepared/sdk/my_sdk" \
        --sdk-name "My SDK v1.0"
"""

import argparse
import json
import os
import re
import sys
from pathlib import Path

try:
    from bs4 import BeautifulSoup
except ImportError:
    print("Error: beautifulsoup4 is required. Install with: pip3 install --user beautifulsoup4")
    sys.exit(1)


def find_class_html_files(javadoc_root):
    """Find all HTML files that look like class documentation pages."""
    root = Path(javadoc_root)
    candidates = []
    for html_file in root.rglob("*.html"):
        name = html_file.stem
        if name in ("index", "index-all", "allclasses-index", "allpackages-index",
                     "overview-tree", "overview-summary", "deprecated-list",
                     "help-doc", "constant-values", "serialized-form",
                     "package-summary", "package-tree", "hierarchy", "packages",
                     "classes"):
            continue
        if "script-dir" in str(html_file) or "resources" in str(html_file):
            continue
        candidates.append(html_file)
    return sorted(candidates)


def infer_package_from_path(html_file, javadoc_root):
    """Infer Java package name from the file's directory relative to the Javadoc root."""
    rel = html_file.relative_to(javadoc_root)
    parts = list(rel.parent.parts)
    if not parts or parts == ["."]:
        return ""
    return ".".join(parts)


def is_doclava_format(soup):
    """Detect if the HTML uses Doclava documentation format (common in Android vendor SDKs)."""
    return bool(soup.find_all("div", class_="jd-details"))


def extract_doclava_class(soup, class_name, package_name):
    """Extract API info from Doclava-format HTML."""
    class_desc = ""
    jd_descr = soup.find("div", class_="jd-descr")
    if jd_descr:
        p = jd_descr.find("p")
        if p:
            class_desc = p.get_text(strip=True)[:200]

    methods = []
    constants = []

    pubmethods_table = soup.find("table", id="pubmethods")
    method_names_from_table = set()
    if pubmethods_table:
        for row in pubmethods_table.find_all("tr"):
            cells = row.find_all("td")
            if len(cells) >= 2:
                method_names_from_table.add(cells[-1].get_text(" ", strip=True).split("(")[0].strip())

    for detail in soup.find_all("div", class_="jd-details"):
        h4 = detail.find("h4")
        if not h4:
            continue

        sig_text = h4.get_text(" ", strip=True)

        jd_tagdata = detail.find("div", class_="jd-tagdata")
        jd_comment = detail.find("div", class_="jd-comment")
        description = ""
        if jd_comment:
            p = jd_comment.find("p")
            description = (p or jd_comment).get_text(strip=True)[:150]

        is_method = "(" in sig_text

        if is_method:
            name_match = re.search(r'(\w+)\s*\(', sig_text)
            name = name_match.group(1) if name_match else sig_text.split("(")[0].split()[-1]
            methods.append({
                "name": name,
                "signature": clean_signature(sig_text),
                "description": description
            })
        else:
            parts = sig_text.split()
            name = parts[-1] if parts else sig_text
            value_text = ""
            if jd_tagdata:
                value_text = jd_tagdata.get_text(strip=True)[:80]
            constants.append({
                "name": name,
                "signature": clean_signature(sig_text),
                "description": value_text or description
            })

    return {
        "class_name": class_name,
        "package": package_name,
        "description": class_desc,
        "methods": methods,
        "constants": constants
    }


def extract_class_info(html_file, javadoc_root):
    """Parse a single Javadoc HTML file and extract class API information."""
    with open(html_file, "r", encoding="utf-8", errors="replace") as f:
        content = f.read()

    soup = BeautifulSoup(content, "html.parser")

    class_name = html_file.stem
    package_name = infer_package_from_path(html_file, Path(javadoc_root))

    if is_doclava_format(soup):
        return extract_doclava_class(soup, class_name, package_name)

    class_desc = ""
    desc_block = soup.find("div", class_="block")
    if desc_block:
        class_desc = desc_block.get_text(strip=True)[:200]

    methods = []
    constants = []

    method_container = soup.find("section", class_="method-details")
    if method_container:
        method_sections = method_container.find_all("section", class_="detail")
    else:
        method_sections = soup.find_all("section", class_=re.compile(r"method|constructor"))
        if not method_sections:
            method_sections = soup.find_all("div", class_=re.compile(r"method|member"))

    for section in method_sections:
        heading = section.find(["h3", "h4"])
        if not heading:
            continue

        method_name = heading.get_text(strip=True)

        sig_elem = section.find("div", class_="member-signature")
        if sig_elem:
            signature = sig_elem.get_text(" ", strip=True)
        else:
            pre = section.find("pre")
            signature = pre.get_text(" ", strip=True) if pre else method_name

        desc_elem = section.find("div", class_="block")
        description = desc_elem.get_text(strip=True)[:150] if desc_elem else ""

        methods.append({
            "name": method_name,
            "signature": clean_signature(signature),
            "description": description
        })

    field_container = soup.find("section", class_="field-details")
    if field_container:
        field_sections = field_container.find_all("section", class_="detail")
    else:
        field_sections = soup.find_all("section", class_=re.compile(r"field|constant|enum"))
        if not field_sections:
            field_sections = soup.find_all("div", class_=re.compile(r"field|constant"))

    for section in field_sections:
        heading = section.find(["h3", "h4"])
        if not heading:
            continue

        field_name = heading.get_text(strip=True)

        sig_elem = section.find("div", class_="member-signature")
        if sig_elem:
            signature = sig_elem.get_text(" ", strip=True)
        else:
            pre = section.find("pre")
            signature = pre.get_text(" ", strip=True) if pre else field_name

        desc_elem = section.find("div", class_="block")
        description = desc_elem.get_text(strip=True)[:150] if desc_elem else ""

        constants.append({
            "name": field_name,
            "signature": clean_signature(signature),
            "description": description
        })

    if not methods and not constants:
        methods, constants = extract_from_tables(soup)

    return {
        "class_name": class_name,
        "package": package_name,
        "description": class_desc,
        "methods": methods,
        "constants": constants
    }


def extract_from_tables(soup):
    """Fallback: extract from summary tables (older Javadoc / Doclava format)."""
    methods = []
    constants = []

    for table in soup.find_all("table"):
        table_id = table.get("id", "")
        table_summary = table.get("summary", "")
        header_text = ""
        prev_heading = table.find_previous(["h2", "h3"])
        if prev_heading:
            header_text = prev_heading.get_text(strip=True).lower()

        is_method = any(kw in (table_id + table_summary + header_text).lower()
                        for kw in ["method", "constructor"])
        is_field = any(kw in (table_id + table_summary + header_text).lower()
                       for kw in ["field", "constant", "enum"])

        for row in table.find_all("tr"):
            cells = row.find_all("td")
            if len(cells) < 2:
                continue

            sig_text = cells[0].get_text(" ", strip=True)
            desc_text = cells[-1].get_text(" ", strip=True)[:150]

            link = cells[0].find("a") or cells[1].find("a")
            name = link.get_text(strip=True) if link else sig_text.split("(")[0].split()[-1]

            entry = {
                "name": name,
                "signature": clean_signature(sig_text),
                "description": desc_text
            }

            if is_method:
                methods.append(entry)
            elif is_field:
                constants.append(entry)
            elif "(" in sig_text:
                methods.append(entry)
            else:
                constants.append(entry)

    return methods, constants


def clean_signature(sig):
    """Clean up a signature string."""
    sig = re.sub(r"\s+", " ", sig).strip()
    sig = sig.replace("\n", " ")
    if len(sig) > 200:
        sig = sig[:197] + "..."
    return sig


def generate_api_summary_md(sdk_name, class_infos):
    """Generate the api_summary.md content."""
    lines = [f"# {sdk_name} — API Summary\n"]
    lines.append(f"Auto-extracted from Javadoc HTML. Total classes: {len(class_infos)}\n")

    by_package = {}
    for info in class_infos:
        pkg = info["package"] or "(default)"
        by_package.setdefault(pkg, []).append(info)

    for pkg in sorted(by_package.keys()):
        lines.append(f"\n---\n\n## Package: `{pkg}`\n")

        for info in sorted(by_package[pkg], key=lambda x: x["class_name"]):
            lines.append(f"\n### {info['class_name']}\n")
            if info["description"]:
                lines.append(f"\n{info['description']}\n")

            if info["constants"]:
                lines.append("\n**Constants / Fields:**\n")
                lines.append("| Name | Signature | Description |")
                lines.append("|------|-----------|-------------|")
                for c in info["constants"]:
                    lines.append(f"| `{c['name']}` | `{c['signature']}` | {c['description']} |")

            if info["methods"]:
                lines.append("\n**Methods:**\n")
                lines.append("| Name | Signature | Description |")
                lines.append("|------|-----------|-------------|")
                for m in info["methods"]:
                    lines.append(f"| `{m['name']}` | `{m['signature']}` | {m['description']} |")

    return "\n".join(lines) + "\n"


def generate_class_index(sdk_name, class_infos):
    """Generate the class_index.json content."""
    classes = []
    for info in class_infos:
        classes.append({
            "name": info["class_name"],
            "package": info["package"],
            "summary": info["description"][:100] if info["description"] else "",
            "method_count": len(info["methods"]),
            "constant_count": len(info["constants"])
        })

    return {
        "sdk_name": sdk_name,
        "total_classes": len(classes),
        "classes": sorted(classes, key=lambda x: f"{x['package']}.{x['name']}")
    }


def main():
    parser = argparse.ArgumentParser(description="Extract API summaries from Javadoc HTML")
    parser.add_argument("javadoc_root", help="Root directory of Javadoc HTML (containing package dirs)")
    parser.add_argument("output_dir", help="Output directory for api_summary.md and class_index.json")
    parser.add_argument("--sdk-name", default="SDK", help="Display name for the SDK")
    args = parser.parse_args()

    javadoc_root = Path(args.javadoc_root)
    if not javadoc_root.is_dir():
        print(f"Error: Javadoc root not found: {javadoc_root}")
        sys.exit(1)

    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    print(f"Scanning Javadoc HTML in: {javadoc_root}")
    html_files = find_class_html_files(javadoc_root)
    print(f"Found {len(html_files)} candidate class files")

    class_infos = []
    for html_file in html_files:
        try:
            info = extract_class_info(html_file, args.javadoc_root)
            if info["methods"] or info["constants"]:
                class_infos.append(info)
                print(f"  ✓ {info['package']}.{info['class_name']} "
                      f"({len(info['methods'])} methods, {len(info['constants'])} constants)")
        except Exception as e:
            print(f"  ✗ {html_file.name}: {e}")

    print(f"\nExtracted {len(class_infos)} classes with API content")

    summary_md = generate_api_summary_md(args.sdk_name, class_infos)
    summary_path = output_dir / "api_summary.md"
    with open(summary_path, "w", encoding="utf-8") as f:
        f.write(summary_md)
    print(f"Written: {summary_path} ({len(summary_md)} chars)")

    class_index = generate_class_index(args.sdk_name, class_infos)
    index_path = output_dir / "class_index.json"
    with open(index_path, "w", encoding="utf-8") as f:
        json.dump(class_index, f, ensure_ascii=False, indent=2)
    print(f"Written: {index_path} ({class_index['total_classes']} classes)")


if __name__ == "__main__":
    main()
