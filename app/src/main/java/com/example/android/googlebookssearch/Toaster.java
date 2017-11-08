package com.example.android.googlebookssearch;

import android.widget.Toast;

/**
 * Created by Gavin on 07/11/2017.
 */

public class Toaster {
    public static void show(String text) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(GoogleBooksApplication.getContext(), text, duration);
        toast.show();

    }
}

