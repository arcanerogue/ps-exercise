package com.glopez.platformsciencecodeexercise.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.glopez.platformsciencecodeexercise.R
import com.glopez.platformsciencecodeexercise.data.Driver
import com.glopez.platformsciencecodeexercise.data.DriverShipmentsRepository
import com.glopez.platformsciencecodeexercise.data.Resource
import kotlinx.coroutines.launch

class DriversViewModel : ViewModel() {
    private val repo = DriverShipmentsRepository
    private var _drivers = MutableLiveData< Resource<List<Driver>>>()
    val drivers: LiveData<Resource<List<Driver>>>
        get() = _drivers

    init {
        getDrivers()
        calculateScores()
    }

    private fun getDrivers() {
        viewModelScope.launch {
            _drivers.value = Resource.Loading(emptyList())
            val drivers = repo.getDrivers()
            if (drivers.isNullOrEmpty()) {
                _drivers.value = Resource.Error(Exception("Error retrieving reviews from the network"))
            } else {
                _drivers.value = Resource.Success(drivers)
            }
        }
    }

    fun getShipmentForDriver(driverId: Int) : String {
        val shipments = repo.getShipments()
        val shipment = shipments.find { it.assignedDriverId == driverId }
        return shipment?.streetName.toString()
    }

    private fun calculateScores() {
        viewModelScope.launch {
            repo.calculateAllShipmentScores()
        }
    }
}