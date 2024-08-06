package com.lazyman.todo.others.utilities

import android.app.Activity
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.lazyman.todo.R

fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.snack(view: View, message: String, anchorView: View? = null) {
    Snackbar.make(this, view, message, Snackbar.LENGTH_SHORT)
        .setAnchorView(anchorView)
        .show()
}

fun Activity.snackNotAvailable(view: View, anchorView: View? = null) {
    snack(view, getString(R.string.feature_not_available), anchorView)
}

fun Activity.snackArchived(view: View, anchorView: View? = null) {
    snack(view, getString(R.string.task_archived_msg), anchorView)
}

fun Activity.snackAlert(view: View, message: String, anchorView: View? = null) {
    Snackbar.make(this, view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(
            getColor(
                android.R.color.holo_red_dark
            )
        )
        .setTextColor(
            getColor(
                android.R.color.white
            )
        )
        .setAnchorView(anchorView)
        .show()
}

