package com.sourcepointmeta.app.ui;

public class ConsentViewActivity {

//    private final String TAG = "ConsentViewActivity";
//    private ProgressDialog mProgressDialog;
//    private AlertDialog mAlertDialog;
//    private boolean isShow = false;
//    private boolean onConsentReadyCalled = false;
//    private boolean isShowOnceOrError = false;
//    private boolean isVendorSuccess = false, isVendorFailure = false;
//    private boolean isPurposeSuccess = false, isPurposeFailure = false;
//    private boolean isSiteSaved = false;
//    private boolean isCookiesCleared = false;
//
//    private List<Consents> mVendorConsents = new ArrayList<>();
//    private List<Consents> mPurposeConsents = new ArrayList<>();
//    private String mError = "";
//
//    private ConsentLib mConsentLib;
//    private TextInputEditText mConsentUUID;
//    private TextInputEditText mEUConsent;
//    private RecyclerView mConsentRecyclerView;
//    private List<Consents> mConsentList = new ArrayList<>();
//    private ConsentListRecyclerView mConsentListRecyclerAdapter;
//    private TextView mTitle, mConsentNotAvailable;
//    private SharedPreferences preferences;
//    private ConstraintLayout mConstraintLayout;
//
//    private ConsentLib buildConsentLib(Website website, Activity activity) throws ConsentLibException {
//
//
//        ConsentLibBuilder consentLibBuilder = ConsentLib.newBuilder(website.getAccountID(), website.getName(), website.getSiteID(),website.getPmID(),activity)
//                // optional, used for running stage campaigns
//                .setStagingCampaign(website.isStaging())
//                .setShowPM(website.isShowPM())
//                .setViewGroup(findViewById(android.R.id.content))
//                //optional message timeout default timeout is 5 seconds
//                .setMessageTimeOut(15000)
//                .setConsentUIReady(new ConsentLib.Callback() {
//                    @Override
//                    public void run(ConsentLib _c) {
//                        hideProgressBar();
//                        Log.d(TAG, "OnMessageReady");
//
//                        isShow = true;
//                        saveToDatabase();
//                        Log.i(TAG, "The message is about to be shown.");
//
//                    }
//                })
//                // optional, callback triggered when message choice is selected when called choice
//                // type will be available as Integer at cLib.choiceType
//                .setOnMessageChoiceSelect(new ConsentLib.Callback() {
//                    @Override
//                    public void run(ConsentLib c) {
//                        Log.i(TAG, "Choice type selected by user: " + c.choiceType.toString());
//                        Log.d(TAG, "setOnMessageChoiceSelect");
//                    }
//                })
//                // optional, callback triggered when consent data is captured when called
//                .setOnConsentReady(new ConsentLib.Callback() {
//                    @Override
//                    public void run(ConsentLib c) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // showActionBar();
//                                showProgressBar();
//                            }
//                        });
//                        onConsentReadyCalled = true;
//                        Log.d(TAG, "setOnInteractionComplete");
//                        // Get the consents for a collection of non-IAB vendors
//
//                        c.getCustomVendorConsents(
//                                new ConsentLib.OnLoadComplete() {
//                                    @Override
//                                    public void onSuccess(Object result) {
//                                        HashSet<CustomVendorConsent> customVendorConsents = (HashSet<CustomVendorConsent>) result;
//                                        List<Consents> vendorConsents = new ArrayList<>();
//                                        Log.d(TAG, "getCustomVendorConsents : success");
//
//                                        if (customVendorConsents.size() > 0) {
//                                            Consents consents = new Consents("0", "Vendor Consents", "Header");
//                                            vendorConsents.add(consents);
//                                            for (CustomVendorConsent consent : customVendorConsents) {
//                                                Consents vendorConsent = new Consents(consent.id, consent.name, "vendorConsents");
//                                                vendorConsents.add(vendorConsent);
//                                            }
//                                        }
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                mVendorConsents = vendorConsents;
//                                                isVendorSuccess = true;
//                                                showSiteDebugInfo();
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onFailure(ConsentLibException exception) {
//                                        Log.d(TAG, "Something went wrong :( " + exception);
//                                        if (!TextUtils.isEmpty(exception.getMessage())) {
//                                            if (exception.getMessage().equalsIgnoreCase("Bad Request")) {
//                                                mError = "Bad Request";
//                                                showAlertDialog("Could not find a site " + website.getName() + " for the account id " + website.getAccountID(), false);
//                                            } else {
//                                                mError = exception.getMessage();
//                                                showAlertDialog(exception.getMessage(), false);
//                                            }
//                                        } else {
//                                            showAlertDialog("Failed while getting custom vendor consents with empty error", false);
//                                        }
//
//                                        runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                isVendorFailure = true;
//                                                showSiteDebugInfo();
//                                            }
//                                        });
//                                    }
//                                });
//
//                        // Example usage of getting all purpose consent results
//                        c.getCustomPurposeConsents(new ConsentLib.OnLoadComplete() {
//                            public void onSuccess(Object result) {
//                                HashSet<CustomPurposeConsent> customPurposeConsents = (HashSet<CustomPurposeConsent>) result;
//                                List<Consents> purposeConsents = new ArrayList<>();
//                                Log.d(TAG, "getCustomPurposeConsents : success");
//                                if (customPurposeConsents.size() > 0) {
//                                    Consents consents = new Consents("0", "Purpose Consents", "Header");
//                                    purposeConsents.add(consents);
//                                    for (CustomPurposeConsent consent : customPurposeConsents) {
//                                        Consents vendorConsent = new Consents(consent.id, consent.name, "purposeConsents");
//                                        purposeConsents.add(vendorConsent);
//                                    }
//                                }
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mPurposeConsents = purposeConsents;
//                                        isPurposeSuccess = true;
//                                        showSiteDebugInfo();
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(ConsentLibException exception) {
//                                Log.d(TAG, "Something went wrong :( " + exception);
//                                if (!TextUtils.isEmpty(exception.getMessage())) {
//                                    if (exception.getMessage().equalsIgnoreCase("Bad Request")) {
//                                        mError = "Bad Request";
//                                        showAlertDialog("Could not find a site " + website.getName() + " for the account id " + website.getAccountID(), false);
//                                    } else {
//                                        mError = exception.getMessage();
//                                        showAlertDialog(exception.getMessage(), false);
//                                    }
//                                } else {
//                                    showAlertDialog("Failed while getting custom purpose consents with empty error", false);
//                                }
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        isPurposeFailure = true;
//                                        showSiteDebugInfo();
//                                    }
//                                });
//                            }
//                        });
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (!isShow && onConsentReadyCalled) {
//                                    isShowOnceOrError = true;
//                                }
//                            }
//                        });
//                    }
//                })
//                .setOnError(new ConsentLib.Callback() {
//                    @Override
//                    public void run(ConsentLib c) {
//                        hideProgressBar();
//                        Log.d(TAG, "setOnError");
//                        showAlertDialog("" + c.error.getMessage(), false);
//                        Log.d(TAG, "Something went wrong: ", c.error);
//                    }
//                });
//
//        //get and set targeting param
//        List<TargetingParam> list = website.getTargetingParamList();//getTargetingParamList(website);
//        for (TargetingParam tps : list) {
//            consentLibBuilder.setTargetingParam(tps.getKey(), tps.getValue());
//            Log.d(TAG, "" + tps.getKey() + " " + tps.getValue());
//        }
//
//        if (!TextUtils.isEmpty(website.getAuthId())){
//            consentLibBuilder.setAuthId(website.getAuthId());
//            Log.d(TAG,"AuthID : " + website.getAuthId() );
//        }else {
//            Log.d(TAG,"AuthID Not available : " + website.getAuthId() );
//        }
//        // generate ConsentLib at this point modifying builder will not do anything
//        return consentLibBuilder.build();
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_consent_view);
//
//        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        getSupportActionBar().setCustomView(R.layout.tool_bar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
//        mConstraintLayout = findViewById(R.id.parentLayout);
//        mConstraintLayout.setVisibility(View.GONE);
//
//        getSupportActionBar().hide();
//
//        preferences = PreferenceManager.getDefaultSharedPreferences(this);
//
//        mConsentUUID = findViewById(R.id.tvConsentUUID);
//        mEUConsent = findViewById(R.id.tvEUConsent);
//        mConsentNotAvailable = findViewById(R.id.tv_consentsNotAvailable);
//
//        mConsentRecyclerView = findViewById(R.id.consentRecyclerView);
//        mConsentListRecyclerAdapter = new ConsentListRecyclerView(mConsentList);
//
//        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
//        mConsentRecyclerView.addItemDecoration(itemDecor);
//        mConsentRecyclerView.setAdapter(mConsentListRecyclerAdapter);
//
//
//        Bundle data = getIntent().getExtras();
//        Website website = data.getParcelable(Constants.WEBSITE);
//
//
//        try {
//            mConsentLib = buildConsentLib(website, this);
//            if (Util.isNetworkAvailable(this)) {
//                showProgressBar();
//                mConsentLib.run();
//            } else showAlertDialog(getString(R.string.network_check_message), false);
//        } catch (Exception e) {
//            showAlertDialog("" + e.toString(), false);
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            this.finish();
//            onBackPressed();
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }
//
//    @Override
//    ViewModel getViewModel() {
//        WebsiteListRepository websiteListRepository = ((SourcepointApp) getApplication()).getWebsiteListRepository();
//        return new ConsentViewViewModel(websiteListRepository);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        ViewModelProvider.Factory viewModelFactory = ViewModelUtils.createFor(viewModel);
//        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModel.getClass());
//    }
//
//    private void addWebsite(Website website) {
//        viewModel.addWebsite(website);
//    }
//
//    private void updateWebsite(Website website) {
//        viewModel.updateWebsite(website);
//    }
//
//    private void showProgressBar() {
//
//        if (mProgressDialog == null) {
//
//            mProgressDialog = new ProgressDialog(ConsentViewActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
//            mProgressDialog.setMessage("Please wait...");
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setIndeterminate(true);
//            mProgressDialog.getWindow().setTransitionBackgroundFadeDuration(1000);
//            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            mProgressDialog.show();
//
//        } else if (!mProgressDialog.isShowing()) {
//            mProgressDialog.show();
//        }
//    }
//
//    private void hideProgressBar() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        if (mProgressDialog != null) {
//            hideProgressBar();
//            mProgressDialog = null;
//        }
//
//        if (mAlertDialog != null) {
//            mAlertDialog = null;
//        }
//    }
//
//    // method to show alert/error dialog
//    private void showAlertDialog(String message, boolean isSiteList) {
//        hideProgressBar();
//        if (!isDestroyed()) {
//            if (mAlertDialog == null) {
//                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
//                        .setMessage(message)
//                        .setCancelable(false)
//                        .setPositiveButton("OK", (dialog, which) -> {
//                                    dialog.cancel();
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            if (!isSiteList) {
//                                                if (isSiteSaved) {
//                                                    onBackPressed();
//                                                } else {
//                                                    ConsentViewActivity.this.finish();
//                                                }
//                                            } else {
//                                                setConsents();
//                                            }
//                                        }
//                                    });
//                                }
//                        );
//                mAlertDialog = alertDialog.create();
//            }
//
//            if (!mAlertDialog.isShowing())
//                mAlertDialog.show();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(ConsentViewActivity.this, WebsiteListActivity.class);
//        startActivity(intent);
//        ConsentViewActivity.this.finish();
//    }
//
//    // method to set consents to recycler view
//    private void setConsents() {
//        hideProgressBar();
//        showEUConsentAndConsentUUID();
//        showActionBar();
//
//        mConsentList.addAll(mVendorConsents);
//        mConsentList.addAll(mPurposeConsents);
//        if (isShowOnceOrError) {
//            saveToDatabase();
//        }
//
//        if (mConsentList.size() > 0) {
//            mConsentNotAvailable.setVisibility(View.GONE);
//            mConsentListRecyclerAdapter.setConsentList(mConsentList);
//            mConsentListRecyclerAdapter.notifyDataSetChanged();
//        } else {
//            mConsentNotAvailable.setVisibility(View.VISIBLE);
//        }
//    }
//
//    //method to show action bar
//    private void showActionBar() {
//        getSupportActionBar().show();
//        mConstraintLayout.setVisibility(View.VISIBLE);
//        mTitle.setText(getResources().getString(R.string.site_info_screen_title));
//    }
//
//    // method to show consent UUID and EUConsent
//    private void showEUConsentAndConsentUUID() {
//        if (preferences.getString(Constants.CONSENT_UUID_KEY, null) != null) {
//            mConsentUUID.setText(preferences.getString(Constants.CONSENT_UUID_KEY, null));
//        }
//        if (preferences.getString(Constants.EU_CONSENT_KEY, null) != null) {
//            mEUConsent.setText(preferences.getString(Constants.EU_CONSENT_KEY, null));
//        }
//    }
//
//    // show debug info of site
//    private void showSiteDebugInfo() {
//
//        if (isPurposeSuccess && isVendorSuccess) {
//            if (isShowOnceOrError) {
//                showAlertDialogForShowMessageOnce(getResources().getString(R.string.no_message_matching_scenario), true);
//            } else {
//                setConsents();
//            }
//        } else if (isVendorFailure || isPurposeFailure) {
//            showAlertDialog(mError, false);
//        }
//
//    }
//
//    // method to update or add site to database
//    private void saveToDatabase() {
//        Bundle bundle = getIntent().getExtras();
//        Website website;
//        if (bundle != null && !isSiteSaved) {
//            website = bundle.getParcelable(Constants.WEBSITE);
//            if (bundle.containsKey("Update")) {
//                if (website != null && bundle.getString("Update") != null)
//                    website.setId(Integer.parseInt(bundle.getString("Update")));
//                updateWebsite(website);
//                isSiteSaved = true;
//            } else if (bundle.containsKey("Add")) {
//                addWebsite(website);
//                isSiteSaved = true;
//            } else {
//                Log.d(TAG, "No need to add or update as its from sitelist");
//            }
//        } else {
//            Log.d(TAG, "Data not present to update or add");
//        }
//
//    }
//
//    private void showAlertDialogForShowMessageOnce(String message, boolean isSiteList) {
//        hideProgressBar();
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
//                .setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton("Clear Cookies", (dialog, which) -> {
//                    dialog.cancel();
//                    showAlertDialogForCookiesCleared(isSiteList);
//                })
//                .setNegativeButton("Show Site Info", (dialog, which) -> {
//                    dialog.cancel();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isSiteList) {
//                                ConsentViewActivity.this.finish();
//                            } else {
//                                setConsents();
//                            }
//                        }
//                    });
//                });
//        AlertDialog mAlertDialog = alertDialog.create();
//        mAlertDialog.show();
//    }
//
//    private void showAlertDialogForCookiesCleared(boolean isSiteList) {
//        SpannableString cookieConfirmation = new SpannableString(getResources().getString(R.string.cookie_confirmation_message));
//        cookieConfirmation.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        cookieConfirmation.setSpan(new RelativeSizeSpan(1.2f), 12, 21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConsentViewActivity.this)
//                .setMessage(cookieConfirmation)
//                .setCancelable(false)
//                .setPositiveButton("YES", (dialog, which) -> {
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.clear();
//                    editor.commit();
//                    clearCookies(isSiteList);
//
//                })
//                .setNegativeButton("NO", (dialog, which) -> {
//                    dialog.cancel();
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isSiteList) {
//                                ConsentViewActivity.this.finish();
//                            } else {
//                                setConsents();
//                            }
//                        }
//                    });
//                });
//        AlertDialog mAlertDialog = alertDialog.create();
//        mAlertDialog.show();
//    }
//
//    private void clearCookies(boolean isSiteList) {
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.removeAllCookies(value -> {
//            Log.d(TAG, "Cookies cleared : " + value.toString());
//            isCookiesCleared = value;
//            if (value) {
//                resetFlag();
//                Bundle data = getIntent().getExtras();
//                Website website = data.getParcelable(Constants.WEBSITE);
//                try {
//                    mConsentLib = buildConsentLib(website, this);
//                    if (Util.isNetworkAvailable(this)) {
//                        showProgressBar();
//                        mConsentLib.run();
//                    } else showAlertDialog(getString(R.string.network_check_message), false);
//                } catch (Exception e) {
//                    showAlertDialog("" + e.toString(), false);
//                    e.printStackTrace();
//                }
//            } else {
//                showAlertDialog(getString(R.string.unable_to_clear_cookies), isSiteList);
//            }
//        });
//
//    }
//
//    private void resetFlag() {
//        isShow = onConsentReadyCalled = isShowOnceOrError = isVendorSuccess = isVendorFailure = isPurposeSuccess = isPurposeFailure = false;
//        mConsentList.clear();
//        mVendorConsents.clear();
//        mPurposeConsents.clear();
//    }
}