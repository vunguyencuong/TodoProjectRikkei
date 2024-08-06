package com.lazyman.todo.others.utilities

import android.util.Log
import androidx.fragment.app.Fragment
import com.lazyman.todo.others.constants.Constants.DEBUG_KEY

fun Fragment.log(msg: String) {
    Log.d(DEBUG_KEY, this.javaClass.simpleName + ":" + msg)
}