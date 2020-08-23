package com.joshuacerdenia.android.starsearch.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuacerdenia.android.starsearch.R
import com.joshuacerdenia.android.starsearch.data.TrackPreferences
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal

class TrackListFragment: Fragment(),
    TrackMinimalAdapter.SelectListener,
    SortTracksFragment.Callbacks
{

    private lateinit var viewModel: TrackListViewModel
    private lateinit var toolbar: Toolbar
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: TrackMinimalAdapter
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onTrackSelected(id: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = activity as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrackListViewModel::class.java)
        context?.let { context ->
            TrackPreferences.savePage(context, PAGE_TRACK_LIST)
            viewModel.changeOrder(TrackPreferences.getStoredOrder(context))
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track_list, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        recyclerView = view.findViewById(R.id.recycler_view)
        progressBar = view.findViewById(R.id.progress_bar)

        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        adapter = TrackMinimalAdapter(this, resources)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE

        toolbar.apply {
            subtitle = getString(R.string.last_opened, TrackPreferences.getDateLastViewed(context))
            setOnClickListener {
                recyclerView.smoothScrollToPosition(0)
            }
        }

        viewModel.tracksLiveData.observe(viewLifecycleOwner, Observer { tracks ->
            adapter.submitList(tracks)
            progressBar.visibility = View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_track_list, menu)
        val searchItem: MenuItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as SearchView

        searchView.apply {
            if (viewModel.currentQuery.isNotEmpty()) {
                searchItem.expandActionView()
                setQuery(viewModel.currentQuery, false)
                clearFocus()
            }

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(queryText: String): Boolean {
                    if (queryText.isNotEmpty()) {
                        viewModel.submitQuery(queryText)
                    }
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(queryText: String): Boolean {
                    return if (queryText.isEmpty()) {
                        viewModel.submitQuery(queryText)
                        true
                    } else {
                        false
                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_sort -> {
                SortTracksFragment.newInstance(context, viewModel.currentOrder)?.apply {
                    setTargetFragment(this@TrackListFragment, 0)
                    show(this@TrackListFragment.requireFragmentManager(), "sort")
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onOrderSelected(order: Int) {
        viewModel.changeOrder(order)
    }

    override fun onTrackSelected(track: TrackMinimal) {
        callbacks?.onTrackSelected(track.id)
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onStop() {
        super.onStop()
        context?.let { context ->
            TrackPreferences.savePage(context, PAGE_TRACK_LIST)
            TrackPreferences.saveOrder(context, viewModel.currentOrder)
        }
    }

    companion object {
        fun newInstance(): TrackListFragment {
            return TrackListFragment()
        }
    }
}