package com.sourcepointmeta.app.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.SourcepointApp;
import com.sourcepointmeta.app.adapters.TargetingParamsAdapter;
import com.sourcepointmeta.app.common.Constants;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;

import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class SiteDetailsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_site_details);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        mTitle.setText(getString(R.string.site_details_title));

        Website website = null;
        Bundle data = getIntent().getExtras();
        if (data != null)
            website = data.getParcelable(Constants.WEBSITE);

        if (website != null) {
            setupUI(website);
        } else {
            showAlertDialog();
        }


    }

    private void setupUI(Website website) {

        TextInputEditText mAccountID = findViewById(R.id.tvAccountID);
        TextInputEditText mSiteName = findViewById(R.id.tvSiteName);
        TextView mNoTargetingParamMessage = findViewById(R.id.tv_noTargetingParams);

        SwitchCompat mStagingSwitch = findViewById(R.id.toggleStaging);

        mAccountID.setText(String.valueOf(website.getAccountID()));
        mSiteName.setText(website.getName());
        mStagingSwitch.setChecked(website.isStaging());
        mStagingSwitch.setClickable(false);

        RecyclerView recyclerView = findViewById(R.id.targetingParamsRecycleView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        List<TargetingParam> targetingParamList = website.getTargetingParamList();
        if (targetingParamList.size() > 0) {
            mNoTargetingParamMessage.setVisibility(View.GONE);
        } else {
            mNoTargetingParamMessage.setVisibility(View.VISIBLE);
        }
        TargetingParamsAdapter targetingParamsAdapter = new TargetingParamsAdapter(getRecyclerViewClickListener(), true);
        targetingParamsAdapter.setmTargetingParamsList(targetingParamList);
        recyclerView.setAdapter(targetingParamsAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private RecyclerViewClickListener getRecyclerViewClickListener() {
        return new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }
        };
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SiteDetailsActivity.this)
                .setMessage(getResources().getString(R.string.site_details_not_available))
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.cancel();
                    SiteDetailsActivity.this.finish();
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }
}
