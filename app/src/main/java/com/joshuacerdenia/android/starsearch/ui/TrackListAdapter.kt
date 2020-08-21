package com.joshuacerdenia.android.starsearch.ui

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.joshuacerdenia.android.starsearch.R
import com.joshuacerdenia.android.starsearch.data.model.Track
import com.squareup.picasso.Picasso

class TrackListAdapter(
    private val listener: SelectListener,
    private val resources: Resources
) : ListAdapter<Track, TrackListAdapter.TrackHolder>(DiffCallback()) {

    interface SelectListener {
        fun onTrackSelected(track: Track)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_track,
            parent,
            false
        )
        return TrackHolder(view)
    }

    override fun onBindViewHolder(holder: TrackHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackHolder(
        view: View
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var track: Track
        private val titleTextView = itemView.findViewById<TextView>(R.id.textView_title)
        private val genreTextView = itemView.findViewById<TextView>(R.id.textView_genre)
        private val priceTextView = itemView.findViewById<TextView>(R.id.textView_price)
        private val imageView = itemView.findViewById<ImageView>(R.id.imageView)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(track: Track) {
            this.track = track

            titleTextView.text = track.name ?: track.album
            genreTextView.text = track.genre
            priceTextView.text = resources.getString(R.string.price, "%.2f".format(track.price))

            Picasso.get()
                .load(track.artwork)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imageView)
        }

        override fun onClick(v: View) {
            listener.onTrackSelected(track)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Track>() {

        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }
}