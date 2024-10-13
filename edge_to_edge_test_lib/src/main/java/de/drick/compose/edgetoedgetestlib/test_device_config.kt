package de.drick.compose.edgetoedgetestlib

import android.app.Instrumentation
import android.app.UiAutomation
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice

interface DeviceConfigDSL {
    fun turnScreenOn()
    fun sleep(millis: Long)
    fun setNavigationMode(isThreeButton: Boolean)
    fun rotateScreen(rotation: TestRotation)

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
private fun UiAutomation.sendDemoCommand(command: String) {
    executeShellCommand("am broadcast -a com.android.systemui.demo -e command $command")
}
private class DeviceConfigurationImpl(
    private val instrumentation: Instrumentation
) {
    private val uiAutomation = instrumentation.uiAutomation
    private val device = UiDevice.getInstance(instrumentation)

    private val deviceConfigDSL = object : DeviceConfigDSL {
        override fun turnScreenOn() {
            uiAutomation.executeShellCommand("input keyevent KEYCODE_WAKEUP")
        }
        override fun sleep(millis: Long) {
            SystemClock.sleep(millis)
        }
        override fun setNavigationMode(isThreeButton: Boolean) {
            val modeName = if (isThreeButton) "threebutton" else "gestural"
            uiAutomation.executeShellCommand("cmd overlay enable-exclusive com.android.internal.systemui.navbar.$modeName")
        }
        override fun rotateScreen(rotation: TestRotation) {
            uiAutomation.setRotation(rotation.rotation)
        }
        override fun demoStatusBar(command: String) {
            uiAutomation.sendDemoCommand(command)
        }
    }

    private var displayRotationBeforeTest = 0


    private fun enableDemoMode() {
        uiAutomation.executeShellCommand("settings put global sysui_demo_allowed 1")
    }

    fun prepare(block: DeviceConfigDSL.() -> Unit) {
        displayRotationBeforeTest = device.displayRotation
        enableDemoMode()
        block(deviceConfigDSL)
        instrumentation.waitForIdleSync()
    }

    fun restore() {
        //Exit demo mode
        uiAutomation.sendDemoCommand("exit")
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.uiAutomation.setRotation(displayRotationBeforeTest)
    }
}


private val getDemoModeEnabledCmd = "adb shell settings get global sysui_demo_allowed"
private val setDemoModeEnabledCmd = "adb shell settings put global sysui_demo_allowed 1"

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
