package com.kuraku.starwars.details

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.kuraku.starwars.R
import com.kuraku.starwars.search.PeopleActivity

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [PeopleActivity].
 */
class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don"t need to manually add it.
        if (savedInstanceState == null) {
            navigateToDetails(
                intent.getIntExtra(DetailFragment.EXTRA_DATA_ID, -1),
                intent.getStringExtra(DetailFragment.EXTRA_DATA_TYPE)
            )
        }
    }

    fun navigateToDetails(dataId: Int, dataType: String?) {
        val fragment = DetailFragment().apply {
            arguments = Bundle().apply {
                putInt(DetailFragment.EXTRA_DATA_ID, dataId)
                putString(DetailFragment.EXTRA_DATA_TYPE, dataType)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.item_detail_container, fragment)
            // backstack disabled
            // .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    navigateUpTo(Intent(this, PeopleActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}