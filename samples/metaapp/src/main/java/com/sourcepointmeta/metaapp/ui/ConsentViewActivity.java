package com.sourcepointmeta.metaapp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sourcepoint.gdpr_cmplibrary.ConsentLibBuilder;
import com.sourcepoint.gdpr_cmplibrary.GDPRConsentLib;
import com.sourcepoint.gdpr_cmplibrary.GDPRUserConsent;
import com.sourcepoint.gdpr_cmplibrary.MessageLanguage;
import com.sourcepoint.gdpr_cmplibrary.NativeMessage;
import com.sourcepoint.gdpr_cmplibrary.NativeMessageAttrs;
import com.sourcepoint.gdpr_cmplibrary.PrivacyManagerTab;
import com.sourcepointmeta.metaapp.R;
import com.sourcepointmeta.metaapp.SourcepointApp;
import com.sourcepointmeta.metaapp.adapters.ConsentListRecyclerView;
import com.sourcepointmeta.metaapp.common.Constants;
import com.sourcepointmeta.metaapp.database.entity.Property;
import com.sourcepointmeta.metaapp.database.entity.TargetingParam;
import com.sourcepointmeta.metaapp.models.Consents;
import com.sourcepointmeta.metaapp.repository.PropertyListRepository;
import com.sourcepointmeta.metaapp.utility.Util;
import com.sourcepointmeta.metaapp.viewmodel.ConsentViewViewModel;
import com.sourcepointmeta.metaapp.viewmodel.ViewModelUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class ConsentViewActivity extends BaseActivity<ConsentViewViewModel> {

    private final String TAG = "ConsentViewActivity";
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;

    private List<Consents> mVendorConsents = new ArrayList<>();
    private List<Consents> mPurposeConsents = new ArrayList<>();
    private List<Consents> mConsentList = new ArrayList<>();

    private TextInputEditText mConsentUUID;
    private TextInputEditText mEUConsent;
    private ConsentListRecyclerView mConsentListRecyclerAdapter;
    private TextView mTitle, mConsentNotAvailable;
    private SharedPreferences preferences;
    private ConstraintLayout mConstraintLayout;

    private ViewGroup mMainViewGroup;

    private CountDownTimer mCountDownTimer = null;
    private boolean isPMLoaded = false;
    private boolean isError = false;

    private void showMessageWebView(View view) {
        view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));
        view.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        view.bringToFront();
        view.requestLayout();
        if (view != null && view.getParent() != null)
            mMainViewGroup.removeView(view);
        mMainViewGroup.addView(view);
    }

    private void removeWebView(View view) {
        if (view != null && view.getParent() != null)
            mMainViewGroup.removeView(view);
    }

    private GDPRConsentLib buildConsentLib(Property property, Activity activity) {
        ConsentLibBuilder consentLibBuilder = GDPRConsentLib.newBuilder(property.getAccountID(), property.getProperty(), property.getPropertyID(), property.getPmID(), activity)
                .setStagingCampaign(property.isStaging())
                .setMessageTimeOut(30000)
                .setOnConsentUIReady(view -> {
                            getSupportActionBar().hide();
                            isPMLoaded = true;
                            cancelCounter();
                            hideProgressBar();
                            Log.i(TAG, "The message is about to be shown.");
                            showMessageWebView(view);
                        }
                ).setOnConsentUIFinished(view -> {
                    getSupportActionBar().show();
                    Log.i(TAG, "The message is removed.");
                    removeWebView(view);
                })
                .setOnConsentReady(userConsent -> {
                    Log.i(TAG, "setOnConsentReady called.");
                    mConsentList.clear();
                    mVendorConsents.clear();
                    mPurposeConsents.clear();

                    runOnUiThread(this::showProgressBar);
                    getConsentsFromConsentLib(userConsent);
                    Log.d(TAG, "OnConsentReady");
                    runOnUiThread(() -> {
                        showPropertyDebugInfo();
                    });
                })
                .setOnError(error -> {
                    hideProgressBar();
                    Log.d(TAG, "setOnError");
                    isError = true;
                    showAlertDialog("" + error.consentLibErrorMessage);
                    Log.d(TAG, "Something went wrong: ", error);
                });

        //get and set targeting param
        List<TargetingParam> list = property.getTargetingParamList();//getTargetingParamList(property);
        for (TargetingParam tps : list) {
            consentLibBuilder.setTargetingParam(tps.getKey(), tps.getValue());
        }

        if (!TextUtils.isEmpty(property.getAuthId())) {
            consentLibBuilder.setAuthId(property.getAuthId());
        } else {
            Log.d(TAG, "AuthID Not available : " + property.getAuthId());
        }

        if (!TextUtils.isEmpty(property.getMessageLanguage())) {
            consentLibBuilder.setMessageLanguage(MessageLanguage.findByName(property.getMessageLanguage()));
        } else {
            Log.d(TAG, "MessageLanguage Not selected : " + property.getMessageLanguage());
        }
        if (!TextUtils.isEmpty(property.getPmTab())) {
            consentLibBuilder.setPrivacyManagerTab(PrivacyManagerTab.findTabByName(property.getPmTab()));
        } else {
            Log.d(TAG, "privacy manager tab Not selected : " + property.getPmTab());
        }
        // generate ConsentLib at this point modifying builder will not do anything
        return consentLibBuilder.build();
    }

    public void getConsentsFromConsentLib(GDPRUserConsent userConsent) {
        GDPRUserConsent consent = userConsent;

        ArrayList<String> acceptedVendors = consent.acceptedVendors;
        ArrayList<String> acceptedPurposes = consent.acceptedCategories;

        if (acceptedVendors.size() > 0) {
            Consents vendorHeader = new Consents("0", "Accepted Vendor Consents Ids", "Header");
            mVendorConsents.add(vendorHeader);
            for (String vendorId : acceptedVendors) {
                Log.i(TAG, "The vendor " + vendorId + " was accepted.");
                Consents vendorConsent = new Consents(vendorId, vendorId, "vendorConsents");
                mVendorConsents.add(vendorConsent);
            }
        }
        if (acceptedPurposes.size() > 0) {
            Consents purposeHeader = new Consents("0", "Accepted Purpose Consents Ids", "Header");
            mPurposeConsents.add(purposeHeader);
            for (String purposeId : acceptedPurposes) {
                Log.i(TAG, "The category " + purposeId + " was accepted.");
                Consents purposeConsents = new Consents(purposeId, purposeId, "purposeConsents");
                mPurposeConsents.add(purposeConsents);
            }
        }
    }

    private void cancelCounter() {
        if (mCountDownTimer != null) mCountDownTimer.cancel();
    }

    private CountDownTimer getTimer(long defaultMessageTimeOut) {
        return new CountDownTimer(defaultMessageTimeOut, defaultMessageTimeOut) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (!isPMLoaded) {
                    showAlertDialog("Unable to load PM, No response from SDK ");
                }
                cancel();
            }
        };
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_view);
        setUPActivity();
    }

    public void setUPActivity() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        mConstraintLayout = findViewById(R.id.parentLayout);
        mConstraintLayout.setVisibility(View.GONE);
        mMainViewGroup = findViewById(android.R.id.content);

        getSupportActionBar().hide();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mConsentUUID = findViewById(R.id.tvConsentUUID);
        mEUConsent = findViewById(R.id.tvEUConsent);
        mConsentNotAvailable = findViewById(R.id.tv_consentsNotAvailable);

        RecyclerView mConsentRecyclerView = findViewById(R.id.consentRecyclerView);
        mConsentListRecyclerAdapter = new ConsentListRecyclerView(mConsentList);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mConsentRecyclerView.addItemDecoration(itemDecor);
        mConsentRecyclerView.setAdapter(mConsentListRecyclerAdapter);

        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);
        if (data.getParcelableArrayList(Constants.CONSENTS) != null) {
            mConsentList = data.getParcelableArrayList(Constants.CONSENTS);
            setConsents(true);
        } else {
            if (Util.isNetworkAvailable(this)) {
                showProgressBar();
                if (property.isNative()){
                    buildConsentLib(property, this).run(buildNativeMessage());
                }else {
                    buildConsentLib(property, this).run();
                }
            } else showAlertDialog(getString(R.string.network_check_message));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_showPM:
                getSupportActionBar().hide();
                resetFlag();
                buildAndShowConsentLibPM();
                break;
            case android.R.id.home:
                this.finish();
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.consent_view_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void buildAndShowConsentLibPM() {
        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);
        if (Util.isNetworkAvailable(this)) {
            showProgressBar();
            isPMLoaded = false;
            mCountDownTimer = getTimer(30000);
            mCountDownTimer.start();
            buildConsentLib(property, this).showPm();
        } else showAlertDialog(getString(R.string.network_check_message));

    }

    @Override
    ViewModel getViewModel() {
        PropertyListRepository propertyListRepository = ((SourcepointApp) getApplication()).getPropertyListRepository();
        return new ConsentViewViewModel(propertyListRepository);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewModelProvider.Factory viewModelFactory = ViewModelUtils.createFor(viewModel);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModel.getClass());
        showActionBar();
        showEUConsentAndConsentUUID();
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(ConsentViewActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.show();

        } else if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgressBar() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mProgressDialog != null) {
            hideProgressBar();
            mProgressDialog = null;
        }

        if (mAlertDialog != null) {
            mAlertDialog = null;
        }
    }

    // method to show alert/error dialog
    @SuppressLint("NewApi")
    private void showAlertDialog(String message) {
        hideProgressBar();
        if (!isDestroyed()) {
            if (mAlertDialog == null) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {
                                    dialog.cancel();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            setConsents(false);
                                        }
                                    });
                                }
                        );
                mAlertDialog = alertDialog.create();
            }

            if (!mAlertDialog.isShowing())
                mAlertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConsentViewActivity.this, PropertyListActivity.class);
        startActivity(intent);
        ConsentViewActivity.this.finish();
    }

    // method to set consents to recycler view
    private void setConsents(boolean isNewProperty) {
        hideProgressBar();
        showEUConsentAndConsentUUID();
        showActionBar();
        if (!isNewProperty && !isError) {
            mConsentList.addAll(mVendorConsents);
            mConsentList.addAll(mPurposeConsents);
        }

        if (mConsentList.size() > 0) {
            mConsentNotAvailable.setVisibility(View.GONE);
            mConsentListRecyclerAdapter.setConsentList(mConsentList);
            mConsentListRecyclerAdapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "mConsentList is empty");
            mConsentNotAvailable.setVisibility(View.VISIBLE);
        }
    }

    //method to show action bar
    private void showActionBar() {
        getSupportActionBar().show();
        mConstraintLayout.setVisibility(View.VISIBLE);
        mTitle.setText(getResources().getString(R.string.property_info_screen_title));
    }

    // method to show consent UUID and EUConsent
    private void showEUConsentAndConsentUUID() {
        if (preferences.getString(Constants.CONSENT_UUID_KEY, null) != null) {
            mConsentUUID.setText(preferences.getString(Constants.CONSENT_UUID_KEY, null));
        }
        if (preferences.getString(Constants.EU_CONSENT_KEY, null) != null) {
            mEUConsent.setText(preferences.getString(Constants.EU_CONSENT_KEY, null));
        }
    }

    // show debug info of property
    private void showPropertyDebugInfo() {
        setConsents(false);
    }

    private void resetFlag() {
        isError = false;
    }

    private NativeMessage buildNativeMessage(){
        return new NativeMessage(this){
            @Override
            public void init(){
                inflate(getContext(), R.layout.meta_app_native_message, this);
                setAcceptAll(findViewById(R.id.AcceptAll));
                setRejectAll(findViewById(R.id.RejectAll));
                setShowOptions(findViewById(R.id.ShowOption));
                setCancel(findViewById(R.id.Cancel));
                setTitle(findViewById(R.id.Title));
                setBody(findViewById(R.id.msgBody));
            }
            @Override
            public void setAttributes(NativeMessageAttrs attrs){

                setChildAttributes(getTitle(), attrs.title);
                setChildAttributes(getBody(), attrs.body);
                for(NativeMessageAttrs.Action action: attrs.actions){
                    setChildAttributes(findActionButton(action.choiceType), action);
                }
            }

            @Override
            public void setCallBacks(GDPRConsentLib consentLib) {
                super.setCallBacks(consentLib);
            }
        };
    }
}