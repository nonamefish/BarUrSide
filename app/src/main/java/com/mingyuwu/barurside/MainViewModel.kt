package com.mingyuwu.barurside

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.mingyuwu.barurside.data.Venue

class MainViewModel: ViewModel() {

    var location = MutableLiveData<LatLng>()

}