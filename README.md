# learning-notes

31歳で年収800万、36歳で年収1000万を目指すための技術学習カリキュラム。実務3年・SES・AWS×Java×Python軸のバックエンド/インフラエンジニアを対象。

## 構成

- **[aws/](aws/README.md)** — AWS/インフラ側。`studymate-ai`のStage1〜6ロードマップ、IaC・CI/CD・可観測性・障害対応
- **[backend/](backend/README.md)** — バックエンド（アプリケーション設計）側。DB設計、SOLID、デザインパターン、DDD、API設計
- **[reference/](reference/)** — 参考資料
  - [engineering-design-guide.md](reference/engineering-design-guide.md) — エンジニアの設計知識6階層フレーム
  - [skill-priority-matrix.md](reference/skill-priority-matrix.md) — バックエンド15項目の優先度マトリクス（Tier S〜D）と根拠

## 前提となる方針

- **Phase1（今〜1-2年）の主目標**：リーダー経験を商流と戦って取りに行くより、「設計から実装まで一気通貫で自走できる」ことを優先する
- **技術方針**：AWS×Java/Python軸を「尖らせる」（ゼロから別言語へ転向するより時間効率が良いという市場調査の示唆）
- **除外事項**：主体性・説明能力等のヒューマンスキルは別トラックで磨く。このリポジトリは技術・設計面のみを扱う

## `java-learning/` `code-reading-notes/` について

当初の計画枠として作成したディレクトリ（現状`.gitkeep`のみ）。実態は`studymate-ai`リポジトリでの実装そのものがこの学習をカバーしているため、当面このリポジトリは`aws/`・`backend/`・`reference/`を中心に更新する。
