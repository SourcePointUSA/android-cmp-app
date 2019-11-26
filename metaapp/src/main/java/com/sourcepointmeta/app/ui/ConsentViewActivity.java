package com.sourcepointmeta.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.sourcepoint.cmplibrary.ConsentLib;
import com.sourcepoint.cmplibrary.ConsentLibBuilder;
import com.sourcepoint.cmplibrary.ConsentLibException;
import com.sourcepoint.cmplibrary.CustomPurposeConsent;
import com.sourcepoint.cmplibrary.CustomVendorConsent;
import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.SourcepointApp;
import com.sourcepointmeta.app.adapters.ConsentListRecyclerView;
import com.sourcepointmeta.app.common.Constants;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.models.Consents;
import com.sourcepointmeta.app.repository.PropertyListRepository;
import com.sourcepointmeta.app.utility.Util;
import com.sourcepointmeta.app.viewmodel.ConsentViewViewModel;
import com.sourcepointmeta.app.viewmodel.ViewModelUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class ConsentViewActivity extends BaseActivity<ConsentViewViewModel> {

    private final String TAG = "ConsentViewActivity";
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private boolean isShow = false;
    private boolean onConsentReadyCalled = false;
    private boolean isShowOnceOrError = false;
    private boolean isVendorSuccess = false, isVendorFailure = false;
    private boolean isPurposeSuccess = false, isPurposeFailure = false;
    private boolean isPropertySaved = false;

    private List<Consents> mVendorConsents = new ArrayList<>();
    private List<Consents> mPurposeConsents = new ArrayList<>();
    private String mError = "";

    private ConsentLib mConsentLib;
    private TextInputEditText mConsentUUID;
    private TextInputEditText mEUConsent;
    private RecyclerView mConsentRecyclerView;
    private List<Consents> mConsentList = new ArrayList<>();
    private ConsentListRecyclerView mConsentListRecyclerAdapter;
    private TextView mTitle, mConsentNotAvailable;
    private SharedPreferences preferences;
    private ConstraintLayout mConstraintLayout;

    private ConsentLib buildConsentLib(Property property, Activity activity) throws ConsentLibException {


        ConsentLibBuilder consentLibBuilder = ConsentLib.newBuilder(property.getAccountID(), property.getProperty(), property.getPropertyID(),property.getPmID(),activity)
                // optional, used for running stage campaigns
                .setStage(property.isStaging())
                .setShowPM(property.isShowPM())
                .setViewGroup(findViewById(android.R.id.content))
                //optional message timeout default timeout is 5 seconds
                .setMessageTimeOut(15000)
                .setOnMessageReady(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib _c) {
                        hideProgressBar();
                        Log.d(TAG, "OnMessageReady");

                        isShow = true;
                        saveToDatabase();
                        Log.i(TAG, "The message is about to be shown.");

                    }
                })
                // optional, callback triggered when message choice is selected when called choice
                // type will be available as Integer at cLib.choiceType
                .setOnMessageChoiceSelect(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        Log.i(TAG, "Choice type selected by user: " + c.choiceType.toString());
                        Log.d(TAG, "setOnMessageChoiceSelect");
                    }
                })
                // optional, callback triggered when consent data is captured when called
                .setOnConsentReady(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // showActionBar();
                                showProgressBar();
                            }
                        });
                        onConsentReadyCalled = true;
                        Log.d(TAG, "setOnInteractionComplete");
                        // Get the consents for a collection of non-IAB vendors

                        c.getCustomVendorConsents(
                                new ConsentLib.OnLoadComplete() {
                                    @Override
                                    public void onSuccess(Object result) {
                                        HashSet<CustomVendorConsent> customVendorConsents = (HashSet<CustomVendorConsent>) result;
                                        List<Consents> vendorConsents = new ArrayList<>();
                                        Log.d(TAG, "getCustomVendorConsents : success");

                                        if (customVendorConsents.size() > 0) {
                                            Consents consents = new Consents("0", "Vendor Consents", "Header");
                                            vendorConsents.add(consents);
                                            for (CustomVendorConsent consent : customVendorConsents) {
                                                Consents vendorConsent = new Consents(consent.id, consent.name, "vendorConsents");
                                                vendorConsents.add(vendorConsent);
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mVendorConsents = vendorConsents;
                                                isVendorSuccess = true;
                                                showPropertyDebugInfo();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(ConsentLibException exception) {
                                        Log.d(TAG, "Something went wrong :( " + exception);
                                        if (!TextUtils.isEmpty(exception.getMessage())) {
                                            if (exception.getMessage().equalsIgnoreCase("Bad Request")) {
                                                mError = "Bad Request";
                                                showAlertDialog("Could not find a property " + property.getProperty() + " for the account id " + property.getAccountID(), false);
                                            } else {
                                                mError = exception.getMessage();
                                                showAlertDialog(exception.getMessage(), false);
                                            }
                                        } else {
                                            showAlertDialog("Failed while getting custom vendor consents with empty error", false);
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                isVendorFailure = true;
                                                showPropertyDebugInfo();
                                            }
                                        });
                                    }
                                });

                        // Example usage of getting all purpose consent results
                        c.getCustomPurposeConsents(new ConsentLib.OnLoadComplete() {
                            public void onSuccess(Object result) {
                                HashSet<CustomPurposeConsent> customPurposeConsents = (HashSet<CustomPurposeConsent>) result;
                                List<Consents> purposeConsents = new ArrayList<>();
                                Log.d(TAG, "getCustomPurposeConsents : success");
                                if (customPurposeConsents.size() > 0) {
                                    Consents consents = new Consents("0", "Purpose Consents", "Header");
                                    purposeConsents.add(consents);
                                    for (CustomPurposeConsent consent : customPurposeConsents) {
                                        Consents vendorConsent = new Consents(consent.id, consent.name, "purposeConsents");
                                        purposeConsents.add(vendorConsent);
                                    }
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPurposeConsents = purposeConsents;
                                        isPurposeSuccess = true;
                                        showPropertyDebugInfo();
                                    }
                                });
                            }

                            @Override
                            public void onFailure(ConsentLibException exception) {
                                Log.d(TAG, "Something went wrong :( " + exception);
                                if (!TextUtils.isEmpty(exception.getMessage())) {
                                    if (exception.getMessage().equalsIgnoreCase("Bad Request")) {
                                        mError = "Bad Request";
                                        showAlertDialog("Could not find a property " + property.getProperty() + " for the account id " + property.getAccountID(), false);
                                    } else {
                                        mError = exception.getMessage();
                                        showAlertDialog(exception.getMessage(), false);
                                    }
                                } else {
                                    showAlertDialog("Failed while getting custom purpose consents with empty error", false);
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        isPurposeFailure = true;
                                        showPropertyDebugInfo();
                                    }
                                });
                            }
                        });
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!isShow && onConsentReadyCalled) {
                                    isShowOnceOrError = true;
                                }
                            }
                        });
                    }
                })
                .setOnErrorOccurred(new ConsentLib.Callback() {
                    @Override
                    public void run(ConsentLib c) {
                        hideProgressBar();
                        Log.d(TAG, "setOnErrorOccurred");
                        showAlertDialog("" + c.error.getMessage(), false);
                        Log.d(TAG, "Something went wrong: ", c.error);
                    }
                });

        //get and set targeting param
        List<TargetingParam> list = property.getTargetingParamList();//getTargetingParamList(property);
        for (TargetingParam tps : list) {
            consentLibBuilder.setTargetingParam(tps.getKey(), tps.getValue());
            Log.d(TAG, "" + tps.getKey() + " " + tps.getValue());
        }

        if (!TextUtils.isEmpty(property.getAuthId())){
            consentLibBuilder.setAuthId(property.getAuthId());
            Log.d(TAG,"AuthID : " + property.getAuthId() );
        }else {
            Log.d(TAG,"AuthID Not available : " + property.getAuthId() );
        }
        // generate ConsentLib at this point modifying builder will not do anything
        return consentLibBuilder.build();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_view);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        mConstraintLayout = findViewById(R.id.parentLayout);
        mConstraintLayout.setVisibility(View.GONE);

        getSupportActionBar().hide();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mConsentUUID = findViewById(R.id.tvConsentUUID);
        mEUConsent = findViewById(R.id.tvEUConsent);
        mConsentNotAvailable = findViewById(R.id.tv_consentsNotAvailable);

        mConsentRecyclerView = findViewById(R.id.consentRecyclerView);
        mConsentListRecyclerAdapter = new ConsentListRecyclerView(mConsentList);

        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        mConsentRecyclerView.addItemDecoration(itemDecor);
        mConsentRecyclerView.setAdapter(mConsentListRecyclerAdapter);


        Bundle data = getIntent().getExtras();
        Property property = data.getParcelable(Constants.PROPERTY);


        try {
            mConsentLib = buildConsentLib(property, this);
            if (Util.isNetworkAvailable(this)) {
                showProgressBar();
                mConsentLib.run();
            } else showAlertDialog(getString(R.string.network_check_message), false);
        } catch (Exception e) {
            showAlertDialog("" + e.toString(), false);
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            onBackPressed();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
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
    }

    private void addProperty(Property property) {
        viewModel.addProperty(property);
    }

    private void updateProperty(Property property) {
        viewModel.updateProperty(property);
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(ConsentViewActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.getWindow().setTransitionBackgroundFadeDuration(1000);
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
    private void showAlertDialog(String message, boolean isPropertyList) {
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
                                            if (!isPropertyList) {
                                                if (isPropertySaved) {
                                                    onBackPressed();
                                                } else {
                                                    ConsentViewActivity.this.finish();
                                                }
                                            } else {
                                                setConsents();
                                            }
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
    private void setConsents() {
        hideProgressBar();
        showEUConsentAndConsentUUID();
        showActionBar();

        mConsentList.addAll(mVendorConsents);
        mConsentList.addAll(mPurposeConsents);
        if (isShowOnceOrError) {
            saveToDatabase();
        }

        if (mConsentList.size() > 0) {
            mConsentNotAvailable.setVisibility(View.GONE);
            mConsentListRecyclerAdapter.setConsentList(mConsentList);
            mConsentListRecyclerAdapter.notifyDataSetChanged();
        } else {
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

        if (isPurposeSuccess && isVendorSuccess) {
            if (isShowOnceOrError) {
                showAlertDialogForShowMessageOnce(getResources().getString(R.string.no_message_matching_scenario), true);
            } else {
                setConsents();
            }
        } else if (isVendorFailure || isPurposeFailure) {
            showAlertDialog(mError, false);
        }

    }

    // method to update or add property to database
    private void saveToDatabase() {
        Bundle bundle = getIntent().getExtras();
        Property property;
        if (bundle != null && !isPropertySaved) {
            property = bundle.getParcelable(Constants.PROPERTY);
            if (bundle.containsKey("Update")) {
                if (property != null && bundle.getString("Update") != null)
                    property.setId(Integer.parseInt(bundle.getString("Update")));
                updateProperty(property);
                isPropertySaved = true;
            } else if (bundle.containsKey("Add")) {
                addProperty(property);
                isPropertySaved = true;
            } else {
                Log.d(TAG, "No need to add or update as its from propertyList");
            }
        } else {
            Log.d(TAG, "Data not present to update or add");
        }

    }

    private void showAlertDialogForShowMessageOnce(String message, boolean isPropertyList) {
        hideProgressBar();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Clear Cookies", (dialog, which) -> {
                    dialog.cancel();
                    showAlertDialogForCookiesCleared(isPropertyList);
                })
                .setNegativeButton("Show property Info", (dialog, which) -> {
                    dialog.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPropertyList) {
                                ConsentViewActivity.this.finish();
                            } else {
                                setConsents();
                            }
                        }
                    });
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showAlertDialogForCookiesCleared(boolean isPropertyList) {
        SpannableString cookieConfirmation = new SpannableString(getResources().getString(R.string.cookie_confirmation_message));
        cookieConfirmation.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cookieConfirmation.setSpan(new RelativeSizeSpan(1.2f), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
                .setMessage(cookieConfirmation)
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    clearCookies(isPropertyList);

                })
                .setNegativeButton("NO", (dialog, which) -> {
                    dialog.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isPropertyList) {
                                ConsentViewActivity.this.finish();
                            } else {
                                setConsents();
                            }
                        }
                    });
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void clearCookies(boolean isPropertyList) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(value -> {
            Log.d(TAG, "Cookies cleared : " + value.toString());
            if (value) {
                resetFlag();
                Bundle data = getIntent().getExtras();
                Property property = data.getParcelable(Constants.PROPERTY);
                try {
                    mConsentLib = buildConsentLib(property, this);
                    if (Util.isNetworkAvailable(this)) {
                        showProgressBar();
                        mConsentLib.run();
                    } else showAlertDialog(getString(R.string.network_check_message), false);
                } catch (Exception e) {
                    showAlertDialog("" + e.toString(), false);
                    e.printStackTrace();
                }
            } else {
                showAlertDialog(getString(R.string.unable_to_clear_cookies), isPropertyList);
            }
        });

    }

    private void resetFlag() {
        isShow = onConsentReadyCalled = isShowOnceOrError = isVendorSuccess = isVendorFailure = isPurposeSuccess = isPurposeFailure = false;
        mConsentList.clear();
        mVendorConsents.clear();
        mPurposeConsents.clear();
    }
}