package com.slings.vasantham

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogsViewModel : ViewModel() {
    // MutableLiveData is a subclass of LiveData that can be modified
    private val _yourLiveData = MutableLiveData<String>()

    // Exposing an immutable LiveData to the external components
    val yourLiveData: LiveData<String>
        get() = _yourLiveData

    // Function to update the value of LiveData
    fun updateData(newValue: String) {
        _yourLiveData.value = newValue
    }
}
