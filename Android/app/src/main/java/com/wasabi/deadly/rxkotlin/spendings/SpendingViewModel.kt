package com.wasabi.deadly.rxkotlin.spendings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.wasabi.deadly.rxkotlin.spendings.Spending
import com.wasabi.deadly.rxkotlin.spendings.SpendingServerRepository

/**
 * Spending View Model Class
 * Contains functions to perform CRUD operations with the repository
 * @param application context
 */
class SpendingViewModel(application: Application) : AndroidViewModel(application) {

    // Server repository
    private var repository: SpendingServerRepository = SpendingServerRepository(application)
    // List of all spending
    private var allSpending = repository.getAllSpending()

    /**
     * @param spending POST to repository
     */
    fun insert(spending: Spending) {
        repository.insert(spending)
    }

    /**
     * @param spending PUT to repository
     */
    fun update(spending: Spending) {
        repository.update(spending)
    }

    /**
     * @param spending DELETE to repository
     */
    fun delete(spending: Spending) {
        repository.delete(spending)
    }

    /**
     * Undo delete
     */
    fun undoDelete() {
        repository.undoDelete()
    }

    /**
     * Delete all spending
     */
    fun deleteAllSpending() {
        repository.deleteAllSpending()
    }

    /**
     * Get all spending without LiveData
     */
    fun refresh() {
        repository.getAllSpending()
    }

    /**
     * Return LiveData for allSpending
     */
    fun getAllSpending(): LiveData<List<Spending>> {
        return allSpending
    }
}