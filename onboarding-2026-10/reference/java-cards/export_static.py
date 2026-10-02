import re, os

BASE = os.path.dirname(os.path.abspath(__file__))
SRC = os.path.join(BASE, "source")
OUT = os.path.join(BASE, "static")
os.makedirs(OUT, exist_ok=True)

shared_css = open(os.path.join(SRC, "shared.css"), encoding="utf-8").read()

# (dc.html filename, output kebab-case filename, Japanese title for nav)
files = [
    ("Main.dc.html", "jdk-jre-jvm.html", "JDK・JRE・JVMの構造", True),
    ("Execution.dc.html", "execution-flow.html", "プログラムの実行の流れ", True),
    ("Library.dc.html", "library-maven.html", "外部ライブラリとMaven", True),
    ("Annotation.dc.html", "annotation.html", "アノテーションの3つの用途", True),
    ("Memory.dc.html", "memory.html", "JVMのメモリ（スタックとヒープ）", False),
    ("Array.dc.html", "array-list.html", "配列とArrayList", False),
    ("ClassStructure.dc.html", "class-structure.html", "Javaの基本構造", False),
    ("Method.dc.html", "method.html", "関数・メソッドとは", False),
    ("OOP.dc.html", "oop.html", "オブジェクト指向の大事なとこ", False),
    ("SOLID.dc.html", "solid.html", "SOLIDって？", False),
    ("Types.dc.html", "types.html", "型システムとキャスト", False),
    ("ControlFlow.dc.html", "control-flow.html", "演算子と制御構文", False),
    ("StringEquality.dc.html", "string-equality.html", "文字列と等価性", False),
    ("Exceptions.dc.html", "exceptions.html", "例外処理", False),
    ("NullSafety.dc.html", "null-safety.html", "null安全性とOptional", False),
    ("Collections.dc.html", "collections.html", "コレクションの使い分け", False),
    ("Interface.dc.html", "interface.html", "インターフェースと抽象クラス", False),
]

TEMPLATE = """<!doctype html>
<html lang="ja">
<head>
<meta charset="utf-8">
<title>{title}</title>
<style>
{css}
</style>
</head>
<body>
{content}
</body>
</html>
"""

nav_items = []

for dc_name, out_name, title, self_contained in files:
    src = open(os.path.join(SRC, dc_name), encoding="utf-8").read()

    if self_contained:
        style_match = re.search(r"<helmet>\s*<style>(.*?)</style>\s*</helmet>", src, re.S)
        board_css = style_match.group(1).strip()
        css = board_css
    else:
        style_match = re.search(r"<helmet><style>(.*?)</style></helmet>", src, re.S)
        board_css = style_match.group(1).strip() if style_match else ""
        css = shared_css.strip() + "\n" + board_css

    content_match = re.search(r'(<div class="poster">.*?</div>)\s*</x-dc>', src, re.S)
    content = content_match.group(1).strip()

    html = TEMPLATE.format(title=title, css=css, content=content)
    with open(os.path.join(OUT, out_name), "w", encoding="utf-8") as f:
        f.write(html)
    nav_items.append((out_name, title))
    print("wrote", out_name)

# index
index_lines = [
    "# Java実行の仕組み図鑑（静的HTML版）",
    "",
    "claude.ai のDesignキャンバスで作成したカードを、ブラウザで直接開ける静的HTMLとして書き出したもの。",
    "day1〜day3の座学の補助教材。内容は宮乃やみ氏のJava入門動画シリーズ（JDK/JRE/JVM・外部ライブラリ/Maven・アノテーション）と、",
    "やむう氏の図解スタイルを参考にした文法・OOPカード。",
    "",
    "- 編集元: `source/`（claude.ai Designキャンバスの `project/` と同じ構成）。編集後は `python3 export_static.py` で `static/` を再生成する",
    "- 裏取り: `verify/Verify.java` を `java verify/Verify.java` で実行すると、カード内のコード片の挙動を確認できる（2026-10-02 JDK 25で確認済み）",
    "",
    "## 実行の仕組み系",
    "",
]
for out_name, title in nav_items[:5]:
    index_lines.append(f"- [{title}](static/{out_name})")
index_lines += ["", "## 文法・思想系", ""]
for out_name, title in nav_items[5:]:
    index_lines.append(f"- [{title}](static/{out_name})")

with open(os.path.join(BASE, "README.md"), "w", encoding="utf-8") as f:
    f.write("\n".join(index_lines) + "\n")

print("done")
