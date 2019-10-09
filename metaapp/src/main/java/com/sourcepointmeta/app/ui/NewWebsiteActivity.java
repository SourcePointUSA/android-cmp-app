package com.sourcepointmeta.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.SourcepointApp;
import com.sourcepointmeta.app.adapters.TargetingParamsAdapter;
import com.sourcepointmeta.app.common.Constants;
import com.sourcepointmeta.app.database.entity.TargetingParam;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;
import com.sourcepointmeta.app.repository.WebsiteListRepository;
import com.sourcepointmeta.app.viewmodel.NewWebsiteViewModel;
import com.sourcepointmeta.app.viewmodel.ViewModelUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class NewWebsiteActivity extends BaseActivity<NewWebsiteViewModel> {

    private final String TAG = "NewWebsiteActivity";
    private ProgressDialog mProgressDialog;
    private TextInputEditText mAccountIdET, mSiteNameET, mAuthIdET, mKeyET, mValueET;

    private TextView mAddParamBtn;

    private SwitchCompat mStagingSwitch;
    private TextView mTitle;
    private AlertDialog mAlertDialog;
    private TargetingParamsAdapter mTargetingParamsAdapter;
    private List<TargetingParam> mTargetingParamList = new ArrayList<>();
    private TextView mAddParamMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_website);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);

        setupUI();
    }

    private void setupUI() {
        mAccountIdET = findViewById(R.id.etAccountID);
        mSiteNameET = findViewById(R.id.etSiteName);
        mStagingSwitch = findViewById(R.id.toggleStaging);
        mStagingSwitch.setChecked(false);

        mAuthIdET = findViewById(R.id.etAuthID);

        mKeyET = findViewById(R.id.etKey);
        mValueET = findViewById(R.id.etValue);
        mAddParamBtn = findViewById(R.id.btn_addParams);
        mAddParamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTargetingParam();
            }
        });


        RecyclerView tpRecyclerView = findViewById(R.id.targetingParamsRecycleView);
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, VERTICAL);
        tpRecyclerView.addItemDecoration(itemDecor);
        tpRecyclerView.setNestedScrollingEnabled(false);

        RecyclerViewClickListener listener = getRecyclerViewClickListener();
        mTargetingParamsAdapter = new TargetingParamsAdapter(listener, false);
        mTargetingParamsAdapter.setmTargetingParamsList(mTargetingParamList);
        tpRecyclerView.setAdapter(mTargetingParamsAdapter);
        mTargetingParamsAdapter.notifyDataSetChanged();

        mAddParamMessage = findViewById(R.id.tv_noTargetingParams);
        setAddParamsMessage();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            Website website = data.getParcelable(Constants.WEBSITE);

            if (website != null) {
                mAccountIdET.setText(String.valueOf(website.getAccountID()));
                mSiteNameET.setText(website.getName());
                mStagingSwitch.setChecked(website.isStaging());
                if (!TextUtils.isEmpty(website.getAuthId())){
                    mAuthIdET.setText(website.getAuthId());
                }
                mTargetingParamList = website.getTargetingParamList();
                mTargetingParamsAdapter.setmTargetingParamsList(mTargetingParamList);
                if (mTargetingParamList.size() != 0)
                    mAddParamMessage.setVisibility(View.GONE);
                mTargetingParamsAdapter.notifyDataSetChanged();

                mTitle.setText(R.string.edit_website_title);
            }
        } else {
            mTitle.setText(R.string.new_website_title);
        }

        mValueET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addTargetingParam();
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });


        // hides keyboard when touch outside
        mAccountIdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mSiteNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mAuthIdET.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mKeyET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mValueET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewModelProvider.Factory viewModelFactory = ViewModelUtils.createFor(viewModel);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModel.getClass());
    }

    @Override
    ViewModel getViewModel() {
        WebsiteListRepository websiteListRepository = ((SourcepointApp) getApplication()).getWebsiteListRepository();
        return new NewWebsiteViewModel(websiteListRepository);
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(NewWebsiteActivity.this);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);
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
    }

    private void showAlertDialog(String message) {
        if (!(mAlertDialog != null && mAlertDialog.isShowing())) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewWebsiteActivity.this)
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> dialog.cancel()
                    );
            mAlertDialog = alertDialog.create();
        }
        mAlertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_website, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard();

        switch (item.getItemId()) {
            case R.id.action_saveWebsite:
                loadWebsiteWithInput();
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

    // validate user data input
    private Website getFormData() {

        String accountID = mAccountIdET.getText().toString().trim();
        String siteName = mSiteNameET.getText().toString().trim();
        String authId = mAuthIdET.getText().toString().trim();
        boolean isStaging = mStagingSwitch.isChecked();
        if (TextUtils.isEmpty(accountID)) {
            return null;
        }
        if (TextUtils.isEmpty(siteName)) {
            return null;
        }
        int account = Integer.parseInt(accountID);

        return new Website(account, siteName, isStaging,authId ,mTargetingParamList);
    }

    private void loadWebsiteWithInput() {

        Website website = getFormData();
        if (website == null) {
            showAlertDialog(getString(R.string.empty_accountid_sitename_message));
        } else {
            showProgressBar();
            LiveData<Integer> listSize = viewModel.getWebsiteWithDetails(website);
            listSize.observe(this, size -> {
                if (size > 0) {
                    showAlertDialog(getResources().getString(R.string.site_details_exists));
                    hideProgressBar();
                } else {
                    startConsentViewActivity(website);
                }
                listSize.removeObservers(this);
            });
        }
    }

    private TargetingParam getTargetingParam() {
        String key = mKeyET.getText().toString().trim();
        String value = mValueET.getText().toString().trim();
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        mKeyET.setText("");
        mValueET.setText("");
        mKeyET.clearFocus();
        mValueET.clearFocus();
        return new TargetingParam(key, value);
    }

    private void addTargetingParam() {
        TargetingParam targetingParam = getTargetingParam();
        if (targetingParam == null) {
            showAlertDialog("Please enter targeting param Key/Value");
        } else if (mTargetingParamList.contains(targetingParam)) {
            for (TargetingParam param : mTargetingParamList) {
                if (param.getKey().equals(targetingParam.getKey()))
                    param.setValue(targetingParam.getValue());
            }
        } else {
            mTargetingParamList.add(targetingParam);
        }

        mTargetingParamsAdapter.setmTargetingParamsList(mTargetingParamList);
        mTargetingParamsAdapter.notifyDataSetChanged();
        setAddParamsMessage();
    }

    //hides soft keyboard
    private void hideSoftKeyboard() {
        if (this.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    0);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void hideSoftKeyboard(View v, boolean hasFocus) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (!hasFocus) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        } else {
            imm.showSoftInput(v, 0);
        }
    }

    private void startConsentViewActivity(Website website) {

        Intent intent = new Intent(NewWebsiteActivity.this, ConsentViewActivity.class);
        intent.putExtra(Constants.WEBSITE, website);
        Log.d(TAG, "" + website.getId());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(Constants.UPDATE)) {
            intent.putExtra(Constants.UPDATE, bundle.getString(Constants.UPDATE));
        } else {
            intent.putExtra(Constants.ADD, Constants.ADD);
        }
        startActivity(intent);
    }

    private RecyclerViewClickListener getRecyclerViewClickListener() {
        return new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                mTargetingParamList.remove(mTargetingParamList.get(position));
                mTargetingParamsAdapter.notifyDataSetChanged();
                setAddParamsMessage();
            }
        };
    }

    private void setAddParamsMessage() {
        if (mTargetingParamList != null && mTargetingParamList.size() == 0) {
            mAddParamMessage.setVisibility(View.VISIBLE);
        } else {
            mAddParamMessage.setVisibility(View.GONE);
        }
    }
}
