package com.peterponterio.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by peterponterio on 2/17/18.
 */

//implemented the interface so we can get the callbacks from GetRawData
class GetFlickrJsonData extends AsyncTask<String, Void, List<Photo>> implements GetRawData.OnDownloadComplete {
    private static final String TAG = "GetFlickrJsonData";

    //store a list of photo objects that we parse out of the json data
    private List<Photo> mPhotoList = null;
    private String mBaseURL;
    private String mLanguage;
    private boolean mMatchAll;


    /*This class calls GetRawData which runs asynchronously on a background thread which means
        anything using this GetFlickrJsonData class wont get any data back immediately
      So we're going to use the same callback mechanism as we did for GetRawData by creating a field
        to store the callback object and then define an interface
     */

    private final OnDataAvailable mCallBack;
    private boolean runningOnSameThread = false;

    interface OnDataAvailable {
        void onDataAvailable(List<Photo> data, DownloadStatus status);
    }


    public GetFlickrJsonData(OnDataAvailable callBack, String baseURL, String language, boolean matchAll) {
        Log.d(TAG, "GetFlickrJsonData call");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }


    //the main activity's OnCreate method where it creates and gets a new GetRawData object and then
    //calls the execute method
    //Before GetRawData is created and its execute method is called, we build up the URL so that its got
    //the correct parameters
    void executeOnSameThread(String searchCriteria) {
        Log.d(TAG, "executeOnSameThread starts");
        runningOnSameThread = true;
        String destinationUri = createUri(searchCriteria, mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(destinationUri);
        Log.d(TAG, "executeOnSameThread ends");
    }


    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute starts");

        if(mCallBack != null) {
            mCallBack.onDataAvailable(mPhotoList, DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute ends");
    }

    @Override
    protected List<Photo> doInBackground(String... params) {
        Log.d(TAG, "doInBackground starts");
        String destinationUri = createUri(params[0], mLanguage, mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground ends");
        return mPhotoList;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll) {
        Log.d(TAG, "createUri starts");

        /* We need to build the parameters ontop of the url so we create this uri builder object and
           get a builder using the buildUpon methods, which returns a builder we can use

           Using the appendQueryParameter method to add each parameter to the uri.

           The appendQueryParameter method takes care of separating the parameters with either a
           question mark or an ampersand and makes sure that each step results in a valid uri.

           So each time we are appending another parameter and getting a builder object back with the
           new parameters added to the end of the previous value.
        */
        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags", searchCriteria)
                .appendQueryParameter("tagmode", matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .build().toString();
    }



    @Override
    public void onDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "onDownloadComplete starts. Status = " + status);

        //if download status is ok, we have some data to process
        //As long as status is ok we can assign new arraylist to the photolist and then
        //process the json code
        if(status == DownloadStatus.OK) {
            mPhotoList = new ArrayList<>(); //initialize to new array list to clear it out, ready to receive the data

            //get the array object and then we can go through the individual items and access
            //the values that we want back
            try{
                JSONObject jsonData = new JSONObject(data);
                JSONArray itemsArray = jsonData.getJSONArray("items");

                //process the individual data
                //going through each array entry that is in the JSON data
                for(int i = 0; i<itemsArray.length(); i++) {
                    JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                    String title = jsonPhoto.getString("title");
                    String author = jsonPhoto.getString("author");
                    String authorID = jsonPhoto.getString("author_id");
                    String tags = jsonPhoto.getString("tags");

                    JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                    String photoUrl = jsonMedia.getString("m");

                    /*
                        photoURl will become the image field of the photo object so its parsed as the
                        last parameter to the constructor because the last parameter in the constructor
                        of the photo class sets the value of image (string image)

                        We'll be using that field to display the image for each photo in the list

                        When an item in the list is tapped, another activity is going to be launched
                        to display the photo much larger so that it fills the screen and to do that we
                        need to use the link value, which is why its separate to the photoURL

                        getImage will give us the URL of the photo to show in the initial list and
                        getLink will provide the URL of the full-size picture
                     */

                    //m and b represent a specific size of the photo, m being smaller and b being larger
                    //grabs the photo which will have an 'm' in it and replaces the 'm' with a 'b'
                    // so that the link is going to contain the url of the big version of the photo
                    String link = photoUrl.replaceFirst("_m.", "_b.");


                    //create photo object
                    Photo photoObject = new Photo(title, author, authorID, link, tags, photoUrl);
                    mPhotoList.add(photoObject); //add photo object to photo list

                    Log.d(TAG, "onDownloadComplete " + photoObject.toString());
                }
            } catch(JSONException jsone) {
                jsone.printStackTrace();
                Log.e(TAG, "onDownloadComplete: Error processing Json data " + jsone.getMessage());
                status = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        //notify the calling class that everythings done and send it the list of photos that we've created
        //only going be called if youre running on the same thread as the calling process
        if(runningOnSameThread && mCallBack != null) {
            //now inform the caller that processing is done - possibly returning null if there
            //was an error
            mCallBack.onDataAvailable(mPhotoList, status);
        }

        Log.d(TAG, "onDownloadComplete ends");
    }













}
