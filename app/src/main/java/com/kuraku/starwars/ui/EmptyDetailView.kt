package com.kuraku.starwars.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.kuraku.starwars.models.Section

/**
 * Just a placeholder for unsupported DetailViews (Currently only support Person)
 */
class EmptyDetailView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs), DetailView<Section> {
    override fun updateData(it: Section): Map<String, Section> {
        return mapOf()
    }

    override fun updateExtra(key: String, value: Section) {

    }
}