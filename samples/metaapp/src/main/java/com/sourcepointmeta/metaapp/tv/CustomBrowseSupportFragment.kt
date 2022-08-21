package com.sourcepointmeta.metaapp.tv

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.HeadersSupportFragment
import androidx.leanback.transition.TransitionHelper
import androidx.leanback.util.StateMachine
import androidx.leanback.widget.BrowseFrameLayout
import androidx.leanback.widget.ObjectAdapter
import androidx.leanback.widget.PageRow
import androidx.leanback.widget.ScaleFrameLayout

//open class CustomBrowseSupportFragment: BrowseSupportFragment() {
//    // StateMachine can be used only in package 'leanback'
//    @SuppressLint("RestrictedApi")
//    val mStateMachine = StateMachine()
//
//
//    private val mMainFragmentAdapterRegistry = MainFragmentAdapterRegistry()
//    var mCanShowHeaders = true
//    var mPageRow: Any? = null
//
//
//    private val IS_PAGE_ROW = "isPageRow"
//    private val CURRENT_SELECTED_POSITION = "currentSelectedPosition"
//
//
//    var mHeadersSupportFragment: HeadersSupportFragment? = null
//    var mMainFragment: Fragment? = null
//    private var mSelectedPosition = -1
//    var mIsPageRow = false
//    var mMainFragmentAdapter: MainFragmentAdapter<*>? = null
//    private val mAdapter: ObjectAdapter? = null
//
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (childFragmentManager.findFragmentById(androidx.leanback.R.id.scale_frame) == null) {
//            mHeadersSupportFragment = onCreateHeadersSupportFragment()
//            createMainFragment(mAdapter, mSelectedPosition)
//            val ft = childFragmentManager.beginTransaction().replace(androidx.leanback.R.id.browse_headers_dock, mHeadersSupportFragment!!)
//            if (mMainFragment != null) {
//                ft.replace(androidx.leanback.R.id.scale_frame, mMainFragment!!)
//            } else {
//                mMainFragmentAdapter = MainFragmentAdapter(null)
//                mMainFragmentAdapter?.let { it.fragmentHost = FragmentHostImpl() }
//            }
//            ft.commit()
//        } else {
//            mHeadersSupportFragment = childFragmentManager
//                .findFragmentById(androidx.leanback.R.id.browse_headers_dock) as HeadersSupportFragment?
//            mMainFragment = childFragmentManager.findFragmentById(androidx.leanback.R.id.scale_frame)
//            mIsPageRow = (savedInstanceState != null
//                    && savedInstanceState.getBoolean(IS_PAGE_ROW, false))
//            mSelectedPosition =
//                savedInstanceState?.getInt(CURRENT_SELECTED_POSITION, 0) ?: 0
//            setMainFragmentAdapter()
//        }
//        mHeadersSupportFragment.setHeadersGone(!mCanShowHeaders)
//        if (mHeaderPresenterSelector != null) {
//            mHeadersSupportFragment.presenterSelector = mHeaderPresenterSelector
//        }
//        mHeadersSupportFragment.adapter = mAdapter
//        mHeadersSupportFragment.setOnHeaderViewSelectedListener(mHeaderViewSelectedListener)
//        mHeadersSupportFragment.setOnHeaderClickedListener(mHeaderClickedListener)
//        val root = inflater.inflate(androidx.leanback.R.layout.lb_browse_fragment, container, false)
//        progressBarManager.setRootView(root as ViewGroup)
//        mBrowseFrame = root.findViewById<View>(androidx.leanback.R.id.browse_frame) as BrowseFrameLayout
//        mBrowseFrame.onChildFocusListener = mOnChildFocusListener
//        mBrowseFrame.onFocusSearchListener = mOnFocusSearchListener
//        installTitleView(inflater, mBrowseFrame, savedInstanceState)
//        mScaleFrameLayout = root.findViewById<View>(androidx.leanback.R.id.scale_frame) as ScaleFrameLayout
//        mScaleFrameLayout.pivotX = 0f
//        mScaleFrameLayout.pivotY = mContainerListAlignTop.toFloat()
//        if (mBrandColorSet) {
//            mHeadersSupportFragment.setBackgroundColor(mBrandColor)
//        }
//        mSceneWithHeaders = TransitionHelper.createScene(
//            mBrowseFrame
//        ) { showHeaders(true) }
//        mSceneWithoutHeaders = TransitionHelper.createScene(
//            mBrowseFrame
//        ) { showHeaders(false) }
//        mSceneAfterEntranceTransition = TransitionHelper.createScene(
//            mBrowseFrame
//        ) { setEntranceTransitionEndState() }
//        return root
//    }
//
//    protected fun createMainFragment(adapter: ObjectAdapter?, position: Int): Boolean {
//        var position = position
//        var item: Any? = null
//        if (!mCanShowHeaders) {
//            // when header is disabled, we can decide to use RowsSupportFragment even no data.
//        } else if (adapter == null || adapter.size() == 0) {
//            return false
//        } else {
//            if (position < 0) {
//                position = 0
//            } else require(position < adapter.size()) {
//                String.format(
//                    "Invalid position %d requested",
//                    position
//                )
//            }
//            item = adapter[position]
//        }
//        val oldIsPageRow = mIsPageRow
//        val oldPageRow = mPageRow
//        mIsPageRow = mCanShowHeaders && item is PageRow
//        mPageRow = if (mIsPageRow) item else null
//        val swap: Boolean
//        swap = if (mMainFragment == null) {
//            true
//        } else {
//            if (oldIsPageRow) {
//                if (mIsPageRow) {
//                    if (oldPageRow == null) {
//                        // fragment is restored, page row object not yet set, so just set the
//                        // mPageRow object and there is no need to replace the fragment
//                        false
//                    } else {
//                        // swap if page row object changes
//                        oldPageRow !== mPageRow
//                    }
//                } else {
//                    true
//                }
//            } else {
//                mIsPageRow
//            }
//        }
//        if (swap) {
//            mMainFragment = mMainFragmentAdapterRegistry.createFragment(item)
//            require(mMainFragment is MainFragmentAdapterProvider) { "Fragment must implement MainFragmentAdapterProvider" }
//            setMainFragmentAdapter()
//        }
//        return swap
//    }
//    open fun setMainFragmentAdapter() {
//        mMainFragmentAdapter = (mMainFragment as MainFragmentAdapterProvider).mainFragmentAdapter
//        mMainFragmentAdapter?.let{ it.setFragmentHost(BrowseSupportFragment.FragmentHostImpl()) }
//        if (!mIsPageRow) {
//            if (mMainFragment is MainFragmentRowsAdapterProvider) {
//                setMainFragmentRowsAdapter(
//                    (mMainFragment as MainFragmentRowsAdapterProvider)
//                        .mainFragmentRowsAdapter
//                )
//            } else {
//                setMainFragmentRowsAdapter(null)
//            }
//            mIsPageRow = mMainFragmentRowsAdapter == null
//        } else {
//            setMainFragmentRowsAdapter(null)
//        }
//    }
//
//
//    open class MainFragmentAdapter<T : Fragment?>(val fragment: T) {
//        /**
//         * Returns whether row scaling is enabled.
//         */
//        /**
//         * Sets the row scaling property.
//         */
//        var isScalingEnabled = false
//        private var mFragmentHost: CustomBrowseSupportFragment.FragmentHostImpl? = null
//
//        /**
//         * Returns whether its scrolling.
//         */
//        open val isScrolling: Boolean
//            get() = false
//
//        /**
//         * Set the visibility of titles/hover card of browse rows.
//         */
//        open fun setExpand(expand: Boolean) {}
//
//        /**
//         * For rows that willing to participate entrance transition,  this function
//         * hide views if afterTransition is true,  show views if afterTransition is false.
//         */
//        open fun setEntranceTransitionState(state: Boolean) {}
//
//        /**
//         * Sets the window alignment and also the pivots for scale operation.
//         */
//        open fun setAlignment(windowAlignOffsetFromTop: Int) {}
//
//        /**
//         * Callback indicating transition prepare start.
//         */
//        open fun onTransitionPrepare(): Boolean {
//            return false
//        }
//
//        /**
//         * Callback indicating transition start.
//         */
//        open fun onTransitionStart() {}
//
//        /**
//         * Callback indicating transition end.
//         */
//        open fun onTransitionEnd() {}
//
//        fun getFragmentHost(): FragmentHost = mFragmentHost!!
//
//        private fun setFragmentHost(fragmentHost: CustomBrowseSupportFragment.FragmentHostImpl?) {
//            mFragmentHost = fragmentHost
//        }
//    }
//
//
//    /**
//     * Default implementation of [FragmentHost] that is used only by
//     * [BrowseSupportFragment].
//     */
//    inner private class FragmentHostImpl constructor() : FragmentHost {
//        /**
//         * Event for [.getMainFragment] view is created, it's additional requirement to execute
//         * [.onEntranceTransitionPrepare].
//         */
//        val EVT_MAIN_FRAGMENT_VIEW_CREATED = StateMachine.Event("mainFragmentViewCreated")
//
//        /**
//         * Event that data for the screen is ready, this is additional requirement to launch entrance
//         * transition.
//         */
//        val EVT_SCREEN_DATA_READY = StateMachine.Event("screenDataReady")
//
//        var mShowTitleView = true
//        @SuppressLint("RestrictedApi")
//        override fun notifyViewCreated(fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<*>?) {
//            mStateMachine.fireEvent(EVT_MAIN_FRAGMENT_VIEW_CREATED)
//            if (!mIsPageRow) {
//                // If it's not a PageRow: it's a ListRow, so we already have data ready.
//                mStateMachine.fireEvent(EVT_SCREEN_DATA_READY)
//            }
//        }
//
//        @SuppressLint("RestrictedApi")
//        override fun notifyDataReady(fragmentAdapter: BrowseSupportFragment.MainFragmentAdapter<*>?) {
//            // If fragment host is not the currently active fragment (in BrowseSupportFragment), then
//            // ignore the request.
//            if (mMainFragmentAdapter == null || mMainFragmentAdapter!!.getFragmentHost() !== this) {
//                return
//            }
//
//            // We only honor showTitle request for PageRows.
//            if (!mIsPageRow) {
//                return
//            }
//            mStateMachine.fireEvent(EVT_SCREEN_DATA_READY)
//        }
//
//        override fun showTitleView(show: Boolean) {
//            mShowTitleView = show
//
//            // If fragment host is not the currently active fragment (in BrowseSupportFragment), then
//            // ignore the request.
//            if (mMainFragmentAdapter == null || mMainFragmentAdapter!!.getFragmentHost() !== this) {
//                return
//            }
//
//            // We only honor showTitle request for PageRows.
//            if (!mIsPageRow) {
//                return
//            }
//            updateTitleViewVisibility()
//        }
//    }
//
//
//}