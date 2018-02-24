package com.peterponterio.flickrbrowser;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;

public class SearchActivity extends BaseActivity {

    private static final String TAG = "SearchActivity";
    private SearchView mSearchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        activateToolbar(true);
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: starts");
        //inflate the menu
        getMenuInflater().inflate(R.menu.menu_search, menu);

        /*
            the SearchManager provides access to the system search services and a way to get a SearchManager
            instance is to call getSystemService with a search_service constant to identify which service
            we want.

            We then get a reference to the SearchView widget thats embedded in the search menu item of the
            toolbar. To retrieve that menu item, we use the findItem method and provide the id that we used
            when we created the search menu item app_bar_search.

            we added the searchView widget using the actionView class property and to retrieve it we used the
            getActionView method

            Next, we get the SearchManager to retrieve the searchableInfo from searchable.xml by calling
            its getSearchableInfo method and passing it the component name of the activity that we want the
             information for.

             The searchableInfo is then set into the searchView widget to configure it
         */
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        mSearchView.setSearchableInfo(searchableInfo);
//        Log.d(TAG, "onCreateOptionsMenu: " + getComponentName().toString());
//        Log.d(TAG, "onCreateOptionsMenu: hint is " + mSearchView.getQueryHint());
//        Log.d(TAG, "onCreateOptionsMenu: searchable info is " + searchableInfo.toString());

        //when set to false, after clicking the search icon the first time, the search box instantly pops up
        //when set to true, you must click search icon 2 different times to get search box
        mSearchView.setIconified(false);


        /*
            calling onQueryTextListener method and passing an anonymous class that implements the
            onQueryTextListener interface

            finish method closes the activity, brings you back to mainActivity
             */
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: called");

                /*
                    get a sharedPreferences object by calling the PreferenceManager's getDefaultSharedPreferences
                    method and passing it a context.

                    here we want to pass the application context rather than using 'this' because the data
                    is going to be retrieved by a different activity to the one that saved it

                    so searchActivity will store the data and main activity will retrieve it

                    call the edit method to put the sharedPreferenced into a writable state. Then we use
                    putString to store the search query using the FLICKR_QUERY constant in baseActivity
                    as the key. The data is actually stored when we call the apply method

                    this is how we save the data
                 */
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sharedPreferences.edit().putString(FLICKR_QUERY, query).apply();

                mSearchView.clearFocus(); //takes us back to previous screen
                finish();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                finish();
                return false;
            }
        });



        Log.d(TAG, "onCreateOptionsMenu: returned " + true);

        return true;
    }
}
