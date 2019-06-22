package one.mann.weatherman.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import kotlinx.android.synthetic.main.activity_main.*
import one.mann.domain.model.Location
import one.mann.domain.model.LocationResponse.*
import one.mann.domain.model.LocationType.DB
import one.mann.domain.model.LocationType.DEVICE
import one.mann.interactors.data.repository.WeatherRepository
import one.mann.interactors.usecase.*
import one.mann.weatherman.R
import one.mann.weatherman.api.Keys
import one.mann.weatherman.api.openweathermap.OwmDataSource
import one.mann.weatherman.api.teleport.TeleportDataSource
import one.mann.weatherman.framework.data.database.DbDataSource
import one.mann.weatherman.framework.data.location.LocationDataSource
import one.mann.weatherman.ui.common.base.BaseActivity
import one.mann.weatherman.ui.common.util.app
import one.mann.weatherman.ui.common.util.getViewModel
import one.mann.weatherman.ui.main.adapter.MainPagerAdapter

internal class MainActivity : BaseActivity() {

    companion object {
        private const val autocompleteRequest = 1021
    }

    private val pagesAdapter = MainPagerAdapter(supportFragmentManager)
    private lateinit var mainViewModel: MainViewModel
    private var firstRun = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleLocationPermission { result -> initActivity(result) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == autocompleteRequest)
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data!!)
                mainViewModel.addCity(Location(arrayOf(place.latLng!!.latitude.toFloat(),
                        place.latLng!!.longitude.toFloat())))
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) { // add more options
            R.id.menu_add_city -> autocompleteWidget()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initActivity(success: Boolean) {
        if (!success) { // If permission denied then exit
            toast(R.string.permission_required)
            finish()
            return
        }
        setSupportActionBar(main_toolbar)
        main_viewPager.adapter = pagesAdapter
        // Fix horizontal scrolling
        main_viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                if (!swipe_refresh_layout.isRefreshing)
                    swipe_refresh_layout.isEnabled = state == ViewPager.SCROLL_STATE_IDLE
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {}
        })
        main_tabLayout.setupWithViewPager(main_viewPager)

        mainViewModel = getViewModel {
            val weatherRepository = WeatherRepository(OwmDataSource(), TeleportDataSource(),
                    LocationDataSource(app), DbDataSource(app.db))
            MainViewModel(
                    AddCity(weatherRepository),
                    GetAllWeather(weatherRepository),
                    RemoveCity(weatherRepository),
                    UpdateWeather(weatherRepository),
                    GetCityCount(weatherRepository)
            )
        }
        mainViewModel.loadingState.observe(this, Observer { swipe_refresh_layout.isRefreshing = it })
        mainViewModel.cityCount.observe(this, Observer {
            if (it == 0) handleLocationServiceResult()
            else {
                pagesAdapter.updatePages(it!!)
                firstRun = false
            }
        })
        swipe_refresh_layout.setColorSchemeColors(Color.RED, Color.BLUE)
        swipe_refresh_layout.setOnRefreshListener { handleLocationServiceResult() }
    }

    private fun handleLocationServiceResult() = handleLocationPermission { success ->
        if (success) checkLocationService {
            when (it) {
                NO_NETWORK -> toast(R.string.no_internet_connection)
                ENABLED -> {
                    if (firstRun) mainViewModel.addCity()
                    else mainViewModel.updateWeather(DEVICE)
                }
                DISABLED -> {
                    if (firstRun) toast(R.string.gps_needed_for_location)
                    else mainViewModel.updateWeather(DB)
                }
                UNAVAILABLE -> toast(R.string.location_settings_not_available)
            }
        }
    }

    // Widget for Places Autocomplete API that needs to run in activity scope
    private fun autocompleteWidget() = try {
        if (!Places.isInitialized()) Places.initialize(applicationContext, Keys.Places_APP_KEY)
        val filter: List<Place.Field> = listOf(Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, filter).build(this)
        startActivityForResult(intent, autocompleteRequest)
    } catch (ignored: GooglePlayServicesRepairableException) {
    } catch (ignored: GooglePlayServicesNotAvailableException) {
    }
}