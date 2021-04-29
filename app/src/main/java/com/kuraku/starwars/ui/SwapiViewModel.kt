package com.kuraku.starwars.ui

import androidx.lifecycle.LiveData
import com.kuraku.starwars.models.Detailable

/**
 * Interface shared by all ViewModels required to interact with various components
 *
 * @param T operative class
 */
interface SwapiViewModel<T: Detailable> {
    /**
     * Each viewModel knows how to get the Details for its particular type and id
     * TODO: the type is kind of redundant but needed so long as Section is a placeholder for missing models
     *
     * @param itemType
     * @param itemId
     * @return observable T
     */
    fun getDetails(itemType: String, itemId: Int): LiveData<T>

    /**
     * Every viewModel should have some way to see if it's loading
     *
     * @return observable Boolean
     */
    fun isLoading(): LiveData<Boolean>
}