package com.peterponterio.flickrbrowser;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        activateToolbar(true);

        /*
            use the getIntent method to retrieve the intent that started this activity. To retrieve the
            photo object, we use the getSerializableExtra method because the photo objects are serialized.
            we cast that to a Photo to make sure we get the right type back. Using the PHOTO_TRANSFER key
            to make sure that we retrieve the same value that was stored in main activity.
         */
        Intent intent = getIntent();
        Photo photo = (Photo) intent.getSerializableExtra(PHOTO_TRANSFER);
        if(photo != null) {
            //calling getResources
            Resources resources = getResources();

            /*
                get a reference to each of the widgets in the layout and set their values to display
                the fields of the photo object
             */

            TextView photoTitle = (TextView) findViewById(R.id.photo_title);
            /*
                uses resources getString method to get the string with the ID photo_title_text from the
                strings.xml resource file. the getString method will replace any place holders with the
                actual value, which we get by retrieving the title field of our photo objects. The getString
                method takes care of replacing the placeholders with the values that we specify.
             */
            photoTitle.setText(resources.getString(R.string.photo_title_text, photo.getTitle()));


            TextView photoTags = (TextView) findViewById(R.id.photo_tags);
            /*
                uses resources getString method to get the string with the ID photo_tags_text from the
                strings.xml resource file. the getString method will replace any place holders with the
                actual value, which we get by retrieving the tags field of our photo objects. The getString
                method takes care of replacing the placeholders with the values that we specify.
             */
            photoTags.setText(resources.getString(R.string.photo_tags_text, photo.getTags()));


            TextView photoAuthor = (TextView) findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());

            ImageView photoImage = (ImageView) findViewById(R.id.photo_image);
            /*
                we dont have a context stored in a field and we dont need one cause an activity is a context
                we can just use 'this' as the context
             */
            Picasso.with(this).load(photo.getLink())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(photoImage);




        }
    }

}
