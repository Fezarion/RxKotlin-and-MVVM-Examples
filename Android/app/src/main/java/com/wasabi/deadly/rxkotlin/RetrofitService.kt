package com.wasabi.deadly.rxkotlin

import com.wasabi.deadly.rxkotlin.spendings.Spending
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.*

data class ResponseBody(var test: String?, var test2: String?, var test3: String?) {
    override fun toString(): String {
        return "${test ?: ""} ${test2 ?: ""} ${test3 ?: ""}"
    }
}

data class Sport(var sport: String?, var old: String?)

@JvmSuppressWildcards
interface RetrofitService {

    @GET("/api/test")
    fun getTest(): Call<ResponseBody>

    @GET("/api/sport")
    fun getSport(): Call<Sport>

    @PUT("/api/sport")
    fun putSport(@Body body: HashMap<String, Any>): Call<Sport>

    @GET("/api/sport")
    fun getRxSport(): Observable<Sport>

    @PUT("/api/sport")
    fun putRxSport(@Body body: HashMap<String, Any>): Observable<Sport>

    @GET("/api/test")
    fun getRxTest(): Observable<ResponseBody>

    @GET("/api/test2")
    fun getRxTest2(): Observable<ResponseBody>

    @GET("/api/test3")
    fun getRxTest3(): Observable<ResponseBody>

    @GET("/api/spending")
    fun getSpending(): Call<List<Spending>>

    @PUT("/api/spending")
    fun putSpending(@Body spending: Map<String, Any>): Call<List<Spending>>

    @POST("/api/spending")
    fun postSpending(@Body spending: Map<String, Any>): Call<List<Spending>>

    @DELETE("/api/spending/{id}")
    fun deleteSpending(@Path("id") id: Int): Call<List<Spending>>

    @GET("/api/spending/undo")
    fun undoDeleteSpending(): Call<List<Spending>>

    @DELETE("/api/spending/all")
    fun deleteAllSpending(): Call<Void>
}