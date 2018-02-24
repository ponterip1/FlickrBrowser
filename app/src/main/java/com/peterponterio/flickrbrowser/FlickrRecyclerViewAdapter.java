package com.peterponterio.flickrbrowser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by peterponterio on 2/17/18.
 */


//use a generic type parameter to ensure that only our view holder objects can be used with this adapter
class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder> {
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhotoList;
    private Context mContext;

    public FlickrRecyclerViewAdapter(Context context, List<Photo> photoList) {
        mContext = context;
        mPhotoList = photoList;
    }





    //inflate a view from the browse xml layout and then return the view
    @Override
    public FlickrImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //called by the layout manager when it needs a new view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse, parent, false);
        return new FlickrImageViewHolder(view);
    }



    //called by the recyclerView when it wants new data to be stored in a viewholder so that it can
    //display it. as the items are scolled off the screen, the recycler view will provide a recycled viewholder
    //object and tell us the position of the data object that it needs to display
    @Override
    public void onBindViewHolder(FlickrImageViewHolder holder, int position) {
        // called by the layout manager when it wants new data in an existing row


        /*
            retrieved the current photo object from the list and the recyclerView helps us here because
            it tells us the position of the data we need in the position parameter, so we can retrieve the
            exact photo from our ArrayList that weve already saved in this object

            logging what was retrieved

            using the picasso.width method to get a picasso object. The picasso class is a singleton, so instead
            of using 'new' to create a new object, we use that static method that makes sure theres only ever
            one picasso object in our app

            the load method loads an image from a URL and we store the thumbnail URL in the image field of the
            photo class. Sets the placeholder image to be used if theres an error.

            .into is where we store the downloaded image into the imageview widget in the viewholder

            IF no image can be found, display thumbnail and 'no photos match method'

         */
        if((mPhotoList == null) || (mPhotoList.size() == 0)) {
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.empty_photo);
        } else {
            Photo photoItem = mPhotoList.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + " --> " + position);
            Picasso.with(mContext).load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);

            holder.title.setText(photoItem.getTitle());
        }
    }





    @Override
    public int getItemCount() {
        //returns the numbers of photos in the list
        //the 1 is the 1 item that is our placeholder when theres no image found for a search
        return ((mPhotoList != null) && (mPhotoList.size() !=0) ? mPhotoList.size() : 1);
    }



    //when the query changes and new data is downloaded, we need to be able to provide the adapter with
    //the new list
    void loadNewData(List<Photo> newPhoto) {
        mPhotoList = newPhoto;
        notifyDataSetChanged(); //tells the recyclerview that the data has changed so it can refresh the display
    }


    public Photo getPhoto(int position) {
        //if the lust isnt null and it has atleast one item, then were going to return that requested item
        //otherwise return null which is an indication that theres either no records or for some reason
        //the mphotos list field wasnt initialized
        return ((mPhotoList != null) && (mPhotoList.size() != 0) ? mPhotoList.get(position) : null);
    }



    /*
        when using a recyclerView, the viewholder has to be available to our adapter, which is in the
        same package as the viewholder, but it also has to be available for the RecyclerView to use. So
        it has to be a package private class
     */


    //by making it static, it behaves just like an ordinary top level class
    static class FlickrImageViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder starts");
            this.thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            this.title = (TextView) itemView.findViewById(R.id.title);
        }
    }
}
