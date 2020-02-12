package com.sourcepointmeta.app.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;

import java.util.List;

public class TargetingParamsAdapter extends RecyclerView.Adapter<TargetingParamsAdapter.TargetingParamsViewHolder> {

    private List<TargetingParam> mTargetingParamsList;
    private RecyclerViewClickListener mListener;


    public TargetingParamsAdapter(RecyclerViewClickListener listener) {
        this.mListener = listener;
    }

    public void setmTargetingParamsList(List<TargetingParam> targetingParamsList) {
        this.mTargetingParamsList = targetingParamsList;
    }


    @NonNull
    @Override
    public TargetingParamsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.targeting_param_row, parent, false);
        return new TargetingParamsViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TargetingParamsViewHolder targetingParamsViewHolder, int i) {

        TargetingParam targetingParam = mTargetingParamsList.get(i);
        targetingParamsViewHolder.bind(targetingParam);

    }

    @Override
    public int getItemCount() {
        return mTargetingParamsList == null ? 0 : mTargetingParamsList.size();
    }

    public static class TargetingParamsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvKey;
        private TextView btnDelete;
        private RecyclerViewClickListener mListener;
        private boolean isDetails;

        TargetingParamsViewHolder(View itemView, RecyclerViewClickListener listener) {
            super(itemView);

            mListener = listener;
            tvKey = itemView.findViewById(R.id.tv_targetingParam);
            btnDelete = itemView.findViewById(R.id.btn_deleteParam);
        }

        private void bind(TargetingParam param) {
            String keyValuePair = param.getKey() + " : " + param.getValue();
            tvKey.setText(keyValuePair);
            if (isDetails){
                btnDelete.setVisibility(View.GONE);
            }
            btnDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }
}
