# Architecture & Design Decisions

## 目的
このドキュメントは、プロジェクトにおける重要な技術的決定、設計判断、およびその理由（Why）を記録します。
仕様の詳細は `docs/spec.md` を参照してください。

## 記録ルール
- 1決定 = 1エントリ
- 簡潔に記述（結論 → 理由 → 影響）
- 判断の履歴として残し、変更時は新しいIDで追記し過去分を Superseded に更新する

## 新規エントリ用テンプレ
```markdown
### [ID]: [Title]
- **Date**: YYYY-MM-DD
- **Status**: [Accepted / Superseded]
- **Context**: [前提・問題]
- **Options**: [検討した選択肢]
- **Decision**: [採用した結論]
- **Rationale**: [理由]
- **Consequences**: [影響/トレードオフ]
- **References**: [spec/PRリンク等]
```

## Decisions

### D001: 意思決定記録（ADR）の導入
- **Date**: 2026-01-30
- **Status**: Accepted
- **Context**: 開発中の「なぜこの設計にしたか」という背景情報が失われやすく、後からの変更や意図の把握が困難になる。
- **Options**: コミットメッセージのみ、ドキュメント化（ADR）
- **Decision**: `docs/decisions.md` による判断記録の導入
- **Rationale**: 仕様（What）とは別に判断理由（Why）を明文化することで、チーム内での認識共有と将来のメンテナンス性を高めるため。
- **Consequences**: ドキュメントの維持コストが発生するが、情報の透明性が向上する。
- **References**:
