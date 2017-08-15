package com.example.axel.appproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.IOException;


public class MainActivity extends Activity implements View.OnClickListener {

    Button btnNewReport;
    Button btnGetReports;
    Button btnMyReports;
    ImageButton btnHelp;
    AlertDialog.Builder networkDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        btnHelp = (ImageButton) findViewById(R.id.help_button);
        btnHelp.setOnClickListener(this);
        btnNewReport = (Button)findViewById(R.id.new_report);
        btnNewReport.setOnClickListener(this);
        btnGetReports = (Button) findViewById(R.id.all_reports);
        btnGetReports.setOnClickListener(this);
        btnMyReports = (Button) findViewById(R.id.my_reports);
        btnMyReports.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNetworkConnection();
    }

    private void checkNetworkConnection() {
        if(!isNetworkConnected()) {
            networkDialog = new AlertDialog.Builder(this);
            networkDialog.setMessage(R.string.network);
            networkDialog.setTitle(R.string.network_title);
            networkDialog.setCancelable(false);
            networkDialog.setPositiveButton("Försök igen", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!isNetworkConnected()) {
                        networkDialog.show();
                    }
                }
            });
            networkDialog.show();
        }
    }

    private void newReport() {
        startActivity(new Intent(MainActivity.this, IssueReportMapLocation.class));
    }

    private void getReports() {
        startActivity(new Intent(MainActivity.this,ViewReports.class));
    }

    private void myReports() {
        startActivity(new Intent(MainActivity.this,MyReports.class));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_report:
                newReport();
                break;
            case R.id.all_reports:
                getReports();
                break;
            case R.id.my_reports:
                myReports();
                break;
            case R.id.help_button:
                helpDialog();
                break;
        }
    }

    private void helpDialog() {
        AlertDialog.Builder welcomeDialog = new AlertDialog.Builder(this);
        welcomeDialog.setMessage(R.string.welcome);
        welcomeDialog.setTitle(R.string.welcome_title);
        welcomeDialog.setPositiveButton(R.string.btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        welcomeDialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    private boolean isNetworkConnected() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }
}
