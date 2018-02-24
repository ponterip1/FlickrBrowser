package com.peterponterio.flickrbrowser;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by peterponterio on 8/23/17.
 */

//IDLE means its not processing anything at this time
//PROCESSING means its downloading the data
//NOT_INITIALIZED means we havent got a valid URL to download. its an error condition because we wont set this untill the
//download has been attempted
//FAILED_OR_EMPTY means we either failed to download anything or the data came back empty
//OK means we have some valid data and the download was successful
enum DownloadStatus { IDLE, PROCESSING, NOT_INITIALIZED, FAILED_OR_EMPTY, OK }

class GetRawData extends AsyncTask<String, Void, String> {

    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    /* Theres nothing to guarantee that the main activity actually has an onDownloadComplete method so what we need to do is
     * define an interface that the callback object must implement. Anything that implements the interface guarantees it will implement
     * the methods specified
     *
     * To define an interface, we declare it in a very similar way to declaring a class. Then we specify the methods that must be implemented
     * by anything that implements the interface
     */
    interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    //sets status to idle
    public GetRawData(OnDownloadComplete callback) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallback = callback;
    }


    /*
        When you call the execute method of an async task, it creates a new thread and runs the
        doInBackground method.
        When that completes, the onPostExecute method is called on the main thread
     */
    void runInSameThread(String s) {
        Log.d(TAG, "runInSameThread starts");

        //onPostExecute(doInBackground(s));

        if(mCallback != null) {
            mCallback.onDownloadComplete(doInBackground(s),mDownloadStatus);
        }

        Log.d(TAG, "runInSameThread ends");
    }


    /*
        Since GetRawData extends async task it can be used as either an async task object or it can
        also be used as a regular GetRawData object.
        So when we call the execute method, we are treating it as an async task because the execute
        method exists in the async tasks superclass to run doInBackground on a separate thread.
        But when we run the inSameThread method, we are treating GetRawData as a GetRawData object.
        Theres no superclass involved and no background thread created
     */


    @Override
    protected void onPostExecute(String s) {
        //Log.d(TAG, "onPostExecute: parameter = " + s);

        //when the download finishes and the onPostExecute method is called, its going to call the MainActivity's onDownloadComplete method
        //and provide it with the data and the status result
        if(mCallback != null) {
            mCallback.onDownloadComplete(s, mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");
    }



    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;


        //checking to see whether we've been given a url when the methods called
        if(params == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }

        try {
            //create a url from the string parameter
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(params[0]);

            //opens connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET"); //uses GET request
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was " + response);

            StringBuilder result = new StringBuilder();

            /* Setup buffered reader to read data on the input stream and then adding that to the string builder.
             * Its basically going through the data until we've got no more data to read. (while loop)
             * Reading one line at a time.
             * When using readLine, the newline characters are stripped off at the end of each line that we get,
             * so we have to append them back onto the string that we're building up
             */
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            //putting null first to draw attention to the assignment thats happening before the conditions test it
            //line gets the result of the readLine method, then its tested to see if its null
            //so by putting null first, anyone reading the code is forced to pause and have a good look at whats actually happening
            while(null != (line = reader.readLine())) {
                result.append(line).append("\n");
            }

            /* Another way to go through the data and read one line at a time, using a for loop.
             * advantage being the variable line is only defined within the loop.
             * for loop creates a string called line with its initial value being the result of readLine.
             * the condition part of the for loop is the test for null.
             * Each time around, line is given the result of reading a line from the reader again.
             */
//            for(String line = reader.readLine(); line != null; line = reader.readLine()) {
//                result.append(line).append("\n");
//            }


            //we get through the loop without any exceptions being thrown
            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        //catches exceptions
        } catch(MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL " + e.getMessage());
        } catch(IOException e) {
            Log.e(TAG, "doInBackground: IO Exception Reading Data " + e.getMessage());
        } catch(SecurityException e) {
            Log.e(TAG, "doInBackground: Security Exception. Needs Permission? " + e.getMessage());
        } finally {
            //a finally block is guaranteed to run, whether an exception is thrown or not
            //a good place to do things like closing streams and readers
            //finally is executed right before the method returns
            //so if no exception is thrown, finally will run, then return result.toString() in the try block will run
            //if exception is thrown, finally will run, then return null will run
            if(connection != null) {
                connection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream " + e.getMessage());
                }
            }
        }

        //if exceptions are thrown
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
    
    

}
