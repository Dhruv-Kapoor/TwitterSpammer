package com.example.twitterspammer.bindingUtils

import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.BindingAdapter

object SpinnerUtils {

    @BindingAdapter("adapter")
    @JvmStatic
    fun adapter(spinner: Spinner, adapter: ArrayAdapter<String>) {
        spinner.adapter = adapter
    }

}