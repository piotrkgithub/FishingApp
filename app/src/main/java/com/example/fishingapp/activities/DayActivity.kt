package com.example.fishingapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fishingapp.R
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.karumi.dexter.Dexter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.location.*
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.util.*
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.example.fishingapp.*
import com.example.fishingapp.models.PostModel
import com.example.fishingapp.services.ApiService
import com.example.fishingapp.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_add_event.*
import kotlinx.android.synthetic.main.activity_add_fish.*
import kotlinx.android.synthetic.main.activity_day.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.card_post.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DateFormat
import java.text.SimpleDateFormat

class DayActivity : AppCompatActivity() {
    private var cal = Calendar.getInstance()
    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private var mProgressDialog: Dialog? = null
    var dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
    var monthOfYear = cal.get(Calendar.MONTH)+1
    var year = cal.get(Calendar.YEAR)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val cal = Calendar.getInstance()
        takeData()

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cal.set(year,month,dayOfMonth)
            this.dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            monthOfYear = cal.get(Calendar.MONTH)+1
            this.year = cal.get(Calendar.YEAR)
            takeData()
        }
    }

    private fun takeData(){
        if(!isLocationEnabled()){
            Toast.makeText(
                this,
                "Lokalizacja tego urządzenia jest wyłączona. Włącz lokalizację.",
                Toast.LENGTH_LONG
            ).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }else{
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener{
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if(report!!.areAllPermissionsGranted()){
                            requestLocationData()
                            checkLocationPermission()
                        }
                        if(report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(
                                this@DayActivity,
                                "Aplikacja nie ma dostępu do lokalizacji. " +
                                        "Nadaj uprawnienia dostępu do lokalizacji, " +
                                        "aby w pełni korzystać z dostępnych funkcji.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    private fun checkLocationPermission() {
        val task = mFusedLocationClient.lastLocation

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if(it != null) {
                getAllUrl(it.latitude, it.longitude)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
    }

    private fun getAllUrl(mLatitude: Double, mLongitude: Double) {
        var allUrl: String
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.getDefault())
        val currentLocalTime: Date = calendar.getTime()
        val date: DateFormat = SimpleDateFormat("Z", Locale.getDefault())
        val localTime = date.format(currentLocalTime)
        var utc: String
        var utc0 = localTime.get(0)
        var utc1 = localTime.get(1)
        var utc2 = localTime.get(2)

        if(utc1.toString() == "0"){
            if(utc0.toString() == "-"){
                utc = "-" + utc2
            }else{
                utc = utc2.toString()
            }
        }else{
            if(utc0.toString() == "-"){
                utc = "-1" + utc2
            }else{
                utc = "1" + utc2.toString()
            }
        }

        var lat = BigDecimal(mLatitude).setScale(2, RoundingMode.HALF_EVEN)
        var lon = BigDecimal(mLongitude).setScale(2, RoundingMode.HALF_EVEN)

        if(dayOfMonth < 10 && monthOfYear < 10) {
            allUrl = lat.toString() + "," + lon.toString() + ',' + year.toString() + "0" +
                    monthOfYear.toString() + "0" + dayOfMonth.toString() + "," + utc
        } else if(monthOfYear < 10 && dayOfMonth > 9) {
            allUrl = lat.toString() + "," + lon.toString() + ',' + year.toString() + "0" +
                    monthOfYear.toString()  + dayOfMonth.toString() + "," + utc
        } else if(dayOfMonth < 10 && monthOfYear > 9){
            allUrl = lat.toString() + "," + lon.toString() + ',' + year.toString() +
                    monthOfYear.toString() + "0" + dayOfMonth.toString() + "," + utc
        } else {
            allUrl = lat.toString() + "," + lon.toString() + ',' + year.toString() +
                    monthOfYear.toString() + dayOfMonth.toString() + "," + utc
        }

        var recyclerView = findViewById<RecyclerView>(R.id.myRecyclerDayView)
        var serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        var call = serviceGenerator.getPosts(allUrl)

        showCustomProgressDialog()

        call.enqueue(object : Callback<PostModel> {
            override fun onResponse(call: Call<PostModel>, response: Response<PostModel>) {
                if(response.isSuccessful){
                    hideProgressDialog()
                    recyclerView.apply {
                        layoutManager = LinearLayoutManager(this@DayActivity)
                        adapter = PostAdapter(response.body()!!)
                    }
                }
            }

            override fun onFailure(call: Call<PostModel>, t: Throwable) {
                hideProgressDialog()
                t.printStackTrace()
                Log.e("error", t.message.toString())
                Toast.makeText(
                    this@DayActivity,
                    "Połączenie z internetem jest wyłączone.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                startActivity(Intent(this, MainActivity::class.java))
                true
            }
            R.id.action_events -> {
                startActivity(Intent(this, EventsListActivity::class.java))
                true
            }
            R.id.action_archiver -> {
                startActivity(Intent(this, FishListActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            val mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")
            val mLongitude = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitude")
        }
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("Aplikacja nie ma nadanych wymaganych zezwoleń. " +
                    "Nadaj uprawnienia w ustawieniach aplikacji.")
            .setPositiveButton("USTAWIENIA") {
                _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("ZAMKNIJ") {
                    dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if(mProgressDialog != null){
            mProgressDialog!!.dismiss()
        }
    }
}