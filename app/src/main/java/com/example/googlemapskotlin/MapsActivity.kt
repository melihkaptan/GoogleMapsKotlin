package com.example.googlemapskotlin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(listener)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latestLocation = LatLng(location.latitude, location.longitude)
                latestLocation?.let {
                    mMap.addMarker(MarkerOptions().position(latestLocation).title("Güncel Konumunuz"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latestLocation, 15f))
                }
            }

            override fun onProviderDisabled(provider: String) {
            }

            override fun onProviderEnabled(provider: String) {
            }


        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // izinVerilmemiş
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            //izinVerilmiş
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)

            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            lastKnownLocation?.let {
                val lastKnownLocationLatLng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(lastKnownLocationLatLng).title("Son Güncel Konumunuz"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocationLatLng, 15f))
            } ?: kotlin.run {
                // Add a marker in Sydney and move the camera
                val istanbul = LatLng(41.0054958, 28.872096)
                mMap.addMarker(MarkerOptions().position(istanbul).title("Marker in Istanbul"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul, 15f))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 1) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)

                    val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    lastKnownLocation?.let {
                        val lastKnownLocationLatLng = LatLng(it.latitude, it.longitude)
                        mMap.addMarker(MarkerOptions().position(lastKnownLocationLatLng).title("Son Güncel Konumunuz"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastKnownLocationLatLng, 15f))
                    } ?: kotlin.run {
                        // Add a marker in Istanbul and move the camera
                        val istanbul = LatLng(41.0054958, 28.872096)
                        mMap.addMarker(MarkerOptions().position(istanbul).title("Marker in Istanbul"))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul, 15f))
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    val listener = GoogleMap.OnMapLongClickListener { it ->

        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        var title = ""
        try {
            val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            addressList?.let { address ->
                address[0].thoroughfare?.let {
                    title += address[0].thoroughfare
                }
                address[0].subThoroughfare?.let {
                    title += " " + address[0].subThoroughfare
                }

            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        mMap.clear()
        mMap.addMarker(MarkerOptions().position(it).title(title)).showInfoWindow()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f))
    }
}
