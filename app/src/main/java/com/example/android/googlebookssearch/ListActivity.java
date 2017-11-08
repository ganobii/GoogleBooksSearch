package com.example.android.googlebookssearch;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gavin on 07/11/2017.
 */

public class ListActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<GoogleBooks>> {

    public static final String LOG_TAG = ListActivity.class.getName();

    //Create a constant to uniquely identify the loader.
    public static final int BOOK_LOADER = 1;

    // Create a global variable to pass in search term from GoogleBooksActivity.
    String passSearchUrl;

    // Adapter for the list of earthquakes
    private GoogleBooksAdapter mAdapter;

    // Create a global variable for the TextView that is displayed when the list is empty
    private TextView mEmptyTextView;

    // Create a global variable for the ProgressBar
    private ProgressBar mProgressBar;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Set the back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // back button
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        try {
            passSearchUrl = (String) getIntent().getStringExtra("fullUrl");
        } catch (Exception ignored) {

        }

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find a reference to the empty TextView in the layout and assign it to this variable.
        mEmptyTextView = (TextView) findViewById(R.id.empty_state);
        bookListView.setEmptyView(mEmptyTextView);

        // Find a reference to the ProgressBar and assign to this variable.
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // Create a new {@link GoogleBooksAdapter} of googleBooks.
        mAdapter = new GoogleBooksAdapter(this, new ArrayList<GoogleBooks>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnected()) {

            // Get a reference to LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader using constant EARTHQUAKE_LOADER integer as ID.
            loaderManager.initLoader(BOOK_LOADER, null, this);

        } else {
            // Set progress bar visibility to 'gone'.
            mProgressBar.setVisibility(View.GONE);

            // Set empty state text to display "No earthquakes found".
            mEmptyTextView.setText(R.string.no_internet_connection);
        }

        // Set a click listener to the listView.
        //Set an onClickListener on the listItemView.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current earthquake that was clicked on
                GoogleBooks currentBook = mAdapter.getItem(position);

                // Store current url and store in String variable.
                String addressUri = currentBook.getWebReaderLink();

                // Create new implicit Intent which will open a browser app and populate the search
                // field with the ArrayList website when activity is started.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(addressUri));
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });
    }

    @Override
    public Loader<List<GoogleBooks>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL
        Log.i(LOG_TAG, "Testing onCreateLoader...");
        return new GoogleBooksLoader(this, passSearchUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<GoogleBooks>> loader, List<GoogleBooks> earthquakes) {
        //Think of this as AsyncTask onPostExecute method, the result from onCreateLoader will be
        // available in operationResult variable and here you can update UI with the data fetched.

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }

        // Set empty state text to display "No earthquakes found".
        mEmptyTextView.setText(R.string.no_books_found);

        // Set progress bar visibility to 'gone'.
        mProgressBar.setVisibility(View.GONE);

        Log.i(LOG_TAG, "Testing onLoadFinished...");
    }


    @Override
    public void onLoaderReset(Loader<List<GoogleBooks>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
        Log.i(LOG_TAG, "Testing onLoaderReset...");
    }

}
