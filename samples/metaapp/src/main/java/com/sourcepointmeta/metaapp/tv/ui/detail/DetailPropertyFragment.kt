package com.sourcepointmeta.metaapp.tv.ui.detail

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.widget.*
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv
import com.sourcepoint.cmplibrary.exception.CampaignType
import com.sourcepointmeta.metaapp.R
import com.sourcepointmeta.metaapp.data.localdatasource.Property
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign
import com.sourcepointmeta.metaapp.tv.ui.detail.model.PropDto
import com.sourcepointmeta.metaapp.tv.ui.detail.presenter.PropertyDescriptionPresenter
import java.util.*

class DetailPropertyFragment: DetailsSupportFragment() {

    companion object{
        const val TRANSITION_NAME = "t_for_transition"
        const val EXTRA_CARD = "card"
        const val ACTION_BUY: Long = 1
        const val ACTION_WISHLIST: Long = 2
        const val ACTION_RELATED: Long = 3
    }

    private fun getActionAdapter() = ArrayObjectAdapter().apply {
        add(
            Action(
                1,
                "mobile.property",
                "property name"
            )
        )
        add(
            Action(
                2,
                "22",
                "account Id"
            )
        )
    }

    private fun createDetailsOverviewRow(
        selectedVideo: PropDto,
        detailsAdapter: ArrayObjectAdapter
    ): DetailsOverviewRow {

        val context = requireContext()

        // 1
        val row = DetailsOverviewRow(selectedVideo).apply {
//            imageDrawable = ContextCompat.getDrawable(context, R.drawable.character_focused)
            actionsAdapter = getActionAdapter()
        }

        // 2
        val width = resources.getDimensionPixelSize(R.dimen.lb_playback_transport_hero_thumbs_width)
        val height = resources.getDimensionPixelSize(R.dimen.lb_playback_transport_hero_thumbs_width)

        // 3
//        loadDrawable(requireActivity(), selectedVideo.cardImageUrl, R.drawable.default_background, width, height)
//        { resource ->
//            row.imageDrawable = resource
//            // 4
//            detailsAdapter.notifyArrayItemRangeChanged(0, detailsAdapter.size())
//        }

        return row
    }

    private fun createDetailsOverviewRowPresenter(
        propDto: PropDto,
        actionHandler: (Action, PropDto) -> Unit
    ): FullWidthDetailsOverviewRowPresenter =
        // 1
        FullWidthDetailsOverviewRowPresenter(PropertyDescriptionPresenter(requireContext())).apply {
            // 2
//            backgroundColor =
//                ContextCompat.getColor(requireContext(), R.color.selected_background)

            // 3
            val sharedElementHelper = FullWidthDetailsOverviewSharedElementHelper()
            sharedElementHelper.setSharedElementEnterTransition(
                activity,
                "action_prop"
            )
            setListener(sharedElementHelper)
            isParticipatingEntranceTransition = true

            // 4
            onActionClickedListener = OnActionClickedListener {
                actionHandler(it, propDto)
            }
        }

    private fun createPresenterSelector(videoItem: PropDto) =
        ClassPresenterSelector().apply {
            // 1
            addClassPresenter(
                DetailsOverviewRow::class.java,
                createDetailsOverviewRowPresenter(videoItem, {
                        Action, PropDto ->
                    println(Action)
                })
            )

            // 2
            addClassPresenter(
                ListRow::class.java,
                ListRowPresenter()
            )
        }


    val helper by lazy {
        FullWidthDetailsOverviewSharedElementHelper().apply {
            this.setSharedElementEnterTransition(requireActivity(), DetailViewExampleFragment.TRANSITION_NAME)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rowPresenter: FullWidthDetailsOverviewRowPresenter = object : FullWidthDetailsOverviewRowPresenter(
            DetailsDescriptionPresenter(requireActivity())
        ) {
            override fun createRowViewHolder(parent: ViewGroup): RowPresenter.ViewHolder {
                // Customize Actionbar and Content by using custom colors.
                val viewHolder = super.createRowViewHolder(parent)
                val actionsView = viewHolder.view.findViewById<View>(R.id.details_overview_actions_background)
                actionsView.setBackgroundColor(activity!!.resources.getColor(R.color.cardview_dark_background))
                val detailsView = viewHolder.view.findViewById<View>(R.id.details_frame)
                detailsView.setBackgroundColor(
                    resources.getColor(R.color.cardview_dark_background)
                )
                return viewHolder
            }
        }

        rowPresenter.setListener(helper)
        rowPresenter.isParticipatingEntranceTransition = false
        prepareEntranceTransition()

        val shadowDisabledRowPresenter = ListRowPresenter()
        shadowDisabledRowPresenter.shadowEnabled = false

        // Setup PresenterSelector to distinguish between the different rows.

        // Setup PresenterSelector to distinguish between the different rows.
        val rowPresenterSelector = ClassPresenterSelector()
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, rowPresenter)
        rowPresenterSelector.addClassPresenter(CardListRow::class.java, shadowDisabledRowPresenter)
        rowPresenterSelector.addClassPresenter(ListRow::class.java, ListRowPresenter())

        val mRowsAdapter = ArrayObjectAdapter(rowPresenterSelector)

        //        detailsOverview.setImageDrawable(getResources().getDrawable(imageResId, null));
        val actionAdapter = ArrayObjectAdapter()

        val prop = Property(
            "cmaurer.fire.tv",
            22,
            680497L,
            null,
            "App",
            false,
            LinkedList(),
            3000L,
            null,
            "EN",
            null,
            setOf(StatusCampaign("cmaurer.fire.tv", CampaignType.GDPR, true)),
            CampaignsEnv.PUBLIC,
            1000,
            null,
            false,
            null,
            false
        )


        // Setup action and detail row.
        val detailsOverview = DetailsOverviewRow(prop)

        val mActionBuy = Action(ACTION_BUY, "Buy")
        val mActionWishList = Action(ACTION_WISHLIST, "Wishlist")
        val mActionRelated = Action(ACTION_RELATED, "Related")

        actionAdapter.add(mActionBuy)
        actionAdapter.add(mActionWishList)
        actionAdapter.add(mActionRelated)

        val p = PropDto(
            name = "carmelo.iriti",
            accId = 22,
            timeout = 3000
        )

        adapter = ArrayObjectAdapter(createPresenterSelector(p)).apply {
            add(createDetailsOverviewRow(p, this))
//            add(createDetailsOverviewRow(p1, this))
        }
        Handler().postDelayed({ startEntranceTransition() }, 500)

    }


}