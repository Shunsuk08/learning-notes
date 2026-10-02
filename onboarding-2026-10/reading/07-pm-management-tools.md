# 進捗管理の実例調査: WBS表・ガント・課題管理表・週次報告・カンバン・Copilot活用

day1 §3（進捗管理・課題管理）の実例集め。概念（WBS/予実、課題とリスクの違い、90%症候群）は本人がday1 §3・day1確認問題で習得済みという前提で、**手法・ツールの名前と実際のフォーマットだけ**をここにまとめる。すべてWebFetchで内容を確認した出典のみ記載。

## WBS表

- **BizTemplateLab**: https://biztemplatelab.com/template/wbs/
  番号/作業名/担当/開始終了/工数に加え、予定・実績の開始終了日＋進捗を並べる「ストライプ型」が予実比較に向く。「基本型」「縦向き簡易型」も収録。
- **Incubation Base（システム開発向け本格版）**: https://incubation-base.com/column/system-development-wbs/
  担当/確認者/承認者、優先度・リスク・バッファ要否、予定実績工数、依存関係・完了条件まで含む。開始終了日の入力でガントが自動表示され、予実差分・遅延件数も自動集計。Web開発/業務刷新の2サンプル入り。

## ガントチャート

- **BizTemplateLab（Excel基本形）**: https://biztemplatelab.com/template/ganttchart/
  タスク名/開始日/終了日/工数を列入力し、条件付き書式でバー表示、WORKDAY関数で完了日を自動算出する典型的なExcelガント構造。
- **Backlog公式ブログ（Excel vs 専用ツール比較）**: https://backlog.com/ja/blog/ganttchart-annualplan-excel/
  Excel版（WBS作成→横軸設定→セル塗り/条件付き書式）と、Backlogのような専用ツール版（ドラッグ&ドロップ変更、日週月表示切替）を比較。更新頻度が高い現場はツール、月次報告中心ならExcelが向くと整理。

## 課題管理表

- **NotePM（テンプレ10選）**: https://notepm.jp/blog/31380
  最小構成は「課題番号/課題名/詳細/優先度/担当者/期限/状況/備考」。進捗率%、リスク評価と対応策、障害発生日などを足した派生パターンあり。**公共系では「対応策」列を必須にする運用が多い**との言及。

## 週次ステータス報告

- **PLUS PM（週次作業報告書）**: https://plus-pm.jp/blog/weekly-report-project-activity-history/
  基本情報(作成日/所属/承認欄)＋活動履歴テーブル(日付/プロジェクト名/作業内容/進捗率/作業時間)＋振り返り(連絡相談・反省・改善)＋次週予定という構成。
- **SIMPLEONESOFT（進捗報告の型解説）**: https://simpleonesoft.com/blog/pm0008/
  プロジェクト名/報告期間→スケジュール可視化→進捗状況（総括＋個別、判断根拠を明記）→課題と対策（重要なものだけ厳選）→相談事項、という「事実→評価→対策→依頼」の型。day1の報告の型と一致。

## カンバン/タスクボードの採否（公共系での実態）

- Qiita（タスクボード実務解説、アジャイル前提）: https://qiita.com/kakashi_h5/items/c2c9e1a8bc0bbe3ea016 — ToDo/Doing/Doneの3レーンと朝会運用を紹介。ウォーターフォールへの適用には言及なし。
- OGIS-RI（ウォーターフォール×アジャイル×かんばん併用）: https://www.ogis-ri.co.jp/otc/hiroba/technical/Agile_using_Hansoft/chap2.html — WIP制限だけは手法を問わず輸入可能という評価。

**結論（未確認込み）**: 公共系プロジェクトでカンバンが実際に使われている一次情報は確認できなかった。「ウォーターフォール現場はExcel管理表が主流、カンバン導入は少数派」という評価が複数記事で一致しており、Excelの課題管理表/WBSで代替しているのが実態に近いと判断（裏付けは業界記事の傾向のみ、一次情報ではない）。

## GitHub Copilotの進捗管理活用事例

- **Zenn / Microsoft公式（GitHub Copilot CLIでタスク管理）**: https://zenn.dev/microsoft/articles/copilot-task-management
  GitHub Issues/Projectsと連携し、自然言語で「In Progressのタスク一覧を教えて」「ステータスをIn Progressに変更して」と指示するとCopilotがIssue作成・ラベル付け・Projects更新まで行う実例。
- **Docswell（Copilot User Group Japan #6 登壇資料）**: https://www.docswell.com/s/RohomanShahin/KPR3W2-2026-09-29-215143
  会議中のアクションアイテムをその場でIssue化する周辺活用のみで、本格的なPM/ステータスレポート生成の専用機能紹介ではない。

**結論**: 確認できた事例はいずれも**GitHub Issues/Projects文化の現場向け**。公共系のExcel管理表をCopilotが直接効率化する一次情報は見つからず、ここは「確認できず」のまま。GitHub Copilot（コード生成アシスタント）と、Excel内で動くMicrosoft 365 Copilotは別製品であることに注意。

**Why**: day1 §3の概念はすでに習得済みで、参画準備として不足していたのは「手法・ツールの名前と具体的なフォーマット」。この調査で埋めた。
**How to apply**: 参画後、実際の案件がどの管理表・ツールを使っているか確認し、ここに実例の形を当てはめて素早くキャッチアップする。カンバン・Copilot連携は過度な期待を持たず、実際の案件のツールに合わせる。
