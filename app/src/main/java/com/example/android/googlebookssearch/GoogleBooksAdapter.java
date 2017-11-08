package com.example.android.googlebookssearch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gavin on 06/11/2017.
 */

public class GoogleBooksAdapter extends ArrayAdapter<GoogleBooks> {


    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context     The current context. Used to inflate the layout file.
     * @param googleBooks A List of GoogleBooks objects to display in a list
     */
    public GoogleBooksAdapter(Activity context, ArrayList<GoogleBooks> googleBooks) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, googleBooks);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // Get the {@link GoogleBooks} object located at this position in the list
        final GoogleBooks currentBook = getItem(position);

        // Find the TextView in the list_item.xml layout with the ID title.
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        // Set text using the string value stored in the variable primaryLocation.
        titleTextView.setText(currentBook.getTitle());

        // Find the TextView in the list_item.xml layout with the ID description.
        TextView authorTextView = (TextView) listItemView.findViewById(R.id.authors);

        //Get the Title and set it to a authors textview
        ArrayList<String> authorsArray = new ArrayList<>(currentBook.getAuthors());
        StringBuilder authors = new StringBuilder();
        if (authorsArray.size() == 1) {
            authors.append(authorsArray.get(0));
        }
        if (authorsArray.size() > 1) {
            authors.append(authorsArray.get(0));
            for (int i = 1; i < authorsArray.size(); i++) {
                authors.append(", " + authorsArray.get(i));
            }
        }

        authorTextView.setText(authors);

        if(currentBook.getSmallThumbnail() != null) {
            ImageView smallThumbnail = (ImageView) listItemView.findViewById(R.id.small_thumbnail);

            smallThumbnail.setImageBitmap(currentBook.getSmallThumbnail());
        }



        return listItemView;
    }

}
