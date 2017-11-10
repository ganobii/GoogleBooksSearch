package com.example.android.googlebookssearch;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeStream;

/**
 * Created by Gavin on 06/11/2017.
 */

public final class QueryUtils {

    // Tag for the log messages.
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the Google Books dataset and return an {@link GoogleBooks} object to represent a single book.
     */
    public static List<GoogleBooks> fetchGoogleBooksData(String requestUrl) {
        // Create URL object.
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link GoogleBooks} object.
        List<GoogleBooks> googleBooks = extractFeatureFromJson(jsonResponse);

        Log.i(LOG_TAG, "Testing fetchGoogleBooksData...");

        // Return the {@link GoogleBooks}
        return googleBooks;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the Google Books results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link GoogleBooks} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<GoogleBooks> extractFeatureFromJson(String googleBooksJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(googleBooksJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<GoogleBooks> googleBooks = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Initilaze the JSON variables.
            JSONObject currentBookObject;
            JSONObject volumeInfoObject;
            JSONObject accessInfo;
            String title = null;
            JSONArray authorsArray;
            JSONObject imageLinks;
            Bitmap smallThumbnail = null;
            String webReaderLink = null;

            // Create a JSONObject from the JSON response string.
            JSONObject baseJsonObject = new JSONObject(googleBooksJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (books).
            JSONArray itemsArray = baseJsonObject.getJSONArray("items");

            // For each book in the googleBooksArray, create an {@link GoogleBooks} object
            for (int i = 0; i < itemsArray.length(); i++) {

                // Initialize an ArrayList to later build a list of authors.
                ArrayList<String> authors = new ArrayList<>();

                // Get a single book at position i within the list of books.
                currentBookObject = itemsArray.getJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of volume info.
                // for that book.
                volumeInfoObject = currentBookObject.getJSONObject("volumeInfo");

                // Extract the value for the key called "title"
                if (volumeInfoObject.has("title")) {
                    title = volumeInfoObject.getString("title");
                }

                //Check for the key, if it exists, getString
                if (volumeInfoObject.has("authors")) {
                    authorsArray = volumeInfoObject.getJSONArray("authors");
                    //Extract author names.
                    for (int a = 0; a < authorsArray.length(); a++) {
                        String author = authorsArray.getString(a);
                        authors.add(author);
                    }
                }

                // Extract the JSON object that contains image links.
                if (volumeInfoObject.has("imageLinks")) {
                    imageLinks = volumeInfoObject.getJSONObject("imageLinks");
                    if (imageLinks.has("smallThumbnail")) {
                        smallThumbnail = fetchThumbnail((imageLinks.getString("smallThumbnail")));
                    }
                }

                // Extract the JSONObject associated with the key called "accessInfo".
                accessInfo = currentBookObject.getJSONObject("accessInfo");

                // Extract the value for the key called "webReaderLink".
                if (accessInfo.has("webReaderLink")) {
                    webReaderLink = accessInfo.getString("webReaderLink");
                }

                // Create a new {@link GoogleBooks} object with the title, authors, smallThumbnail,
                // and webReaderLink from the JSON response.
                GoogleBooks currentBook = new GoogleBooks(title, authors, smallThumbnail, webReaderLink);

                // Add the new {@link GoogleBooks} to the list of books.
                googleBooks.add(currentBook);

            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the Google Books results", e);
        }

        // Return the list of books.
        return googleBooks;
    }


     // For smallThumbnail Loading.
     public static Bitmap fetchThumbnail(String requestUrl) {
        //Create URL object
        URL url = createUrl(requestUrl);

        //Perform HTTP request to the URL and receive a JSON response back
        Bitmap smallThumbnail = null;
        try {
            smallThumbnail = makeHttpRequestForBitmap(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        Log.v("SmallThumbnail", String.valueOf(smallThumbnail));
        return smallThumbnail;
    }

    // Method that makes HTTP request and returns a Bitmap as the response
    private static Bitmap makeHttpRequestForBitmap(URL url) throws IOException {
        Bitmap bitmapResponse = null;

        //If the URL is null, then return early.
        if (url == null) {
            return null;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                bitmapResponse = decodeStream(inputStream);

            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return bitmapResponse;
    }

}
