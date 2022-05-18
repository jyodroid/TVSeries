package com.jyodroid.tvseries.ui.lockscreen

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jyodroid.tvseries.ui.settings.pinCodeKey

class LockViewModel : ViewModel() {
    private val _screenLockedLiveData = MutableLiveData<Boolean>()
    val screenLockedLiveData: LiveData<Boolean>
        get() = _screenLockedLiveData

    fun unlockScreen(pin: String, sharedPreferences: SharedPreferences) {
        val storedPin = sharedPreferences.getString(pinCodeKey, null)
        _screenLockedLiveData.postValue(pin != storedPin)
    }

    fun lockScreen() {
        _screenLockedLiveData.postValue(true)
    }
}