package com.example.twitterspammer.activities

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.twitterspammer.Commons
import com.example.twitterspammer.Commons.TYPE_TIME_INTERVAL
import com.example.twitterspammer.Commons.TYPE_TWEET_COUNT
import com.example.twitterspammer.customViews.EditListDialog
import java.lang.NumberFormatException

class MainActivityViewModel : ViewModel() {

    private val _hashTagList = ArrayList<String>()
    private val _textList = ArrayList<String>()

    private val _tweetCountList = arrayListOf(
        "1",
        "50",
        "100",
        "200",
        "500",
        "999",
        "Custom"
    )
    private val _timeIntervalsList =
        arrayListOf("1", "2", "5", "10", "30", "60", "Custom")

    private val hashTagList = MutableLiveData(_hashTagList)
    private val textList = MutableLiveData(_textList)
    private val tweetCountList = MutableLiveData(_tweetCountList)
    private val timeIntervalsList = MutableLiveData(_timeIntervalsList)
    private val timeRequired = MutableLiveData("0m")
    private val toast = MutableLiveData("")
    private val itemRemovedFromList = MutableLiveData(-1)
    private val showAddValueDialog = MutableLiveData(-1)

    fun getHashTagList() = hashTagList as LiveData<ArrayList<String>>
    fun getTextList() = textList as LiveData<ArrayList<String>>
    fun getTweetCountList() = tweetCountList as LiveData<ArrayList<String>>
    fun getTimeIntervalList() = timeIntervalsList as LiveData<ArrayList<String>>
    fun getTimeRequired() = timeRequired as LiveData<String>
    fun getToast() = toast as LiveData<String>
    fun getItemRemovedFromList() = itemRemovedFromList as LiveData<Int>
    fun getShowAddValueDialog() = showAddValueDialog as LiveData<Int>

    var text = ObservableField("")
    var hashTag = ObservableField("")
    var newValue = ObservableField("")
    val selectedTweetCount = MutableLiveData(0).apply {
        observeForever {
            if(it==_tweetCountList.size-1) {
                showAddValueDialog.postValue(TYPE_TWEET_COUNT)
                if(_tweetCountList.size!=1) {
                    resetTweetCount()
                }
            }else{
                updateTimeRequired()
            }
        }
    }

    private fun resetTweetCount() {
        selectedTweetCount.postValue(0)
    }

    val selectedTimeInterval = MutableLiveData(0).apply {
        observeForever{
            if(it==_timeIntervalsList.size-1){
                showAddValueDialog.postValue(TYPE_TIME_INTERVAL)
                if(_timeIntervalsList.size!=1){
                    resetTimeInterval()
                }
            }else {
                updateTimeRequired()
            }
        }
    }

    private fun resetTimeInterval() {
        selectedTimeInterval.postValue(0)
    }

    fun addHashTag() {
        val t = hashTag.get()?.trim()
        if(t.isNullOrEmpty()){
            toast.postValue("HashTag cannot be empty")
            return
        }
        if(_hashTagList.size>=10){
            toast.postValue("Limit Reached")
            return
        }
        if (t[0] != '#') {
            _hashTagList.add("#$t")
        } else {
            _hashTagList.add(t)
        }
        hashTag.set("")
        hashTagList.postValue(_hashTagList)
    }

    fun addText() {
        val t = text.get()?.trim()
        if(t.isNullOrEmpty()){
            toast.postValue("Text cannot be empty")
            return
        }
        if(_textList.size>=10){
            toast.postValue("Limit Reached")
            return
        }
        _textList.add(t)
        text.set("")
        textList.postValue(_textList)
    }

    fun updateTweetCountList():Boolean {
        var value = 0
        try {
            value = Integer.parseInt(newValue.get()!!)
        }catch (e: NumberFormatException){
            toast.postValue("Only integers accepted")
            return false
        }
        if(value>999){
            toast.postValue("Max value of 999 is accepted")
            return false
        }
        _tweetCountList.add(_tweetCountList.size - 1, value.toString())
        tweetCountList.postValue(_tweetCountList)
        selectedTweetCount.postValue(_tweetCountList.size-2)
        return true
    }

    fun updateTimeIntervalList(): Boolean {
        var value = 0
        try {
            value = Integer.parseInt(newValue.get()!!)
        }catch (e: NumberFormatException){
            toast.postValue("Only integers accepted")
            return false
        }
        if(value>60){
            toast.postValue("Max value of 60 is accepted")
            return false
        }
        _timeIntervalsList.add(_timeIntervalsList.size - 1, value.toString())
        timeIntervalsList.postValue(_timeIntervalsList)
        selectedTimeInterval.postValue(_timeIntervalsList.size-2)
        return true
    }


    private fun updateTimeRequired() {
        val tweetCount = Integer.valueOf(_tweetCountList[selectedTweetCount?.value?:0])
        val timeInterval = Integer.valueOf(_timeIntervalsList[selectedTimeInterval?.value?:0])

        timeRequired.postValue(Commons.getTimeRequired(tweetCount, timeInterval))
    }

    fun removeHashTag(pos: Int){
        _hashTagList.removeAt(pos)
        hashTagList.postValue(_hashTagList)
        itemRemovedFromList.postValue(pos)
    }

    fun removeText(pos: Int){
        _textList.removeAt(pos)
        textList.postValue(_textList)
        itemRemovedFromList.postValue(pos)
    }

    fun resetToast() {
        toast.postValue("")
    }

    fun resetItemRemoved() {
        itemRemovedFromList.postValue(-1)
    }

    fun resetAddValueDialog() {
        newValue.set("")
        showAddValueDialog.postValue(-1)
    }

}