package com.joshuacerdenia.android.starsearch.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.joshuacerdenia.android.starsearch.PAGE_TRACK_DETAIL
import com.joshuacerdenia.android.starsearch.R
import com.joshuacerdenia.android.starsearch.data.TrackPreferences
import com.joshuacerdenia.android.starsearch.data.model.Track
import com.squareup.picasso.Picasso

private const val TAG = "TrackDetailFragment"

class TrackDetailFragment: Fragment() {

    companion object {
        private const val ARG_TRACK_ID = "ARG_TRACK_ID"

        fun newInstance(trackId: String?): TrackDetailFragment {
            val args = Bundle().apply {
                putString(ARG_TRACK_ID, trackId)
            }
            return TrackDetailFragment().apply {
                arguments = args
            }
        }
    }

    private val viewModel: TrackDetailViewModel by lazy {
        ViewModelProvider(this).get(TrackDetailViewModel::class.java)
    }
    private lateinit var toolbar: Toolbar
    private lateinit var nameTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var bgImageView: ImageView
    private lateinit var artistTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var descriptionTextView: TextView
    private var url: String? = null
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun onHomePressed()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = activity as Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        arguments?.getString(ARG_TRACK_ID)?.let { id ->
            viewModel.getTrackById(id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_track_detail, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        nameTextView = view.findViewById(R.id.title_text_view)
        imageView = view.findViewById(R.id.image_view)
        bgImageView = view.findViewById(R.id.background_image_view)
        artistTextView = view.findViewById(R.id.artist_text_view)
        infoTextView = view.findViewById(R.id.info_text_view)
        descriptionTextView = view.findViewById(R.id.description_text_view)

        (activity as AppCompatActivity?)?.setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_home)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.apply {
            subtitle = getString(R.string.last_opened, viewModel.dateLastViewed)
            setNavigationOnClickListener {
                callbacks?.onHomePressed()
            }
        }

        viewModel.trackLiveData.observe(viewLifecycleOwner, Observer { track ->
            track?.let { populateViews(it) }
            track?.url?.let { url = it }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_track_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_view_in_browser -> {
                if (url != null) {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url)).also { intent ->
                        startActivity(intent)
                    }
                } else {
                    showErrorMessage()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateViews(track: Track) {
        nameTextView.text = track.name ?: track.album
        artistTextView.text = track.artist
        infoTextView.text = getString(
            R.string.track_info,
            track.album ?: getString(R.string.none),
            track.genre ?: getString(R.string.none),
            "%.2f".format(track.price)
        )

        if (track.description != null) {
            descriptionTextView.visibility = View.VISIBLE
            descriptionTextView.text = track.description
        } else {
            descriptionTextView.visibility = View.GONE
        }

        Picasso.get()
            .load(track.artwork)
            .fit()
            .centerCrop()
            .placeholder(R.drawable.stars)
            .into(imageView)

        Picasso.get()
            .load(track.artwork)
            .fit()
            .centerCrop()
            .placeholder(ColorDrawable())
            .into(bgImageView)
    }

    private fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.error_message), Toast.LENGTH_SHORT).show()
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onStop() {
        super.onStop()
        context?.let { context ->
            TrackPreferences.savePage(context, PAGE_TRACK_DETAIL)
            viewModel.trackLiveData.value?.let { track ->
                TrackPreferences.saveTrackId(context, track.id)
            }
        }
    }
}