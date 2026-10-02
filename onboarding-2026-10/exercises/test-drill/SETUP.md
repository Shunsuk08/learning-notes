# テスト演習の環境構築（再現用）

[hands-on-02](../../hands-on-02-stub-mock-coverage.md) を動かすための環境の作り方と、**実際にハマった点**の記録。
初見で同じところで止まらないために、エラーの実物と対処をそのまま残している。

> **この記録を取った環境（2026-10-02 実測）**
>
> | 項目 | 値 |
> |---|---|
> | マシン | macOS 15.7.7 / **x86_64（Intel）** |
> | JDK | **25.0.1** (Oracle, x86_64) — `/Library/Java/JavaVirtualMachines/jdk-25.jdk` の1つだけ |
> | Homebrew | `/usr/local`（Intel版の配置） |
> | Maven | **PATHには無い**（`brew install maven` は未完了）。演習は同梱の `./mvnw` で動く |
> | ローカルリポジトリ | `~/.m2/repository` に 283MB（studymate-aiの分が既にある） |

---

## 0. 結論: Phase1だけならセットアップ不要

```bash
cd ~/git/learning-notes/onboarding-2026-10/exercises/test-drill
java StubMockDrill.java
java CoverageDrill.java
```

**JDK 11以降なら、`.java` ファイルを直接 `java` で実行できる**（単一ファイルソースコード実行）。コンパイル（`javac`）もビルドツールも要らない。
Phase1の2本はこれで動くように、外部ライブラリをあえて使わずに書いてある。

Mavenが必要になるのは Phase2（JUnit5 + Mockito + JaCoCo）から。

---

## 1. Mavenを用意する3つの方法

| 方法 | コマンド | 向いている場面 | 注意 |
|---|---|---|---|
| **① 同梱の mvnw を使う（推奨）** | `./mvnw test` | **この演習はこれで足りる**。JDKさえあれば動く | このプロジェクト専用 |
| ② 既存のラッパーを直接呼ぶ | `~/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn` | 他のプロジェクトでも使いたいとき | パスが長い |
| ③ Homebrew | `brew install maven` | どのディレクトリでも `mvn` を使いたい（day1のTERASOLUNA生成など） | **時間がかかる＋JDKがもう1つ入る**（下記） |

### ① mvnw（Maven Wrapper）— このプロジェクトに同梱済み

```bash
cd ~/git/learning-notes/onboarding-2026-10/exercises/test-drill/with-tools
./mvnw test
```

Mavenのインストールは不要。初回だけ指定バージョン（3.9.16）を `~/.m2/wrapper/` に取得して使う。
**Maven本体のバージョンがリポジトリに固定されるので、再現性の点ではこれが一番強い**（他のマシンでも同じ結果になる）。
`mvnw` / `mvnw.cmd` / `.mvn/wrapper/maven-wrapper.properties` がその仕組み。**`./mvnw test` の動作確認済み**。

### ③ brew の注意（2026-10-02に実際に起きたこと）

`brew install maven` を実行したが、**完了しなかった**。出力の最後は:

```
==> Installing maven dependency: openjdk
```

ここで止まっており、`brew list maven` は `Error: No such keg` を返した（＝未インストール）。

2つ知っておくこと:

1. **時間がかかる**: Intel Mac（`/usr/local/Homebrew`）では依存（gettext等）を**ソースからビルド**することがあり、10分以上かかった。
2. **JDKがもう1つ増える**: brewのmavenは `openjdk` に依存するため、**Homebrew版のJDKが別途インストールされる**。手元は現在JDK 25が1つだけの状態なので、どのJDKでビルドされているか分かりにくくなる可能性がある。

**この演習にbrewは不要**（①で足りる）。day1のTERASOLUNA生成（`mvn archetype:generate`）のようにプロジェクト外で`mvn`が要る場面だけ、②のパスを直接使うか、brewを入れる。

> 失敗の教訓: `brew install maven 2>&1 | tail -5` のように**パイプでつなぐと終了コードが `tail` のものになり、brewの失敗が0として見える**。インストールの成否は終了コードではなく `brew list <formula>` や `which` で確かめる。

### ② 既存ラッパーを直接使う（最初の検証はこれで行った）

studymate-aiが Maven Wrapper を使っているため、Maven本体が既にダウンロード済みだった。

```bash
MVN=~/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn
$MVN -v
```

実測の出力:
```
Apache Maven 3.9.16
Maven home: /Users/kimurashunyuu/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f
Java version: 25.0.1, vendor: Oracle Corporation
```

毎回打つのが面倒ならエイリアスにする（`~/.zshrc` 等に）:
```bash
alias mvn='~/.m2/wrapper/dists/apache-maven-3.9.16/56ba1f9f/bin/mvn'
```
※ brewのmavenが入ったらこのエイリアスは消す（どちらが動いているか分からなくなるため）。

---

## 2. 動作確認の手順

```bash
cd ~/git/learning-notes/onboarding-2026-10/exercises/test-drill/with-tools
./mvnw test         # Mavenのインストール不要。これが推奨
```

**成功したときの実測結果**（初期状態、テスト3件）:

```
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- in example.tax.TaxServiceTest
```

カバレッジは `target/site/jacoco/jacoco.csv` で数値確認できる:

```bash
cat target/site/jacoco/jacoco.csv | cut -d, -f3-9
```

```
CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED
Vehicle,0,15,0,0,0,1
TaxService,21,39,3,5,4,13      ← 命令65% / 分岐62.5%（わざと足りない状態）
```

HTMLで見る:
```bash
open target/site/jacoco/index.html
```

TODOを3件埋めた後は **`TaxService,0,60,0,8,0,17`（すべて100%）** になることを確認済み。

---

## 3. ハマった点（実際に出たエラーと対処）

### 3-1. Mockitoの自己アタッチ警告（JDK 25）

**最初に出た警告**:
```
Mockito is currently self-attaching to enable the inline-mock-maker.
This will no longer work in future releases of the JDK.
Please add Mockito as an agent to your build ...
```

テストは通るが、**JDKの将来版では動かなくなる**という警告。案件でJDKを上げたときに突然壊れる種類のもの。

**対処**: Mockitoを正式にJavaエージェントとして渡す。`pom.xml` の surefire に:
```xml
<argLine>@{argLine} -javaagent:${org.mockito:mockito-core:jar}</argLine>
```

### 3-2. ところが、それだけでは起動に失敗する

上を書いただけで実行すると、**VMが起動せずビルドが落ちた**:

```
[ERROR] Error occurred during initialization of VM
[ERROR] Error occurred in starting fork, check output in log
...  '-javaagent:${org.mockito:mockito-core:jar}'   ← 展開されていない
```

`${org.mockito:mockito-core:jar}` が**文字列のまま**JVMに渡っていた。このプロパティは自動では解決されない。

**対処**: `maven-dependency-plugin` の `properties` ゴールを実行させて、依存jarのパスをプロパティとして使えるようにする。

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-dependency-plugin</artifactId>
  <version>3.8.1</version>
  <executions>
    <execution>
      <goals><goal>properties</goal></goals>
    </execution>
  </executions>
</plugin>
```

これで警告が消え、テストも通る（検証済み）。

### 3-3. `@{argLine}` を書き忘れるとカバレッジが0%になる

JaCoCoは「エージェントを起動引数に追加する」形で計測する。その引数は `argLine` プロパティに入る。
surefireの `<argLine>` を**自分で上書きしてしまうと、JaCoCoの指定が消えてカバレッジが0%**になる。

```xml
<!-- NG: JaCoCoの指定を消してしまう -->
<argLine>-javaagent:...</argLine>

<!-- OK: @{argLine} で引き継ぐ -->
<argLine>@{argLine} -javaagent:...</argLine>
```

**`@{...}`（アットマーク）であって `${...}` ではない**。ここは間違えやすい。
「テストは動くのにカバレッジが0%」のときは、まずここを疑う。

### 3-4. 残る無害な警告

```
Java HotSpot(TM) 64-Bit Server VM warning: Sharing is only supported for boot loader classes
because bootstrap classpath has been appended
```

JavaエージェントとCDS（クラスデータ共有）の組み合わせで出る**無害な警告**。消したいなら `-Xshare:off` を argLine に足す。テスト結果・カバレッジには影響しない。

---

## 4. 検証済みのバージョン組み合わせ

JDK 25 は比較的新しいため、古いバージョンだと動かないものがある。**この組み合わせで動作確認済み**:

| 用途 | ライブラリ / プラグイン | バージョン |
|---|---|---|
| ビルド | Apache Maven | 3.9.16 |
| 実行 | JDK | 25.0.1 |
| コンパイル対象 | `maven.compiler.release` | **21**（JDK25で21向けにビルド） |
| テスト | junit-jupiter | 5.11.4 |
| モック | mockito-core / mockito-junit-jupiter | 5.15.2 |
| テスト実行 | maven-surefire-plugin | 3.5.2 |
| カバレッジ | jacoco-maven-plugin | **0.8.13** |
| jarパス解決 | maven-dependency-plugin | 3.8.1 |

> **`release` を21にしている理由**: 案件のJava版数がまだ未確認のため、手元のJDK25の新機能に依存しないようにしている。案件が17なら `21` → `17` に変えれば同じように動くはず（未検証）。

---

## 5. 片付け・やり直し

```bash
# ビルド成果物を消して最初から
cd ~/git/learning-notes/onboarding-2026-10/exercises/test-drill/with-tools
rm -rf target
./mvnw test
```

`target/` は `.gitignore` 済みなのでコミットされない。

ローカルリポジトリ（`~/.m2/repository`）はstudymate-aiと共用。消すと他プロジェクトのビルドもやり直しになるので**消さない**。

---

## 6. 案件で同じことをするときの確認事項（未確認）

参画後に確認し、この表を埋める。

| 項目 | 手元 | 案件 |
|---|---|---|
| Java版数 | 25（`release=21`でビルド） | ？ |
| ビルドツール | Maven 3.9.16 | ？（Maven / Gradle / Ant） |
| テストフレームワーク | JUnit 5 | ？（**JUnit 4 の可能性あり**。アノテーションが違う） |
| モックライブラリ | Mockito 5 | ？（JMockit・PowerMockの現場もある） |
| カバレッジ | JaCoCo | ？（Coverityは静的解析で別物） |
| カバレッジ目標値 | — | ？（C0何%／C1何%が合格基準か） |

**TERASOLUNA 5.x は Spring Boot ではない**ので、`@SpringBootTest` ではなく `spring-test` の `@ContextConfiguration` 系を使う構成になるはず（未検証）。案件の既存テストコードを読んで確かめる。
