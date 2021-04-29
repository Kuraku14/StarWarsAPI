package com.kuraku.starwars.details

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.models.Person
import com.kuraku.starwars.ui.SwapiViewModel

/**
 * Used to render Person Details
 *
 * @property app requires Application context
 */
class PersonViewModel(private val app: Application) : AndroidViewModel(app), SwapiViewModel<Person> {

    private val loading = MutableLiveData<Boolean>()
    override fun isLoading(): LiveData<Boolean> {
        return loading
    }

    private val person: MutableLiveData<Person> by lazy {
        MutableLiveData()
    }

    override fun getDetails(itemType: String, itemId: Int): LiveData<Person> {
        if (person.value == null) {
            findPerson(itemId)
        }
        return person
    }

    private fun findPerson(personId: Int) {
        loading.value = true
        NetworkHelper(app.applicationContext).getPerson(personId,
            {
                person.value = it
                loading.value = false
                Log.d("PersonViewModel", "findPerson (${it.name})")
            },
            {
                Log.e("PersonViewModel", "findPerson ($personId) -> FAILED")
                loading.value = false
            })
    }
}