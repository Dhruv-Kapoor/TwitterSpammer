package com.example.twitterspammer.customViews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.twitterspammer.Commons.TYPE_HASHTAG
import com.example.twitterspammer.Commons.TYPE_TEXT
import com.example.twitterspammer.Commons.showToast
import com.example.twitterspammer.R
import com.example.twitterspammer.activities.MainActivityViewModel
import com.example.twitterspammer.adapters.ListAdapter
import com.example.twitterspammer.databinding.DialogEditListBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditListDialog(val type: Int = -1) : BottomSheetDialogFragment() {

    private lateinit var viewModel: MainActivityViewModel
    private val adapter by lazy {
        ListAdapter(viewModel, type)
    }
    private lateinit var binding: DialogEditListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_edit_list, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]

        binding.adapter = adapter
        binding.decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        viewModel.resetItemRemoved()
        viewModel.getItemRemovedFromList().observe(viewLifecycleOwner, {
            if (it > -1) {
                adapter.notifyItemRemoved(it)
                if (listIsEmpty()) {
                    this@EditListDialog.dismiss()
                }
            }
        })
        if(listIsEmpty()){
            showToast(requireContext(), "Empty List")
            dismiss()
        }

    }

    private fun listIsEmpty(): Boolean {
        if ((type == TYPE_HASHTAG && viewModel.getHashTagList().value.isNullOrEmpty()) ||
            (type == TYPE_TEXT && viewModel.getTextList().value.isNullOrEmpty())) {
            return true
        }
        return false
    }
}