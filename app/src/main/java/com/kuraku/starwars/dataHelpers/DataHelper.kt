package com.kuraku.starwars.dataHelpers

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.kuraku.starwars.details.SectionViewModel
import com.kuraku.starwars.models.Detailable
import com.kuraku.starwars.models.Section
import com.kuraku.starwars.ui.DetailView
import com.kuraku.starwars.ui.SwapiViewModel

/**
 * This delegates data to SwapiViewModel and DetailView so that parent class
 * doesn't have to think about it.
 *
 * @param T the Detailable class used to render
 * @property dataId ID of the data item (the subclass knows the type)
 */
abstract class DataHelper<T: Detailable>(val app: Application, private val dataId: Int) {

    companion object {
        fun getHelper(app: Application, dataId: Int, type: String, owner: LifecycleOwner) : DataHelper<*> {
            Log.d("DataHelper", "getHelper -> ($type): $dataId")
            return when(type) {
                //TODO: implement the rest of the Helpers
                "people" -> PeopleDataHelper(app, dataId)
                else -> GenericDataHelper(app, dataId, type)
            }.also {
                it.owner = owner
            }
        }
    }

    var owner: LifecycleOwner? = null
    var value: T? = null

    abstract val dataType: String

    abstract val name: String?

    abstract val viewModel: SwapiViewModel<T>

    abstract val detailView: DetailView<T>

    private val sectionViewModel = SectionViewModel(app)

    fun loadData() {
        if (owner == null) {
            throw Exception("Set owner first")
        }

        //  after main data is loaded, find out what extra data the object needs..
        //TODO: Section is being used as a placeholder for unimplemented objects like Starship. This allows us to get just the name
        // It's a lot of extra work for just a name but I'm planning for future use of GraphQL and a Realm DB to cache.
        // For objects that are clickable, we may opt to do the full request anyway so we can pass it onto next detail (when supported)

        viewModel.getDetails(dataType, dataId).observe(owner!!) { data ->
            value = data
            sectionListener.invoke(getSectionUrls())

            // the first update tells us what extra data to load
            detailView.updateData(data).entries.forEach { entry ->
                getExtraData(entry.value.url) { section ->
                    detailView.updateExtra(entry.key, section)
                }
            }
        }
    }

    // creates on observer so updates to url should come to DetailView..but we never reload it
    private fun getExtraData(url: String, listener: (Section) -> Unit) {
        sectionViewModel.getSection(url).observe(owner!!) {
            listener.invoke(it)
        }
    }

    abstract fun getSectionUrls(): List<Section>

    lateinit var sectionListener: (List<Section>) -> Unit
    fun onSectionUrlsAvailable(sectionUrls: (List<Section>) -> Unit) {
        sectionListener = sectionUrls
    }
}