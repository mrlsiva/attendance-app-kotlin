package com.slings.vasantham.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slings.vasantham.model.LoginModel
import com.slings.vasantham.service.RetrofitService
import kotlinx.coroutines.*

class LoginViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val _logindata = MutableLiveData<LoginModel>()
    val logindata: MutableLiveData<LoginModel>
        get() = _logindata

    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun login(mobile: String, password: String, retrofitService: RetrofitService, s: String) {
        viewModelScope.launch {
            val response = retrofitService.login(mobile, password,s)
            if (response.isSuccessful) {
                val loginModel: LoginModel? = response.body()
                if (loginModel != null) {
                    _logindata.postValue(response.body())
                } else {
                    _logindata.postValue(response.body())
                }
            } else {
                _logindata.postValue(response.body())
            }
        }
    }


//                is NetworkState.Success -> {
//                    Toast.makeText(activity,"success", Toast.LENGTH_SHORT).show()

//                }
//                is NetworkState.Error -> {
//                    if (response.response.code() == 401) {
//                        Toast.makeText(activity,"401"+response.response.message().toString(), Toast.LENGTH_SHORT).show()
//                        movieList.postValue(null)
//                    } else {
//                        Toast.makeText(activity,"400"+response.response.message(), Toast.LENGTH_SHORT).show()
//                        movieList.postValue(null)
//                    }

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}