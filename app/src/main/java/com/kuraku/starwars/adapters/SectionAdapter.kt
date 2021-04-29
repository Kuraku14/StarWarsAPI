package com.kuraku.starwars.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kuraku.starwars.R
import com.kuraku.starwars.models.Section

/**
 * Renders a list of Sections
 *
 */
class SectionAdapter : RecyclerView.Adapter<SectionAdapter.ViewHolder>() {

    lateinit var listener: (Section) -> Unit
    var headers = mutableMapOf<Int, String>()
    var sections = listOf<Section>()
        set(value) {
            field = value.sortedBy {
                it.type
            }

            calculateHeaders()
            Log.d("SectionAdapter", "Found headers -> $headers")
            notifyDataSetChanged()
        }

    private fun calculateHeaders() {
        headers.clear()
        var lastHeader = ""
        for (i in sections.indices) {
            val section = sections[i]
            // if we hit an edge, log the index (first is at 0)
            if (section.type != lastHeader) {
                lastHeader = section.type
                headers[i + headers.size] = section.type
            }
        }
    }

    /**
     * Update Section info in place.
     *
     * @param section Updated information
     */
    fun updateSection(section: Section) {
        val index = sections.indexOf(sections.firstOrNull { it.url == section.url })
        if (index > -1) {
            sections[index].apply {
                name = section.name
            }
            // the index of the sections[] is not the same as the full item list.
            var adjustment = 0
            headers.keys.forEach {
                if (it <= index + adjustment) {
                    adjustment++
                }
            }
            notifyItemChanged(index + adjustment)
            Log.d("SectionAdapter", "Updated Section $index -> ${index + adjustment}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val resId = when (viewType) {
            0 -> R.layout.item_list_section_header
            else -> R.layout.item_list_section
        }
        return ViewHolder(LayoutInflater.from(parent.context).inflate(resId, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder.itemViewType) {
            0 -> {
                // index of header is accounted for...
                holder.name.text = headers[position]
                holder.itemView.setOnClickListener(null)
            }
            else -> {
                //.. but section position needs to account for headers
                val adjustment = headers.filterKeys { it < position }.size
                val item = sections[position - adjustment]
                holder.name.text = if (item.name.isNullOrEmpty()) item.url else item.name
                holder.itemView.setOnClickListener {
                    listener.invoke(item)
                }
            }
        }
    }

    override fun getItemCount() = sections.size + headers.size

    override fun getItemViewType(position: Int): Int {
        return if (headers.containsKey(position)) 0 else 1
    }
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
    }
}