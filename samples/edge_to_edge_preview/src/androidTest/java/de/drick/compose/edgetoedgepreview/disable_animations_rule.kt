package de.drick.compose.edgetoedgepreview

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException


class DisableAnimationsRule : TestRule {
    /*override fun starting(description: Description?) {
        setAnimations(areEnabled = false)
    }

    override fun finished(description: Description?) {
        setAnimations(areEnabled = true)
    }*/
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            @Throws(Throwable::class)
            override fun evaluate() {
                println("Disable animations")
                setAnimations(areEnabled = false)
                try {
                    base.evaluate()
                } finally {
                    println("Enable animations")
                    setAnimations(areEnabled = true)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun setAnimations(areEnabled: Boolean) {
        val value = if (areEnabled) "1" else "0"
        //InstrumentationRegistry.getInstrumentation().uiAutomation.run {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).run {
            executeShellCommand("settings put global transition_animation_scale $value")
            executeShellCommand("settings put global window_animation_scale $value")
            executeShellCommand("settings put global animator_duration_scale $value")
        }
    }
}