package com.example.twitterspammer.customViews

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.twitterspammer.Commons.TYPE_TIME_INTERVAL
import com.example.twitterspammer.Commons.TYPE_TWEET_COUNT
import com.example.twitterspammer.R
import com.example.twitterspammer.activities.MainActivityViewModel
import com.example.twitterspammer.databinding.DialogAddValueBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private const val TAG = "AddValueDialog"

class AddValueDialog(val type: Int = 0) : BottomSheetDialogFragment(), View.OnClickListener {
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: DialogAddValueBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_add_value, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        binding.viewmodel = viewModel
        binding.listener = this
        binding.text = if (type == TYPE_TWEET_COUNT) {
            "Add new Tweet Count"
        } else {
            "Add new Time Interval"
        }

    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.btnAddValue) {
            if (type == TYPE_TWEET_COUNT) {
                if (viewModel.updateTweetCountList()) {
                    dismiss()
                    viewModel.resetAddValueDialog()
                }
            } else if (type == TYPE_TIME_INTERVAL) {
                if (viewModel.updateTimeIntervalList()) {
                    dismiss()
                    viewModel.resetAddValueDialog()
                }
            }
        }
    }

}