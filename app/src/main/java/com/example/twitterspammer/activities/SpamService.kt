package com.example.twitterspammer.activities

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.twitterspammer.Commons.getTimeRequired
import com.example.twitterspammer.R
import com.example.twitterspammer.network.TwitterClient
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.awaitResponse

const val ACTION_STOP_SERVICE = "stopService"
private const val NOTIFICATION_ID = 1
private const val TAG = "SpamService"

class SpamService : Service() {

    private val receiver by lazy { StopServiceReceiver(this) }
    private val notificationManager by lazy {
        getSystemService(NotificationManager::class.java)
    }
    private lateinit var notificationBuilder: NotificationCompat.Builder
    var tweetsSent = 0

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("service", "Spam Service")
        }

        val stopServiceIntent =
            PendingIntent.getBroadcast(this, 321, Intent(ACTION_STOP_SERVICE), 0)

        registerReceiver(receiver, IntentFilter().apply {
            addAction(ACTION_STOP_SERVICE)
        })

        val hashTagList = (intent?.getSerializableExtra(HASHTAG_LIST)
            ?: throw RuntimeException("HashTag list cannot be null")) as ArrayList<String>
        val textList = (intent.getSerializableExtra(TEXT_LIST)
            ?: throw java.lang.RuntimeException("Text list cannot be null")) as ArrayList<String>
        val tweetCount = Integer.valueOf(intent.getStringExtra(NUM_OF_TWEETS) ?: "0")
        val timeInterval = Integer.valueOf(intent.getStringExtra(TIME_INTERVAL) ?: "1")

        notificationBuilder = NotificationCompat.Builder(this, "service")
            .setContentTitle("Work in progress")
            .setContentText(
                "Tweets left: $tweetCount\nTime left: ${
                    getTimeRequired(
                        tweetCount,
                        timeInterval
                    )
                }"
            )
            .setSmallIcon(R.drawable.twitter_logo_32)
            .setContentIntent(pendingIntent)
            .addAction(0, "STOP", stopServiceIntent)

        val notification = notificationBuilder.build()

        startForeground(NOTIFICATION_ID, notification)

        startTweeting(hashTagList, textList, tweetCount, timeInterval)
        return START_NOT_STICKY
    }

    private fun startTweeting(
        hashTagList: ArrayList<String>,
        textList: ArrayList<String>,
        tweetCount: Int,
        timeInterval: Int
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            for (text in textList) {
                appendHashTags(hashTagList, tweetCount, timeInterval, text)
                if (tweetsSent >= tweetCount) {
                    break
                }
            }
            if (textList.size == 0) {
                appendHashTags(hashTagList, tweetCount, timeInterval, "")
            }
            unregisterReceiver(receiver)
            stopSelf()
        }

    }


    private suspend fun appendHashTags(
        hashTagList: ArrayList<String>,
        tweetCount: Int,
        timeInterval: Int,
        text: String
    ) {
        if (tweetsSent >= tweetCount || text.length > 140) {
            return
        }
        if (sendTweet(text)) {
            val tweetsLeft = tweetCount - tweetsSent
            val notification = notificationBuilder.setContentText(
                "Tweets left: $tweetsLeft\nTime left: ${
                    getTimeRequired(
                        tweetsLeft,
                        timeInterval
                    )
                }"
            ).build()
            notificationManager.notify(NOTIFICATION_ID, notification)
            delay(timeInterval.toLong() * 1000)
        }else{
            delay(1000)
        }
        for (hashTag in hashTagList) {
            appendHashTags(hashTagList, tweetCount, timeInterval, text + "\n$hashTag")
            if (tweetsSent > tweetCount) {
                return
            }
        }
    }

    private suspend fun sendTweet(text: String): Boolean {
        Log.d(TAG, "sending Tweet: $text")
        val requestBody = RequestBody.create(MediaType.parse("text/plain"),text)
        val call = TwitterClient.apiService.postTweet(requestBody)
        val response = call.awaitResponse()
        if (response.code().div(100) == 2) {
            ++tweetsSent
            Log.d(TAG, "sent Tweet: ")
            return true
        }
        Log.d(TAG, "sendTweet: failed ${response.code()}")
        return false
//        ++tweetsSent
//        return true
    }

    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }


    class StopServiceReceiver(val service: SpamService) : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STOP_SERVICE) {
                service.unregisterReceiver(service.receiver)
                service.tweetsSent = Int.MAX_VALUE
                service.stopSelf()
            }
        }

    }
}