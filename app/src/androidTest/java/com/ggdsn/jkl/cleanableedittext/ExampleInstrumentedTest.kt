package com.ggdsn.jkl.cleanableedittext

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.closeSoftKeyboard
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class) @LargeTest class ExampleInstrumentedTest {
    @get:Rule var activityTestRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test fun changeTextTest() {
        //由于可能是中文输入法，这种情况下会被动输入'，所以只能匹配下面这样的字符串
        val text = "zhe'ge'ce'shi'a'pi'hen'bu'wan'shan'a"
        onView(withId(R.id.cleanableEditText)).perform(typeText(text), closeSoftKeyboard())
        onView(withId(R.id.cleanableEditText)).check(matches(ViewMatchers.withText(text)))
    }

    @Test @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

        assertEquals("com.ggdsn.jkl.cleanableedittext", appContext.packageName)
    }
}
