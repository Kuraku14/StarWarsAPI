package com.kuraku.starwars.ui

import com.kuraku.starwars.models.Section

/**
 * Interface shared by all DetailView required to interact with various components
 *
 * @param T operative class
 */
interface DetailView<T> {
    /**
     * Accepts a data object of a known type
     *
     * @param it
     * @return a map of arbitrary keys to Section that the view still wants to fetch
     */
    fun updateData(it: T): Map<String, Section>

    /**
     * This updates the Sections specified in the updateDate return
     *
     * @param key
     * @param value
     */
    fun updateExtra(key: String, value: Section)
}