package com.lf.fashion.ui

import com.lf.fashion.data.common.PreferenceManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class PrefCheckService(private val userPref: PreferenceManager) {
    fun loginCheck() : Boolean{
        val requestAuthKey: Deferred<String> =
            CoroutineScope(Dispatchers.IO).async {
                userPref.accessToken.first() ?: ""
            }

        val authKey = runBlocking { requestAuthKey.await() }

        return authKey.isNotEmpty()
    }
}