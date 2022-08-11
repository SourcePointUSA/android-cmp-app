/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.sourcepointmeta.metaapp.tv.ui.detail;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.leanback.app.DetailsFragment;
import androidx.leanback.app.DetailsFragmentBackgroundController;
import androidx.leanback.app.DetailsSupportFragment;
import androidx.leanback.app.DetailsSupportFragmentBackgroundController;
import com.sourcepoint.cmplibrary.data.network.util.CampaignsEnv;
import com.sourcepoint.cmplibrary.exception.CampaignType;
import com.sourcepointmeta.metaapp.R;
import com.sourcepointmeta.metaapp.data.localdatasource.MetaTargetingParam;
import com.sourcepointmeta.metaapp.data.localdatasource.Property;
import com.sourcepointmeta.metaapp.data.localdatasource.StatusCampaign;
import com.sourcepointmeta.metaapp.tv.ui.detail.model.Card;
import com.sourcepointmeta.metaapp.tv.ui.detail.model.DetailedCard;
import com.sourcepointmeta.metaapp.tv.ui.detail.CardListRow;
import com.sourcepointmeta.metaapp.tv.ui.detail.Utils;
import androidx.leanback.widget.*;
import com.google.gson.Gson;
import com.sourcepointmeta.metaapp.ui.demo.DemoActivity;

import java.util.LinkedHashSet;
import java.util.LinkedList;

/**
 * Displays a card with more details using a {@link DetailsFragment}.
 */
public class DetailViewExampleFragment extends DetailsSupportFragment implements OnItemViewClickedListener,
        OnItemViewSelectedListener {

    public static final String TRANSITION_NAME = "t_for_transition";
    public static final String EXTRA_CARD = "card";

    private static final long ACTION_BUY = 1;
    private static final long ACTION_WISHLIST = 2;
    private static final long ACTION_RELATED = 3;

    private Action mActionBuy;
    private Action mActionWishList;
    private Action mActionRelated;
    private ArrayObjectAdapter mRowsAdapter;
    private final DetailsSupportFragmentBackgroundController mDetailsBackground =
            new  DetailsSupportFragmentBackgroundController(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
        setupEventListeners();
    }

    private void setupUi() {
        // Load the card we want to display from a JSON resource. This JSON data could come from
        // anywhere in a real world app, e.g. a server.
        String json = Utils
                .inputStreamToString(getResources().openRawResource(R.raw.detail_example));
        DetailedCard data = new Gson().fromJson(json, DetailedCard.class);

        // Setup fragment
        setTitle("Title");

        FullWidthDetailsOverviewRowPresenter rowPresenter = new FullWidthDetailsOverviewRowPresenter(
                new DetailsDescriptionPresenter(getActivity())) {

            @Override
            protected RowPresenter.ViewHolder createRowViewHolder(ViewGroup parent) {
                // Customize Actionbar and Content by using custom colors.
                RowPresenter.ViewHolder viewHolder = super.createRowViewHolder(parent);

                View actionsView = viewHolder.view.
                        findViewById(R.id.details_overview_actions_background);
                actionsView.setBackgroundColor(getActivity().getResources().
                        getColor(R.color.cardview_dark_background));

                View detailsView = viewHolder.view.findViewById(R.id.details_frame);
                detailsView.setBackgroundColor(
                        getResources().getColor(R.color.cardview_dark_background));
                return viewHolder;
            }
        };

        FullWidthDetailsOverviewSharedElementHelper mHelper = new FullWidthDetailsOverviewSharedElementHelper();
        mHelper.setSharedElementEnterTransition(getActivity(), TRANSITION_NAME);
        rowPresenter.setListener(mHelper);
        rowPresenter.setParticipatingEntranceTransition(false);
        prepareEntranceTransition();

        ListRowPresenter shadowDisabledRowPresenter = new ListRowPresenter();
        shadowDisabledRowPresenter.setShadowEnabled(false);

        // Setup PresenterSelector to distinguish between the different rows.
        ClassPresenterSelector rowPresenterSelector = new ClassPresenterSelector();
        rowPresenterSelector.addClassPresenter(DetailsOverviewRow.class, rowPresenter);
        rowPresenterSelector.addClassPresenter(CardListRow.class, shadowDisabledRowPresenter);
        rowPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(rowPresenterSelector);

        // Setup action and detail row.
        DetailsOverviewRow detailsOverview = new DetailsOverviewRow(data);
        int imageResId = data.getLocalImageResourceId(getActivity());

        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null && extras.containsKey(EXTRA_CARD)) {
            imageResId = extras.getInt(EXTRA_CARD, imageResId);
        }
//        detailsOverview.setImageDrawable(getResources().getDrawable(imageResId, null));
        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();

        mActionBuy = new Action(ACTION_BUY, "Buy" + data.getPrice());
        mActionWishList = new Action(ACTION_WISHLIST, "Wishlist");
        mActionRelated = new Action(ACTION_RELATED, "Related");

        actionAdapter.add(mActionBuy);
        actionAdapter.add(mActionWishList);
        actionAdapter.add(mActionRelated);
//        detailsOverview.setActionsAdapter(actionAdapter);
//        mRowsAdapter.add(detailsOverview);
        mRowsAdapter.add(actionAdapter);

//        // Setup related row.
//        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(
//                new CardPresenterSelector(getActivity()));
//        for (Card characterCard : data.getCharacters()) listRowAdapter.add(characterCard);
//        HeaderItem header = new HeaderItem(0, "related");
//        mRowsAdapter.add(new CardListRow(header, listRowAdapter, null));
//
//        // Setup recommended row.
//        listRowAdapter = new ArrayObjectAdapter(new CardPresenterSelector(getActivity()));
//        for (Card card : data.getRecommended()) listRowAdapter.add(card);
//        header = new HeaderItem(1, "recommended");
//        mRowsAdapter.add(new ListRow(header, listRowAdapter));

        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startEntranceTransition();
            }
        }, 500);
//        initializeBackground(data);
    }

    private void initializeBackground(DetailedCard data) {
        mDetailsBackground.enableParallax();
        mDetailsBackground.setCoverBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.background_canyon));
    }

    private void setupEventListeners() {
        setOnItemViewSelectedListener(this);
        setOnItemViewClickedListener(this);
    }

    Property getProp(){
        StatusCampaign statusCamp = new StatusCampaign("cmaurer.fire.tv", CampaignType.GDPR, true);
        LinkedHashSet ls = new LinkedHashSet<StatusCampaign>();
        ls.add(statusCamp);
        return new Property(
                "cmaurer.fire.tv",
                22,
                680497L,
                null,
                "App",
                false,
                new LinkedList<MetaTargetingParam>(),
                3000L,
                null,
                "EN",
                null,
                ls,
                CampaignsEnv.PUBLIC,
                1000,
                null,
                false,
                null,
                false
        );
    }

    @Override
    public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                              RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (!(item instanceof Action)) return;
        Action action = (Action) item;

        if (action.getId() == ACTION_RELATED) {
            setSelectedPosition(1);
        } else {
            Toast.makeText(getActivity(), "cicked", Toast.LENGTH_LONG)
                    .show();
            runDemo();

        }
    }

    public void runDemo() {
        StatusCampaign statusCamp = new StatusCampaign("cmaurer.fire.tv", CampaignType.GDPR, true);
        LinkedHashSet ls = new LinkedHashSet<StatusCampaign>();
        ls.add(statusCamp);
        Property property = new Property(
                "cmaurer.fire.tv",
                22,
                680497L,
                null,
                "App",
                false,
                new LinkedList<MetaTargetingParam>(),
                3000L,
                null,
                "EN",
                null,
                ls,
                CampaignsEnv.PUBLIC,
                1000,
                null,
                false,
                null,
                false
        );
        Bundle bundle = new Bundle();
        bundle.putString("property_name", "cmaurer.fire.tv");
        Intent i = new Intent(getActivity(), DemoActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }

    @Override
    public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                               RowPresenter.ViewHolder rowViewHolder, Row row) {
        if (mRowsAdapter.indexOf(row) > 0) {
            int backgroundColor = getResources().getColor(R.color.cardview_dark_background);
            getView().setBackgroundColor(backgroundColor);
        } else {
            getView().setBackground(null);
        }
    }
}
