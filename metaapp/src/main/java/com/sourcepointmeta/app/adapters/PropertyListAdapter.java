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
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;

import java.util.List;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder> {

    private static final String TAG = "PropertyListAdapter";
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private RecyclerViewClickListener mListener;
    private SwipeRevealLayout mOpenLayout;

    private List<Property> mPropertyList;


    public void setPropertyList(final List<Property> propertyList) {
        mPropertyList = propertyList;
    }

    public void closeLayout() {
        if (mOpenLayout != null) {
            mOpenLayout.close(true);
        }
    }

    public PropertyListAdapter(RecyclerViewClickListener listener) {
        this.mListener = listener;
        this.binderHelper.setOpenOnlyOne(true);
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_property_list, parent, false);
        return new PropertyViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder viewHolder, int position) {

        Log.d(TAG, "inside onBindViewHolder");
        String name = mPropertyList.get(position).getProperty();
        int accountID = mPropertyList.get(position).getAccountID();
        String campaign = mPropertyList.get(position).isStaging() ? "Staging" : "Public";
        String authId = mPropertyList.get(position).getAuthId();
        List<TargetingParam> list = mPropertyList.get(position).getTargetingParamList();
        StringBuilder keyValueListBuilder = new StringBuilder();

        for (TargetingParam targetingParam : list) {
            keyValueListBuilder = keyValueListBuilder.append(targetingParam.getKey());
            keyValueListBuilder.append(" : ");
            keyValueListBuilder.append(targetingParam.getValue());
            keyValueListBuilder.append(",\n");
        }



        binderHelper.bind(viewHolder.swipeRevealLayout, String.valueOf(mPropertyList.get(position).getId()));
        viewHolder.propertyNameTextView.setText(name);
        viewHolder.propertyAccountID.setText("Account ID : " +accountID);
        viewHolder.propertyCampaign.setText("Campaign : "+campaign);

        if (!TextUtils.isEmpty(authId)){
            viewHolder.propertyAuhtId.setText("AuthId : "+authId);
            viewHolder.propertyAuhtId.setVisibility(View.VISIBLE);
        }else {
            viewHolder.propertyAuhtId.setVisibility(View.GONE);
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
            viewHolder.propertyTargetingParam.setText(keyValueList);
            viewHolder.propertyTargetingParam.setVisibility(View.VISIBLE);
            Log.d(TAG, "View Visible : "+keyValueList);

        }else {
            viewHolder.propertyTargetingParam.setVisibility(View.GONE);
            Log.d(TAG, "View Gone : "+keyValueList);
            Log.d(TAG, "View Gone : "+ mPropertyList.get(position).getProperty());
        }


    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PropertyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return mPropertyList == null ? 0 : mPropertyList.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class PropertyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView propertyNameTextView, propertyAccountID, propertyCampaign, propertyAuhtId, propertyTargetingParam;
        private SwipeRevealLayout swipeRevealLayout;
        private View item_view;
        private TextView resetButton;
        private TextView editButton;
        private TextView deleteButton;
        private RecyclerViewClickListener mListener;

        PropertyViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            mListener = listener;

            propertyNameTextView = itemView.findViewById(R.id.propertyNameTextView);
            propertyAccountID = itemView.findViewById(R.id.propertyAccountIDView);
            propertyCampaign = itemView.findViewById(R.id.propertyCampaignView);
            propertyAuhtId = itemView.findViewById(R.id.propertyAuthIdView);
            propertyTargetingParam = itemView.findViewById(R.id.targetingParamView);
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
