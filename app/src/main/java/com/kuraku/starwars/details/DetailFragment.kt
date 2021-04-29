package com.kuraku.starwars.details

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.kuraku.starwars.R
import com.kuraku.starwars.adapters.SectionAdapter
import com.kuraku.starwars.coms.NetworkHelper
import com.kuraku.starwars.dataHelpers.DataHelper

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a [PeopleActivity]
 * in two-pane mode (on tablets) or a [DetailActivity]
 * on handsets.
 */
class DetailFragment : Fragment() {
    private val sectionViewModel: SectionViewModel by viewModels()
    private var sectionAdapter = SectionAdapter().apply {
        listener = {
            (requireActivity() as DetailActivity).navigateToDetails(
                NetworkHelper.getIdFromUrl(it.url),
                NetworkHelper.getTypeFromUrl(it.url)
            )
        }
    }
    companion object {
        const val EXTRA_DATA_TYPE = "extra_data_type"
        const val EXTRA_DATA_ID = "extra_data_id"
    }
    // We only pass the ID and let details screen fetch the details.
    // This feels unoptimized now, but once GraphQL is implemented we don't fetch
    // full objects until we need it anyway.

    private val dataId: Int by lazy { requireArguments().getInt(EXTRA_DATA_ID, 1) }
    private val dataType: String by lazy { requireArguments().getString(EXTRA_DATA_TYPE, "")}
    private val dataHelper: DataHelper<*> by lazy { DataHelper.getHelper(
        requireActivity().applicationContext as Application,
        dataId,
        dataType,
        viewLifecycleOwner
    ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<RecyclerView>(R.id.section_list).apply {
            adapter = sectionAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayout.VERTICAL))
        }

        // Inject DetailView into fragment (don't care how it's made)
        val detailView = dataHelper.detailView
        view.findViewById<ViewGroup>(R.id.detail_container).addView(detailView as View)

        // DataHelper holds the ViewModel we can observe
        view.findViewById<ProgressBar>(R.id.detail_progress_bar).let { progressBar ->
            dataHelper.viewModel.isLoading().observe(viewLifecycleOwner) {
                progressBar.visibility = if (it) View.VISIBLE else View.GONE
            }
        }
        //TODO: this would really benefit from GraphQL or Database persistence

        // DataHelper also knows how to create the Section list
        dataHelper.onSectionUrlsAvailable { sections ->
            // Section only knows type and url here... needs more
            sectionAdapter.sections = sections // set adapter with what we know
            sections.forEach { section ->
                // observe changes on each URL... even though they really only change once.
                sectionViewModel.getSection(section.url).observe(viewLifecycleOwner) {
                    // ..come in and update it in place
                    Log.d("DetailFragment", "Loaded section -> $section.url")
                    sectionAdapter.updateSection(it)
                }
            }
        }

        // kick everything off
        dataHelper.loadData()
    }
}