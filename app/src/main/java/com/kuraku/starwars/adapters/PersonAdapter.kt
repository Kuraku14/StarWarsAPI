package com.kuraku.starwars.adapters

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import com.kuraku.starwars.search.PeopleActivity
import com.kuraku.starwars.details.DetailActivity
import com.kuraku.starwars.details.DetailFragment
import com.kuraku.starwars.R
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.models.Person
import com.kuraku.starwars.models.Roots

/**
 * Provided by Android Master/Detail template. Renders a list of Person objects.
 *
 * @property parentActivity Used to launch the Person details
 * @property twoPane Determines if content is shown in a Fragment or Activity
 */
class PersonAdapter(
    private val parentActivity: PeopleActivity,
    private val twoPane: Boolean
) : PagedListAdapter<Person, PersonAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Person>() {
            override fun areItemsTheSame(oldItem: Person, newItem: Person) = oldItem.url == newItem.url
            override fun areContentsTheSame(oldItem: Person, newItem: Person) = oldItem == newItem
        }
    }

    private val onClickListener: View.OnClickListener = View.OnClickListener { v ->
        val item = v.tag as Person
        val id = NetworkHelper.getIdFromUrl(item.url)

        if (twoPane) {
            parentActivity.supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.item_detail_container,
                    DetailFragment().apply {
                        arguments = Bundle().apply {
                            putInt(DetailFragment.EXTRA_DATA_ID, id)
                            putString(DetailFragment.EXTRA_DATA_TYPE, Roots.PEOPLE)
                        }
                })
                .commit()
        } else {
            v.context.startActivity(Intent(v.context, DetailActivity::class.java).apply {
                putExtra(DetailFragment.EXTRA_DATA_ID, id)
                putExtra(DetailFragment.EXTRA_DATA_TYPE, Roots.PEOPLE)
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_people, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.name.text = it.name
            holder.gender.text = it.gender
            holder.portrait.apply {
                //TODO: show based on alligence or find a thumbnail or something, or set to theme
                val drawable = ContextCompat.getDrawable(context, R.drawable.side)
                setDefaultImageDrawable(drawable)
            }
            with(holder.itemView) {
                tag = it
                setOnClickListener(onClickListener)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val gender: TextView = view.findViewById(R.id.gender)
        val portrait: NetworkImageView = view.findViewById(R.id.portrait)
    }
}
