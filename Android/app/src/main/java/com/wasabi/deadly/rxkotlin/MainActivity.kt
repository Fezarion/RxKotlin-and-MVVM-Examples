package com.wasabi.deadly.rxkotlin

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import com.wasabi.deadly.rxkotlin.spendings.SpendingActivity
import io.reactivex.rxkotlin.Observables
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    // Disposable to dispose
    var mDisposable: Disposable? = null
    // Result of the zipped tests
    var testResults = mutableListOf<ResponseBody>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        rxcall.setOnClickListener {
            getMultipleTests()
        }

        spending.setOnClickListener {
            startActivity(Intent(this, SpendingActivity::class.java))
        }
    }

    /**
     * Tests if server is working
     */
    private fun getMultipleTests() {
        Observables.zip(
            retrofitService.getRxTest(),
            retrofitService.getRxTest2(),
            retrofitService.getRxTest3()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Triple<ResponseBody, ResponseBody, ResponseBody>> {
                override fun onComplete() {
                    Timber.i("Disposing disposable...")
                    mDisposable?.dispose()

                    text.text = getString(
                        R.string.multiple_tests,
                        testResults[0].toString(),
                        testResults[1].toString(),
                        testResults[2].toString()
                    )
                }

                override fun onSubscribe(d: Disposable) {
                    mDisposable = d
                    testResults.clear()
                }

                override fun onNext(t: Triple<ResponseBody, ResponseBody, ResponseBody>) {
                    testResults.add(t.first)
                    testResults.add(t.second)
                    testResults.add(t.third)
                }

                override fun onError(e: Throwable) {
                    Timber.e(e)
                }
            })
    }
}
