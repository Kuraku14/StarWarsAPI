package com.kuraku.starwars.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuraku.starwars.models.Person


/**
 * Used to render a searchable (filterable) List of Person
 *
 * @property app requires Application context
 */
class PeopleSearchViewModel(private val app: Application) : AndroidViewModel(app) {
    private var dataSource = PeopleDataSourceFactory(app)
    lateinit var owner: LifecycleOwner

    private val pagedListConfig = PagedList.Config.Builder()
        .setEnablePlaceholders(true)
        .setInitialLoadSizeHint(10)
        .setPageSize(10).build()

    private val loading: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    fun isLoading(): LiveData<Boolean> = loading

    private val empty: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }
    fun isEmpty(): LiveData<Boolean> = empty

    private val people: LiveData<PagedList<Person>> by lazy {
        empty.value = false
        loading.value = true

        // kind of hacky?
        // every search creates a new PeopleDataSource so this is how i track it
        dataSource.sourceLiveData.observe(owner) { source ->
            source.loading.observe(owner) {
                loading.value = it
            }
            source.empty.observe(owner) {
                empty.value = it
            }
        }

        LivePagedListBuilder(dataSource, pagedListConfig)
            .setBoundaryCallback(object: PagedList.BoundaryCallback<Person>() {
                override fun onZeroItemsLoaded() {
                    Log.d("PeopleSearchViewModel", "onZeroItemsLoaded")
                    empty.value = true
                    loading.value = false
                }

                override fun onItemAtEndLoaded(itemAtEnd: Person) {
                    Log.d("PeopleSearchViewModel", "onItemAtEndLoaded")
                }

                override fun onItemAtFrontLoaded(itemAtFront: Person) {
                    Log.d("PeopleSearchViewModel", "onItemAtFrontLoaded")
                    empty.value = false
                    loading.value = false
                }
            })
            .build()
    }

    //TODO: maybe this is hacky?
    fun getPeople(owner: LifecycleOwner): LiveData<PagedList<Person>> {
        this.owner = owner
        return people
    }

    fun doSearch(searchTerm: String) {
        Log.d("PeopleSearchViewModel", "doSearch -> $searchTerm")
        dataSource.searchTerm = searchTerm
    }
}