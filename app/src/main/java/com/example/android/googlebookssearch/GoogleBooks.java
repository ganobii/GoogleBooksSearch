package com.example.android.googlebookssearch;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Gavin on 06/11/2017.
 */

public class GoogleBooks {

    // String member variable for the title of the book.
    private String mTitle;

    // ArrayList member variable for the author of the book.
    private ArrayList<String> mAuthors;

    // Bitmap member variable for the description of the book.
    private Bitmap mSmallThumbnail;

    // String member variable for the url of the web reader.
    private String mWebReaderLink;

    // Create a new GoogleBooks object.
    public GoogleBooks (String title, ArrayList<String> authors, Bitmap smallThumbnail,
                        String webReaderLink) {
        mTitle = title;
        mAuthors = authors;
        mSmallThumbnail = smallThumbnail;
        mWebReaderLink = webReaderLink;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public Bitmap getSmallThumbnail() {
        return mSmallThumbnail;
    }

    public String getWebReaderLink() {
        return mWebReaderLink;
    }

}
