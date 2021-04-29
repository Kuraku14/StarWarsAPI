package com.kuraku.starwars.coms

import android.util.Log
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * Make a GET request and return a parsed object from JSON.
 * TODO: Make a Post version for GraphQL
 *
 * @param url URL of the request to make
 * @param type Relevant type] object, for Gson's reflection
 * @param headers Map of request headers
 * @param listener Success listener
 * @param errorListener Error listener
 */

class GsonRequest<T>(
    url: String,
    private val type: Type,
    private val listener: Response.Listener<T>,
    errorListener: Response.ErrorListener,
    private val headers: MutableMap<String, String>? = null
) : Request<T>(Method.GET, url, errorListener) {
    private val gson = Gson()

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: T) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            Log.d("GsonRequest", "Url -> $url")
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            Log.d("GsonRequest", ".... success!")
            Response.success(
                gson.fromJson(json, type),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            Log.e("GsonRequest", ".... failed -> ${e.message}")
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Log.e("GsonRequest", ".... failed -> ${e.message}")
            Response.error(ParseError(e))
        }
    }
}