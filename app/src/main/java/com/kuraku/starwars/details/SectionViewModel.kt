package com.kuraku.starwars.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.models.Section
import com.kuraku.starwars.ui.SwapiViewModel

/**
 * Used to render Sections
 *
 * @property app requires Application context
 * @property type data type (Sections are generic)
 */
class SectionViewModel(private val app: Application) : AndroidViewModel(app),
    SwapiViewModel<Section> {

    private val loading = MutableLiveData<Boolean>()
    override fun isLoading(): LiveData<Boolean> {
        return loading
    }

    private var id = 1
    private var type = ""
    private val sections = mutableMapOf<String, MutableLiveData<Section>>()

    private val section: MutableLiveData<Section> by lazy {
        MutableLiveData<Section>().also {
            fetchSection(type, id)
        }
    }

    override fun getDetails(itemType: String, itemId: Int): LiveData<Section> {
        id = itemId
        type = itemType
        return section
    }

    fun getSection(url: String): LiveData<Section> {
        val httpsUrl = url.replace("http", "https")
        var section = sections[httpsUrl]
        if (section == null) {
            section = MutableLiveData<Section>().also {
                fetchSection(httpsUrl)
            }
            sections[httpsUrl] = section
        }
        return section
    }

    private fun fetchSection(type: String, id: Int) {
        loading.value = true
        NetworkHelper(app.applicationContext).getSection(type, id.toString(),
            {
                section.value = it
                loading.value = false
                Log.d("SectionViewModel", "getSection (${it.name})")
            },
            {
                Log.e("SectionViewModel", "getSection ($id) -> FAILED")
                loading.value = false
            })
    }

    private fun fetchSection(url: String) {
        loading.value = true
        NetworkHelper(app.applicationContext).getSection(url,
            {
                sections[url]!!.value = it
                loading.value = false
                Log.d("SectionViewModel", "fetchSection ($url)")
            },
            {
                loading.value = false
                Log.e("SectionViewModel", "fetchSection ($url) -> FAILED: ${it.message}")
            })
    }
}