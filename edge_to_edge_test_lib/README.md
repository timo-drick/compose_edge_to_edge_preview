# Testing window insets

There do exists a lot of different testing strategies for Android apps. Because this library is focused on WindowInsets and maybe foldable support later we will focus on tests of the UI. So how we can make sure with tests that system window insets do not overlap with UI components and maybe also interactive areas of the do not overlap areas reserved for the system (like gesture navigation when in gesture navigation mode).

Here I describe how to do following test:
- Integration tests (androidTest)
  - Testing on a real device or an emulator.
- Robolectric tests
  - Testing on your host system so no emulator is needed.
- Screenshot tests (Android beta)
  - On host system using preview definitions.

## Integration tests (androidTest)

If you do not have setup any compose ui tests yet just follow the official Android tutorial here: https://developer.android.com/develop/ui/compose/testing

If you are using the `createComposeRule()` approach please note that an Activity is used behind the scene that does not enable edge-to-edge. So you need to set target api to 35 and use an android 15 device to see any window insets.
Or you could enable it manually in your test code.
```kotlin
@Composable
fun enableEdgeToEdge(): ComponentActivity? {
    val ctx = LocalContext.current
    return remember {
        (ctx as? ComponentActivity)?.apply { enableEdgeToEdge() }
    }
}

class EdgeToEdgeTestEmpty {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun testWindowInsets() {
        composeTestRule.setContent {
            enableEdgeToEdge()
            SemanticsWindowInsetsAnchor()
            YourAppTheme {
                ComposeScreenToTest()
            }
        }
    }
}
```