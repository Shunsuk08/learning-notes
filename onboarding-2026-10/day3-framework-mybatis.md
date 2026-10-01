# Day3（10/3 土）午前: 項目9〜14（TERASOLUNA・Spring・MyBatis・Security）／午後: Stream・Optional と MyBatis3版

| 時間 | 内容 |
|---|---|
| 午前 30分 | 9. TERASOLUNAアーキテクチャ全体理解 |
| 午前 25分 | 10. TERASOLUNA標準コーディング規約 |
| 午前 25分 | 11. Spring MVC実践 |
| 午前 40分 | 12. Spring DI/AOP/Transaction |
| 午前 30分 | 13. TERASOLUNA×MyBatis |
| 午前 30分 | 14. Spring Security基礎 |
| 午後 60分 | Javaリハビリドリル（ラムダ・Stream・Optional） |
| 午後 120分 | TodoアプリをMyBatis3版に作り直す |
| 締め 10分 | 確認問題・3問プロトコル・一行ログ |

この日の座学は、**Day1・Day2で書いたコードを開いたまま**読む。説明に出てくるクラスやアノテーションを、自分のコードの中で指さしながら進める。

---

# 午前（座学）

## 9. TERASOLUNAアーキテクチャ全体理解

> 会社資料の教材: TERASOLUNA Server Framework for Java ガイドライン、TERASOLUNA Tutorial（どちらも無料の公式）
> - [アプリケーションのレイヤ化](https://terasolunaorg.github.io/guideline/current/ja/Overview/ApplicationLayering.html)（**Day1で描いた図の答え合わせ**）

### 3つの層

| 層 | 主なクラス | 役割 | 知ってはいけないこと |
|---|---|---|---|
| アプリケーション層 | Controller、Form、JSP | 画面の入出力、入力チェック、画面遷移 | SQL、DBの構造 |
| ドメイン層 | Service、Repository（インタフェース）、Model | 業務ロジック、トランザクションの境界 | 画面（Form、HTTP） |
| インフラストラクチャ層 | RepositoryImpl、Mapper XML | DB・外部システムへのアクセス | 業務ルール |

**依存の向き**: アプリケーション層 → ドメイン層 ← インフラストラクチャ層。ドメイン層は誰にも依存しない（インフラ層はドメイン層のRepositoryインタフェースを実装する側）。

前案件で設計したクリーンアーキテクチャと同じ考え方。違いは、TERASOLUNAは「ドメイン層のRepositoryはインタフェース、実装はインフラ層」を**パッケージと設定で強制している**こと（Day1の確認問題: `@ComponentScan` の範囲）。

### レビューでまず見ること

- ServiceがFormを受け取っていないか（アプリ層の型がドメイン層に漏れている）
- ControllerがRepositoryを直接呼んでいないか（業務ロジックとトランザクションが抜ける）
- Serviceの中にSQLや画面の文言が書かれていないか

---

## 10. TERASOLUNA標準コーディング規約

> 会社資料の教材: TERASOLUNA開発ガイドライン、『Clean Code』
> **書籍の代わり**: 下の要約 ＋ 公式
> - [ドメイン層の実装](https://terasolunaorg.github.io/guideline/current/ja/ImplementationAtEachLayer/DomainLayer.html)（命名・Serviceの作り方・トランザクション）
> - [例外ハンドリング](https://terasolunaorg.github.io/guideline/current/ja/ArchitectureInDetail/WebApplicationDetail/ExceptionHandling.html)

**案件独自のコーディング規約が必ずある。参画したら最初にそれを読む。** ここでは、どの案件でも土台になるTERASOLUNAの作法を押さえる。

### 命名（Todoチュートリアルで出てきたもの）

| 種類 | 名前の付け方 | 例 |
|---|---|---|
| Controller | 〜Controller | `TodoController` |
| Form | 〜Form | `TodoForm` |
| Service | 〜Service ＋ 実装は〜ServiceImpl | `TodoService` / `TodoServiceImpl` |
| Repository | 〜Repository | `TodoRepository` |

### 例外の使い分け

| 例外 | 使う場面 | 画面への出方 |
|---|---|---|
| `BusinessException` | 業務ルール違反（未完了の上限超え、二重完了など） | 元の画面にメッセージを出す |
| `ResourceNotFoundException` | 指定されたデータが無い | 「見つかりません」の画面 |
| `SystemException` | 続行できない異常（設定ミス、想定外の状態） | システムエラー画面 |

**やってはいけないこと**: 例外を `catch` して何もしない（握りつぶし）／`catch (Exception e)` で全部まとめて捕まえる／例外の原因（`e`）を捨てて新しい例外を投げる

### Clean Codeの要点（書籍の代わり）

- 名前で意図を伝える（`flg`・`tmp`・`data2` は使わない）
- 1つのメソッドは1つのことだけ。長くなったら分ける
- コメントで「何をしているか」を説明しない。名前で分かるようにする。コメントは「なぜ」だけ
- 引数は少なく。`boolean` 引数で動きを切り替えない（メソッドを分ける）

---

## 11. Spring MVC実践

> 会社資料の教材: Spring公式ガイド「Building Web Applications with Spring Boot」、Spring Framework Reference
> - [Spring MVCアーキテクチャ概要（TERASOLUNA）](https://terasolunaorg.github.io/guideline/current/ja/Overview/SpringMVCOverview.html)（こちらを主に読む）
> - [Serving Web Content with Spring MVC（Spring公式ガイド）](https://spring.io/guides/gs/serving-web-content)（Spring Boot版。studymate-aiとの比較用に眺める）

### 1リクエストの流れ

```
ブラウザ
  → DispatcherServlet（全リクエストの受付）
  → HandlerMapping（URLから担当のControllerメソッドを探す。@GetMapping等を見る）
  → Controller（入力チェック → Serviceを呼ぶ → 結果をModelに入れる → 画面名を返す）
  → ViewResolver（画面名 "todo/list" → /WEB-INF/views/todo/list.jsp）
  → JSP（Modelの値でHTMLを組み立てる）
  → ブラウザ
```

studymate-aiで追った `@GetMapping` → URL対応表の登録（`RequestMappingHandlerMapping`）は、ここの「HandlerMapping」に当たる。

### PRG（Post-Redirect-Get）パターン

Todoの「作成」は、POSTで受けた後に `redirect:/todo/list` を返していた。理由は、**画面を再読み込みしたときに同じ登録が2回走らないようにする**ため。公共系の画面は二重登録が致命的なので、レビューで必ず見る。

---

## 12. Spring DI/AOP/Transaction

> 会社資料の教材: Spring Framework Reference、書籍『Spring徹底入門 第2版』
> **書籍の代わり**: 下の要約 ＋ 公式
> - [ドメイン層の実装 — トランザクション管理の節（TERASOLUNA）](https://terasolunaorg.github.io/guideline/current/ja/ImplementationAtEachLayer/DomainLayer.html)
> - [Declarative Transaction Management（Spring公式）](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative.html) / [AOP（Spring公式）](https://docs.spring.io/spring-framework/reference/core/aop.html)

### DI（依存性の注入）

Serviceは `new TodoRepositoryImpl()` と書かずに、`@Inject`（または `@Autowired`）で受け取る。**どの実装を渡すかはSpringが決める** → Day3午後のMyBatis版で、Serviceを変えずに実装を差し替えられるのはこのため。studymate-aiで学んだIoC（制御の反転）と同じ話。

### AOP（横断的な処理を後付けする）

ログ・トランザクション・例外のログ出力など、**全Serviceに共通の処理**を、各メソッドに書かずに外から差し込む仕組み。Springは、対象のBeanを**プロキシ（代理オブジェクト）**で包み、メソッド呼び出しの前後に処理を挟む。

Day1で作ったブランクの `TodoDomainConfig` に実例がある:

```java
pointcut.setExpression("@within(org.springframework.stereotype.Service)");
```

「`@Service` が付いたクラスのメソッドすべてに、業務例外のログ出力を差し込む」という設定。自分のコードで確かめること。

### `@Transactional` の落とし穴（レビューの頻出ポイント）

| 落とし穴 | なぜ起きるか |
|---|---|
| **検査例外（`Exception` の子で `RuntimeException` でないもの）ではロールバックされず、コミットされる** | Springの既定動作。TERASOLUNAのガイドラインにも「注意が必要」と書かれている |
| 同じクラスの中から `@Transactional` メソッドを呼んでも効かない | 呼び出しがプロキシを通らないため |
| `private` メソッドに付けても効かない | プロキシが横取りできないため |
| 参照だけの処理に `readOnly = true` が無い | 付けると読み取り専用で実行され、誤った更新を防げる・最適化されることがある |

---

## 13. TERASOLUNA×MyBatis

> 会社資料の教材: MyBatis公式ドキュメント、TERASOLUNA MyBatis連携ガイド（どちらも無料の公式）
> - [データベースアクセス（MyBatis3編）（TERASOLUNA）](https://terasolunaorg.github.io/guideline/current/ja/ArchitectureInDetail/DataAccessDetail/DataAccessMyBatis3.html)
> - [MyBatis Mapper XMLファイル（公式・日本語）](https://mybatis.org/mybatis-3/ja/sqlmap-xml.html)

### 仕組み

```
TodoRepository（Javaのインタフェース） ←同じ名前で対応→ TodoRepository.xml（SQLを書く）
```

インタフェースのメソッド名と、XMLの `<select id="...">` が対応する。**実装クラスはMyBatisが自動で作る**（午後に確かめる）。

### レビューで見ること

| 観点 | 内容 |
|---|---|
| `#{}` と `${}` | `#{}` はバインド変数（安全）。`${}` は文字列をそのままSQLに埋め込む＝**SQLインジェクションの危険**。`${}` を見たら、値が画面入力から来ていないか必ず確かめる |
| 件数 | 一覧取得に上限やページングがあるか。全件取得は本番データ量で遅くなる |
| N+1 | ループの中で1件ずつSELECTしていないか（100件なら101回SQLが走る） |
| 動的SQL | `<if>` の条件が全部偽のとき、`WHERE` が空になって全件更新・全件削除にならないか |

---

## 14. Spring Security基礎

> 会社資料の教材: Spring Security Reference、OWASP Top10、日本セキュリティオペレーション事業者協議会資料
> **書籍の代わり**: 下の要約 ＋ 無料の公式
> - [Spring Security概要](https://terasolunaorg.github.io/guideline/current/ja/Security/SpringSecurity.html) / [CSRF対策](https://terasolunaorg.github.io/guideline/current/ja/Security/CSRF.html) / [XSS対策](https://terasolunaorg.github.io/guideline/current/ja/Security/XSS.html)（TERASOLUNA）
> - [IPA 安全なウェブサイトの作り方](https://www.ipa.go.jp/security/vuln/websecurity/about.html)（日本語で網羅的。公共案件のレビュー観点に近い）
> - [OWASP Top 10（2025）](https://owasp.org/Top10/2025/)

| 用語 | 意味 | TERASOLUNAでの対策 |
|---|---|---|
| 認証 | あなたは誰か（ログイン） | Spring Securityのログイン処理。パスワードはハッシュ化して保存（ブランクの `ApplicationContextConfig` に `PasswordEncoder` がある） |
| 認可 | あなたは何をしてよいか | URLやメソッドごとにロールで制限 |
| CSRF | 罠サイトから、ログイン中の利用者に意図しない更新をさせる攻撃 | フォームにトークンを埋め込み、POST時に照合（`<form:form>` が自動で入れる。Day2で確認済み） |
| XSS | 入力値に仕込んだスクリプトが画面で実行される攻撃 | JSPで値を出すときに必ずエスケープする（TERASOLUNAでは `${f:h(...)}`） |
| SQLインジェクション | 入力値でSQLを書き換える攻撃 | MyBatisで `#{}` を使う（項目13） |

---

# 午後（手を動かす）

## A. Javaリハビリドリル（ラムダ・Stream・Optional）

```bash
cd ~/git/learning-notes/onboarding-2026-10/exercises/java-refresh
java VehicleDrill.java   # 最初は全部 NG
```

`TODO` の5メソッドを書き、全部 `OK` にする。ルール:

- **for文を使わない**（ラムダ・Stream・Optionalで書く）
- **AIに聞かない**。詰まったら、下の「ヒント」→ Javadocの順に見る
- 1問ごとに、昔の書き方（`...Old` メソッド）と並べて「何が短くなったか」を一言メモ

公共系の更改案件ではJavaの版数が古いことがある。`record`・`var`・`switch`式などは案件の版数が分かってから。ラムダ・Stream・Optional（Java 8から使える）はどの版でも出てくるので先にやる。

<details><summary>ヒント</summary>

- Q1: `filter` → `map` → `collect(Collectors.toList())`
- Q2: `BigDecimal` は `+` で足せない。`reduce(初期値, 足し算の関数)`
- Q3: `Collectors.groupingBy(キー, Collectors.counting())`
- Q4: `Optional#map` は中身が `null` を返すと空のOptionalになる
- Q5: `Comparator.comparing(...).reversed().thenComparing(...)`
</details>

<details><summary>解答（全部OKになってから見る）</summary>

```java
static List<String> unpaidIds() {
    return VEHICLES.stream().filter(v -> !v.isPaid()).map(Vehicle::getId).collect(Collectors.toList());
}

static BigDecimal unpaidTotal() {
    return VEHICLES.stream().filter(v -> !v.isPaid()).map(Vehicle::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

static Map<String, Long> countByPrefecture() {
    return VEHICLES.stream().collect(Collectors.groupingBy(Vehicle::getPrefecture, Collectors.counting()));
}

static String ownerNameOrUnknown(String id) {
    return findById(id).map(Vehicle::getOwnerName).orElse("不明");
}

static List<String> unpaidIdsSortedByAmount() {
    return VEHICLES.stream().filter(v -> !v.isPaid())
            .sorted(Comparator.comparing(Vehicle::getTaxAmount).reversed().thenComparing(Vehicle::getId))
            .map(Vehicle::getId).collect(Collectors.toList());
}
```

- Q4がこの日一番大事。`isPresent()` → `get()` の組み合わせはOptionalを使う意味が無くなる書き方で、レビューでよく指摘される。
- Q5の `reversed()` は、それより前に組み立てた比較全体を逆にする。`thenComparing` の後に `reversed()` を付けると、IDの順番まで逆になる。
- `toList()`（Java 16〜）を使えば短くなるが、案件の版数が分かるまでは `collect(Collectors.toList())` で書く。
</details>

## B. TodoアプリをMyBatis3版に作り直す

MyBatis3を使うには、**MyBatis3用のブランクプロジェクトを作り直し**、Day2のファイルをコピーする（公式チュートリアル「O/R Mapperに依存したブランクプロジェクトの作成」の方針）。

```bash
cd ~/git/terasoluna-todo
mvn archetype:generate -B \
 -DarchetypeGroupId=org.terasoluna.gfw.blank \
 -DarchetypeArtifactId=terasoluna-gfw-web-blank-jsp-mybatis3-archetype \
 -DarchetypeVersion=5.11.0.RELEASE \
 -DgroupId=com.example.todo \
 -DartifactId=todo-mybatis3 \
 -Dversion=1.0.0-SNAPSHOT
```

10/1にこのarchetypeで生成・ビルドできることを確認済み。DBはH2のインメモリ（`META-INF/spring/` 配下の `*-infra.properties` に接続先がある）。

### 手順

1. Day2で書いたファイルのうち、**インメモリのRepository実装以外**を新しいプロジェクトにコピーする
2. 公式チュートリアル「MyBatis3を使用したインフラストラクチャ層の作成」に従い、テーブル作成の設定とMapperファイル（`TodoRepository.xml`）を書く
3. ビルド → Tomcatの `webapps/` に置いて起動（warの名前が変わるのでURLは `/todo-mybatis3/`）
4. 画面の操作がDay2と同じに動くことを確かめる

### ここで掴むこと（午前の項目と結びつける）

| 問い | 確かめ方 | 項目 |
|---|---|---|
| Repositoryの実装クラスを書いていないのに、なぜ動くのか | チュートリアルの説明と設定ファイルを探す | 13 |
| ServiceとControllerを1行も変えずにDBに切り替えられたのはなぜか | Day1の図の「依存の向き」と結びつける | 9・12 |
| SQLはいつ・何回発行されているか | `logback.xml` でSQLのログを出し、画面操作と見比べる | 13 |
| トランザクションはどこで始まり、どこでコミットされるか | ログの `DataSourceTransactionManager` の行を探す | 12 |

### 改造課題

1. 一覧画面に「未完了のみ表示」を追加する。**先に日本語フローパイプを書き**、どの層に何を足すかを決めてから書く
2. タイトルに `<script>alert(1)</script>` を入れて登録し、一覧で実行されないことを確かめる。JSPのどこでエスケープされているか探す（項目14）

---

## 確認問題

**Q1.** 次のコードの問題点を2つ挙げよ。

```java
Optional<Todo> todo = todoRepository.findById(id);
if (todo.isPresent()) {
    return todo.get().getTitle();
}
return null;
```

<details><summary>答え</summary>

①`isPresent()` + `get()` でOptionalの意味が無い。`findById(id).map(Todo::getTitle)` と書ける。②無いときに `null` を返していて、呼び出し側でNPEが起きうる。`Optional<String>` を返すか、TERASOLUNAなら `ResourceNotFoundException` を投げるのが定石。
</details>

**Q2.** 次のServiceで、`sendMail` が `IOException`（検査例外）を投げた。DBの更新はどうなるか。

```java
@Transactional
public void finish(String todoId) throws IOException {
    todoRepository.update(todo);   // 完了に更新
    mailSender.sendMail(todo);     // IOException を投げることがある
}
```

<details><summary>答え</summary>

**コミットされる**（完了に更新されたまま、メールは送られていない）。Springの既定では、検査例外ではロールバックしない。ロールバックしたいなら `@Transactional(rollbackFor = Exception.class)` にするか、業務例外（非検査例外）に包んで投げる。更新と外部送信の順番そのものも設計の論点になる。
</details>

**Q3.** MyBatis3版でServiceとControllerを変えずに済んだのは、レイヤ化のどの性質のおかげか。

<details><summary>答え</summary>

ドメイン層（Service）はRepositoryの**インタフェース**にだけ依存し、実装（インメモリかMyBatisか）を知らない。実装の差し替えはインフラ層の中で閉じる。更改案件で「DBやフレームワークを入れ替えても業務ロジックを書き換えない」ための前提。
</details>

---

## 締め

1. セッション締めの3問プロトコル
2. 一行ログ
