# Java実行の仕組み図鑑（静的HTML版）

claude.ai のDesignキャンバスで作成したカードを、ブラウザで直接開ける静的HTMLとして書き出したもの。
day1〜day3の座学の補助教材。内容は宮乃やみ氏のJava入門動画シリーズ（JDK/JRE/JVM・外部ライブラリ/Maven・アノテーション）と、
やむう氏の図解スタイルを参考にした文法・OOPカード。

- 編集元: `source/`（claude.ai Designキャンバスの `project/` と同じ構成）。編集後は `python3 export_static.py` で `static/` を再生成する
- 裏取り: `verify/Verify.java` を `java verify/Verify.java` で実行すると、カード内のコード片の挙動を確認できる（2026-10-02 JDK 25で確認済み）

## 実行の仕組み系

- [JDK・JRE・JVMの構造](static/jdk-jre-jvm.html)
- [プログラムの実行の流れ](static/execution-flow.html)
- [外部ライブラリとMaven](static/library-maven.html)
- [アノテーションの3つの用途](static/annotation.html)
- [JVMのメモリ（スタックとヒープ）](static/memory.html)

## 文法・思想系

- [配列とArrayList](static/array-list.html)
- [Javaの基本構造](static/class-structure.html)
- [関数・メソッドとは](static/method.html)
- [オブジェクト指向の大事なとこ](static/oop.html)
- [SOLIDって？](static/solid.html)
- [型システムとキャスト](static/types.html)
- [演算子と制御構文](static/control-flow.html)
- [文字列と等価性](static/string-equality.html)
- [例外処理](static/exceptions.html)
- [null安全性とOptional](static/null-safety.html)
- [コレクションの使い分け](static/collections.html)
- [インターフェースと抽象クラス](static/interface.html)
