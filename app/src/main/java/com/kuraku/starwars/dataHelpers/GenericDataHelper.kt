package com.kuraku.starwars.dataHelpers

import android.app.Application
import android.util.Log
import com.kuraku.starwars.details.SectionViewModel
import com.kuraku.starwars.models.Section
import com.kuraku.starwars.ui.DetailView
import com.kuraku.starwars.ui.EmptyDetailView
import kotlin.reflect.full.declaredMemberProperties

/**
 * DataHelper used for an unsupported types. (Only currently support Person)
 *
 * @param app requires Application context
 * @param dataId data unique identifier
 * @param type data type
 */
class GenericDataHelper(app: Application, dataId: Int, override val dataType: String): DataHelper<Section>(app, dataId) {
    override val name: String  = "Generic"

    override val viewModel = SectionViewModel(app)

    override val detailView: DetailView<Section> = EmptyDetailView(app, null)

    override fun getSectionUrls(): List<Section> {
        val list = mutableListOf<Section>()
        Log.d("GenericDataHelper", "getSectionUrls()")

        value?.apply {
            this::class.declaredMemberProperties.forEach { property ->
                Log.d("GenericDataHelper", "found an item... -> ${property.name}")
                when (val value = property.getter.call(this)) {
                    is Collection<*> -> {
                        Log.d("GenericDataHelper", "... it was a list")
                        value.forEach {
                            list.add(Section(property.name.capitalize(), "", it.toString()))
                        }
                    }
                }
            }
        }
        return list
    }
}