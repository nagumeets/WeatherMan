package one.mann.weatherman.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_weather.*
import one.mann.weatherman.R
import one.mann.weatherman.application.WeatherManApp
import one.mann.weatherman.ui.common.util.getViewModel
import one.mann.weatherman.ui.common.util.loadIcon
import one.mann.weatherman.ui.main.MainViewModel.UiModel
import one.mann.weatherman.ui.main.adapter.MainRecyclerAdapter
import javax.inject.Inject

internal class MainFragment : Fragment() {

    private var position = 0
    private val mainViewModel: MainViewModel by lazy { activity?.run { getViewModel(viewModelFactory) }!! }
//    private val mainRecyclerAdapter by lazy { MainRecyclerAdapter() }
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    companion object {
        private const val POSITION = "POSITION"
        @JvmStatic
        fun newInstance(position: Int) = MainFragment().apply {
            arguments = Bundle().apply { putInt(POSITION, position) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getInt(POSITION)?.let { position = it }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_main, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        injectDependencies()
//        city_recyclerview.setHasFixedSize(true)
//        city_recyclerview.layoutManager = LinearLayoutManager(context)
//        city_recyclerview.adapter = mainRecyclerAdapter
        // Init ViewModel
//        mainViewModel.uiModel.observe(this, Observer {
//            if (it is UiModel.DisplayUi) city_recyclerview.visibility = if (it.display) View.VISIBLE else View.GONE
//        })
        mainViewModel.weatherData.observe(this, Observer {
            if (it.size >= position + 1) {
                val weather = it[position]
                weather_icon.loadIcon(weather.iconId, weather.sunPosition)
                current_temp.text = weather.currentTemp
                time_updated.text = weather.lastUpdated
                city_name.text = weather.cityName
//                mainRecyclerAdapter.update(it[position])
            }
        })
    }

    private fun injectDependencies() = WeatherManApp.appComponent.getMainComponent().injectFragment(this)
}