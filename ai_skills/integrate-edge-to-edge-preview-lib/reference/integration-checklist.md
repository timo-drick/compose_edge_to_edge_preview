# Integration Handoff Checklist (`edge_to_edge_preview_lib`)

Use this checklist after applying the integration skill.

- [ ] Target module identified (`Android-only` or `KMP`).
- [ ] Dependency added: `de.drick.compose:edge-to-edge-preview:<version>`.
- [ ] If KMP: dependency is in `commonMain`.
- [ ] If KMP: `ui-tooling-preview` exists in `commonMain`.
- [ ] If KMP: `ui-tooling` exists in `androidMain`.
- [ ] If KMP: Android target exists in the same module.
- [ ] At least one `@Preview` wraps content in `EdgeToEdgeTemplate`.
- [ ] At least one non-default variant is present (e.g., `NavigationMode.Gesture`, `CameraCutoutMode.Middle`, `InsetMode.Hidden`, `showInsetsBorder = true`).
- [ ] Preview renders in IDE without import/symbol errors.
- [ ] Module compiles successfully after integration.