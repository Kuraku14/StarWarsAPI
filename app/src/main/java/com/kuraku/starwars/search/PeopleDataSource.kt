package com.kuraku.starwars.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.models.Person

class PeopleDataSource(val app: Application, val searchTerm: String) : PageKeyedDataSource<String, Person>() {
    val loading = MutableLiveData<Boolean>()
    val empty = MutableLiveData<Boolean>()

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, Person>) {
        loading.postValue(true)
        NetworkHelper(app.applicationContext).getPeople(searchTerm, 1,
            {
                Log.i("PeopleDataSource", "loadInitial -> $searchTerm")
                loading.postValue(false)
                if (it.count > 0) {
                    empty.value = false
                }
                callback.onResult(it.results, it.previous, it.next)
            },
            {
                Log.e("PeopleDataSource", "loadInitial -> FAILED")
                loading.postValue(false)
                empty.postValue(true)
                callback.onResult(listOf(), null, null)
            })
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, Person>) {
        // we don't support paging back
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, Person>) {
        loading.postValue(true)
        empty.postValue(false)

        val pageNum = params.key.split("=").last()
        NetworkHelper(app.applicationContext).getPeople(searchTerm, pageNum.toInt(),
            {
                Log.i("PeopleDataSource", "loadAfter -> $searchTerm ($pageNum)")
                loading.postValue(false)
                callback.onResult(it.results, it.next)
            },
            {
                Log.e("PeopleDataSource", "loadAfter -> FAILED")
                loading.postValue(false)
                callback.onResult(listOf(), null)
            })
    }
}