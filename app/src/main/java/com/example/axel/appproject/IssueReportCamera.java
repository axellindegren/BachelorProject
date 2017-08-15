package com.example.axel.appproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Axel on 2015-04-15.
 */
public class IssueReportCamera extends Activity implements View.OnClickListener {

    Button button_take_photo;
    Button button_view_next;
    Button button_from_archive;
    ImageView taken_photo;

    private static final int CAM_REQUEST = 1313;
    private static final int PICK_IMAGE = 100;
    FileOutputStream fos = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_report);
        button_take_photo = (Button) findViewById(R.id.button_take_photo);
        button_view_next = (Button) findViewById(R.id.button_view_next);
        taken_photo = (ImageView) findViewById(R.id.imageViewPhoto);
        button_from_archive = (Button) findViewById(R.id.button_from_archive);

        sendCameraIcon();

        button_take_photo.setOnClickListener(this);
        button_from_archive.setOnClickListener(this);
        button_view_next.setOnClickListener(this);

        createFont();
    }

    public void createFont() {
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "fonts/SourceSansPro-Bold.otf");

        Button buttonFont = (Button) findViewById(R.id.button_take_photo);
        buttonFont.setTypeface(myTypeface);

        buttonFont = (Button) findViewById(R.id.button_from_archive);
        buttonFont.setTypeface(myTypeface);

        buttonFont = (Button) findViewById(R.id.button_view_next);
        buttonFont.setTypeface(myTypeface);
    }

    public void sendCameraIcon(){
        Bitmap thumbnail = BitmapFactory.decodeResource(getResources(), R.drawable.camera_icon);
        try {
            fos = openFileOutput("BITMAP_A", Context.MODE_PRIVATE);
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAM_REQUEST && resultCode==RESULT_OK) {

            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");

            taken_photo.setImageBitmap(thumbnail);

            // store bitmap in FilOutputStream
            try {
                fos = openFileOutput("BITMAP_A", Context.MODE_PRIVATE);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE) {
            Uri imageUri = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(imageUri, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            //BitmapFactory.Options options = new BitmapFactory.Options();
            //options.inSampleSize = 2;
            Bitmap thumbnail = BitmapFactory.decodeFile(picturePath);
            int nh = (int) (thumbnail.getHeight()*(512.0/thumbnail.getWidth()));
            Bitmap scaled = Bitmap.createScaledBitmap(thumbnail,512,nh,true);
            taken_photo.setImageBitmap(scaled);
            try {
                fos = openFileOutput("BITMAP_A", Context.MODE_PRIVATE);
                scaled.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_take_photo:
                Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camera_intent, CAM_REQUEST);
                break;
            case R.id.button_from_archive:
                Intent archive_intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                archive_intent.setType("image/*");
                startActivityForResult(archive_intent, PICK_IMAGE);
                break;
            case R.id.button_view_next:
                Intent i = new Intent(IssueReportCamera.this,IssueReportMapLocation.class);
                startActivity(i);
        }
    }
}
