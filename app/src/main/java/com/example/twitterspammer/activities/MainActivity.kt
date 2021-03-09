package com.example.twitterspammer.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.twitterspammer.Commons.TYPE_HASHTAG
import com.example.twitterspammer.Commons.TYPE_TEXT
import com.example.twitterspammer.Commons.TYPE_TIME_INTERVAL
import com.example.twitterspammer.Commons.TYPE_TWEET_COUNT
import com.example.twitterspammer.R
import com.example.twitterspammer.customViews.AddValueDialog
import com.example.twitterspammer.customViews.EditListDialog
import com.example.twitterspammer.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
const val HASHTAG_LIST = "hashtagList"
const val TEXT_LIST = "textList"
const val NUM_OF_TWEETS = "noOfTweets"
const val TIME_INTERVAL = "timeInterval"
const val TAG_ADD_VALUE_DIALOG = "AddValueDialog"

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val viewModel by lazy {
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }
    private val tweetCountAdapter by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.spinner_item_view,
            R.id.text,
            viewModel.getTweetCountList().value!!
        )
    }
    private val timeIntervalAdapter by lazy {
        ArrayAdapter<String>(
            this,
            R.layout.spinner_item_view,
            R.id.text,
            viewModel.getTimeIntervalList().value!!
        )
    }
    private val addTweetCountDialog by lazy {
        AddValueDialog(TYPE_TWEET_COUNT)
    }
    private val addTimeIntervalDialog by lazy {
        AddValueDialog(TYPE_TIME_INTERVAL)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        binding.tweetCountAdapter = tweetCountAdapter
        binding.timeIntervalAdapter = timeIntervalAdapter
        binding.listener = this

        viewModel.resetToast()
        viewModel.getToast().observe(this, {
            if (it.isNotEmpty()) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.getShowAddValueDialog().observe(this, {
            if(supportFragmentManager.findFragmentByTag(TAG_ADD_VALUE_DIALOG) == null){
                if (it == TYPE_TWEET_COUNT) {
                    addTweetCountDialog.show(supportFragmentManager, TAG_ADD_VALUE_DIALOG)
                } else if (it == TYPE_TIME_INTERVAL) {
                    addTimeIntervalDialog.show(supportFragmentManager, TAG_ADD_VALUE_DIALOG)
                }
                viewModel.resetAddValueDialog()
            }
        })

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnAddHashTag -> {
                viewModel.addHashTag()
            }
            R.id.btnAddText -> {
                viewModel.addText()
            }
            R.id.btnStart -> {
                startTweetService()
            }
            R.id.tvEditHasTagList -> {
                EditListDialog(TYPE_HASHTAG).show(supportFragmentManager, null)
            }
            R.id.tvEditTextList -> {
                EditListDialog(TYPE_TEXT).show(supportFragmentManager, null)
            }
        }
    }

    private fun startTweetService() {
        startForegroundService(
            Intent(this, SpamService::class.java).apply {
                putExtra(HASHTAG_LIST, viewModel.getHashTagList().value)
                putExtra(TEXT_LIST, viewModel.getTextList().value)
                putExtra(
                    NUM_OF_TWEETS,
                    viewModel.selectedTweetCount.value?.let {
                        viewModel.getTweetCountList().value?.get(
                            it
                        )
                    }
                )
                putExtra(
                    TIME_INTERVAL,
                    viewModel.selectedTimeInterval.value?.let {
                        viewModel.getTimeIntervalList().value?.get(
                            it
                        )
                    })
            }
        )
    }

    override fun finishAfterTransition() {
        finish()
    }
}