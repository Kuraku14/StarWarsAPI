package com.kuraku.starwars.dataHelpers

import android.app.Application
import com.kuraku.starwars.details.PersonViewModel
import com.kuraku.starwars.models.Person
import com.kuraku.starwars.models.Roots
import com.kuraku.starwars.models.Section
import com.kuraku.starwars.ui.DetailView
import com.kuraku.starwars.ui.PersonDetailView
import com.kuraku.starwars.ui.SwapiViewModel

/**
 * DataHelper used for Person
 *
 * @param app requires Application context
 * @param dataId data unique identifier
 */
class PeopleDataHelper(app: Application, dataId: Int): DataHelper<Person>(app, dataId) {

    override val name: String? //TODO: assumes its loaded
        get() = value?.name
    override val dataType = Roots.PEOPLE

    override val viewModel: SwapiViewModel<Person> by lazy {
        PersonViewModel(app)
    }

    override val detailView: DetailView<Person> by lazy {
        PersonDetailView(app, null)
    }

    override fun getSectionUrls() : List<Section> {
        val list = mutableListOf<Section>()
        value?.apply {
            species.forEach {
                list.add(Section("Species", "", it))
            }
            starships.forEach {
                list.add(Section("Starships", "", it))
            }
            vehicles.forEach {
                list.add(Section("Vehicles", "", it))
            }
        }
        return list
    }
}