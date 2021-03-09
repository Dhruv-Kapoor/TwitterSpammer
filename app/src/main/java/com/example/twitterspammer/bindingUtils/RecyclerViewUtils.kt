package com.example.twitterspammer.bindingUtils

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {

    @BindingAdapter("adapter")
    @JvmStatic
    fun adapter(
        recyclerView: RecyclerView,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ) {
        recyclerView.adapter = adapter
    }

    @BindingAdapter("addDecoration")
    @JvmStatic
    fun addDecoration(
        recyclerView: RecyclerView,
        decoration: RecyclerView.ItemDecoration
    ) {
        recyclerView.addItemDecoration(decoration)
    }
}