package com.slings.vasantham.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slings.vasantham.model.AttendanceInModel
import com.slings.vasantham.model.AttendanceLogModel
import com.slings.vasantham.model.AttendanceMonthModel
import com.slings.vasantham.model.CommonModel
import com.slings.vasantham.model.IsLoginModel
import com.slings.vasantham.model.PermissionModel
import com.slings.vasantham.model.PermissionStatusModel
import com.slings.vasantham.model.ProfileModel
import com.slings.vasantham.model.ShiftTimingsModel
import com.slings.vasantham.service.RetrofitService
import kotlinx.coroutines.*
import java.io.File

class MainViewModel constructor() : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    val ShiftTimeLiveData = MutableLiveData<ShiftTimingsModel>()
    fun getShifTime(): MutableLiveData<ShiftTimingsModel> = ShiftTimeLiveData

    val isLoginLiveData = MutableLiveData<IsLoginModel>()
    fun isloginstatus(): MutableLiveData<IsLoginModel> = isLoginLiveData

    val attendancePunchInLiveData = MutableLiveData<AttendanceInModel>()
    fun getAttenPunchInLiveData(): MutableLiveData<AttendanceInModel> = attendancePunchInLiveData

    val punchOUtLiveData = MutableLiveData<CommonModel>()
    fun getpunchOUtLiveData(): MutableLiveData<CommonModel> = punchOUtLiveData

    val attendanceDayLogLiveData = MutableLiveData<AttendanceLogModel>()
    fun getAttenDayLogLiveData(): MutableLiveData<AttendanceLogModel> = attendanceDayLogLiveData

    val permissionDataLiveData = MutableLiveData<PermissionModel>()
    fun getPermissionLiveData(): MutableLiveData<PermissionModel> = permissionDataLiveData

    val permissionStatusDataLiveData = MutableLiveData<PermissionStatusModel>()
    fun getPermissionStatusLiveData(): MutableLiveData<PermissionStatusModel> = permissionStatusDataLiveData

    val attMonthLogDataLiveData = MutableLiveData<AttendanceMonthModel>()
    fun getAttMonthLogLiveData(): MutableLiveData<AttendanceMonthModel> = attMonthLogDataLiveData

    val profileLiveData = MutableLiveData<ProfileModel>()
    fun getprofileLiveData(): MutableLiveData<ProfileModel> = profileLiveData


    var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()


     fun getShift(userId:String,retrofitService:RetrofitService) {
         viewModelScope.launch {
             val response = retrofitService.getshift(Integer.parseInt(userId))
             if (response.isSuccessful) {
                 val responseModel: ShiftTimingsModel? = response.body()
                 if (responseModel != null) {
                     ShiftTimeLiveData.postValue(response.body())
                 } else {

                 }
             } else {

             }
         }
    }

    fun isLogin(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.islogin(userId)
            if (response.isSuccessful) {
                val loginModel: IsLoginModel? = response.body()
                if (loginModel != null) {
                    isLoginLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun punchIn(userId:String,shiftId:String,associateId:String,image:File,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.attendanceIn(userId,shiftId,associateId,image)
            if (response.isSuccessful) {
                val attenInModel: AttendanceInModel? = response.body()
                if (attenInModel != null) {
                    attendancePunchInLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun punchOut(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.attendanceOut(userId)
            if (response.isSuccessful) {
                val commonModel: CommonModel? = response.body()
                if (commonModel != null) {
                    punchOUtLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun attendanceDayLog(userId:String,date:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.attendanceDayLog(userId,date)
            if (response.isSuccessful) {
                val attenLogModel: AttendanceLogModel? = response.body()
                if (attenLogModel != null) {
                    attendanceDayLogLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun permissionStatus(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.permissionStatus(userId,userId)
            if (response.isSuccessful) {
                val attenLogModel: PermissionStatusModel? = response.body()
                if (attenLogModel != null) {
                    permissionStatusDataLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun permissionApplyOut(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.permissionApplyOut(userId)
            if (response.isSuccessful) {
                val permissionApplyModel: PermissionModel? = response.body()
                if (permissionApplyModel != null) {
                    permissionDataLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun attendanceMonthlyLog(userId:String,year:Int,month:Int,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.attendanceMonthLog(userId,year,month)
            if (response.isSuccessful) {
                val permissionApplyModel: AttendanceMonthModel? = response.body()
                if (permissionApplyModel != null) {
                    attMonthLogDataLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun profileEditLoad(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
            val response = retrofitService.profileEditData(userId)
            if (response.isSuccessful) {
                val profileModel: ProfileModel? = response.body()
                if (profileModel != null) {
                    profileLiveData.postValue(response.body())
                } else {

                }
            } else {

            }
        }
    }

    fun permissionApplyIn(userId:String,retrofitService:RetrofitService) {
        viewModelScope.launch {
//            val response = retrofitService.permissionApplyIn(userId)
//            if (response.isSuccessful) {
//                val attenLogModel: PermissionModel? = response.body()
//                if (attenLogModel != null) {
//                    permissionApplyIn.postValue(response.body())
//                } else {
//
//                }
//            } else {
//
//            }
        }
    }

    private fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}