package com.example.twitterspammer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.twitterspammer.Commons
import com.example.twitterspammer.Commons.TYPE_HASHTAG
import com.example.twitterspammer.Commons.TYPE_TEXT
import com.example.twitterspammer.R
import com.example.twitterspammer.activities.MainActivityViewModel
import com.example.twitterspammer.databinding.DialogEditListItemViewBinding

class ListAdapter(
    val viewModel: MainActivityViewModel,
    val type: Int
) : RecyclerView.Adapter<ListItemViewHolder>() {

    private val list = if (type == TYPE_HASHTAG) {
        viewModel.getHashTagList().value!!
    } else {
        viewModel.getTextList().value!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder =
        ListItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.dialog_edit_list_item_view, parent, false
            ),
            viewModel,
            type
        )


    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size
}

class ListItemViewHolder(
    val binding: DialogEditListItemViewBinding,
    val viewModel: MainActivityViewModel,
    val type: Int
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    fun bind(text: String) {
        binding.text = text
        binding.listener = this
        binding.executePendingBindings()
    }

    override fun onClick(p0: View?) {
        if (type == TYPE_HASHTAG) {
            viewModel.removeHashTag(adapterPosition)
        } else if (type == TYPE_TEXT) {
            viewModel.removeText(adapterPosition)
        }
    }
}