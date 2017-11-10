package com.example.android.googlebookssearch;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Gavin on 06/11/2017.
 */

public class GoogleBooksLoader extends AsyncTaskLoader<List<GoogleBooks>> {

    // Tag for log messages.
    private static final String LOG_TAG = GoogleBooksLoader.class.getName();

    // Query URL.
    private String mUrl;

    /**
     * Constructs a new {@link GoogleBooksLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public GoogleBooksLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        //Think of this as AsyncTask onPreExecute() method,start your progress bar,
        // and at the end call forceLoad();
        forceLoad();
        Log.i(LOG_TAG, "Testing onStartLoading...");
    }

    //This is the background thread.
    @Override
    public List<GoogleBooks> loadInBackground() {

        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        List<GoogleBooks> result = QueryUtils.fetchGoogleBooksData(mUrl);

        Log.i(LOG_TAG, "Testing loadInBackground...");

        return result;
    }

}
