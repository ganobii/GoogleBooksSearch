package com.example.android.googlebookssearch;

import android.app.Application;
import android.content.Context;

/**
 * Created by Gavin on 07/11/2017.
 */

public class GoogleBooksApplication extends Application {

    private static GoogleBooksApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static Context getContext() {
        return mContext;
    }
}
