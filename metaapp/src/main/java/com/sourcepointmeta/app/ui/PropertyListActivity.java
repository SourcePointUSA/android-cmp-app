package com.sourcepointmeta.app.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.TextView;

import com.sourcepointmeta.app.R;
import com.sourcepointmeta.app.SourcepointApp;
import com.sourcepointmeta.app.adapters.PropertyListAdapter;
import com.sourcepointmeta.app.common.Constants;
import com.sourcepointmeta.app.database.entity.Property;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;
import com.sourcepointmeta.app.repository.PropertyListRepository;
import com.sourcepointmeta.app.viewmodel.ViewModelUtils;
import com.sourcepointmeta.app.viewmodel.PropertyListViewModel;

import java.util.List;

public class PropertyListActivity extends BaseActivity<PropertyListViewModel> {

    private final String TAG = "PropertyListActivity";
    private PropertyListAdapter mPropertyListAdapter;
    private List<Property> mPropertyList;
    private TextView mAddPropertyMessage;
    private RecyclerView mPropertyListRecyclerView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        Toolbar toolbar = (Toolbar) getSupportActionBar().getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        TextView title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.property_list_title));

        mAddPropertyMessage = findViewById(R.id.tvAddPropertyMessage);

        //get recycler view
        mPropertyListRecyclerView = findViewById(R.id.propertyListRecycleView);

        //get recycler view item click listener
        RecyclerViewClickListener listener = getRecyclerViewClickListener();
        mPropertyListAdapter = new PropertyListAdapter(listener);
        mPropertyListRecyclerView.setAdapter(mPropertyListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addProperty) {
            Intent intent = new Intent(PropertyListActivity.this, NewPropertyActivity.class);
            mPropertyListAdapter.closeLayout();
            startActivity(intent);
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        ViewModelProvider.Factory viewModelFactory = ViewModelUtils.createFor(viewModel);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModel.getClass());

        subscribeUi(viewModel);
    }

    @Override
    ViewModel getViewModel() {
        PropertyListRepository propertyListRepository = ((SourcepointApp) getApplication()).getPropertyListRepository();
        return new PropertyListViewModel(propertyListRepository);
    }

    private void subscribeUi(PropertyListViewModel viewModel) {
        viewModel.getPropertyListLiveData().observe(this, properties -> {

            mPropertyList = properties;
            mPropertyListAdapter.setPropertyList(properties);
            mPropertyListAdapter.notifyDataSetChanged();
            if (mPropertyList.size() == 0) {
                mAddPropertyMessage.setVisibility(View.VISIBLE);
                mAddPropertyMessage.setText(getString(R.string.please_press_at_top_right_corner_to_add_property));
            } else {
                mAddPropertyMessage.setVisibility(View.GONE);
            }
        });
    }

    // this method returns onClick listener
    private RecyclerViewClickListener getRecyclerViewClickListener() {

        return (view, position) -> {
            Property property = mPropertyList.get(position);
            Log.d(TAG, "" + property.getId());
            Intent intent;
            switch (view.getId()) {

                case R.id.item_view:
                    mPropertyListAdapter.closeLayout();
                    startConsentViewActivity(property);
                    break;

                case R.id.reset_button:
                    showAlertDialogForCookiesCleared(property);
                    break;

                case R.id.edit_button:
                    mPropertyListAdapter.closeLayout();
                    intent = new Intent(this, NewPropertyActivity.class);
                    intent.putExtra(Constants.PROPERTY, property);
                    intent.putExtra(Constants.UPDATE, String.valueOf(property.getId()));
                    startActivity(intent);
                    break;

                case R.id.delete_button:
                    showAlertDialogForPropertyDelete(property , position);
                    break;

                default:
                    break;
            }
        };
    }

    private void deleteProperty(Property property , int position) {
        mPropertyList.remove(position);
        viewModel.deleteProperty(property);
        mPropertyListAdapter.notifyItemRemoved(position);
        mPropertyListAdapter.notifyItemRangeChanged(position , mPropertyList.size());
        if (mPropertyList.size() == 0) {
            mAddPropertyMessage.setVisibility(View.VISIBLE);
            mAddPropertyMessage.setText(getString(R.string.please_press_at_top_right_corner_to_add_property));
        } else {
            mAddPropertyMessage.setVisibility(View.GONE);
        }
    }

    private void clearCookies(Property property) {
        CookieManager cookieManager = CookieManager.getInstance();
        showProgressBar();
        cookieManager.removeAllCookies(value -> {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    hideProgressBar();
                }
            }, 3000);
             Log.d(TAG, "Cookies cleared "+value.toString());
        if (value){
            startConsentViewActivity(property);
        }else {
            showErrorDialog(getString(R.string.unable_to_clear_cookies));
        }});
    }

    private void showAlertDialogForPropertyDelete(Property property , int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PropertyListActivity.this)
                .setMessage(getResources().getString(R.string.delete_confirmation_message))
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    mPropertyListAdapter.closeLayout();
                    deleteProperty(property, position);
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    mPropertyListAdapter.closeLayout();
                    dialog.cancel();
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showAlertDialogForCookiesCleared(Property property) {

        SpannableString cookieConfirmation = new SpannableString(getResources().getString(R.string.cookie_confirmation_message));
        cookieConfirmation.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),12,21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cookieConfirmation.setSpan(new RelativeSizeSpan(1.2f),12,21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PropertyListActivity.this)
                .setMessage(cookieConfirmation)
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    mPropertyListAdapter.closeLayout();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    startConsentViewActivity(property);
                    //clearCookies( property);

                })
                .setNegativeButton("NO", (dialog, which) -> {
                    mPropertyListAdapter.closeLayout();
                    dialog.cancel();
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showErrorDialog(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PropertyListActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void startConsentViewActivity(Property property){
        Intent intent = new Intent(PropertyListActivity.this, ConsentViewActivity.class);
        intent.putExtra(Constants.PROPERTY, property);
        startActivity(intent);
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(PropertyListActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
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
    }

}
