package com.wasabi.deadly.rxkotlin

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.wasabi.deadly.rxkotlin.spendings.SpendingActivity
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.awaitility.Awaitility.await
import org.awaitility.core.ConditionTimeoutException
import org.junit.Assert.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.junit.Test
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private lateinit var activity: MainActivity

    /**
     * BeforeClass sets the AndroidSchedulers.MainThread() to work in a testing environment
     */
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }
    }

    @Before
    fun init() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
    }

    /**
     * A test to check if clicking Spending will launch Spending Activity
     */
    @Test
    fun clickingSpending_shouldStartSpendingActivity() {
        activity.spending.performClick()

        val applicationContext: Application = ApplicationProvider.getApplicationContext()

        val expectedIntent = Intent(activity, SpendingActivity::class.java)
        val actualIntent = shadowOf(applicationContext).nextStartedActivity

        assertEquals(expectedIntent.component, actualIntent.component)
    }

    /**
     * A test to check if clicking RXCall will call the 3 zipped tests and change the text view
     */
    @Test
    fun clickingMulti_shouldGetTests() {
        activity.rxcall.performClick()

        try {
            await()
                .atMost(5, TimeUnit.SECONDS)
                .until {
                    activity.testResults.size > 0
                }
        } catch (e: ConditionTimeoutException) {
            fail("Can't reach server")
        }

        assertNotEquals("The network call failed", 0, activity.testResults.size)
    }

}