package com.joshuacerdenia.android.starsearch.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuacerdenia.android.starsearch.R
import com.joshuacerdenia.android.starsearch.data.model.Track

private const val TAG = "TrackListFragment"

class TrackListFragment: Fragment(), TrackListAdapter.SelectListener {

    companion object {
        fun newInstance(): TrackListFragment {
            return TrackListFragment()
        }
    }

    private val viewModel: TrackListViewModel by lazy {
        ViewModelProvider(this).get(TrackListViewModel::class.java)
    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: TrackListAdapter
    private var tracks: List<Track>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TrackListAdapter(this, resources)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track_list, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressBar = view.findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE

        viewModel.tracksLiveData.observe(viewLifecycleOwner, Observer { tracks ->
            this.tracks = tracks
            adapter.submitList(tracks)
            progressBar.visibility = View.GONE
        })
    }

    override fun onTrackSelected(track: Track) {
        // TODO
    }

    override fun onStop() {
        super.onStop()
        tracks?.let { tracks ->
            viewModel.updateCache(tracks)
        }
    }
}