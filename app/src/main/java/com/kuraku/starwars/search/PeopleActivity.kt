package com.kuraku.starwars.search

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.kuraku.starwars.R
import com.kuraku.starwars.adapters.PersonAdapter
import com.kuraku.starwars.coms.VolleySingleton


/**
 * Searchable Activity that shows a Master/Detail list of Person
 *
 */
class PeopleActivity : AppCompatActivity() {
    private val viewModel: PeopleSearchViewModel by viewModels()
    private var twoPane: Boolean = false
    private lateinit var personAdapter: PersonAdapter
    private val emptyView: LinearLayout by lazy { findViewById(R.id.empty_view) }
    private val refreshButton: Button by lazy { findViewById(R.id.refresh) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_people)

        findViewById<Toolbar>(R.id.toolbar).let {
            setSupportActionBar(it)
            it.title = title
        }

        if (findViewById<NestedScrollView>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoPane = true
        }

        findViewById<RecyclerView>(R.id.item_list).let { recycler ->
            recycler.adapter = PersonAdapter(this, twoPane).apply {
                personAdapter = this
            }
            recycler.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
        }

        viewModel.owner = this

        // Observe the people list
        viewModel.getPeople(this).observe(this, { newPeople ->
            personAdapter.submitList(newPeople)
        })

        // Observe the empty state
        viewModel.isEmpty().observe(this, {
            emptyView.visibility = if (it) View.VISIBLE else View.GONE
        })

        // Observe the loading state
        findViewById<ProgressBar>(R.id.search_progress_bar). let { progress ->
            viewModel.isLoading().observe(this) {
                progress.visibility = if(it) View.VISIBLE else View.GONE
            }
        }

        refreshButton.setOnClickListener {
            viewModel.doSearch("")
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                viewModel.doSearch(query)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        VolleySingleton.getInstance(this).let {
            if (it.peopleImageUrlCache.isEmpty()) {
                it.updatePeopleImageUrlCache(this)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        // Get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setIconifiedByDefault(false)
        }

        menu.findItem(R.id.action_search).setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem): Boolean {
                Log.d("PeopleActivity", "Search closed")
                viewModel.doSearch("")
                return true
            }
        })
        return true
    }
}