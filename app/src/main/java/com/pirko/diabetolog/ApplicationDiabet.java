package com.pirko.diabetolog;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.TimePickerDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import Data.Activitys_record;
import Data.AddRecord;
import Data.Eat_record;
import Data.Notification;
import Data.User;

public class ApplicationDiabet extends Application
{
    private User user;

    private static final String FILE_NAME ="AddRecordi.json";
    private static final String FILE_notify ="Notification.json";
    private static final String MAP_NAME="Data";
    boolean IMPORT=false,IMPORT_FAILED=false, EXPORT=false, EXPORT_FAILED=false, NO_FILES_FOUND=false;

    public User getUser(){
        return user;
    }

    public void setUser(){
        user=new User();
    }

    public void showTimePickerDialog(int h, int m, View v) {

        final TextView tvTime= v.findViewById(R.id.tv_reminder_time_desc1);
        final LocalData localData = new LocalData(v.getContext());
        final View v1=(View) v;
        TimePickerDialog builder = new TimePickerDialog(v1.getContext(), R.style.DialogTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int min) {

                        localData.set_hour(hour);
                        localData.set_min(min);

                        tvTime.setText(hour + ":"+min);
                        NotificationScheduler.setReminder(v1.getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());

                    }
                }, h, m, true);


        builder.show();

    }

    public String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }}
    //запис даних у файл
    public void save(){
        Log.i("Info-AppDiabetoLog","Saving data into Json file");
        if(isExternalStorageWritable()){
            File[] files=new File[1];

            files[0]=new File(this.getExternalFilesDir(MAP_NAME), FILE_NAME);
       //     files[1]=new File(this.getExternalFilesDir(MAP_NAME), FILE_notify);
            Gson gson=new GsonBuilder().setPrettyPrinting().create();
            PrintWriter printWriter;
            try{
                for(int i=0;i<files.length;i++){
                    String jsonString="";
                    switch (i){

                        case 0:
                            if(user.getAddRecord().size()!=0) {
                                RuntimeTypeAdapterFactory runtimeTypeAdapterFactory=RuntimeTypeAdapterFactory
                                        .of(AddRecord.class, "AddRecord")
                                        .registerSubtype(AddRecord.class, "AddRecord")
                                        .registerSubtype(Eat_record.class, "Products")
                                        .registerSubtype(Activitys_record.class, "Activitys");
                                gson=new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                                jsonString=gson.toJson(user.getAddRecord(), new TypeToken<ArrayList<AddRecord>>() {
                                }.getType());
                            }
                            break;
                     /*   case 1:
                            if(user.getNotify().size()!=0) {
                                ArrayList<Notification> arrayList = user.getNotify();
                                for(int j=0;j<arrayList.size();j++){
                                    if(arrayList.get(j).toString()!=null) {
                                        jsonString += arrayList.get(j).toString()+"\n";
                                    }
                                }
                            }
                            break;*/
                    }
                    printWriter=new PrintWriter(files[i]);
                    if(!jsonString.equals("")){
                        Log.i("Info-AppDiabetoLog",i+" "+files[i].getName());
                        printWriter.println(jsonString);
                        printWriter.close();
                    } else {
                        if(files[i].delete()){
                            Log.i("Info-AppDiabetoLog","Nothing to save, file was succesfully deleted");
                        }
                    }

                }
            }catch (FileNotFoundException e){
                Log.e("ERROR-AppDiabetoLog","FILE NOT FOUND!");
            }
        } else {
            Log.e("ERROR-AppDiabetoLog","STORAGE IS NOT WRITABLE!");
        }
        Log.i("Info-AppDiabetoLog","Saving is completed.");
    }

    public boolean wasLoadSuccesfull(){
        user=load();
        if(user!=null){
            return true;
        }else {
            user=new User();
        }
        return false;
    }

    @Nullable
    public User load(){
        if(isExternalStorageReadable()){
            User userLoad=new User();
            File[] files=new File[1];
            files[0]=new File(this.getExternalFilesDir(MAP_NAME), FILE_NAME);
          //  files[]=new File(this.getExternalFilesDir(MAP_NAME), FILE_notify);
            FileInputStream fileInputStream;
            BufferedReader bufferedReader;
            StringBuilder stringBuilder;
            for(int i=0;i<1;i++) {
                try {
                    fileInputStream=new FileInputStream(files[i]);
                } catch (FileNotFoundException e) {
                    Log.e("ERROR-AppDiabet", i+" FILE NOT FOUND:" + files[i].getName());
                    Log.i("Info-AppDiabet", "Continuing with loading");
                    fileInputStream=null;
                }
                if(fileInputStream!=null) {
                    Log.i("Info-AppDiabet", i+" File found:" + files[i].getName());
                    bufferedReader=new BufferedReader(new InputStreamReader(fileInputStream));
                    stringBuilder=new StringBuilder();
                    String readLine;
                    try {
                        while ((readLine=bufferedReader.readLine()) != null) {
                            stringBuilder.append(readLine).append("\n");
                        }
                    } catch (IOException e) {
                        Log.e("ERROR-AppDiabetoLog", "ERROR WHILE READING FILE:" + files[i].getName());
                    }
                    switch (i) {
                     /*  case 0:
                            userLoad.setNotification(processLoadData(stringBuilder.toString()));
                            break;
*/
                        case 0:
                            RuntimeTypeAdapterFactory runtimeTypeAdapterFactory=RuntimeTypeAdapterFactory
                                    .of(AddRecord.class, "AddRecord")
                                    .registerSubtype(AddRecord.class, "AddRecord")
                                    .registerSubtype(Eat_record.class, "Products")
                                    .registerSubtype(Activitys_record.class, "Activitys");
                            Gson gson=new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                            ArrayList<AddRecord> addrecordArrayList=gson.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<AddRecord>>() {
                            }.getType());
                            userLoad.setAddRecordi(addrecordArrayList);
                            break;
                    }
                    try {
                        fileInputStream.close();
                    }catch (IOException e){
                        Log.e("ERROR-AppDiabet", "ERROR WHILE CLOSING STREAM!");
                    }finally {
                        Log.i("Info-AppDiabet","Streams closed!");
                    }
                }
            }
            if(checkUserSetValues(userLoad)){
                return userLoad;
            }
        } else {
            Log.e("ERROR-AppDiabet","STORAGE IS NOT READABLE");
        }
        return null;
    }
  /*  private ArrayList<Notification> processLoadData(String string){

        String[] stringSplitByNewLine = string.split("\n");
        ArrayList<Notification> arrayList = new ArrayList<>();
        for (String aStringSplitByNewLine : stringSplitByNewLine) {
            String[] stringSplit = aStringSplitByNewLine.split(",");


            String from = stringSplit[0];
            boolean temp=Boolean.valueOf(stringSplit[1]);
              int count=Integer.parseInt(stringSplit[2]);
            String about=stringSplit[3];
            Notification nastavitev = new Notification(from, temp, count, about);
            arrayList.add(nastavitev);
        }
        return arrayList;
    }*/
    public boolean isExternalStorageWritable() {
        String state= Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state= Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) ||  Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    private boolean checkUserSetValues(User testUser){
        return !(testUser.getAddRecord().size() == 0 );
    }

    //локальне збереження даних завантажених з google drive

    public String getExportData(){
        Gson gson=new GsonBuilder().setPrettyPrinting().create();
        StringBuilder stringBuilder=new StringBuilder();

        if(user.getAddRecord().size()!=0) {
          /*  if(user.getNotify().size()!=0) {
                ArrayList<Notification> arrayList = user.getNotify();
                for(int j=0;j<arrayList.size();j++){
                    if(arrayList.get(j).toString()!=null) {
                        stringBuilder.append(arrayList.get(j).toString()).append("\n");
                    }
                }
            }
            stringBuilder.append("-----------------\n");*/
            RuntimeTypeAdapterFactory runtimeTypeAdapterFactory=RuntimeTypeAdapterFactory
                    .of(AddRecord.class, "AddRecord")
                    .registerSubtype(AddRecord.class, "AddRecord")
                    .registerSubtype(Eat_record.class, "Products")
                    .registerSubtype(Activitys_record.class, "Activitys");
            gson=new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
            stringBuilder.append(
                    gson.toJson(
                            user.getAddRecord(),
                            new TypeToken<ArrayList<AddRecord>>(){}.getType()
                    )
            );
        }


        return stringBuilder.toString();
    }
    //створення тесту сформатованого в потрібному форматі для експорту на google drive
    public boolean saveImportData(BufferedReader bufferedReader){
        Log.i("Info-AppDiabet","Saving imported data...");
        StringBuilder stringBuilder=new StringBuilder();
        String line;
        try {
            while ((line=bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        }catch (IOException e){
            Log.e("Error-AppDiabet","Error while reading from google drive stream!");
            return false;
        }
        String[] strings= stringBuilder.toString().split("-----------------\n");
        for(int i=0;i<strings.length;i++) {
            switch (i) {
           /*     case 0:
                    user.setNotification(processLoadData(strings[i]));*/
                case 0:
                    RuntimeTypeAdapterFactory runtimeTypeAdapterFactory=RuntimeTypeAdapterFactory
                            .of(AddRecord.class, "AddRecord")
                            .registerSubtype(AddRecord.class, "AddRecord")
                            .registerSubtype(Eat_record.class, "Products")
                            .registerSubtype(Activitys_record.class, "Activitys");
                    Gson gson=new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).setPrettyPrinting().create();
                    ArrayList<AddRecord> addrecordArrayList=gson.fromJson(strings[i], new TypeToken<ArrayList<AddRecord>>() {
                    }.getType());
                    user.setAddRecordi(addrecordArrayList);
                    break;

            }
        }
        Log.i("Info-AppDiabeto","Importing from Google Drive is finished.");
        IMPORT=true;
        return true;
    }}



//    public void setUporabnikTestScenario(){
//        user.setAddRecordi(new TestEntries().testEntries());
//        user.setBazalniOdmerki(new TestEntries().testBazal());
//        user.setRazmerjaOH(new TestEntries().testOHrazmerja());
//        user.setObcutljivost(new TestEntries().testObcutljivost());
//        user.setCiljnaGKSpodnjaMeja(5.0);
//        user.setCiljnaGKZgornjaMeja(6.8);
//        user.setCasDelovanjaInzulina(new LocalTime(3,0));
//    }
