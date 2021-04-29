package com.kuraku.starwars.coms

import android.content.Context
import com.android.volley.Response
import com.google.gson.reflect.TypeToken
import com.kuraku.starwars.models.*
import java.lang.reflect.Type

/**
 * Used to make managed network requests. Responsible for all the endpoints
 * and how to convert them to POJOs
 *
 * @property context
 */
class NetworkHelper(private val context: Context) {

    companion object {
        const val API_ROOT = "https://swapi.dev/api/"
        const val URL_PEOPLE = "people/"
        const val URL_PEOPLE_SEARCH = "$URL_PEOPLE?search"

        const val API_IMAGE_ROOT = "https://akabab.github.io/starwars-api/api/all.json"

        fun getIdFromUrl(url: String): Int {
            return url.trim('/').split("/").last().toInt()
        }

        fun getTypeFromUrl(url: String): String {
            return url.trim('/').split("/").let {
                it[it.size - 2]
            }
        }
    }

    private fun getRootUrl() = API_ROOT
    private fun getSectionUrl(url: String) = url
    private fun getSectionUrl(type: String, id: String) = "$API_ROOT$type/$id/"
    private fun getPeopleUrl(page: Int = 1) = "$API_ROOT${URL_PEOPLE}?page=$page"
    private fun getPersonUrl(personId: Int) = "$API_ROOT$URL_PEOPLE${personId}/"
    private fun getPersonSearchUrl(searchTerm: String, page: Int = 1) = "$API_ROOT$URL_PEOPLE_SEARCH=${searchTerm}&page=$page"
    private fun getPeopleImagesUrl() = API_IMAGE_ROOT

    fun getRoot(
        listener: Response.Listener<Root>,
        errorListener: Response.ErrorListener
    ) {
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            getRootUrl(),
            object : TypeToken<Root>() {}.type,
            listener,
            errorListener
        ).apply {
            tag = "root"
        })
    }

    fun getSection(
        type: String,
        id: String,
        listener: Response.Listener<Section>,
        errorListener: Response.ErrorListener
    ) {
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            getSectionUrl(type, id),
            Section::class.java,
            listener,
            errorListener
        ).apply {
            tag = url
        })
    }

    fun getSection(
        url: String,
        listener: Response.Listener<Section>,
        errorListener: Response.ErrorListener
    ) {
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            getSectionUrl(url),
            Section::class.java,
            listener,
            errorListener
        ).apply {
            tag = url
        })
    }

    fun getPerson(
        personId: Int,
        listener: Response.Listener<Person>,
        errorListener: Response.ErrorListener
    ) {
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            getPersonUrl(personId),
            Person::class.java,
            listener,
            errorListener
        ).apply {
            tag = "person"
        })
    }

    fun getPeople(
        searchTerm: String = "",
        page: Int = 1,
        listener: Response.Listener<People>,
        errorListener: Response.ErrorListener
    ) {
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            if (searchTerm.isEmpty()) getPeopleUrl(page) else getPersonSearchUrl(searchTerm, page),
            People::class.java,
            listener,
            errorListener
        ).apply {
            tag = "people"
        })
    }

    fun getPeopleImages(
        listener: Response.Listener<List<PersonImage>>,
        errorListener: Response.ErrorListener
    ) {
        val type: Type = object : TypeToken<ArrayList<PersonImage>>() {}.type
        VolleySingleton.getInstance(context).addToRequestQueue(GsonRequest(
            getPeopleImagesUrl(),
            type,
            listener,
            errorListener
        ).apply {
            tag = "images"
        })
    }

    fun cancel(tag: String) {
        VolleySingleton.getInstance(context).requestQueue.cancelAll(tag)
    }
}