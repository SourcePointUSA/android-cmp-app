package com.sourcepointmeta.metaapp.tv

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.*
import androidx.lifecycle.map
import androidx.recyclerview.widget.ItemTouchHelper
import com.sourcepointmeta.metaapp.BuildConfig
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.tv.cards.CardPresenter
import com.sourcepointmeta.metaapp.tv.samples.DataSamples
import com.sourcepointmeta.metaapp.tv.samples.MovieSample
import com.sourcepointmeta.metaapp.ui.BaseState
import java.util.*

import com.sourcepointmeta.metaapp.ui.component.PropertyAdapter
import com.sourcepointmeta.metaapp.ui.component.SwipeToDeleteCallback
import com.sourcepointmeta.metaapp.ui.component.toPropertyDTO
import com.sourcepointmeta.metaapp.ui.propertylist.PropertyListViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.qualifier.named


class PropertyListFragmentTV() : BrowseSupportFragment() {
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
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(requireActivity(), R.color.blue_link_600)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(requireActivity(), R.color.blue_link_200)
    }
    private fun loadRows() {
//        val list = DataSamples.list

        viewModel.liveData.observe(viewLifecycleOwner) { propertyBaseState ->
            (propertyBaseState as BaseState.StatePropertyList).propertyList
                .map { p -> p.toPropertyDTO() }.let { propertyList ->
                    val rowsAdapter = ArrayObjectAdapter(object : ListRowPresenter() {
                        override fun isUsingDefaultListSelectEffect() = false
                    }.apply { shadowEnabled = false })
                    val cardPresenter = context?.let { CardPresenter(it) }

                    for (dataCategory in 0 until DataSamples.DATA_CATEGORY.size) {
                        // TODO: hardcode corresponding with DataSamples.DATA_CATEGORY
                        when(DataSamples.DATA_CATEGORY[dataCategory]){
                            "GDPR+CCPA" -> {
                                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                                for (j in propertyList.indices) {
                                    if(propertyList[j].gdprEnabled && propertyList[j].ccpaEnabled)
                                        listRowAdapter.add(propertyList[j])
                                }
                                val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                rowsAdapter.add(ListRow(header, listRowAdapter))
                            }
                            "GDPR" -> {
                                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                                for (j in propertyList.indices) {
                                    if(propertyList[j].gdprEnabled && !propertyList[j].ccpaEnabled)
                                        listRowAdapter.add(propertyList[j])
                                }
                                val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                rowsAdapter.add(ListRow(header, listRowAdapter))
                            }
                            "CCPA" -> {
                                val listRowAdapter = ArrayObjectAdapter(cardPresenter)
                                for (j in propertyList.indices) {
                                    if(!propertyList[j].gdprEnabled && propertyList[j].ccpaEnabled)
                                        listRowAdapter.add(propertyList[j])
                                }
                                val header = HeaderItem(dataCategory.toLong(), DataSamples.DATA_CATEGORY[dataCategory])
                                rowsAdapter.add(ListRow(header, listRowAdapter))
                            }
                        }
                    }

                    val gridHeader = HeaderItem(NUM_ROWS.toLong(), "PREFERENCES")

                    val mGridPresenter = GridItemPresenter()
                    val gridRowAdapter = ArrayObjectAdapter(mGridPresenter)
                    rowsAdapter.add(ListRow(gridHeader, gridRowAdapter))

                    adapter = rowsAdapter
                }
        }

    }
    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(requireActivity(), "Update", Toast.LENGTH_LONG)
                .show()
            viewModel.fetchPropertyList()
        }

        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }

    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {

            /*if (item is MovieSample) {
                Log.d(TAG, "Item: " + item.toString())
                val intent = Intent(activity!!, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                startActivity(intent, bundle)
            } else if (item is String) {
                if (item.contains(getString(R.string.error_fragment))) {
                    val intent = Intent(activity!!, BrowseErrorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(activity!!, item, Toast.LENGTH_SHORT).show()
                }
            }*/
        }
    }
    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is MovieSample) {
                mBackgroundUri = item.backgroundImageUrl
                startBackgroundTimer()
            }
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