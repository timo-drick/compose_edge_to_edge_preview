# Skill: Integrate `edge_to_edge_test_lib`

Use this skill when an AI agent must add automated window-insets overlap checks to an Android Compose test setup by integrating:

- Module: `edge_to_edge_test_lib`
- Maven artifact: `de.drick.compose:edge-to-edge-test`

This skill covers both Android instrumented tests (`androidTest`) and local Robolectric tests (`test`).

## Goal

After applying this skill, the target project should be able to assert that UI nodes do not overlap with selected `WindowInsets` types by using `assertWindowInsets(...)` in Compose tests.

## Prerequisites

- The target project already uses Compose UI tests (`createComposeRule` or equivalent).
- The agent can edit Gradle files and at least one test file.
- For Robolectric flow, the module already has Robolectric test infrastructure.

## Step 1: Detect test mode and target source set

Classify where checks will run before editing:

1. **Instrumented/device tests**
   - Source set: `src/androidTest`
   - Dependency scope: `androidTestImplementation`
2. **Robolectric/local JVM tests**
   - Source set: `src/test`
   - Dependency scope: `testImplementation`

If both are required, configure both paths.

## Step 2: Add dependencies

### 2A. Instrumented tests (`androidTest`)

Add:

```kotlin
dependencies {
    androidTestImplementation("de.drick.compose:edge-to-edge-test:<version>")
}
```

### 2B. Robolectric tests (`test`)

Add:

```kotlin
dependencies {
    implementation("de.drick.compose:edge-to-edge-preview:<version>")
    testImplementation("de.drick.compose:edge-to-edge-test:<version>")
}
```

Why both in Robolectric:
- `edge-to-edge-test` performs overlap assertions.
- `edge-to-edge-preview` can simulate deterministic inset configurations (for example with `EdgeToEdgeTemplate`) in local tests.

If the project uses a version catalog, prefer aliases over hardcoded coordinates.

## Step 3: Prepare test content correctly

In the composable test content:

1. Ensure edge-to-edge is enabled when needed (especially with `createComposeRule` activity-backed setup).
2. Add `SemanticsWindowInsetsAnchor()` in the hierarchy.

Minimal setup pattern:

```kotlin
composeTestRule.setContent {
    // If required by your setup:
    // (LocalContext.current as ComponentActivity).enableEdgeToEdge()

    SemanticsWindowInsetsAnchor()
    AppTheme {
        ScreenToTest()
    }
}
```

Important:
- If `SemanticsWindowInsetsAnchor()` is missing, assertions fail with an anchor-not-found error.
- If insets are empty, assertions can fail with a message indicating edge-to-edge is likely not enabled.

## Step 4: Add overlap assertions

Use `assertWindowInsets(...)` on one node or a node collection.

Typical checks:

```kotlin
composeTestRule
    .onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.Text))
    .assertWindowInsets(
        insetType = WindowInsetsCompat.Type.systemBars() or
            WindowInsetsCompat.Type.displayCutout()
    )

composeTestRule
    .onAllNodes(hasClickAction())
    .assertWindowInsets(
        insetType = WindowInsetsCompat.Type.tappableElement()
    )
```

Useful options:
- `excludeVerticalScrollSides` (default `true`) for scroll-aware checks.
- `complainAboutNoWindowInsets` (collection overload) when debugging empty-insets environments.
- `onOverlap` callback for custom diagnostics.

## Step 5: Optional diagnostics screenshots (instrumented)

For instrumented tests, you can capture overlap screenshots in `onOverlap`:

```kotlin
.assertWindowInsets(
    insetType = WindowInsetsCompat.Type.systemBars(),
    onOverlap = { node, insetRects ->
        createScreenshot(
            screenshotBaseName = "window_insets_overlap",
            node = node,
            insetBounds = insetRects
        )
    }
)
```

This helps triage failures by persisting highlighted overlap images in test outputs.

## Step 6: Robolectric-specific setup guidance

In Robolectric tests, use deterministic inset simulation in `setContent`, typically via `EdgeToEdgeTemplate`, then run the same `assertWindowInsets(...)` checks.

Common pattern:
- Wrap UI in `EdgeToEdgeTemplate(...)`.
- Include `SemanticsWindowInsetsAnchor()`.
- Optionally set Robolectric SDK explicitly when needed (for example `@Config(sdk = [34])`).

## Step 7: Verify integration

Minimum verification checklist:

1. Gradle sync succeeds.
2. Tests compile with imports:
   - `SemanticsWindowInsetsAnchor`
   - `assertWindowInsets`
   - `WindowInsetsCompat.Type`
3. At least one test runs and exercises overlap assertions.
4. At least one assertion checks `systemBars` + `displayCutout` or `tappableElement`.

## Common pitfalls and fixes

1. **`SemanticsWindowInsetsAnchor` not found error**
   - Add `SemanticsWindowInsetsAnchor()` inside `setContent` before assertions execute.
2. **Detected window insets are empty**
   - Ensure edge-to-edge is enabled in the host activity/test environment.
3. **Robolectric does not reflect expected insets**
   - Simulate insets using `EdgeToEdgeTemplate(...)` and ensure proper SDK config.
4. **Wrong dependency scope**
   - Use `androidTestImplementation` for instrumented tests and `testImplementation` for local tests.

## Done criteria

Mark integration complete only when all are true:

- Correct dependency scope(s) are configured for intended test mode(s).
- Test content includes `SemanticsWindowInsetsAnchor()`.
- At least one meaningful `assertWindowInsets(...)` check is present.
- Target test task(s) compile and run successfully.

## Reference

- [`reference/integration-checklist.md`](reference/integration-checklist.md)
  - Compact handoff checklist for fast agent-to-agent transfer.