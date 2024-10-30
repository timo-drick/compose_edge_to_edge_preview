package de.drick.compose.edgetoedgetestlib

import android.app.Instrumentation
import android.app.UiAutomation
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

/**
 * TODO check if we can also change layout direction, locale, etc.
 */
interface DeviceConfigDSL {
    fun shellCmd(cmd: String)
    fun turnScreenOn()
    fun turnOffAnimations()
    fun sleep(millis: Long)
    fun setNavigationMode(isThreeButton: Boolean)
    fun rotateScreen(rotation: TestRotation)
    fun fontSize(size: Float)
    fun displaySize(size: Float)
    /**
     * possible commands are documented here:
     * https://android.googlesource.com/platform/frameworks/base/+/master/packages/SystemUI/docs/demo_mode.md
     * Following prefix is attached automatically:
     * "adb shell am broadcast -a com.android.systemui.demo -e command"
     * So if you want to set the clock call: demoStatusBar("clock -e hhmm 1200")
     */
    fun demoStatusBar(command: String)
}


class DeviceConfigurationUtils {
    private var impl: DeviceConfigurationImpl? = null
    fun prepare(block: DeviceConfigDSL.() -> Unit) {
        impl = DeviceConfigurationImpl(InstrumentationRegistry.getInstrumentation()).apply {
            prepare(block)
        }
    }
    fun restore() {
        impl?.restore()
    }
}
/**
 *
 */
/**
 * Sources:
 * https://medium.com/androiddevelopers/preview-and-test-your-apps-edge-to-edge-ui-da645c905d78
 * https://alexzh.com/adb-commands-accessibility
 * https://stackoverflow.com/questions/44631555/change-accessibility-font-size-and-display-size-in-an-espresso-test
 * https://android.googlesource.com/platform/frameworks/base/+/master/packages/SystemUI/docs/demo_mode.md
 *
 */
private class DeviceConfigurationImpl(
    private val instrumentation: Instrumentation
) {
    private val uiAutomation = instrumentation.uiAutomation
    private val device = UiDevice.getInstance(instrumentation)

    private var displayRotationBeforeTest = 0
    private var animationsTurnedOff = false
    private var fontSizeChanged = false
    private var displaySizeChanged = false

    private fun shellCmd(cmd: String) {
        uiAutomation.executeShellCommand(cmd)
    }
    private val deviceConfigDSL = object : DeviceConfigDSL {
        override fun shellCmd(cmd: String) {
            this@DeviceConfigurationImpl.shellCmd(cmd)
        }
        override fun turnScreenOn() {
            val screenWasOn = device.isScreenOn
            shellCmd("input keyevent KEYCODE_WAKEUP")
            if (screenWasOn.not()) { // Longer sleep because screen was off
                sleep(2000)
            }
        }

        override fun turnOffAnimations() {
            setAnimations(false)
            animationsTurnedOff = true
        }

        override fun sleep(millis: Long) {
            Thread.sleep(millis)
        }
        override fun setNavigationMode(isThreeButton: Boolean) {
            val modeName = if (isThreeButton) "threebutton" else "gestural"
            shellCmd("cmd overlay enable-exclusive com.android.internal.systemui.navbar.$modeName")
        }
        override fun rotateScreen(rotation: TestRotation) {
            uiAutomation.setRotation(rotation.rotation)
        }
        override fun demoStatusBar(command: String) {
            sendDemoCommand(command)
        }
        override fun fontSize(size: Float) {
            fontSizeChanged = true
            setFontSize(size)
        }

        /**
         * Density change (Display size setting on device)
         * https://cs.android.com/android-studio/platform/tools/adt/idea/+/mirror-goog-studio-main:streaming/src/com/android/tools/idea/streaming/uisettings/ui/GoogleDensityRange.kt
         */
        override fun displaySize(size: Float) {
            displaySizeChanged = true
            setDisplaySize(size)
        }
    }

    fun prepare(block: DeviceConfigDSL.() -> Unit) {
        displayRotationBeforeTest = device.displayRotation
        enableDemoMode()
        animationsTurnedOff = false
        fontSizeChanged = false
        displaySizeChanged = false
        block(deviceConfigDSL)
        instrumentation.waitForIdleSync()
    }

    fun restore() {
        sendDemoCommand("exit") //Exit demo mode
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.setRotation(displayRotationBeforeTest)
        if (animationsTurnedOff) {
            setAnimations(true)
        }
        if (fontSizeChanged) {
            setFontSize(1f)
        }
        if (displaySizeChanged) {
            shellCmd("wm density reset")
        }
    }

    fun sendDemoCommand(command: String) {
        shellCmd("am broadcast -a com.android.systemui.demo -e command $command")
    }
    private fun setAnimations(enabled: Boolean) {
        val value = if (enabled) "1" else "0"
        shellCmd("settings put global transition_animation_scale $value")
        shellCmd("settings put global window_animation_scale $value")
        shellCmd("settings put global animator_duration_scale $value")
    }
    private fun setFontSize(size: Float) {
        shellCmd("settings put system font_scale $size")
    }
    private fun setDisplaySize(size: Float) {
        // 2.625 * 160 -> 420
        // 3.375 * 160 -> 540
        //TODO get density and calculate correct size
        shellCmd("wm density 540")
    }
    private fun enableDemoMode() {
        shellCmd("settings put global sysui_demo_allowed 1")
    }
}


private val getDemoModeEnabledCmd = "adb shell settings get global sysui_demo_allowed"
private val setDemoModeEnabledCmd = "adb shell settings put global sysui_demo_allowed 1"

private val commandsForPopulatedStatusBar = """
settings put global sysui_demo_allowed 1
am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1200
am broadcast -a com.android.systemui.demo -e command battery -e level 69 -e plugged true -e powersave false
am broadcast -a com.android.systemui.demo -e command network -e fully true
am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 3 -e fully true
am broadcast -a com.android.systemui.demo -e command network -e mobile show -e datatype 5g -e level 2 -e fully true
am broadcast -a com.android.systemui.demo -e command notifications -e visible false
am broadcast -a com.android.systemui.demo -e command status -e bluetooth connected -e alarm show -e mute show -e sync show

""".trimIndent()

enum class TestRotation(val rotation: Int) {
    Normal(UiAutomation.ROTATION_FREEZE_0),
    Rotated90(UiAutomation.ROTATION_FREEZE_90),
    Rotated180(UiAutomation.ROTATION_FREEZE_180),
    Rotated270(UiAutomation.ROTATION_FREEZE_270)
}

@Composable
fun initializeActivity(
    block: ComponentActivity.() -> Unit
): ComponentActivity? {
    val ctx = LocalContext.current
    return remember {
        (ctx as? ComponentActivity)?.apply {
            block()
        }
    }
}
