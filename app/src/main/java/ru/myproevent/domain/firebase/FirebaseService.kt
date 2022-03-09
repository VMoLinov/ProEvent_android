package ru.myproevent.domain.firebase

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import ru.myproevent.ProEventApp

class FirebaseService : FirebaseMessagingService() {

    companion object {
        const val SHARED_PREF_FILE = "Shared_pref_token"
        const val SHARED_PREF_VALUE = "Shared_pref_token_value"
    }

    override fun onNewToken(token: String) {
        val sharedPref = ProEventApp.instance.applicationContext.getSharedPreferences(
            SHARED_PREF_FILE, Context.MODE_PRIVATE
        )
        sharedPref.edit().putString(SHARED_PREF_VALUE, token).apply()
    }
}
