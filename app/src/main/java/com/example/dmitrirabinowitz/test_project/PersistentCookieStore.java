/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Lukas Zorich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.dmitrirabinowitz.test_project;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;
import java.util.Map;

import android.util.Log;

/**
 * Repository for cookies. CookieManager will store cookies of every incoming HTTP response into
 * CookieStore, and retrieve cookies for every outgoing HTTP request.
 * <p/>
 * Cookies are stored in {@link android.content.SharedPreferences} and will persist on the
 * user's device between application session. {@link com.google.gson.Gson} is used to serialize
 * the cookies into a json string in order to be able to save the cookie to
 * {@link android.content.SharedPreferences}
 * <p/>
 * Created by lukas on 17-11-14.
 */
public class PersistentCookieStore implements CookieStore {

    private final static String TAG = PersistentCookieStore.class.getName();

    /**
     * The preferences name.
     */
    private final static String PREFS_NAME = PersistentCookieStore.class.getName();

    private CookieStore mStore;
    private Context mContext;

    /**
     * @param context The application context
     */
    public PersistentCookieStore(Context context) {
        // prevent context leaking by getting the application context
        mContext = context.getApplicationContext();

        // get the default in memory store and if there is a cookie stored in shared preferences,
        // we added it to the cookie store
        mStore = new CookieManager().getCookieStore();
        Map<String, ?> allPrefs = getPrefs().getAll();
        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                String strVal = (String) value;
                if (key != null && value != null) {
                    Gson gson = new Gson();
                    Log.i(TAG, "Adding cookie: " + strVal);
                    HttpCookie cookie = gson.fromJson(strVal, HttpCookie.class);
                    mStore.add(URI.create(cookie.getDomain()), cookie);
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        Gson gson = new Gson();
        String jsonCookieString = gson.toJson(cookie);
        Log.i(TAG, "Saving cookie: " + jsonCookieString);
        SharedPreferences.Editor editor = getPrefs().edit();
        editor.putString(cookie.getDomain() + ":" + cookie.getName(), jsonCookieString);
        editor.apply();

        mStore.add(URI.create(cookie.getDomain()), cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return mStore.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return mStore.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return mStore.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        return mStore.remove(uri, cookie);
    }

    @Override
    public boolean removeAll() {
        return mStore.removeAll();
    }

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}