package com.peterponterio.flickrbrowser;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by peterponterio on 2/19/18.
 */

class RecyclerItemClickListener extends RecyclerView.SimpleOnItemTouchListener {
    private static final String TAG = "RecyclerItemClickListen";

    

    /*
        define an interface that we can use to call back on
     */
    interface OnRecyclerClickListener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view, int position);
    }


    private final OnRecyclerClickListener mListener;
    private final GestureDetectorCompat mGestureDetector;


    /*
        we need a context for the gesture detector to work and we also need a reference to the recyclerView
        that were detecting the taps on
     */
    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnRecyclerClickListener listener) {
        mListener = listener;

        //Creating an anonymous class that extends SimpleOnGestureListener for the second parameter so that
        //we can override the methods were interested in
        mGestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "onSingleTapUp: starts");
                /*
                    Using the motion event thats passed as a parameter, they check to see which view
                    is underneath it by using the coordinates on the screen that were tapped. its getting
                    that with the e.getx and e.gety.
                    The findChildViewUnderMethod checks to see what was underneath the X,Y coordinates
                    of the tap and returns the view that it found, if any
                    Now here, there will always be a view because our linear layout thats used by the recycler
                    view will result in the items appearing one below the other with no gap inbetween them
                 */
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && mListener != null) {
                    Log.d(TAG, "onSingleTapUp: calling listener.onItemClick");
                    mListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.d(TAG, "onLongPress: starts");
                /*
                    Using the motion event thats passed as a parameter, they check to see which view
                    is underneath it by using the coordinates on the screen that were tapped. its getting
                    that with the e.getx and e.gety.
                    The findChildViewUnderMethod checks to see what was underneath the X,Y coordinates
                    of the tap and returns the view that it found, if any
                    Now here, there will always be a view because our linear layout thats used by the recycler
                    view will result in the items appearing one below the other with no gap inbetween them
                 */
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if(childView != null && mListener != null) {
                    Log.d(TAG, "onLongPress: calling listener.onItemLongClick");
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }

        });
    }

    
    
    
    //the method will be called whenever any sort or touch happens, whether its a tap, double tap, swipe, etc
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        Log.d(TAG, "onInterceptTouchEvent: starts");

        /*
            Anything the gesture detectors onTouchEvent method deals with should return true. Anything it
            doesnt handle should return false, so that something else can deal with it
         */
        if(mGestureDetector != null) {
            boolean result = mGestureDetector.onTouchEvent(e);
            Log.d(TAG, "onInterceptTouchEvent: returned: " + result );
            return result;
        } else {
            Log.d(TAG, "onInterceptTouchEvent: returned: false");
            return false;
        }

    }

}
