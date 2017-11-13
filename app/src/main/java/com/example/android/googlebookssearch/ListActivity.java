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
import android.support.v4.widget.SwipeRefreshLayout;
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

    // Tag for log messages.
    public static final String LOG_TAG = ListActivity.class.getName();

    // Handles loading the list of searched books.
    private LoaderManager mLoaderManager;

    // Handles callback when user swipes to reload view.
    private SwipeRefreshLayout swipeContainer;

    //Create a constant to uniquely identify the loader.
    public static final int BOOK_LOADER = 100;

    // Variable to later pass in the full URL from GoogleBooksActivity.
    private String passSearchUrl;

    // Adapter for the list of books.
    private GoogleBooksAdapter mAdapter;

    // Create a global variable for the TextView that is displayed when the list is empty
    private TextView mEmptyTextView;

    // Create a global variable for the ProgressBar.
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
            // Store the full URL passed from GoogleBooksActivity.
            passSearchUrl = (String) getIntent().getStringExtra("fullUrl");
        } catch (Exception ignored) {

        }

        // Find a reference to the {@Link SwipeRefreshLayout} in the layout
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        // Set the colors associated with the swipe refresh.
        swipeContainer.setColorSchemeResources(
                R.color.swiperedcolor,
                R.color.swipegreencolor,
                R.color.swipeyellowcolor,
                R.color.swipebluecolor);

        // Find a reference to the {@link ListView} in the layout book_list.xml.
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Find a reference to the empty TextView in the layout and assign it to this variable.
        mEmptyTextView = (TextView) findViewById(R.id.empty_state);
        bookListView.setEmptyView(mEmptyTextView);

        // Create a new {@link GoogleBooksAdapter} of googleBooks.
        mAdapter = new GoogleBooksAdapter(this, new ArrayList<GoogleBooks>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network.
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // Find a reference to the ProgressBar and assign to this variable.
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        // If there is a network connection, fetch data.
        if (activeNetwork != null && activeNetwork.isConnected()) {

            // Get a reference to LoaderManager, in order to interact with loaders.
            mLoaderManager = getLoaderManager();

            // Initialize the loader using constant BOOK_LOADER integer as ID.
            mLoaderManager.initLoader(BOOK_LOADER, null, this);

        } else {
            // Set progress bar visibility to 'gone'.
            mProgressBar.setVisibility(View.GONE);

            // Set empty state text to display "No earthquakes found".
            mEmptyTextView.setText(R.string.no_internet_connection);
        }

        // Set listener for swipe refresh event.
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(LOG_TAG, "onRefresh called...");
                // Clear the adapter of existing data.
                mAdapter.clear();
                // Show text to let the user know app is loading during refresh.
                mEmptyTextView.setText("");

                mProgressBar.setVisibility(View.VISIBLE);
                // Restart the Loader Manager.
                mLoaderManager.restartLoader(BOOK_LOADER, null, ListActivity.this);
                // Remove the refresh icon.
                swipeContainer.setRefreshing(false);
            }
        });

        // Set a click listener to the listView.
        // Set an onClickListener on the listItemView.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current book that was clicked on.
                GoogleBooks currentBook = mAdapter.getItem(position);

                // Store current url and store in String variable.
                String addressUri = currentBook.getWebReaderLink();

                // Create new implicit Intent which will open a browser app and load a viewable
                // copy of the book selected.
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(addressUri));
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });
    }

    @Override
    public Loader<List<GoogleBooks>> onCreateLoader(int i, Bundle bundle) {
        // Create a new loader for the given URL.
        Log.i(LOG_TAG, "Testing onCreateLoader...");
        return new GoogleBooksLoader(this, passSearchUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<GoogleBooks>> loader, List<GoogleBooks> books) {
        //Think of this as AsyncTask onPostExecute method, the result from onCreateLoader will be
        // available in operationResult variable and here you can update UI with the data fetched.

        // Clear the adapter of previous book data.
        mAdapter.clear();

        // If there is a valid list of {@link GoogleBooks}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

        // Set empty state text to display "No books found".
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
