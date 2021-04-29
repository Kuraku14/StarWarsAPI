package com.kuraku.starwars.search

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.kuraku.starwars.models.Person

class PeopleDataSourceFactory(val app: Application): DataSource.Factory<String, Person>() {
    var searchTerm: String = ""
    set(value) {
        field = value
        sourceLiveData.value?.invalidate()
    }

    val sourceLiveData = MutableLiveData<PeopleDataSource>()
    override fun create(): DataSource<String, Person> {
        Log.d("PeopleDataSourceFactory", "Create() -> $searchTerm")
        val source = PeopleDataSource(app, searchTerm)
        sourceLiveData.postValue(source)
        return source
    }
}