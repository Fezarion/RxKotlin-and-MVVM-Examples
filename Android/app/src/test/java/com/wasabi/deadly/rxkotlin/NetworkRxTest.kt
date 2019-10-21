package com.wasabi.deadly.rxkotlin

import android.os.Build
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import org.robolectric.annotation.Config
import io.reactivex.rxkotlin.toObservable
import org.junit.*
import org.junit.Assert.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Config(sdk = [Build.VERSION_CODES.O_MR1])
class NetworkRxTest {

    /**
     * BeforeClass sets the AndroidSchedulers.MainThread() to work in a testing environment
     * AfterClass resets the server to it's original value
     */
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            println("Starting Rx Tests...")

            RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            println("...Rx Tests has concluded")

            // Create signal to stall test
            val signal = CountDownLatch(1)

            // Original value
            val body = hashMapOf<String, Any>()
            body["sport"] = "soccer"

            println("Resetting Server...")

            // Reset original value in the server
            retrofitService.putSport(body).enqueue(object : Callback<Sport> {
                override fun onFailure(call: Call<Sport>, t: Throwable) {
                    signal.countDown()
                    fail("...Server reset failed ${t.localizedMessage}")
                }

                override fun onResponse(call: Call<Sport>, response: Response<Sport>) {
                    signal.countDown()
                    println("...Server reset successful")
                }
            })

            // Timeout of 5 seconds
            signal.await(5, TimeUnit.SECONDS)

            // If signal is not 0, the server hasn't replied within 5 seconds
            // or it was unsuccessful
            assertEquals("Server failed to reset", signal.count, 0)
        }
    }

    /**
     * A simple test to check if Rx is working
     */
    @Test
    fun simple() {
        val inputCountries = arrayListOf<String>()
        inputCountries.add("USA")
        inputCountries.add("China")
        inputCountries.add("Atlantis")

        val expectedCountries = arrayListOf<String>()
        expectedCountries.add("USA")
        expectedCountries.add("China")

        val observable = inputCountries.toObservable()

        val testObserver = observable.test()

        val countries = testObserver.values()
        assertTrue(
            "Result doesn't contain the expected countries",
            countries.containsAll(expectedCountries)
        )

    }

    /**
     * A test to check if it can reach the server, doesn't require Rx
     */
    @Test
    fun canReachServer() {
        try {
            val response = retrofitService.getTest().execute()
            assertTrue("Response code is not 200", response.code() == 200)
            assertTrue("Response is unsuccessful", response.isSuccessful)
        } catch (e: ConnectException) {
            fail("Can't reach server")
        }
    }

    /**
     * A test to check if Get a single value works in Rx
     */
    @Test
    fun canGetSingle() {
        val expectedValue = ResponseBody(
            "Hello World",
            "Hello",
            "World"
        )

        val observable = retrofitService.getRxTest()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val testObserver = observable.test()

        // Stall the observer until it receives a respond
        testObserver.awaitTerminalEvent()

        testObserver
            .assertNoErrors()
            .assertNoTimeout()
            .assertValue(expectedValue)

        testObserver.dispose()
        assertTrue("Test Observer has not yet been disposed", testObserver.isDisposed)
    }

    /**
     * A test to check if Get multiple values in a zip works in Rx
     */
    @Test
    fun canGetMultiple() {
        val expectedValues = Triple(
            ResponseBody(
                "Hello World",
                "Hello",
                "World"
            )
            , ResponseBody("Jello", null, null)
            , ResponseBody("Jelly", null, null)
        )

        val observable = Observables.zip(
            retrofitService.getRxTest(),
            retrofitService.getRxTest2(),
            retrofitService.getRxTest3()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        val testObserver = observable.test()

        // Stall the observer until it receives a respond
        testObserver.awaitTerminalEvent()

        testObserver
            .assertNoErrors()
            .assertNoTimeout()
            .assertValueSet(mutableListOf(expectedValues))

        testObserver.dispose()
        assertTrue("Test Observer has not yet been disposed", testObserver.isDisposed)
    }

    /**
     * A test to check if PUT and GET is working and running sequentially through concat in Rx
     */
    @Test
    fun canPut_thenGet() {
        val body = hashMapOf<String, Any>()
        body["sport"] = "basketball"

        val observable =
            Observable.concat(retrofitService.putRxSport(body), retrofitService.getRxSport())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        val testObserver = observable.test()

        // Stall the observer until it receives a respond
        testObserver.awaitTerminalEvent()

        val first = testObserver.values()[0]
        val second = testObserver.values()[1]

        testObserver
            .assertNoErrors()
            .assertNoTimeout()

        assertNotEquals(
            "Nothing has changed after put $first $second",
            first.old,
            second.sport
        )

        testObserver.dispose()
        assertTrue("Test Observer has not yet been disposed", testObserver.isDisposed)
    }
}