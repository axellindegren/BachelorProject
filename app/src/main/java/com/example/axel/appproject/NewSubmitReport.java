package com.example.axel.appproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewFlipper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class NewSubmitReport extends Activity implements View.OnTouchListener {

    ImageButton btnAddPhoto;
    ImageButton btnSummary;
    ImageButton btnAddDescription;
    ImageButton btnHome;
    ImageButton btnHelp;
    AlphaAnimation buttonClick;
    ViewFlipper flipper;
    LayoutInflater inflater;
    photoView photoView;
    descView descView;
    SummaryView summaryView;
    Context context;
    ContentValues values;
    Uri cameraUri;
    Uri archiveUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_submit_report);
        setupUI(findViewById(R.id.Main));
        context = this;

        values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Image");
        values.put(MediaStore.Images.Media.DESCRIPTION,"From camera");
        cameraUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        flipper = (ViewFlipper) findViewById(R.id.flipper);
        inflater = (LayoutInflater) getSystemService(this.LAYOUT_INFLATER_SERVICE);

        btnHome = (ImageButton) findViewById(R.id.home_button);
        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewSubmitReport.this,MainActivity.class));
            }
        });
        btnHelp = (ImageButton) findViewById(R.id.help_button);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.submit_report_help);
                builder.setTitle(R.string.help);
                builder.setPositiveButton(R.string.btn_label,null);
                builder.show();
            }
        });

        photoView = new photoView(this, flipper, inflater);
        descView = new descView(this, flipper, inflater);
        summaryView = new SummaryView(this,flipper,inflater);

        summaryView.setDefaultValues();

        flipper.addView(photoView.getRootView());
        flipper.addView(descView.getRootView());
        flipper.addView(summaryView.getRootView());
        flipper.setDisplayedChild(0);

        buttonClick = new AlphaAnimation(1F, 0.4F);

        btnAddPhoto = (ImageButton) findViewById(R.id.cameraIcon);
        btnAddPhoto.setOnTouchListener(this);
        btnAddDescription = (ImageButton) findViewById(R.id.penIcon);
        btnAddDescription.setOnTouchListener(this);
        btnSummary = (ImageButton) findViewById(R.id.summaryIcon);
        btnSummary.setOnTouchListener(this);

        btnAddPhoto.setImageResource(R.drawable.camera_onselect);
        buttonClick.setFillAfter(true);
        btnSummary.startAnimation(buttonClick);
        btnAddDescription.startAnimation(buttonClick);

        photoView.getPhotoButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

        photoView.getAlbumButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent album_intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                album_intent.setType("image/*");
                startAlbum(album_intent);
            }
        });
    }

    public void getMapScreenshot() {
        Bitmap bm = null;
        String filename = "BITMAP_MAP";
        try {
            FileInputStream fis = this.openFileInput(filename);
            bm = BitmapFactory.decodeStream(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bm = ThumbnailUtils.extractThumbnail(bm,bm.getWidth(),bm.getWidth());
        Bitmap roundcorners = new Roundcorners(bm).getRoundBitmap();
        summaryView.setMapImage(roundcorners);
    }

    public void startCamera() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camera_intent.putExtra(MediaStore.EXTRA_OUTPUT,cameraUri);
        startActivityForResult(camera_intent, 1);
    }

    public void startAlbum(Intent intent) {
        startActivityForResult(intent,2);
    }

    //Metod for att gömma tangentbordet när användaren trycker utanför en EditText ruta
    public void setupUI(View view) {

        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard();
                    return false;
                }
            });
        }
        if (view instanceof ViewFlipper) {
            for (int i = 0; i < flipper.getChildCount(); i++) {
                View innerView = flipper.getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(this.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus()!=null) {
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void setAdress() {

        Intent coordIntent = getIntent();
        Bundle extrasBundle = coordIntent.getExtras();
        summaryView.setAddress(extrasBundle.getString("Address"), extrasBundle.getString("Latitude"), extrasBundle.getString("Longitude"));
    }

    private Bitmap getRoundedBitmap(Uri image) {
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = getContentResolver().query(image, filePath, null, null, null);
        c.moveToFirst();
        int columnIndex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnIndex);
        c.close();
        Bitmap thumbnailFirst = BitmapFactory.decodeFile(picturePath);
        thumbnailFirst = ThumbnailUtils.extractThumbnail(thumbnailFirst, thumbnailFirst.getWidth(), thumbnailFirst.getWidth());
        Bitmap compressed = Bitmap.createScaledBitmap(thumbnailFirst,400,400,false);
        return new Roundcorners(compressed).getRoundBitmap();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bitmap thumbnail = getRoundedBitmap(cameraUri);
            summaryView.setImage(thumbnail);
            photoView.getImageView().setImageBitmap(thumbnail);
        }

        if(requestCode==2 && resultCode==RESULT_OK) {
            archiveUri = data.getData();
            Bitmap thumbnail = getRoundedBitmap(archiveUri);
            summaryView.setImage(thumbnail);
            photoView.getImageView().setImageBitmap(thumbnail);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.cameraIcon:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.clearAnimation();
                        btnAddDescription.setImageResource(R.drawable.desc);
                        btnSummary.setImageResource(R.drawable.summary);
                        btnAddDescription.startAnimation(buttonClick);
                        btnSummary.startAnimation(buttonClick);
                        btnAddPhoto.setImageResource(R.drawable.camera_onselect);
                        break;
                    case MotionEvent.ACTION_UP:
                        hideSoftKeyboard();
                        flipper.setDisplayedChild(flipper.indexOfChild(photoView.getRootView()));
                }
                break;
            case R.id.penIcon:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.clearAnimation();
                        btnSummary.setImageResource(R.drawable.summary);
                        btnAddPhoto.setImageResource(R.drawable.camera);
                        btnSummary.startAnimation(buttonClick);
                        btnAddPhoto.startAnimation(buttonClick);
                        btnAddDescription.setImageResource(R.drawable.desc_onselect);
                        break;
                    case MotionEvent.ACTION_UP:
                        flipper.setDisplayedChild(flipper.indexOfChild(descView.getRootView()));
                }
                break;
            case R.id.summaryIcon:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        v.clearAnimation();
                        btnAddPhoto.setImageResource(R.drawable.camera);
                        btnAddDescription.setImageResource(R.drawable.desc);
                        btnAddPhoto.startAnimation(buttonClick);
                        btnAddDescription.startAnimation(buttonClick);
                        btnSummary.setImageResource(R.drawable.summary_onselect);
                        break;
                    case MotionEvent.ACTION_UP:
                        hideSoftKeyboard();
                        setAdress();
                        getMapScreenshot();
                        summaryView.setDesc(descView.getDesc());
                        System.out.println("Tom beskrivning: " + descView.getDesc().getClass());
                        summaryView.setCategory(descView.getSelectedItem());
                        System.out.println("Tom Kategori: " + descView.getSelectedItem());
                        flipper.setDisplayedChild(flipper.indexOfChild(summaryView.getRootView()));
                }
                break;
        }
        return false;
    }
}