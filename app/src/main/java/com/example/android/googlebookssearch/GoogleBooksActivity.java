package com.example.android.googlebookssearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
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

    // Sets max number of books.
    private static final String API_REQUEST_URL_2 =
            "&orderBy=relevance&maxResults=10";

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
                CallIntent();
                return false;
            }
        });

        // When search button is clicked, intent is called.
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallIntent();
            }
        });
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
    public String SearchTerm() {
        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        String searchTerm = searchView.getQuery().toString();
        return searchTerm;
    }

    // Create the URL from a combination of the base URL, user inputted category,
    // user inputted search term, and the max number of books filter.
    public String CreateUrl() {
        String newUrl = API_REQUEST_URL_1 + mSearchField + "%3C" + SearchTerm().trim() + "%3E" + API_REQUEST_URL_2;
        newUrl = newUrl.replaceAll(" ", "%20");
        return newUrl;
    }

    // Call intent to start ListActivity, pass along the built URL.
    public void CallIntent() {
        Intent listActivityIntent = new Intent(GoogleBooksActivity.this, ListActivity.class);
        listActivityIntent.putExtra("fullUrl", CreateUrl());
        Log.e(LOG_TAG, "The url is:" + CreateUrl());
        startActivity(listActivityIntent);
    }
}
