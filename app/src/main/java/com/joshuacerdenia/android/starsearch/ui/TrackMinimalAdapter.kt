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
import com.joshuacerdenia.android.starsearch.data.model.TrackMinimal
import com.squareup.picasso.Picasso

class TrackMinimalAdapter(
    private val listener: SelectListener,
    private val resources: Resources
) : ListAdapter<TrackMinimal, TrackMinimalAdapter.TrackMinimalHolder>(DiffCallback()) {

    interface SelectListener {
        fun onTrackSelected(track: TrackMinimal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackMinimalHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_track,
            parent,
            false
        )
        return TrackMinimalHolder(view)
    }

    override fun onBindViewHolder(holder: TrackMinimalHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackMinimalHolder(
        view: View
    ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var track: TrackMinimal
        private val titleTextView = itemView.findViewById<TextView>(R.id.title_text_view)
        private val genreTextView = itemView.findViewById<TextView>(R.id.genre_text_view)
        private val priceTextView = itemView.findViewById<TextView>(R.id.price_text_view)
        private val imageView = itemView.findViewById<ImageView>(R.id.image_view)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(track: TrackMinimal) {
            this.track = track
            titleTextView.text = track.name ?: track.album
            genreTextView.text = track.genre
            priceTextView.text = resources.getString(R.string.price, "%.2f".format(track.price))

            Picasso.get()
                .load(track.artwork)
                .fit()
                .centerCrop()
                .placeholder(R.drawable.stars)
                .into(imageView)
        }

        override fun onClick(v: View) {
            listener.onTrackSelected(track)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<TrackMinimal>() {

        override fun areItemsTheSame(oldItem: TrackMinimal, newItem: TrackMinimal): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TrackMinimal, newItem: TrackMinimal): Boolean {
            return oldItem == newItem
        }
    }
}