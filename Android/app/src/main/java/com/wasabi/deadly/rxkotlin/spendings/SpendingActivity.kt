package com.wasabi.deadly.rxkotlin.spendings

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.wasabi.deadly.rxkotlin.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_spending.*

class SpendingActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener, SpendingRecyclerAdapter.OnItemClickListener {

    /**
     * Constant values
     */
    companion object {
        const val SPENDING_ADD_REQUEST_CODE = 1
        const val SPENDING_UPDATE_REQUEST_CODE = 2
        const val SPENDING_EXTRA = "SPENDING_EXTRA"
    }

    // ViewModel
    private lateinit var spendingViewModel: SpendingViewModel
    // Adapter for RecyclerView
    private lateinit var adapter: SpendingRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spending)

        spending_swipe_refresh.setOnRefreshListener(this)

        adapter = SpendingRecyclerAdapter(this)

        spending_recycler_view.layoutManager = LinearLayoutManager(this)
        spending_recycler_view.adapter = adapter
        spending_recycler_view.setHasFixedSize(true)

        spendingViewModel = ViewModelProviders.of(this).get(SpendingViewModel::class.java)

        spendingViewModel.getAllSpending().observe(this,
            Observer<List<Spending>> {
                adapter.setSpending(it)
                spending_swipe_refresh.isRefreshing = false
            })

        // Set swipe to delete Spending
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val spending = adapter.getSpendingAt(viewHolder.adapterPosition)
                spendingViewModel.delete(spending)
                adapter.removeIgnoreAnimationId(spending.id)
                displayUndoSnackBar()
            }
        }).attachToRecyclerView(spending_recycler_view)

        add_button.setOnClickListener {
            startActivityForResult(
                Intent(this, AddSpendingActivity::class.java),
                SPENDING_ADD_REQUEST_CODE
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.spending_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_delete_all -> {
                spendingViewModel.deleteAllSpending()
                adapter.resetAnimation()
                displayUndoSnackBar()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPENDING_ADD_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            spendingViewModel.insert(data.getParcelableExtra(SPENDING_EXTRA) as Spending)
        } else if(requestCode == SPENDING_UPDATE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            spendingViewModel.update(data.getParcelableExtra(SPENDING_EXTRA) as Spending)
        }
    }

    override fun onRefresh() {
        spendingViewModel.refresh()
    }

    // OnRecyclerViewItemClick
    override fun onItemClick(spending: Spending) {
        val intent = Intent(this, AddSpendingActivity::class.java)
        intent.putExtra(SPENDING_EXTRA, spending)
        startActivityForResult(intent, SPENDING_UPDATE_REQUEST_CODE)
    }

    // Undo delete using Snackbar
    fun displayUndoSnackBar() {
        Snackbar.make(spending_container, "Spending deleted!", Snackbar.LENGTH_LONG)
            .setAction("UNDO") {
                spendingViewModel.undoDelete()
            }.show()
    }
}
