package com.pirko.diabetolog;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class ActivityGoogleDrive extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final int RESOLVE_CONNECTION_REQUEST_CODE=1;

    private ApplicationDiabet applicationDiabet;
    private boolean checkExport =true;
    private GoogleApiClient googleDriveClient;
    private DriveContents contents;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.pirko.diabetolog.R.layout.activity_google_drive);
        String firstTimeOpening=getIntent().getExtras().get("FirstTimeOpening").toString();
        if(firstTimeOpening.equals("true")){
            findViewById(com.pirko.diabetolog.R.id.btn_export).setEnabled(false);
        }

        activity=this;
        applicationDiabet =(ApplicationDiabet)getApplication();
        //рядок підключення до Google Drive API
        googleDriveClient = new GoogleApiClient.Builder(this)

                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        findViewById(com.pirko.diabetolog.R.id.btn_export).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(applicationDiabet.isNetworkAvailable()){
                    checkExport =true;
                    if(googleDriveClient.isConnected()) {
                        googleDriveClient.disconnect();
                    }
                    googleDriveClient.connect();
                    findViewById(com.pirko.diabetolog.R.id.btn_export).setEnabled(false);
                    findViewById(com.pirko.diabetolog.R.id.btn_import).setEnabled(false);
                    findViewById(com.pirko.diabetolog.R.id.progress_bar).setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Немає підключення до Інтернету!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(com.pirko.diabetolog.R.id.btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(applicationDiabet.isNetworkAvailable()){
                    checkExport =false;
                    if(googleDriveClient.isConnected()) {
                        googleDriveClient.disconnect();
                    }
                    googleDriveClient.connect();
                    findViewById(com.pirko.diabetolog.R.id.btn_export).setEnabled(false);
                    findViewById(com.pirko.diabetolog.R.id.btn_import).setEnabled(false);
                    findViewById(com.pirko.diabetolog.R.id.progress_bar).setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Немає підключення до Інтернету!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabet.save();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if(resultCode==RESULT_OK){
                    googleDriveClient.connect();
                }
        }
    }
    //функція що викликається під час вдалого встановлення зєднання
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Drive.DriveApi.newDriveContents(googleDriveClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                if (!result.getStatus().isSuccess()) {
                    Log.e("Error-GoogleDrive", "Error while trying to create new file contents");
                    if(googleDriveClient.isConnected()) {
                        googleDriveClient.disconnect();
                    }
                    return;
                }
                contents =result.getDriveContents();
                Drive.DriveApi.getRootFolder(googleDriveClient).listChildren(googleDriveClient).setResultCallback(resultCallback);
            }
        });
    }
    //функція що викликається під час призупинення зєднання
    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Error-GoogleDrive","Connection has been suspended!");
        if(googleDriveClient.isConnected()) {
            googleDriveClient.disconnect();
        }
    }
    //функція що викликається під час невдалого встановлення зєднання
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Error-GoogleDrive","Connection has failed!");
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this,RESOLVE_CONNECTION_REQUEST_CODE);
            }catch (IntentSender.SendIntentException e){
                Log.e("Error-GoogleDrive","Connection resolution has failed!");
                if(googleDriveClient.isConnected()) {
                    googleDriveClient.disconnect();
                }
            }
        }
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> resultCallback=new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(@NonNull DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error-GoogleDrive","Problem while retrieving files");
                        googleDriveClient.disconnect();
                        return;
                    }
                    Log.i("Info-GoogleDrive","Successfully got folder data!");
                    int countOfFiles=result.getMetadataBuffer().getCount();
                    if(checkExport) {
                        Log.i("Info-GoogleDrive","Exporting data...");
                        if(countOfFiles!=0) {
                            Log.i("Info-GoogleDrive","The AppFolder already contains a file or multiple files.");
                            for (int i=0; i < countOfFiles; i++) {
                                if(result.getMetadataBuffer().get(i).getTitle().equals("backup_DiabetoLog1.txt")){
                                    Log.i("Info-GoogleDrive","Deleting file:"+result
                                            .getMetadataBuffer()
                                            .get(i)
                                            .getTitle()
                                    );
                                    DriveFile driveFile=DriveId.decodeFromString(
                                            result
                                                    .getMetadataBuffer()
                                                    .get(i).getDriveId()
                                                    .toString()
                                    ).asDriveFile();
                                    driveFile.delete(googleDriveClient);
                                }
                            }
                        }
                        String title="backup_DiabetoLog1.txt";
                        OutputStream outputstream= contents.getOutputStream();
                        MetadataChangeSet backupFile=new MetadataChangeSet.Builder()
                                .setTitle(title)
                                .setMimeType("text/plain")
                                .build();
                        Writer writer= new OutputStreamWriter(outputstream);
                        try {
                            writer.write(applicationDiabet.getExportData());
                            writer.close();
                        }catch (IOException e){
                            Log.e("Error-GoogleDrive","Error while writing to file!");
                        }
                        Drive.DriveApi.getRootFolder(googleDriveClient)
                                .createFile(googleDriveClient, backupFile, contents)
                                .setResultCallback(fileCallback);
                        result.getMetadataBuffer().release();
                    }else{
                        Log.i("Info-GoogleDrive","Importing data..."+countOfFiles);
                        DriveFile driveFile=null;
                        for (int i=0; i < countOfFiles; i++) {
                            if(result.getMetadataBuffer().get(i).getTitle().equals("backup_DiabetoLog1.txt")){
                                Metadata metadata=result.getMetadataBuffer().get(i);
                                Log.i("Info-GoogleDrive","Файл знайдено"+metadata.getTitle()+" "+metadata.getDriveId()+" "+metadata.isTrashed());
                                Log.i("Info-GoogleDrive","Importing .." + metadata.getTitle());
                                driveFile=DriveId.decodeFromString(
                                        result
                                                .getMetadataBuffer()
                                                .get(i).getDriveId()
                                                .toString()
                                ).asDriveFile();
                            }
                        }
                        if(driveFile!=null){
                            Log.i("Info-GoogleDrive","Файл пустий");
                            driveFile.open(googleDriveClient, DriveFile.MODE_READ_ONLY,null).setResultCallback(
                                    new ResultCallback<DriveApi.DriveContentsResult>() {
                                        @Override
                                        public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                                            if (!result.getStatus().isSuccess()) {
                                                Log.e("Error-GoogleDrive","Problem while retrieving file contents for import");
                                                googleDriveClient.disconnect();
                                                return;
                                            }
                                            Log.i("Info-GoogleDrive","Calling save function...");
                                            contents =result.getDriveContents();
                                            boolean importSuccess= applicationDiabet.saveImportData(
                                                    new BufferedReader(
                                                            new InputStreamReader(
                                                                    contents.getInputStream()
                                                            )
                                                    )
                                            );
                                            if(importSuccess){
                                                activity.finish();
                                            }else{
                                                applicationDiabet.IMPORT_FAILED=true;
                                                activity.finish();
                                            }
                                        }
                                    }
                            );
                        }else{
                            applicationDiabet.IMPORT_FAILED=true;
                            applicationDiabet.NO_FILES_FOUND=true;
                            activity.finish();
                        }
                        result.getMetadataBuffer().release();
                        contents.discard(googleDriveClient);
                    }
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback=new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.e("Error-GoogleDrive","Error while trying to create the file");
                        applicationDiabet.EXPORT_FAILED=true;
                        googleDriveClient.disconnect();
                        return;
                    }
                    Log.i("Info-GoogleDrive","File was succesfully created in the app folder on Google Drive.");
                    applicationDiabet.EXPORT=true;
                    googleDriveClient.disconnect();
                    activity.finish();
                }
            };
}
