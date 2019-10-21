package com.wasabi.deadly.rxkotlin.spendings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.wasabi.deadly.rxkotlin.R
import com.wasabi.deadly.rxkotlin.spendings.SpendingActivity.Companion.SPENDING_EXTRA
import kotlinx.android.synthetic.main.activity_add_spending.*

class AddSpendingActivity : AppCompatActivity() {

    private var editSpending: Spending? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_spending)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = "Add Spending"

        editSpending = intent.getParcelableExtra(SPENDING_EXTRA)
        if (editSpending != null) {
            add_spending_title.setText(editSpending!!.title)
            add_spending_description.setText(editSpending!!.description)
            add_spending_value.setText(editSpending!!.value.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.add_spending_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.save_spending -> {
                createSpending()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    /**
     * Get values from EditTexts and check for blanks,
     * send the new spending back as Parcelable to the previous activity,
     * to be sent to the repository.
     */
    private fun createSpending() {
        val title = add_spending_title.text.toString()
        val description = add_spending_description.text.toString()
        val value = add_spending_value.text.toString()

        if (title.isBlank() || description.isBlank() || value.isBlank()) {
            Snackbar.make(add_spending_layout, R.string.add_spending_missing, Snackbar.LENGTH_SHORT).show()
            return
        }

        val spending = Intent()
        spending.putExtra(SPENDING_EXTRA, Spending(editSpending?.id ?: -1, title, description, value.toDouble()))
        setResult(Activity.RESULT_OK, spending)
        finish()
    }
}
