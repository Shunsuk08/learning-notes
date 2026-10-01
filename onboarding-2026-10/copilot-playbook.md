# GitHub Copilot 従量課金下の使い方（項目7・8）

Claude Code（Teamプラン、ほぼ使い放題）から、GitHub Copilot（従量課金）に移るための手引き。

## 1. 公式で確認した事実（2026-10-01時点）

| 事実 | 出典 |
|---|---|
| 2026年6月から、組織向け（Business / Enterprise）は **AI credits** で課金される。1 AI credit = 0.01ドル | [Usage-based billing for organizations and enterprises](https://docs.github.com/en/copilot/concepts/billing/usage-based-billing-for-organizations-and-enterprises) |
| Copilot Businessは1ユーザー月19ドルで、**1,900 AI credits**（=19ドル分）が付く。組織全体でまとめて使う（プール） | 同上 |
| **コード補完と次の編集候補（next edit suggestions）はAI creditsを消費しない**（有料プランは無制限） | 同上 |
| Chat・エージェント・CLI・コードレビュー等は、**送ったトークン（入力）と返ってきたトークン（出力）の量 × モデル単価**で消費する | 同上 |
| **個人の予算を使い切ると、組織の枠が残っていてもその人のCopilotは止まる。安いモデルへの自動切り替えは無い** | 同上 |
| 単価の例（100万トークンあたり、入力／出力）: GPT-5 mini（既定） 0.25／2.00ドル、Claude Haiku 4.5 1.00／5.00、Claude Sonnet 5 2.00／10.00、Claude Opus 5.5 4.00／20.00 | [Models and pricing](https://docs.github.com/en/copilot/reference/copilot-billing/models-and-pricing) |
| Copilotのコードレビューは、AI creditsに加えてGitHub Actionsの時間も使う | 同上 |

**案件で実際に使えるモデル・予算・機能は、組織の管理者の設定次第**。参画したら最初に確認する（README末尾の質問リスト参照）。

## 2. 試算してみる（自分で計算してから答えを開く）

**問1.** GPT-5 miniに、入力3,000トークン・出力800トークンの質問をした。何credit使うか。

**問2.** Claude Opus 5.5で、エージェントに15往復の作業をさせた。1往復あたり入力40,000トークン（会話履歴＋読ませたファイル）、出力2,000トークン。何credit使うか（キャッシュは無視）。

**問3.** 1,900 creditsで、問1の質問と問2のセッションはそれぞれ何回できるか。

<details><summary>答え</summary>

- 問1: 入力 3,000 × 0.25 / 100万 = 0.00075ドル、出力 800 × 2.00 / 100万 = 0.0016ドル。合計 約0.0024ドル = **約0.24 credit**
- 問2: 入力 15 × 40,000 = 60万トークン × 4.00 / 100万 = 2.40ドル、出力 15 × 2,000 = 3万トークン × 20.00 / 100万 = 0.60ドル。合計 3.00ドル = **300 credits**
- 問3: 問1は約8,000回、問2は**約6回**

**読み取ること**:
1. 重いモデルのエージェント作業は、軽いモデルの質問の**1,000倍以上**かかる
2. コストの大半は**入力**。往復するたびに会話履歴が全部入力として送り直される。**長い会話・大きなファイル・大量のログ**が高くつく
3. Claude Codeで1日に何度もやっていた「エージェントにまとめて任せる」使い方を同じ調子で続けると、**月の前半で止まる**
</details>

## 3. 使い方の型

### 原則

**補完（無料）を主戦場にし、チャットは「考えが決まってから1回で頼む」。重いモデルは設計の相談だけ。**

### 頼む前

1. **日本語フローパイプを先に書く**（Claude Codeで練習してきたもの）。処理手順が決まっていれば、1往復で終わる
2. 調べものは、**論点を1つに絞ってから**聞く。「このエラー何？」ではなく「このスタックトレースの◯行目で、XがnullになるのはYとZのどちらが原因か」

### 頼むとき

3. **必要なファイルだけ添付する**。ワークスペース全体やフォルダごと渡さない
4. ログは**grepで絞ってから**貼る（Day1の手順4と同じ）
5. 「**差分だけ出して**」「説明は3行で」と出力を絞る（出力は入力より単価が高い）
6. **話題が変わったら新しいチャットにする**。履歴が長いほど毎回の入力が増える

### モデルの使い分け

| 用途 | モデルの目安 |
|---|---|
| 補完・短い質問・定型コード（テストの雛形、getter等） | 既定の軽いモデル |
| 普通の実装相談・レビュー | 中くらいのモデル |
| 設計判断・難しいバグの原因調査 | 重いモデルを、**論点を絞って1〜2往復だけ** |

### 頼んだ後

7. 出てきたコードは、**固定チェックリスト（null／境界値／型／タイムアウト／並行性）で自分でレビュー**する。再生成で直そうとしない（再生成のたびに課金される）
8. 週1回、使用量を見て一行ログに書く（[Monitor AI credits usage](https://docs.github.com/en/copilot/concepts/billing/usage-based-billing-for-organizations-and-enterprises) から辿れる）

## 4. Claude Codeとの対応表

| Claude Codeでやっていたこと | Copilotでの置き換え |
|---|---|
| `CLAUDE.md` に教師モード等を書く | `.github/copilot-instructions.md`（リポジトリ全体）や `*.instructions.md`（特定のファイルだけ）に書く。**毎回の入力に乗るので短く** |
| スキル（定型の手順） | プロンプトファイル（`.github/prompts/*.prompt.md`、IDEから呼び出す） |
| Planモード | Copilotの **Plan**（VS Codeでは `/plan`）。実装前に計画を出させ、読んでから実装させる |
| エージェントにまとめて任せる | 範囲を1機能・数ファイルに絞って任せる。任せる前に計画をレビューする |
| `/compact` で履歴を圧縮 | 新しいチャットを開き、前の結論だけを3行で貼る |
| サブエージェントで調査 | 自分で `grep` やIDEの検索で絞り込んでから聞く |

### `copilot-instructions.md` の雛形（案件で使ってよいか確認してから置く）

```markdown
- 日本語で、結論から簡潔に答える。
- コードは変更箇所の差分だけを示す。ファイル全体を書き直さない。
- TERASOLUNA Server Framework for Java 5.x の開発ガイドラインの作法に従う。
- 例外を握りつぶさない。nullを返さず、Optionalか業務例外にする。
- 金額はBigDecimalで扱う。
- 不確かなことは「未確認」と書く。
```

## 5. 公式資料（座学用）

- [GitHub Copilot 基本編（Microsoft Learn、日本語）](https://learn.microsoft.com/ja-jp/training/paths/copilot/)
- [Your first custom instructions（GitHub Docs）](https://docs.github.com/en/copilot/tutorials/customization-library/custom-instructions/your-first-custom-instructions)
- [カスタム指示でコードレビューを活用する（GitHub Docs 日本語）](https://docs.github.com/ja/copilot/tutorials/use-custom-instructions)
- [Copilot code review について（GitHub Docs）](https://docs.github.com/en/copilot/concepts/agents/code-review)
- [Plan work with agents in VS Code](https://code.visualstudio.com/docs/agents/run/planning)
- [Best practices for using AI in VS Code](https://code.visualstudio.com/docs/agents/best-practices)
- [GitHub Skills（ハンズオン教材）](https://skills.github.com/)

案件のIDEがEclipse系の場合、使える機能（Planなど）がVS Codeと違う可能性がある。参画後に確認する。

## 6. セキュリティ（公共案件で特に大事）

- 顧客のソース・データ・設計書をAIに渡してよい範囲は、**案件のルールが最優先**。参画初日に確認するまで、業務コードをチャットに貼らない
- 個人情報・本番データ（実在の氏名、車両番号など）は貼らない。ダミーに置き換える
- 管理者が「コンテンツ除外」（特定ファイルをCopilotに読ませない設定）をしている場合がある
