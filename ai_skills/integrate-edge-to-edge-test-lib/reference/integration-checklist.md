# Integration Handoff Checklist (`edge_to_edge_test_lib`)

Use this checklist after applying the integration skill.

- [ ] Test mode identified (`androidTest`, `test`/Robolectric, or both).
- [ ] Dependency added: `de.drick.compose:edge-to-edge-test:<version>` with correct scope (`androidTestImplementation` and/or `testImplementation`).
- [ ] If Robolectric path is used: `de.drick.compose:edge-to-edge-preview:<version>` is also available for inset simulation.
- [ ] Test content includes `SemanticsWindowInsetsAnchor()`.
- [ ] Test environment enables edge-to-edge when required (especially `createComposeRule` activity-backed setups).
- [ ] At least one assertion uses `assertWindowInsets(...)` with meaningful inset types (for example `systemBars | displayCutout` or `tappableElement`).
- [ ] Optional diagnostics path validated (for example `onOverlap` callback and `createScreenshot(...)` in instrumented tests).
- [ ] Target test task(s) compile and run successfully.