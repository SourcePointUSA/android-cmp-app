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
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;
import com.sourcepointmeta.app.repository.PropertyListRepository;
import com.sourcepointmeta.app.viewmodel.NewPropertyViewModel;
import com.sourcepointmeta.app.viewmodel.ViewModelUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.VERTICAL;

public class NewPropertyActivity extends BaseActivity<NewPropertyViewModel> {

    private final String TAG = "NewPropertyActivity";
    private ProgressDialog mProgressDialog;
    private TextInputEditText mAccountIdET, mPropertyIdET, mPropertyNameET,mPMIdET ,mAuthIdET, mKeyET, mValueET ;

    private TextView mAddParamBtn;

    private SwitchCompat mStagingSwitch , mShowPMSwitch;
    private TextView mTitle;
    private AlertDialog mAlertDialog;
    private TargetingParamsAdapter mTargetingParamsAdapter;
    private List<TargetingParam> mTargetingParamList = new ArrayList<>();
    private TextView mAddParamMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_property);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTitle = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);

        setupUI();
    }

    private void setupUI() {
        mAccountIdET = findViewById(R.id.etAccountID);
        mPropertyIdET = findViewById(R.id.etPropertyId);
        mPropertyNameET = findViewById(R.id.etPropertyName);
        mPMIdET = findViewById(R.id.etPMId);
        mAuthIdET = findViewById(R.id.etAuthID);
        mStagingSwitch = findViewById(R.id.toggleStaging);
        mShowPMSwitch = findViewById(R.id.toggleShowPM);
        mStagingSwitch.setChecked(false);
        mShowPMSwitch.setChecked(false);



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
        mTargetingParamsAdapter = new TargetingParamsAdapter(listener);
        mTargetingParamsAdapter.setmTargetingParamsList(mTargetingParamList);
        tpRecyclerView.setAdapter(mTargetingParamsAdapter);
        mTargetingParamsAdapter.notifyDataSetChanged();

        mAddParamMessage = findViewById(R.id.tv_noTargetingParams);
        setAddParamsMessage();

        Bundle data = getIntent().getExtras();
        if (data != null) {
            Property property = data.getParcelable(Constants.PROPERTY);

            if (property != null) {
                mAccountIdET.setText(String.valueOf(property.getAccountID()));
                mPropertyIdET.setText(String.valueOf(property.getPropertyID()));
                mPropertyNameET.setText(property.getProperty());
                mPMIdET.setText(property.getPmID());
                mStagingSwitch.setChecked(property.isStaging());
                mShowPMSwitch.setChecked(property.isShowPM());
                if (!TextUtils.isEmpty(property.getAuthId())){
                    mAuthIdET.setText(property.getAuthId());
                }
                mTargetingParamList = property.getTargetingParamList();
                mTargetingParamsAdapter.setmTargetingParamsList(mTargetingParamList);
                if (mTargetingParamList.size() != 0)
                    mAddParamMessage.setVisibility(View.GONE);
                mTargetingParamsAdapter.notifyDataSetChanged();

                mTitle.setText(R.string.edit_property_title);
            }
        } else {
            mTitle.setText(R.string.new_property_title);
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
        mPropertyIdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mPropertyNameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(v, hasFocus);
            }
        });
        mPMIdET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        PropertyListRepository propertyListRepository = ((SourcepointApp) getApplication()).getPropertyListRepository();
        return new NewPropertyViewModel(propertyListRepository);
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(NewPropertyActivity.this);
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewPropertyActivity.this)
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
        getMenuInflater().inflate(R.menu.new_property, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideSoftKeyboard();

        switch (item.getItemId()) {
            case R.id.action_saveProperty:
                loadPropertyWithInput();
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
    private Property getFormData() {

        String accountID = mAccountIdET.getText().toString().trim();
        String PropertyID = mPropertyIdET.getText().toString().trim();
        String propertyName = mPropertyNameET.getText().toString().trim();
        String pmID = mPMIdET.getText().toString().trim();
        String authId = mAuthIdET.getText().toString().trim();
        boolean isStaging = mStagingSwitch.isChecked();
        boolean isShowPm = mShowPMSwitch.isChecked();
        if (TextUtils.isEmpty(accountID)) {
            return null;
        }
        if (TextUtils.isEmpty(propertyName)) {
            return null;
        }
        if (TextUtils.isEmpty(PropertyID)) {
            return null;
        }
        if (TextUtils.isEmpty(pmID)) {
            return null;
        }
        int account = Integer.parseInt(accountID);
        int property_id = Integer.parseInt(PropertyID);

        return new Property(account, property_id, propertyName, pmID, isStaging, isShowPm, authId ,mTargetingParamList);
    }

    private void loadPropertyWithInput() {

        Property property = getFormData();
        if (property == null) {
            showAlertDialog(getString(R.string.empty_accountid_propertyname_message));
        } else {
            showProgressBar();
            LiveData<Integer> listSize = viewModel.getPropertyWithDetails(property);
            listSize.observe(this, size -> {
                if (size > 0) {
                    showAlertDialog(getResources().getString(R.string.property_details_exists));
                    hideProgressBar();
                } else {
                    startConsentViewActivity(property);
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

    private void startConsentViewActivity(Property property) {

        Intent intent = new Intent(NewPropertyActivity.this, ConsentViewActivity.class);
        intent.putExtra(Constants.PROPERTY, property);
        Log.d(TAG, "" + property.getId());
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
