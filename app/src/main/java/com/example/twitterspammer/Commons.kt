package com.example.twitterspammer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.twitterspammer.activities.LoginActivity
import com.example.twitterspammer.activities.OAUTH_TOKEN
import com.example.twitterspammer.activities.OAUTH_TOKEN_SECRET

object Commons {
    const val TYPE_HASHTAG = 0
    const val TYPE_TEXT = 1
    const val TYPE_TWEET_COUNT = 2
    const val TYPE_TIME_INTERVAL = 3

    fun logOut(activity: Activity) {
        showToast(activity, "Logged Out, Try Again!!")
        ContextCompat.startActivity(
            activity, Intent(
                activity, LoginActivity::class.java
            ), null
        )
        val editor = PreferenceManager.getDefaultSharedPreferences(activity).edit()
        editor.remove(OAUTH_TOKEN)
        editor.remove(OAUTH_TOKEN_SECRET)
        editor.apply()
        activity.finish()
    }


    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun getTimeRequired(tweetCount: Int, timeInterval: Int): String {
        var hours = (tweetCount / 300) * 3
        val seconds = (tweetCount % 300) * timeInterval
        var minutes = seconds / 60
        hours += minutes / 60
        minutes %= 60
        return "${hours}h ${minutes}m"
    }

}