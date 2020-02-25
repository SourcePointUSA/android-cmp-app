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
import android.text.Html;
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
import com.sourcepointmeta.app.adapters.WebsiteListAdapter;
import com.sourcepointmeta.app.common.Constants;
import com.sourcepointmeta.app.database.entity.Website;
import com.sourcepointmeta.app.listeners.RecyclerViewClickListener;
import com.sourcepointmeta.app.repository.WebsiteListRepository;
import com.sourcepointmeta.app.viewmodel.ViewModelUtils;
import com.sourcepointmeta.app.viewmodel.WebsiteListViewModel;

import java.util.List;


// property list activity.
public class WebsiteListActivity extends BaseActivity<WebsiteListViewModel> {

    private final String TAG = "WebsiteListActivity";
    private WebsiteListAdapter mWebsiteListAdapter;
    private List<Website> mWebsiteList;
    private TextView mAddSiteMessage;
    private RecyclerView mSiteListRecyclerView;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.tool_bar);
        Toolbar toolbar = (Toolbar) getSupportActionBar().getCustomView().getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
        TextView title = getSupportActionBar().getCustomView().findViewById(R.id.toolbar_title);
        title.setText(getString(R.string.website_list_title));

        mAddSiteMessage = findViewById(R.id.tvAddSiteMessage);

        //get recycler view
        mSiteListRecyclerView = findViewById(R.id.websiteListRecycleView);

        //get recycler view item click listener
        RecyclerViewClickListener listener = getRecyclerViewClickListener();
        mWebsiteListAdapter = new WebsiteListAdapter(listener);
        mSiteListRecyclerView.setAdapter(mWebsiteListAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_addWebsite) {
            Intent intent = new Intent(WebsiteListActivity.this, NewWebsiteActivity.class);
            mWebsiteListAdapter.closeLayout();
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
        WebsiteListRepository websiteListRepository = ((SourcepointApp) getApplication()).getWebsiteListRepository();
        return new WebsiteListViewModel(websiteListRepository);
    }

    private void subscribeUi(WebsiteListViewModel viewModel) {
        viewModel.getWebsiteListLiveData().observe(this, websites -> {

            mWebsiteList = websites;
            mWebsiteListAdapter.setWebsiteList(websites);
            mWebsiteListAdapter.notifyDataSetChanged();
            if (mWebsiteList.size() == 0) {
                mAddSiteMessage.setVisibility(View.VISIBLE);
                mAddSiteMessage.setText(getString(R.string.please_press_at_top_right_corner_to_add_sites));
            } else {
                mAddSiteMessage.setVisibility(View.GONE);
            }
        });
    }

    // this method returns onClick listener
    private RecyclerViewClickListener getRecyclerViewClickListener() {

        return (view, position) -> {
            Website website = mWebsiteList.get(position);
            Log.d(TAG, "" + website.getId());
            Intent intent;
            switch (view.getId()) {

                case R.id.item_view:
                    mWebsiteListAdapter.closeLayout();
                    startConsentViewActivity(website);
                    break;

                case R.id.reset_button:
                    showAlertDialogForCookiesCleared(website);
                    break;

                case R.id.edit_button:
                    mWebsiteListAdapter.closeLayout();
                    intent = new Intent(this, NewWebsiteActivity.class);
                    intent.putExtra(Constants.WEBSITE, website);
                    intent.putExtra(Constants.UPDATE, String.valueOf(website.getId()));
                    startActivity(intent);
                    break;

                case R.id.delete_button:
                    showAlertDialogForSiteDelete(website , position);
                    break;

                default:
                    break;
            }
        };
    }

    private void deleteWebsite(Website website , int position) {
        mWebsiteList.remove(position);
        viewModel.deleteWebsite(website);
        mWebsiteListAdapter.notifyItemRemoved(position);
        mWebsiteListAdapter.notifyItemRangeChanged(position , mWebsiteList.size());
        if (mWebsiteList.size() == 0) {
            mAddSiteMessage.setVisibility(View.VISIBLE);
            mAddSiteMessage.setText(getString(R.string.please_press_at_top_right_corner_to_add_sites));
        } else {
            mAddSiteMessage.setVisibility(View.GONE);
        }
    }

    private void clearCookies(Website website) {
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
            startConsentViewActivity(website);
        }else {
            showErrorDialog(getString(R.string.unable_to_clear_cookies));
        }});
    }

    private void showAlertDialogForSiteDelete(Website website , int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebsiteListActivity.this)
                .setMessage(getResources().getString(R.string.delete_confirmation_message))
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    mWebsiteListAdapter.closeLayout();
                    deleteWebsite(website, position);
                })
                .setNegativeButton("NO", (dialog, which) -> {
                    mWebsiteListAdapter.closeLayout();
                    dialog.cancel();
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showAlertDialogForCookiesCleared(Website website) {

        SpannableString cookieConfirmation = new SpannableString(getResources().getString(R.string.cookie_confirmation_message));
        cookieConfirmation.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),12,21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        cookieConfirmation.setSpan(new RelativeSizeSpan(1.2f),12,21, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebsiteListActivity.this)
                .setMessage(cookieConfirmation)
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, which) -> {
                    mWebsiteListAdapter.closeLayout();
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    clearCookies( website);

                })
                .setNegativeButton("NO", (dialog, which) -> {
                    mWebsiteListAdapter.closeLayout();
                    dialog.cancel();
                });
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void showErrorDialog(String message){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WebsiteListActivity.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.cancel());
        AlertDialog mAlertDialog = alertDialog.create();
        mAlertDialog.show();
    }

    private void startConsentViewActivity(Website website){
        Intent intent = new Intent(WebsiteListActivity.this, ConsentViewActivity.class);
        intent.putExtra(Constants.WEBSITE, website);
        startActivity(intent);
    }

    private void showProgressBar() {

        if (mProgressDialog == null) {

            mProgressDialog = new ProgressDialog(WebsiteListActivity.this, ProgressDialog.THEME_HOLO_LIGHT);
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
