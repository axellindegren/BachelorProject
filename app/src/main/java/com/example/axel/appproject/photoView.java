package com.example.axel.appproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewFlipper;

/**
 * Created by Viktor on 2015-05-08.
 */
public class photoView extends View {

    View rootView;
    Button photo;
    Button album;

    public photoView(Context context, ViewFlipper container, LayoutInflater inflater) {
        super(context);
        rootView = inflater.inflate(R.layout.photo_view,container,false);
        photo = (Button) rootView.findViewById(R.id.btn_photo);
        album = (Button) rootView.findViewById(R.id.btn_archive);
    }

    public Button getPhotoButton() {
        return photo;
    }

    public Button getAlbumButton() {
        return album;
    }

    public View getRootView() {
        return rootView;
    }

    public ImageView getImageView() {
        return (ImageView) rootView.findViewById(R.id.image_preview);
    }
}