package com.example.twitterspammer.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.twitterspammer.Commons
import com.example.twitterspammer.Commons.showToast
import com.example.twitterspammer.R
import com.example.twitterspammer.network.ConnectionLiveData
import com.example.twitterspammer.network.TwitterClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SplashActivity"

class SplashActivity : AppCompatActivity() {
    private val preferenceManager by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        val oauthToken = preferenceManager.getString(OAUTH_TOKEN, "")
        val oauthTokenSecret = preferenceManager.getString(OAUTH_TOKEN_SECRET, "")

        TwitterClient.setToken(oauthToken!!, oauthTokenSecret!!)
        val connectionLiveData = ConnectionLiveData(this)
        if(connectionLiveData.value == true){
            checkLogin()
        }else{
            showToast(this, "Waiting for network")
            connectionLiveData.observe(this, {
                if(it){
                    checkLogin()
                }
            })
        }

    }

    private fun checkLogin(){
        TwitterClient.loginService.verifyLogin().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "onResponse: ${response.code()}")
                if (response.code().div(100) == 2) {
                    startActivity(
                        Intent(this@SplashActivity, MainActivity::class.java),
                        ActivityOptions.makeSceneTransitionAnimation(
                            this@SplashActivity,
                            findViewById(R.id.ivLauncherIcon),
                            getString(R.string.launcherTransition)
                        ).toBundle()
                    )
                } else {
                    Commons.logOut(this@SplashActivity)
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Commons.showToast(this@SplashActivity, "Error logging in")
            }

        })
    }
}