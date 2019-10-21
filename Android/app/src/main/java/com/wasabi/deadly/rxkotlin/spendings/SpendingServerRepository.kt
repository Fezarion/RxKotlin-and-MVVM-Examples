package com.wasabi.deadly.rxkotlin.spendings

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.wasabi.deadly.rxkotlin.retrofitService
import com.wasabi.deadly.rxkotlin.spendings.Spending
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

/**
 * Server Repository Class.
 * Contains functions to perform CRUD operations with the server
 * @param application context
 */
class SpendingServerRepository(private val application: Application) {

    // LiveData passed down from the SpendingViewModel Class
    private var allSpending = MutableLiveData<List<Spending>>()

    /**
     * @param spending POST to server
     */
    fun insert(spending: Spending) {
        retrofitService.postSpending(spending.toMap()).enqueue(object : Callback<List<Spending>> {
            override fun onFailure(call: Call<List<Spending>>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(call: Call<List<Spending>>, response: Response<List<Spending>>) {
                if (response.isSuccessful) {
                    allSpending.value = response.body()
                } else {
                    onFailureCallback(response)
                }
            }
        })
    }

    /**
     * @param spending PUT to server
     */
    fun update(spending: Spending) {
        retrofitService.putSpending(spending.toMap()).enqueue(object : Callback<List<Spending>> {
            override fun onFailure(call: Call<List<Spending>>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(call: Call<List<Spending>>, response: Response<List<Spending>>) {
                if (response.isSuccessful) {
                    allSpending.value = response.body()
                } else {
                    onFailureCallback(response)
                }
            }

        })
    }

    /**
     * @param spending DELETE to server
     */
    fun delete(spending: Spending) {
        retrofitService.deleteSpending(spending.id).enqueue(object : Callback<List<Spending>> {
            override fun onFailure(call: Call<List<Spending>>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(call: Call<List<Spending>>, response: Response<List<Spending>>) {
                if (response.isSuccessful) {
                    allSpending.value = response.body()
                } else {
                    onFailureCallback(response)
                }
            }

        })
    }

    /**
     * Undo delete by asking the server to revert to last spending
     */
    fun undoDelete() {
        retrofitService.undoDeleteSpending().enqueue(object: Callback<List<Spending>> {
            override fun onFailure(call: Call<List<Spending>>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(
                call: Call<List<Spending>>,
                response: Response<List<Spending>>
            ) {
                if (response.isSuccessful) {
                    allSpending.value = response.body()
                } else {
                    onFailureCallback(response)
                }
            }
        })
    }

    /**
     * Delete all spending
     */
    fun deleteAllSpending() {
        retrofitService.deleteAllSpending().enqueue(object: Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    allSpending.value = listOf()
                } else {
                    onFailureCallback(response)
                }
            }
        })
    }

    /**
     * Get All Spending
     */
    fun getAllSpending(): LiveData<List<Spending>> {
        retrofitService.getSpending().enqueue(object : Callback<List<Spending>> {
            override fun onFailure(call: Call<List<Spending>>, t: Throwable) {
                onFailureCallback(t)
            }

            override fun onResponse(call: Call<List<Spending>>, response: Response<List<Spending>>) {
                if (response.isSuccessful) {
                    allSpending.value = response.body()
                } else {
                    onFailureCallback(response)
                }
            }
        })

        return allSpending
    }

    /**
     * On Failure show a Toast message
     * @param t throwable
     */
    fun onFailureCallback(t: Any) {
        Toast.makeText(
            application.applicationContext,
            "Something wrong happened, $t",
            Toast.LENGTH_LONG
        ).show()
        Timber.e(t.toString())
    }
}