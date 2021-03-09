package com.example.twitterspammer.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.example.twitterspammer.Commons
import com.example.twitterspammer.R
import com.example.twitterspammer.databinding.ActivityLoginBinding
import com.example.twitterspammer.network.TwitterClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "LoginActivity"
const val LOGGED_IN = "logged_in"
const val OAUTH_TOKEN = "oauth_token"
const val OAUTH_TOKEN_SECRET = "oauth_token_secret"
const val USER_ID = "user_id"

class LoginActivity : AppCompatActivity() {
    private val preferenceManager by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)
    }
    private val preferenceManagerEditor by lazy {
        preferenceManager.edit()
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        requestLogin()
    }

    private fun requestLogin() {
        TwitterClient.loginService.requestToken().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                val responseBody = (response.body() as ResponseBody).string().split("=", "&")
                val oauthToken = responseBody[1]
                val oauthTokenSecret = responseBody[2]

                binding.client = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        val uri = Uri.parse(url)
                        if (uri.scheme == "twittersdk") {
                            handleResponse(uri.query!!)
                            return true
                        }
                        return false
                    }
                }
                binding.url = "https://api.twitter.com/oauth/authenticate?oauth_token=$oauthToken"

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

    private fun handleResponse(response: String) {
        Log.d(TAG, "handleResponse: $response")
        val t = response.split("=", "&")
        if (t[0] == "denied") {
            Commons.showToast(this, "Can't use app without logging in")
            requestLogin()
            return
        }
        val oauthToken = t[1]
        val oauthVerifier = t[3]
        accessToken(oauthToken, oauthVerifier)
    }

    private fun accessToken(oauthToken: String, oauthVerifier: String) {
        TwitterClient.loginService.accessToken(oauthToken, oauthVerifier).enqueue(object :
            Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "onResponse: ${response.body()}")
                if (response.code().div(100) == 4) {
                    Commons.showToast(this@LoginActivity, "Login Failed")
                    requestLogin()
                    return
                }
                val t = (response.body() as ResponseBody).string().split("=", "&")
                preferenceManagerEditor.putString(OAUTH_TOKEN, t[1])
                preferenceManagerEditor.putString(OAUTH_TOKEN_SECRET, t[3])
                preferenceManagerEditor.putString(USER_ID, t[5])
                preferenceManagerEditor.commit()
                startActivity(Intent(this@LoginActivity, SplashActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
            }

        })
    }

}