package com.peterponterio.flickrbrowser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrJsonData.OnDataAvailable,
        RecyclerItemClickListener.OnRecyclerClickListener
{
    private static final String TAG = "MainActivity";
    private FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //we dont want the home button on the main screen
        activateToolbar(false);

        
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //creates an instance of our recycler item click listener class and add it as a touch listener
        //to the recyclerView
        //We can pass 'this' as both the context because its an activity and the listener because weve
        //implemented the required interface
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));


        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);
        

        Log.d(TAG, "onCreate: ends");
    }


    @Override
    protected void onResume() {
        Log.d(TAG, "onResume starts");
        super.onResume();


        /*
            created a sharedPreferences object using the PreferenceManager.

            Used the getString method to read the search string from the stored sharedPreferences

            The getString method attempts to retrieve the data stored with the key and uses that default
            value if it doesnt find anything
         */
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY, "");//if theres no value return empty string instead of null

        //make sure queryResult string isnt empty before attempting to download and parse the data
        if(queryResult.length() > 0){
            GetFlickrJsonData getFlickrJsonData = new GetFlickrJsonData(this, "https://api.flickr.com/services/feeds/photos_public.gne", "en-us", true);
            getFlickrJsonData.execute(queryResult);
        }

        Log.d(TAG, "onResume ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        /*
            launch the search activity, starts a new activity using an intent
         */
        if(id == R.id.action_search) {
            Intent intent = new Intent(this, SearchActivity.class);//class we want to invoke
            startActivity(intent);
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected() returned: returned");
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status) {
        Log.d(TAG, "onDataAvailable: starts");
        if(status == DownloadStatus.OK) {
            mFlickrRecyclerViewAdapter.loadNewData(data);
        } else {
            //download or processing failed
            Log.d(TAG, "onDataAvailable failed with status " + status);
        }

        Log.d(TAG, "onDataAvailable: ends");
    }


    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: starts");
        Toast.makeText(MainActivity.this, "Normal tap at position " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: starts");
//        Toast.makeText(MainActivity.this, "Long tap at position " + position, Toast.LENGTH_SHORT).show();


        /*
            created a new intent and used 'this' as the context because this class extends baseActivity
            which extends appCompatActivity so our mainActivity class is an activity and can be used
            whenever a context is required

            The second parameter is the activity class that we want to launch

            Here we have to tell the photoDetailActivity which photo it should display using the putExtra
            method to add data to the intent. putExtra method stores a photo object in the intent. The
            key we use comes from the PHOTO_TRANSFER constant, and we get the actual photo by using the
            getPhoto method of the adapter and telling it the position of the photo we want. The position
            parameter passed to this onItemLongClick comes from the recyclerView in which its confirming
            the position after we tapped the image
         */
        Intent intent = new Intent(this, PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER, mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}

