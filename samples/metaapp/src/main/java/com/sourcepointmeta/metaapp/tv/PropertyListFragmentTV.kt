package com.sourcepointmeta.metaapp.tv

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.cards.PropertyCardPresenter
import com.sourcepointmeta.metaapp.tv.cards.TextCardPresenter
import com.sourcepointmeta.metaapp.tv.samples.DataSamples
import com.sourcepointmeta.metaapp.ui.BaseState
import java.util.*

import com.sourcepointmeta.metaapp.ui.component.PropertyDTO
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import kotlinx.android.synthetic.main.leanback_card_sample.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named
import kotlin.random.Random


class PropertyListFragmentTV(
    var supportFragmentManager: FragmentManager
) : BrowseSupportFragment() {
    companion object {
        private val TAG = "MainFragment"

        private val BACKGROUND_UPDATE_DELAY = 300
        private val GRID_ITEM_WIDTH = 200
        private val GRID_ITEM_HEIGHT = 200
        private val NUM_ROWS = 3
    }

    private val viewModel: PropertyListViewModel by viewModel()
    private val clearDb: Boolean by inject(qualifier = named("clear_db"))

    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null
    private lateinit var mMetrics: DisplayMetrics
    private val mHandler = Handler(Looper.myLooper()!!)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate")
        super.onActivityCreated(savedInstanceState)

//        prepareBackgroundManager()
        setupUIElements()
        loadRows()
        setupEventListeners()
    }
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + mBackgroundTimer?.toString())
        mBackgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(requireActivity().window)
        mDefaultBackground = ContextCompat.getDrawable(requireActivity(), R.drawable.lb_background)
        mMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(mMetrics)
    }
    private fun setupUIElements() {
        title = "${getString(R.string.app_name)} - ${BuildConfig.VERSION_NAME}"
//        (titleView as TitleView).setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(requireActivity(), R.color.purple_500)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.purple_200)
        androidx.leanback.R.layout.lb_title_view
    }
    private fun loadRows() {
        viewModel.liveData.observe(viewLifecycleOwner) { baseState ->
            if(baseState is BaseState.StatePropertyList){
                baseState.propertyList.map { p -> p.toPropertyDTO() }.let { propertyList ->
                        val rowsAdapter = ArrayObjectAdapter(object : ListRowPresenter() {
                            override fun isUsingDefaultListSelectEffect() = false
                        }.apply { shadowEnabled = false })
                        val propertyCardPresenter = context?.let { PropertyCardPresenter(it) }
                        val textCardPresenter = context?.let { TextCardPresenter(it) }

                        for (dataCategory in 0 until DataSamples.DATA_CATEGORY.size) {
                            when(DataSamples.DATA_CATEGORY[dataCategory]){
                                "GDPR+CCPA" -> {
                                    val listRowAdapter = ArrayObjectAdapter(propertyCardPresenter)
                                    for (j in propertyList.indices) {
                                        if(propertyList[j].gdprEnabled && propertyList[j].ccpaEnabled)
                                            listRowAdapter.add(propertyList[j])
                                    }
                                    val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                    rowsAdapter.add(ListRow(header, listRowAdapter))
                                }
                                "GDPR" -> {
                                    val listRowAdapter = ArrayObjectAdapter(propertyCardPresenter)
                                    for (j in propertyList.indices) {
                                        if(propertyList[j].gdprEnabled && !propertyList[j].ccpaEnabled)
                                            listRowAdapter.add(propertyList[j])
                                    }
                                    val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                    rowsAdapter.add(ListRow(header, listRowAdapter))
                                }
                                "CCPA" -> {
                                    val listRowAdapter = ArrayObjectAdapter(propertyCardPresenter)
                                    for (j in propertyList.indices) {
                                        if(!propertyList[j].gdprEnabled && propertyList[j].ccpaEnabled)
                                            listRowAdapter.add(propertyList[j])
                                    }
                                    val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                    rowsAdapter.add(ListRow(header, listRowAdapter))
                                }
                                "Else" -> {
                                    val listRowAdapter = ArrayObjectAdapter(propertyCardPresenter)
                                    for (j in propertyList.indices) {
                                        if(!propertyList[j].gdprEnabled && !propertyList[j].ccpaEnabled)
                                            listRowAdapter.add(propertyList[j])
                                    }
                                    val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                    rowsAdapter.add(ListRow(header, listRowAdapter))
                                }
                                "Utils" -> {
                                    val listRowAdapter = ArrayObjectAdapter(textCardPresenter)
                                    listRowAdapter.add("Add new")
                                    listRowAdapter.add("Delete all")
                                    listRowAdapter.add("Generate")
                                    val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                    rowsAdapter.add(ListRow(header, listRowAdapter))
                                }
                            }
                        }

                        adapter = rowsAdapter
                    }
            }

        }
        viewModel.fetchPropertyList()
    }
    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireActivity(), "Update", Toast.LENGTH_LONG).show()
            viewModel.fetchPropertyList()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener() : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {
            when(item){
                is PropertyDTO -> {
                    Toast.makeText(requireActivity(), (item as PropertyDTO).propertyName, Toast.LENGTH_SHORT).show()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, PropertyItemControlsMenuTV(supportFragmentManager, item))
                        .commitNow()
                }
                is String -> {
                    when(item){
                        "Add new" -> {}
                        "Delete all" -> {
                            viewModel.clearDB()
                            viewModel.fetchPropertyList()
                        }
                        "Generate" -> {
                            for (i in 0..20){
                                viewModel.updateProperty(DataSamples.generatePropertyDTO().property)
                            }
                            viewModel.fetchPropertyList()
                        }
                        else -> {}
                    }
                }
            }
        }
    }
    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
//            if (item is PropertyDTO) {
//                (itemViewHolder!!.view as TextCardView).setGDPR(item.gdprEnabled)
//                (itemViewHolder!!.view as TextCardView).setCCPA(item.ccpaEnabled)
//            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        mBackgroundTimer?.cancel()
    }
    private fun startBackgroundTimer() {
        mBackgroundTimer?.cancel()
        mBackgroundTimer = Timer()
        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
    }

    private inner class UpdateBackgroundTask : TimerTask() {

        override fun run() {
//            mHandler.post { updateBackground(mBackgroundUri) }
        }
    }
    private inner class GridItemPresenter : Presenter() {
        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
            val view = TextView(parent.context)
            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
            view.isFocusable = true
            view.isFocusableInTouchMode = true
            view.setBackgroundColor(
                ContextCompat.getColor(
                    activity!!,
                    R.color.ic_launcher_background
                )
            )
            view.setTextColor(Color.WHITE)
            view.gravity = Gravity.CENTER
            return Presenter.ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
            (viewHolder.view as TextView).text = item as String
        }

        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
    }
}