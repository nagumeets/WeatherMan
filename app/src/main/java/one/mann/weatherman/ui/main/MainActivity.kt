package one.mann.weatherman.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import one.mann.weatherman.R
import one.mann.weatherman.api.Keys
import one.mann.weatherman.ui.base.BaseActivity
import one.mann.weatherman.ui.main.adapter.ViewPagerAdapter

internal class MainActivity : BaseActivity() {

    private val locationRequestCode = 1011
    private val placeAutocompleteRequestCode = 1021
    private val pagesAdapter = ViewPagerAdapter(supportFragmentManager)
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationPermissionListener = { initObjects() }
        if (getCheckLocationPermission()) initObjects()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            locationRequestCode ->
                when (resultCode) {
                    Activity.RESULT_OK -> mainViewModel.getWeather(true)
                    Activity.RESULT_CANCELED -> mainViewModel.getWeather(false)
                }
            placeAutocompleteRequestCode ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val place = Autocomplete.getPlaceFromIntent(data!!)
                        mainViewModel.newCityLocation(place.latLng!!.latitude, place.latLng!!.longitude)
                    }
                }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) { // add more options
            R.id.menu_add_city -> placeAutocompleteIntent()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initObjects() {
        setSupportActionBar(main_toolbar)
        main_viewPager.adapter = pagesAdapter
        main_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (!swipe_refresh_layout.isRefreshing)
                    swipe_refresh_layout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })
        main_tabLayout.setupWithViewPager(main_viewPager)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.displayLoadingBar.observe(this,
                Observer { result -> swipe_refresh_layout.isRefreshing = result ?: false })
        mainViewModel.displayUi.observe(this,
                Observer { aBoolean -> if (!aBoolean!!) checkLocationSettings() })
        mainViewModel.displayToast.observe(this, Observer { msg -> if (msg != 0) toast(msg) })
        mainViewModel.cityCount.observe(this, Observer { count -> pagesAdapter.updatePages(count!!) })
        swipe_refresh_layout.setColorSchemeColors(Color.RED, Color.BLUE)
        swipe_refresh_layout.setOnRefreshListener { this.checkLocationSettings() }
    }

    private fun checkNetworkConnection(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun checkLocationSettings() {
        if (!checkNetworkConnection()) {
            toast(R.string.no_internet_connection)
            swipe_refresh_layout.isRefreshing = false
            return
        }
        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY))
        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnCompleteListener { task ->
                    try { // Location settings are On
                        task.getResult(ApiException::class.java)
                        mainViewModel.getWeather(true)
                    } catch (exception: ApiException) {
                        when (exception.statusCode) { // Settings are Off. Prompt and check result in onActivityResult()
                            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                                val resolvable = exception as ResolvableApiException
                                resolvable.startResolutionForResult(this@MainActivity, locationRequestCode)
                            } catch (ignored: IntentSender.SendIntentException) {
                            } catch (ignored: ClassCastException) {
                            } // Location settings not available on the device
                            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                                toast(R.string.location_settings_not_available)
                                finish()
                            }
                        }
                    }
                }
    }

    private fun placeAutocompleteIntent() = try {
        if (!Places.isInitialized()) Places.initialize(applicationContext, Keys.Places_APP_KEY)
        val filter: List<Place.Field> = listOf(Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, filter)
                .build(this)
        startActivityForResult(intent, placeAutocompleteRequestCode)
    } catch (ignored: GooglePlayServicesRepairableException) {
    } catch (ignored: GooglePlayServicesNotAvailableException) {
    }
}