package com.tikorst.satset

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tikorst.satset.data.UserModel
import com.tikorst.satset.data.UserPreference
import com.tikorst.satset.data.dataStore
import kotlinx.coroutines.launch

class AuthViewModel(private val userPreference: UserPreference) : ViewModel() {
     fun saveSession(user: UserModel) {
        viewModelScope.launch {
            userPreference.saveSession(user)
        }
    }
    fun getSession(): LiveData<UserModel> {
        return userPreference.getSession().asLiveData()
    }

}

