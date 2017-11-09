package com.example.android.googlebookssearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GoogleBooksActivity extends AppCompatActivity {

    public static final String LOG_TAG = GoogleBooksActivity.class.getName();

    // Pre-search entry part of URL for Google Books list.
    // Actual search (word or phrase) will be added by user.
    private static final String API_REQUEST_URL_1 =
            "https://www.googleapis.com/books/v1/volumes?q=";

    // Post-search entry part of URL for Google Books list.
    // Search will only return 10 results max.
    private static final String API_REQUEST_URL_2 =
            "&maxResults=20";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_books_activity);

        final SearchView searchView = (SearchView) findViewById(R.id.search_view);

        final Button searchButton = (Button) findViewById(R.id.button_search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                CallIntent();
                return false;
            }
        });

        // When search button is clicked, checks to see if user has entered any text. If so,
        // intent is called.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallIntent();
            }
        });
    }

    public String SearchTerm() {
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        String searchTerm = searchView.getQuery().toString();
        return searchTerm;
    }

    public String CreateUrl() {
        String newUrl = API_REQUEST_URL_1 + SearchTerm().trim() + API_REQUEST_URL_2;
        newUrl = newUrl.replaceAll(" ", "%20");
        return newUrl;
    }

    public void CallIntent() {
        Intent listActivityIntent = new Intent(GoogleBooksActivity.this, ListActivity.class);
        listActivityIntent.putExtra("fullUrl", CreateUrl());
        Log.e(LOG_TAG, "The url is:" + CreateUrl());
        startActivity(listActivityIntent);
    }
}
