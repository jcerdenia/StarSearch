package com.joshuacerdenia.android.starsearch.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.joshuacerdenia.android.starsearch.R

class SortTracksFragment: DialogFragment() {

    companion object {
        private const val ARG_ORDER = "ARG_ORDER"

        fun newInstance(context: Context?, currentOrder: Int): SortTracksFragment? {
            val args = Bundle().apply {
                putInt(ARG_ORDER, currentOrder)
            }
            return if (context != null) {
                SortTracksFragment().apply {
                    arguments = args
                }
            } else null
        }
    }

    interface Callbacks {
        fun onOrderSelected(order: Int)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val currentChoice = arguments?.getInt(ARG_ORDER) ?: 0
        val items = arrayOf(
            getString(R.string.relevance),
            getString(R.string.title),
            getString(R.string.genre),
            getString(R.string.price_ascending),
            getString(R.string.price_descending)
        )

        val dialogBuilder = AlertDialog.Builder(context!!)
        val dialog = dialogBuilder.setTitle(getString(R.string.sort_by))
            .setCancelable(true)
            .setSingleChoiceItems(items, currentChoice) { dialog, choice ->
                targetFragment?.let { fragment ->
                    (fragment as Callbacks).onOrderSelected(choice)
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
        return dialog
    }
}