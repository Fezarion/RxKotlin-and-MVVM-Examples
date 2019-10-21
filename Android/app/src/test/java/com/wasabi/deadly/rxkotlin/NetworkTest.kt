package com.wasabi.deadly.rxkotlin

import android.os.Build
import com.google.gson.Gson
import org.junit.Assert.*

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(RobolectricTestRunner::class)
class NetworkTest {
    companion object {
        private var mockWebServer = MockWebServer()
        private var retrofit: Retrofit
        private var gson = Gson()

        init {
            MockitoAnnotations.initMocks(this)
            retrofit = Retrofit.Builder()
                .baseUrl(mockWebServer.url("").toString())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        @AfterClass
        @JvmStatic
        fun teardown() {
            mockWebServer.shutdown()
        }
    }

    @Test
    fun getMockTest() {
        val mockResponseBody = ResponseBody("mock hello world", "hello", "world")
        val mockResponse = MockResponse().setBody(gson.toJson(mockResponseBody))

        mockWebServer.enqueue(mockResponse)

        val mockService = retrofit.create(RetrofitService::class.java)
        val mockCall = mockService.getTest()

        val response = mockCall.execute()

        assertTrue("Response code is not 200", response.code() == 200)
        assertTrue("Response is unsuccessful", response.isSuccessful)
        assertEquals("Unexpected Response body", mockResponseBody, response.body())
    }

    @Test
    fun getRealTest() {
        try {
            val response = retrofitService.getTest().execute()
            assertTrue("Can't reach server", response.isSuccessful)
        } catch (e: ConnectException) {
            fail("Can't reach server")
        }
    }

    @Test
    fun postRealTest() {
        try {
            val body = hashMapOf<String, Any>()
            body["sport"] = "basketball"
            val response = retrofitService.putSport(body).execute()
            assertEquals("Body arguments rejected", 200, response.code())
            assertEquals("Post didn't change anything", body["sport"], response.body()?.sport)
        } catch (e: ConnectException) {
            fail("Can't reach server")
        }
    }
}