package com.example.android.googlebookssearch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class GoogleBooksActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // Tag for log messages.
    public static final String LOG_TAG = GoogleBooksActivity.class.getName();

    // Base URL.
    private static final String API_REQUEST_URL_1 =
            "https://www.googleapis.com/books/v1/volumes?q=";

    // Member variable for spinner search field selected.
    private String mSearchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_books_activity);

        // Spinner instance for category.
        Spinner spinner = (Spinner) findViewById(R.id.categories_spinner);

        // Spinner click listener.
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add(getString(R.string.title_category));
        categories.add(getString(R.string.author_category));
        categories.add(getString(R.string.genre_category));

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style.
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach data adapter to spinner.
        spinner.setAdapter(dataAdapter);

        // SearchView instance for book search.
        final SearchView searchView = (SearchView) findViewById(R.id.search_view);

        // Search button instance.
        final Button searchButton = (Button) findViewById(R.id.button_search);

        // Set onQueryTextListener on SearchView instance.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            // Don't do anything on a text change.
            public boolean onQueryTextChange(String newText) {
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // On keyboard submit begin search.
                callIntent();
                return false;
            }
        });

        // When search button is clicked, intent is called.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callIntent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item.
        String item = parent.getItemAtPosition(position).toString();
        // Set the 'item' variable to equal the String value selected from the spinner.
        switch (item) {
            case "Title":
                item = "intitle:";
                break;
            case "Author":
                item = "inauthor:";
                break;
            case "Genre":
                item = "subject:";
                break;
            default:
                item = "intitle:";
        }
        // Set global variable mSearchField equal to 'item'.
        mSearchField = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // When nothing is selected, set to search by title.
        String item = "intitle:";
        mSearchField = item;
    }

    // Store the user inputted query in a new String variable.
    public String searchTerm() {
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        String searchTerm = searchView.getQuery().toString();
        return searchTerm;
    }

    // Create the URL from a combination of the base URL, user inputted category,
    // user inputted search term, and user selected settings.
    public String createUrl() {
        String newUrl = API_REQUEST_URL_1 + mSearchField + "%3C" + searchTerm().trim() + "%3E";

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResults = sharedPrefs.getString(
                getString(R.string.settings_max_results_key),
                getString(R.string.settings_max_results_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(newUrl);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("orderBy", orderBy);
        uriBuilder.appendQueryParameter("maxResults", maxResults);

        String fullUri = uriBuilder.toString();
        fullUri = fullUri.replaceAll(" ", "%20");
        return fullUri;
    }

    // Call intent to start ListActivity, pass along the built URL.
    public void callIntent() {
        Intent listActivityIntent = new Intent(GoogleBooksActivity.this, ListActivity.class);
        listActivityIntent.putExtra("fullUrl", createUrl());
        Log.e(LOG_TAG, "The url is:" + createUrl());
        startActivity(listActivityIntent);
    }
}
