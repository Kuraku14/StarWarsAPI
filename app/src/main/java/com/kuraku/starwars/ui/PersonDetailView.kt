package com.kuraku.starwars.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.android.volley.toolbox.NetworkImageView
import com.kuraku.starwars.R
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.coms.VolleySingleton
import com.kuraku.starwars.models.Person
import com.kuraku.starwars.models.Section

/**
 * Custom view responsible for rendering Person Details
 *
 * @param context
 * @param attrs
 */
class PersonDetailView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), DetailView<Person> {

    // lazy load allows for "private val" because it waits for inflation
    private val name: TextView by lazy { findViewById(R.id.name) }
    private val gender: TextView by lazy { findViewById(R.id.gender) }
    private val homeworld: TextView by lazy { findViewById(R.id.homeworld) }
    private val birth_year: TextView by lazy { findViewById(R.id.birth_year) }
    private val height: TextView by lazy { findViewById(R.id.height) }
    private val mass: TextView by lazy { findViewById(R.id.mass) }
    private val portrait: NetworkImageView by lazy { findViewById(R.id.portrait) }

    init {
        LayoutInflater.from(context).inflate(R.layout.detail_view_person, this, true)
    }

    //TODO: in the future Section may be other models (like Starship), or maybe not --to keep from mixing models
    override fun updateData(it: Person): Map<String, Section> {
        Log.d("PersonDetailView", "update -> $it")
        bindView(it)
        // make a list of the urls we want for displaying details
        return mapOf("homeworld" to Section(NetworkHelper.getTypeFromUrl(it.homeworld), "", it.homeworld))
    }

    override fun updateExtra(key: String, section: Section) {
        // i know homeworld is an extra because i put it there further up ^^^
        when (key) {
            "homeworld" -> { homeworld.text = section.name }
        }
    }

    private fun bindView(person: Person) {
        name.text = person.name
        gender.text = context.getString(R.string.gender_template, person.gender)
        birth_year.text = context.getString(R.string.birth_template, person.birth_year)
        height.text = context.getString(R.string.height_template, person.height)
        mass.text = context.getString(R.string.mass_template, person.mass)

        portrait.apply {
            VolleySingleton.getInstance(context).let {
                setImageUrl(
                    it.peopleImageUrlCache[NetworkHelper.getIdFromUrl(person.url)],
                    it.imageLoader)

                setErrorImageResId(R.drawable.image_not_found)
            }
        }
    }
}