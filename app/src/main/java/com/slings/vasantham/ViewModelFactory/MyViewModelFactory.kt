package com.slings.vasantham.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MyViewModelFactory<T : ViewModel>(
    private val viewModelClass: Class<T>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(viewModelClass)) {
            try {
                viewModelClass.getDeclaredConstructor().newInstance() as T
            } catch (e: NoSuchMethodException) {
                throw IllegalArgumentException("ViewModel constructor not found. Make sure the ViewModel has a default (parameterless) constructor.")
            }
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}

