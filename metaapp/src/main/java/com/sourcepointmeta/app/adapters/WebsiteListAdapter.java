package com.sourcepointmeta.app.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.SourcepointApp;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;

import java.util.List;

public class WebsiteListAdapter extends RecyclerView.Adapter<WebsiteListAdapter.WebsiteViewHolder> {

    private static final String TAG = "WebsiteListAdapter";
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private RecyclerViewClickListener mListener;
    private SwipeRevealLayout mOpenLayout;

    private List<Website> mWebsiteList;


    public void setWebsiteList(final List<Website> websiteList) {
        mWebsiteList = websiteList;
    }

    public void closeLayout() {
        if (mOpenLayout != null) {
            mOpenLayout.close(true);
        }
    }

    public WebsiteListAdapter(RecyclerViewClickListener listener) {
        this.mListener = listener;
        this.binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public WebsiteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_website_list, parent, false);
        return new WebsiteViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull WebsiteViewHolder viewHolder, int position) {

        Log.d(TAG, "inside onBindViewHolder");
        String name = mWebsiteList.get(position).getName();
        int accountID = mWebsiteList.get(position).getAccountID();
        String campaign = mWebsiteList.get(position).isStaging() ? "Staging" : "Public";
        String authId = mWebsiteList.get(position).getAuthId();
        List<TargetingParam> list = mWebsiteList.get(position).getTargetingParamList();
        StringBuilder keyValueListBuilder = new StringBuilder();

        for (TargetingParam targetingParam : list) {
            keyValueListBuilder = keyValueListBuilder.append(targetingParam.getKey());
            keyValueListBuilder.append(" : ");
            keyValueListBuilder.append(targetingParam.getValue());
            keyValueListBuilder.append(",\n");
        }



        binderHelper.bind(viewHolder.swipeRevealLayout, String.valueOf(mWebsiteList.get(position).getId()));
        viewHolder.websiteNameTextView.setText(name);
        viewHolder.websiteAccountID.setText("Account ID : " +accountID);
        viewHolder.websiteCampaign.setText("Campaign : "+campaign);

        if (!TextUtils.isEmpty(authId)){
            viewHolder.webSiteAuhtId.setText("AuthId : "+authId);
            viewHolder.webSiteAuhtId.setVisibility(View.VISIBLE);
        }else {
            viewHolder.webSiteAuhtId.setVisibility(View.GONE);
        }


        //implement swipe;isterner to get opened layout
        viewHolder.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {
            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                mOpenLayout = view;
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {
            }
        });

        viewHolder.setClickListeners();

        String keyValueList = "";
        if (!keyValueListBuilder.toString().isEmpty()) {
            keyValueList = keyValueListBuilder.toString().substring(0, keyValueListBuilder.length() - 2);
            viewHolder.websiteTargetingParam.setText(keyValueList);
            viewHolder.websiteTargetingParam.setVisibility(View.VISIBLE);
            Log.d(TAG, "View Visible : "+keyValueList);

        }else {
            viewHolder.websiteTargetingParam.setVisibility(View.GONE);
            Log.d(TAG, "View Gone : "+keyValueList);
            Log.d(TAG, "View Gone : "+mWebsiteList.get(position).getName());
        }


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull WebsiteViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mWebsiteList == null ? 0 : mWebsiteList.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class WebsiteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView websiteNameTextView , websiteAccountID ,websiteCampaign, webSiteAuhtId,websiteTargetingParam;
        private SwipeRevealLayout swipeRevealLayout;
        private View item_view;
        private TextView resetButton;
        private TextView editButton;
        private TextView deleteButton;
        private RecyclerViewClickListener mListener;

        WebsiteViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            mListener = listener;

            websiteNameTextView = itemView.findViewById(R.id.websiteNameTextView);
            websiteAccountID = itemView.findViewById(R.id.websiteAccountIDView);
            websiteCampaign = itemView.findViewById(R.id.websiteCampaignView);
            webSiteAuhtId = itemView.findViewById(R.id.websiteAuthIdView);
            websiteTargetingParam = itemView.findViewById(R.id.targetingParamView);
            swipeRevealLayout = itemView.findViewById(R.id.swipe_layout);
            item_view = itemView.findViewById(R.id.item_view);
            resetButton = itemView.findViewById(R.id.reset_button);
            editButton = itemView.findViewById(R.id.edit_button);
            deleteButton = itemView.findViewById(R.id.delete_button);

        }

        private void setClickListeners() {
            //set onClickListeners to desired view
            item_view.setOnClickListener(this);
            resetButton.setOnClickListener(this);
            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            //call to activity click listener
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
