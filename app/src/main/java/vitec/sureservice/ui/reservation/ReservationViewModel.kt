package vitec.sureservice.ui.reservation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vitec.sureservice.data.local.SureServiceDatabase
import vitec.sureservice.data.model.ServiceRequest
import vitec.sureservice.data.remote.ApiClient

class ReservationViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceRequestInterface = ApiClient.buildServiceRequest()
    val clientDao = SureServiceDatabase.getInstance(application).clientDao()

    private var _serviceRequests = MutableLiveData<List<ServiceRequest>>()
    val serviceRequests get() = _serviceRequests

    private var _serviceRequest = MutableLiveData<ServiceRequest>()
    val serviceRequest get() = _serviceRequest


    fun getServiceRequestsByClientId() {
        viewModelScope.launch {
            try {
                val getServiceRequests = serviceRequestInterface.getServiceRequestByClientId(clientDao.getAllClients()[0].id.toInt())
                getServiceRequests.enqueue(object: Callback<List<ServiceRequest>> {
                    override fun onResponse(
                        call: Call<List<ServiceRequest>>,
                        response: Response<List<ServiceRequest>>
                    ) { serviceRequests.postValue(response.body()!!) }

                    override fun onFailure(call: Call<List<ServiceRequest>>, t: Throwable) {
                        Log.d("Fail", t.toString())
                    }
                })
            }
            catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }
    }

}