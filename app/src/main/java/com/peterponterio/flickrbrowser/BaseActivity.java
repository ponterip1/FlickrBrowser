package com.peterponterio.flickrbrowser;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

/**
 * Created by peterponterio on 2/20/18.
 */

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";

    static final String FLICKR_QUERY = "FLICKR_QUERY";
    static final String PHOTO_TRANSFER = "PHOTO_TRANSFER";

    /*
        method to show tool bar and it will allow an activity to choose whether the toolbar should have
        the home button showing or not.

        uses the getSupportActionBar method to get a reference to the action bar so that we can add to it

        If there is an actionBar, we inflate the toolbar from the toolbar xml file. Then we use the
        setSupportActionBar method with our inflated toolbar to put the toolbar in place at the top of the
        screen

        The action bar will automatically add the home button if we tell it to, so we then get a reference
        to the new action bar and call setDisplayHomeAsUpEnabled to either enable or disable the home button
        depending on the parameters that were passed to this method
     */
    void activateToolbar(boolean enableHome) {
        Log.d(TAG, "activateToolbar: starts");
        ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            if(toolbar != null) {
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }


        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }
}
