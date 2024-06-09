package com.tikorst.satset.ui.customer.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tikorst.satset.data.UserPreference
import kotlinx.coroutines.launch

class ProfileViewModel(private val userPreference: UserPreference) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Profile Fragment"
    }
    val text: LiveData<String> = _text
    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }
}