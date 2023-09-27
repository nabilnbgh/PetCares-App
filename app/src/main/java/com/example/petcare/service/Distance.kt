package com.example.petcare.service

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class Distance(private val startLatitude: Double,private val startLongitude: Double) {
    private var endLatitude : Double? = null
    private var endLongitude : Double? = null

    fun setEndLocation(latitude : Double, longitude : Double){
        endLatitude = latitude
        endLongitude = longitude
    }

    fun calculatedistance(): Double {
        if (endLatitude != null && endLongitude != null) {
            val theta = startLongitude - endLongitude!!
            var dist = (sin(deg2rad(startLatitude))
                    * sin(deg2rad(endLatitude!!))
                    + (cos(deg2rad(startLatitude))
                    * cos(deg2rad(endLatitude!!))
                    * cos(deg2rad(theta))))
            dist = acos(dist)
            dist = rad2deg(dist)
            dist *= 60 * 1.1515 * 1.609344
            return dist
        }
        return -1.0
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}