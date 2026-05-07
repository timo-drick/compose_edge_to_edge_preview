# Integration Handoff Checklist (`edge_to_edge_preview_check_lib`)

Use this checklist after applying the integration skill.

- [ ] Target module/source set identified (Android-only or KMP `commonMain`/other preview source set).
- [ ] Dependency added: `de.drick.compose:edge-to-edge-preview-check:<version>`.
- [ ] If matcher APIs are unresolved: Compose UI matcher dependency (commonly `ui-test`) added to the same source set.
- [ ] At least one preview includes `TestWindowInsets { ... }`.
- [ ] At least one `checkOverlap(...)` check targets text/safe area (`SemanticsProperties.Text` + `WindowInsets.safeDrawing`).
- [ ] At least one `checkOverlap(...)` check targets interactions (`hasClickAction()` + `WindowInsets.tappableElement` or `safeContent`).
- [ ] If diagnostics need stable inset behavior: preview is wrapped with `EdgeToEdgeTemplate(...)`.
- [ ] Preview compiles/renders without unresolved symbols or runtime preview errors.