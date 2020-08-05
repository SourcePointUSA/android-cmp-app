package com.sourcepointmeta.metaapp.adapters;

import android.support.annotation.NonNull;
import android.support.constraint.Group;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.sourcepointmeta.metaapp.R;
import com.sourcepointmeta.metaapp.models.Consents;

import java.util.List;

public class ConsentListRecyclerView extends RecyclerView.Adapter<ConsentListRecyclerView.ConsentViewHolder> {

    private List<Consents> mConsentsList;


    public ConsentListRecyclerView(List<Consents> consentsList){
        this.mConsentsList = consentsList;
    }

    public void setConsentList(List<Consents> consentsList){ this.mConsentsList = consentsList; }


    @NonNull
    @Override
    public ConsentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.vendor_purpose_consent_view, parent, false);
        return new ConsentViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsentViewHolder consentViewHolder, int position) {
        Consents consents= mConsentsList.get(position);
        consentViewHolder.bind(consents);
        consentViewHolder.setScaleAnimation(consentViewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return mConsentsList == null ? 0: mConsentsList.size();
    }

     static class ConsentViewHolder extends RecyclerView.ViewHolder {
        private TextView tvConsentIDText, tvConsentID;
        private TextView tvConsentHeader;
        private Group consentGroup;

         ConsentViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);

            tvConsentIDText = itemView.findViewById(R.id.tv_consentIDText);
             tvConsentID = itemView.findViewById(R.id.tv_consentID);
            tvConsentHeader = itemView.findViewById(R.id.tv_consentHeader);
            consentGroup = itemView.findViewById(R.id.consentGroup);

        }

        private void bind(Consents consents){
            if (consents.getType().equals("Header")){
                consentGroup.setVisibility(View.GONE);
                tvConsentHeader.setVisibility(View.VISIBLE);
                tvConsentHeader.setText(consents.getName());

            }else {
                consentGroup.setVisibility(View.VISIBLE);
                tvConsentHeader.setVisibility(View.GONE);
                tvConsentID.setText(consents.getId());
            }
        }

         private void setScaleAnimation(View view) {
             ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
             anim.setDuration(100);
             view.startAnimation(anim);
         }
    }
}
