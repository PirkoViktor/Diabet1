package com.pirko.diabetolog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlarmManager;
import java.util.ArrayList;
import java.util.List;

import Data.Activitys_record;
import Data.AddRecord;
import Data.DecimalDigitsFilter;
import Data.Eat_record;
import Data.Products;
import Data.Products1;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;

import android.content.SharedPreferences;

import android.widget.AutoCompleteTextView;
import	android.preference.PreferenceManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ActivityMain extends AppCompatActivity{
   public  List<Products> mList;
    public AddAdapter adapter;
    private Activity activity;
    private ApplicationDiabet applicationDiabet;
    private ViewGroup parentPanel;
    private Spinner spinner;
    private GridView gridView;
    private GridViewAdapter gridViewAdapter;
    private boolean isMenuOpen=false, firstTimeOpening=false;
    String[] cities = {"Сьогодні", "Тиждень", "Місяць", "Увесь час"};
    public static List<Products> retrievePeople() {
        List<Products> list=new ArrayList<Products>();
        list.add(new Products("Яблуко",  56));
        list.add(new Products("Груша солодка",  44));
        list.add(new Products("Груша звичайна",  67));
        list.add(new Products("Помідор",  16));
        list.add(new Products("Гречка",  78));
        list.add(new Products("Яловичина",  28));
        list.add(new Products("Сир",  30));
        list.add(new Products("Молоко",  67));
        list.add(new Products("Вареники з сиром",  78));
        list.add(new Products("Сухарі",  88));
        list.add(new Products("Рис",  77));
        return list;
    }
   public static LocalDate dateTimeBegin=LocalDate.now(), dateTimeEnd=LocalDate.now();
    private List<View> allEds;
    //счетчик чисто декоративный для визуального отображения edittext'ov
    private int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(com.pirko.diabetolog.R.layout.activity_main_window);
        Toolbar toolbar=(Toolbar) findViewById(com.pirko.diabetolog.R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;
        applicationDiabet =(ApplicationDiabet) getApplication();
        Calendar calendar1=Calendar.getInstance();

        parentPanel =(ViewGroup)findViewById(com.pirko.diabetolog.R.id.parentPanel);

        gridView=(GridView)findViewById(com.pirko.diabetolog.R.id.gridViewGlavnoOkno);


        if(applicationDiabet.wasLoadSuccesfull()){

           spinner = (Spinner) findViewById(R.id.spinner3);
            // Создаем адаптер ArrayAdapter с помощью массива строк и стандартной разметки элемета spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cities);
            // Определяем разметку для использования при выборе элемента
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Применяем адаптер к элементу spinner
            spinner.setAdapter(adapter);

            AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // Получаем выбранный объект
                    String item = (String)parent.getItemAtPosition(position);
                    final Date dates=new Date();


                    dateTimeEnd=LocalDate.now();
                    if(position==0) {
                        dateTimeBegin=LocalDate.now();
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
                        );
                        gridView.setAdapter(gridViewAdapter);
                    }

                    if(position==1)
                    {        dateTimeBegin=LocalDate.now();
                        dateTimeBegin = dateTimeEnd.dayOfYear().setCopy((dateTimeEnd.getDayOfYear()) - 7);
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
                        );
                        gridView.setAdapter(gridViewAdapter);
                    }
                    if(position==2)
                    {        dateTimeBegin=LocalDate.now();
                        dateTimeBegin = dateTimeEnd.dayOfYear().setCopy((dateTimeEnd.getDayOfYear()) - 30);
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
                        );
                        gridView.setAdapter(gridViewAdapter);
                    }
                    if(position==3){
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecord());
                        gridView.setAdapter(gridViewAdapter);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
            spinner.setOnItemSelectedListener(itemSelectedListener);
            Log.i("Info-MainWindow","Loading was successfull");


        } else {
            Log.e("ERROR-MainWindow","Loading wasn't successfull");
            LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
            View alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_load_from_other_source,parentPanel,false);
            alertDialogBuilder.setView(alertView);
            final AlertDialog alertDialog=alertDialogBuilder.create();
            alertDialog.show();
            alertView.findViewById(com.pirko.diabetolog.R.id.btn_negative).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    applicationDiabet.setUser();
                    System.out.println("Negative!-Loading was cancelled");
                    gridViewAdapter=new GridViewAdapter(
                            getApplicationContext(),
                            activity,
                            applicationDiabet.getUser().getAddRecord());

                    gridView.setAdapter(gridViewAdapter);
                }
            });
            alertView.findViewById(com.pirko.diabetolog.R.id.btn_positive).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("Positive!-Getting data!");
                    alertDialog.cancel();
                    firstTimeOpening=true;
                    Intent intent=new Intent(getApplication(), ActivityGoogleDrive.class);
                    intent.putExtra("FirstTimeOpening","true");
                    startActivity(intent);
//                    applicationDiabet.setUporabnikTestScenario();
//                    gridViewAdapter=new GridViewAdapter(
//                            getApplicationContext(),
//                            activity,
//                            applicationDiabet.getUser().getAddRecord()
//                    );
//                    gridView.setAdapter(gridViewAdapter);
                }
            });
        }

        final FloatingActionButton fab=(FloatingActionButton) findViewById(com.pirko.diabetolog.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
            }
        });
        findViewById(com.pirko.diabetolog.R.id.rl_addrecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddRecord(0, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
        findViewById(com.pirko.diabetolog.R.id.rl_eat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddRecord(1, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
        findViewById(com.pirko.diabetolog.R.id.rl_active).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddRecord(2, parentPanel);
                gridView.setAdapter(gridViewAdapter);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabet.save();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i("Info-MainWindow","Resuming MainWindow");
        if(firstTimeOpening){
            firstTimeOpening=false;
            Log.i("Info-Main","First time opening!");
        }
        if (applicationDiabet.IMPORT) {
            Toast.makeText(getApplicationContext(), "Завантаження з Google Диска було успішним!", Toast.LENGTH_SHORT).show();
            applicationDiabet.IMPORT=false;
        } else if(applicationDiabet.IMPORT_FAILED) {
            applicationDiabet.IMPORT_FAILED=false;
            if(applicationDiabet.NO_FILES_FOUND){
                Toast.makeText(getApplicationContext(), "Не знайдено файлів імпорту на Google Диску!", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getApplicationContext(), "Завантажити з Google Диска не вдалося!", Toast.LENGTH_SHORT).show();
            }
        }
        if (applicationDiabet.EXPORT) {
            Toast.makeText(getApplicationContext(), "Завантаження на Google Диск було успішним!", Toast.LENGTH_SHORT).show();
            applicationDiabet.EXPORT=false;
        } else if(applicationDiabet.EXPORT_FAILED) {
            applicationDiabet.EXPORT_FAILED=false;
            Toast.makeText(getApplicationContext(), "Завантаження на Google Диск не було успішним!", Toast.LENGTH_SHORT).show();
        }
        gridViewAdapter=new GridViewAdapter(
                getApplicationContext(),
                activity,
                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
        );
        gridView.setAdapter(gridViewAdapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.pirko.diabetolog.R.menu.menu_activity_main_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        Intent intent;
        switch(id){
            case com.pirko.diabetolog.R.id.action_settings:
                intent=new Intent(this,ActivitySettings.class);
                startActivity(intent);
                break;
            case com.pirko.diabetolog.R.id.action_google_drive:
                intent=new Intent(this, ActivityGoogleDrive.class);
                intent.putExtra("FirstTimeOpening","false");
                startActivity(intent);
                break;
            case com.pirko.diabetolog.R.id.action_statistics:
                filterDatePicker(this,parentPanel);
                break;
            case com.pirko.diabetolog.R.id.action_delete:
                if(gridViewAdapter.getDeleteCount()!=0){
                    AlertDialog.Builder alertbox=new AlertDialog.Builder(this);
                    alertbox.setMessage("Готові видалити записи?");
                    alertbox.setTitle("Видалити");
                    alertbox.setIcon(android.R.drawable.ic_dialog_alert);
                    alertbox.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            gridViewAdapter.deleteAddRecordi();
                        }
                    });
                    alertbox.setNegativeButton("Ні",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                        }
                    });
                    alertbox.show();
                } else {
                    Toast.makeText(getApplicationContext(),"Немає виділених записів для видалення!",Toast.LENGTH_SHORT).show();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addAddRecord(final int type, ViewGroup parent){
        LayoutInflater layoutInflater=(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
        View alertView=null;
        //AddRecord fields
        final EditText etSugar1, etCountOh, etInsulinCounter;
        //Products extra fields
        final EditText etOpis;
        final TextView textv;
        final Spinner spinnerTip;

        final Date dates=new Date();
        final Calendar calendar=Calendar.getInstance();
        calendar.setTime(dates);
        final SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        switch(type){
            case 0:
                alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_addrecord,parent,false);
                alertView.getBackground().setAlpha(40);
                etSugar1=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1);
                etCountOh=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
                etInsulinCounter=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
                spinnerTip=null;
                etOpis=null;
                ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_dates)).setText(simpleDateFormat.format(dates));
                break;
            case 1:
                alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_eat,parent,false);
                alertView.getBackground().setAlpha(40);
                etSugar1=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1);
                etCountOh=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
                etInsulinCounter=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
                spinnerTip=(Spinner) alertView.findViewById(com.pirko.diabetolog.R.id.spinner);
                textv=(TextView) alertView.findViewById(R.id.textView1);
                spinnerTip.setSelection(3,true);
                etOpis=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_opis);
                alertView.findViewById(R.id.textView1).setVisibility(View.INVISIBLE);
                ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_dates)).setText(simpleDateFormat.format(dates));
                Button addButton=(Button) alertView.findViewById(com.pirko.diabetolog.R.id.notifi);
                //инициализировали наш массив с edittext.aьи
                allEds=new ArrayList<View>();
                //находим наш linear который у нас под кнопкой add edittext в activity_main.xml
                final LinearLayout linear=(LinearLayout) alertView.findViewById(com.pirko.diabetolog.R.id.linear);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                     textv.setVisibility(View.VISIBLE);
                            counter++;

                        //берем наш кастомный лейаут находим через него все наши кнопки и едит тексты, задаем нужные данные
                        final View view = getLayoutInflater().inflate(com.pirko.diabetolog.R.layout.products_layout, null);
                        Button deleteField = (Button) view.findViewById(com.pirko.diabetolog.R.id.button2);
                        deleteField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view.getParent()).removeView(view);
                                    allEds.remove(view);
                                    counter--;
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        AutoCompleteTextView text = (AutoCompleteTextView) view.findViewById(com.pirko.diabetolog.R.id.timeinput);
                        //добавляем все что создаем в массив
                        allEds.add(view);
                        //добавляем елементы в linearlayout
                        linear.addView(view);
                        mList = retrievePeople();

                        text.setThreshold(1);
                        adapter = new AddAdapter(getApplicationContext(), com.pirko.diabetolog.R.layout.products_layout, com.pirko.diabetolog.R.id.lbl_products, mList);
                        text.setAdapter(adapter);


                     }

                });

                Button showDataBtn=(Button) alertView.findViewById(com.pirko.diabetolog.R.id.button3);
                showDataBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //преобразуем наш ArrayList в просто String Array
                        String [] name=new String[allEds.size()];
                        double [] OH=new double[allEds.size()];
                        int [] mass=new int[allEds.size()];
                       String op=new String();
                        String temp;
                                double temp1=0;
                        View view=getLayoutInflater().inflate(com.pirko.diabetolog.R.layout.layout_eat, null);
                        //запускаем чтение всех елементов этого списка и запись в массив
                        for(int i=0; i < allEds.size(); i++) {
                            try {
                                name[i]=((EditText) allEds.get(i).findViewById(com.pirko.diabetolog.R.id.timeinput)).getText().toString();

                                OH[i]=Integer.parseInt(name[i].replaceAll("\\D+", "")) / 12;

                                mass[i]=Integer.parseInt(((EditText) allEds.get(i).findViewById(com.pirko.diabetolog.R.id.massa)).getText().toString());

                                OH[i]=(mass[i] * OH[i] / 100);
                                      temp1+=OH[i];
                                ((EditText) allEds.get(i).findViewById(com.pirko.diabetolog.R.id.aboutins)).setText(String.valueOf (OH[i]));



                            }
                            catch (Exception e)
                            {
                                Toast.makeText(getApplicationContext(), "Будь ласка, заповніть дані!", Toast.LENGTH_SHORT).show();
                            }
                        }
                            etCountOh.setText(String.valueOf (temp1));

                    }
                });

                break;
               case 2:
                alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_active,parent,false);
                alertView.getBackground().setAlpha(40);
                etSugar1=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1_add);
                etCountOh=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
                etInsulinCounter=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
                spinnerTip=null;
                etOpis=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.opiss);
                ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_dates)).setText(simpleDateFormat.format(dates));
                alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar).setVisibility(View.INVISIBLE);
                break;
            default:
                etSugar1=null;
                etCountOh=null;
                etInsulinCounter=null;
                spinnerTip=null;
                etOpis=null;
                break;
        }
        etSugar1.setFilters(new InputFilter[]{
                new DecimalDigitsFilter(),
                new InputFilter.LengthFilter(4)
        });
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                closeMenu();
            }
        });
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type!=1) {
                    if (!etSugar1.getText().toString().equals("")){
                        if (type == 0) {
                            AddRecord noviAddRecord=fillAddRecord(etSugar1.getText().toString(),
                                    etCountOh.getText().toString(),
                                    etInsulinCounter.getText().toString(),
                                    calendar
                            );
                            applicationDiabet.getUser().getAddRecord().add(0, noviAddRecord);
                        } else {
                            Activitys_record novaActive=fillActive(etSugar1.getText().toString(),
                                    etCountOh.getText().toString(),
                                    etInsulinCounter.getText().toString(),
                                    calendar,
                                    etOpis.getText().toString()
                            );
                            applicationDiabet.getUser().getAddRecord().add(1, novaActive);
                        }
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
                        );
                        gridView.setAdapter(gridViewAdapter);
                        alertDialog.cancel();
                        closeMenu();
                    }else{
                        Toast.makeText(getApplicationContext(), "Будь ласка, введіть цукор!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ArrayList <Products1> pr=new ArrayList <Products1>();
                    for(int i=0; i < allEds.size(); i++) {

                        String name = ((EditText) allEds.get(i).findViewById(com.pirko.diabetolog.R.id.timeinput)).getText().toString();

                        int OH = Integer.parseInt(name.replaceAll("\\D+", "")) / 12;

                        int mass = Integer.parseInt(((EditText) allEds.get(i).findViewById(com.pirko.diabetolog.R.id.massa)).getText().toString());

                        Products1 prod = new Products1(name, mass, OH);
                        pr.add(prod);
                    }
                    if(!etCountOh.getText().toString().equals("")) {
                        Eat_record noviEat=fillEat(etSugar1.getText().toString(),
                                etCountOh.getText().toString(),
                                etInsulinCounter.getText().toString(),
                                etOpis.getText().toString(),
                                spinnerTip.getSelectedItemPosition(),
                                calendar,
                                pr
                        );
                        applicationDiabet.getUser().getAddRecord().add(0, noviEat);
                        gridViewAdapter=new GridViewAdapter(
                                getApplicationContext(),
                                activity,
                                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
                        );
                        gridView.setAdapter(gridViewAdapter);
                        alertDialog.cancel();
                        closeMenu();
                    }else{
                        Toast.makeText(getApplicationContext(), "Будь ласка, введіть OH!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void infoAddRecord(Activity activity, final int id, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        View alertView;
        final int type;
        final AddRecord current = applicationDiabet.getUser().getAddRecord().get(id);
        //AddRecord fields
        final EditText etDates, etSugar1, etCountOh, etInsulinCounter;
        //Products extra fields
        final EditText etOpis;
        final Spinner spinnerTip;
        //Activitys_record extra fields
        final EditText etSugar, etEndActivei;
        final Button btnEndActive;
        final ArrayList<Double> novoDodaniSugar = new ArrayList<>();
        final ImageButton btnAddSugar1;
        if (current.getClass().equals(AddRecord.class)) {
            alertView = layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_addrecord, parent, false);
            alertView.getBackground().setAlpha(40);
            etDates = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_dates);
            etSugar1 = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1);
            etCountOh = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
            etInsulinCounter = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
            etOpis = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.opiss);
            ;
            etEndActivei = null;
            spinnerTip = null;
            alertView.findViewById(com.pirko.diabetolog.R.id.btn_calculate).setEnabled(false);

            etDates.setText(current.getDatesString());
            if (current.getSugar1() != null) {
                etSugar1.setText(String.valueOf(current.getSugar1()));
            } else {
                etSugar1.setText("");
            }
            if (current.getOH() != null) {
                etCountOh.setText(String.valueOf(current.getOH()));
            } else {
                etCountOh.setText("");
            }
            if (current.getOH() != null) {
                etCountOh.setText(String.valueOf(current.getOH()));
            } else {
                etCountOh.setText("");
            }
            if (current.getInsulinCounter() != null) {
                etInsulinCounter.setText(String.valueOf(current.getInsulinCounter()));
            } else {
                etInsulinCounter.setText("");
            }

            type = 0;
            etSugar1.setEnabled(false);
            etCountOh.setEnabled(false);
            etInsulinCounter.setEnabled(false);
        } else if (current.getClass().equals(Eat_record.class)) {
            alertView = layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_eat, parent, false);
            alertView.getBackground().setAlpha(40);
            etDates = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_dates);
            etSugar1 = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1);
            etCountOh = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
            etInsulinCounter = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
            spinnerTip = (Spinner) alertView.findViewById(com.pirko.diabetolog.R.id.spinner);
            etOpis = (EditText) alertView.findViewById(com.pirko.diabetolog.R.id.et_opis);
            etEndActivei = null;
            alertView.findViewById(com.pirko.diabetolog.R.id.notifi).setEnabled(false);

            alertView.findViewById(com.pirko.diabetolog.R.id.button3).setEnabled(false);
            alertView.findViewById(com.pirko.diabetolog.R.id.notifi).setVisibility(View.GONE);

            alertView.findViewById(com.pirko.diabetolog.R.id.button3).setVisibility(View.GONE);
            alertView.findViewById(com.pirko.diabetolog.R.id.textView1).setVisibility(View.INVISIBLE);
            alertView.findViewById(com.pirko.diabetolog.R.id.textView1).setEnabled(false);
            final LinearLayout linear = (LinearLayout) alertView.findViewById(com.pirko.diabetolog.R.id.linear);
            ArrayList <Products1> a=((((Eat_record) current).getProd()));

            for(int i=0; i<a.size(); i++ ) {
                alertView.findViewById(com.pirko.diabetolog.R.id.textView1).setEnabled(true);
                alertView.findViewById(com.pirko.diabetolog.R.id.textView1).setVisibility(View.VISIBLE);
                final View view = getLayoutInflater().inflate(com.pirko.diabetolog.R.layout.products_layout, null);
                linear.addView(view);
                EditText massa= (EditText) view.findViewById(R.id.massa);
                EditText OH = (EditText) view.findViewById(R.id.aboutins);
                EditText name = (EditText) view.findViewById(R.id.timeinput);

                massa.setText(String.valueOf(a.get(i).getmassa()));
                OH.setText(String.valueOf(a.get(i).getOH()));
                name.setText(String.valueOf(a.get(i).getName()));
                view.findViewById(R.id.button2).setVisibility(View.INVISIBLE);
                massa.setEnabled(false);
                OH.setEnabled(false);
                name.setEnabled(false);
            }

            etDates.setText(current.getDatesString());
            if(current.getSugar1()!=null) {
                etSugar1.setText(String.valueOf(current.getSugar1()));
            }else{
                etSugar1.setText("");
            }
            if(current.getOH()!=null) {
                etCountOh.setText(String.valueOf(current.getOH()));
            }else{
                etCountOh.setText("");
            }
            if(current.getInsulinCounter()!=null) {
                etInsulinCounter.setText(String.valueOf(current.getInsulinCounter()));
            }else{
                etInsulinCounter.setText("");
            }
            switch(((Eat_record)current).getTip()){
                case Eat1:
                    spinnerTip.setSelection(0,true);
                    break;
                case Eat2:
                    spinnerTip.setSelection(1,true);
                    break;
                case Eat3:
                    spinnerTip.setSelection(2,true);
                    break;
                default:
                    spinnerTip.setSelection(3,true);
                    break;
            }
            etOpis.setText(String.valueOf(((Eat_record) current).getOpis()));
            type=1;
            etSugar1.setEnabled(false);
            etCountOh.setEnabled(false);
            etInsulinCounter.setEnabled(false);
            spinnerTip.setEnabled(false);
            etOpis.setEnabled(false);
        } else {
            alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_active,parent,false);
            alertView.getBackground().setAlpha(40);
            etDates=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_dates);
            etSugar1=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar1_edit);
            etOpis=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.opiss);
            etCountOh=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_count_oh);
            etInsulinCounter=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_units_insulins);
            etSugar=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_sugar);
            etEndActivei=(EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_end_activei);
            etOpis.setText(String.valueOf(((Activitys_record) current).getOpiss()));;
                       alertView.findViewById(com.pirko.diabetolog.R.id.btn_calculate).setEnabled(false);
            alertView.findViewById(com.pirko.diabetolog.R.id.edit_layout).setVisibility(View.VISIBLE);
            alertView.findViewById(com.pirko.diabetolog.R.id.til_add).setVisibility(View.INVISIBLE);
            btnAddSugar1=(ImageButton)alertView.findViewById(com.pirko.diabetolog.R.id.btn_add);
            btnAddSugar1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    novoDodaniSugar.add(Double.valueOf(etSugar1.getText().toString()));
                    etSugar.append(","+etSugar1.getText().toString());
                    etSugar1.setText("");
                }
            });

            etDates.setText(current.getDatesString());
            if(current.getSugar1()!=null) {
                etSugar.setText(String.valueOf(current.getSugar1())+((Activitys_record)current).sugarToString());
            }else{
                etSugar1.setText("");
            }
            if(current.getOH()!=null) {
                etCountOh.setText(String.valueOf(current.getOH()));
            }else{
                etCountOh.setText("");
            }
            if(current.getInsulinCounter()!=null) {
                etInsulinCounter.setText(String.valueOf(current.getInsulinCounter()));
            }else{
                etInsulinCounter.setText("");
            }
            etEndActivei.setText(((Activitys_record)current).getEndActivei());
            if(etEndActivei.getText().toString().equals("Операція не завершена")){
                btnEndActive=(Button)alertView.findViewById(com.pirko.diabetolog.R.id.btn_end_active);
                btnEndActive.setEnabled(true);
                btnEndActive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
                        etEndActivei.setText(simpleDateFormat.format(new Date()));
                        btnEndActive.setEnabled(false);
                        btnAddSugar1.setEnabled(false);
                        btnAddSugar1.setVisibility(View.INVISIBLE);
                        etSugar1.setEnabled(false);
                        etOpis.setEnabled(false);
                    }
                });
            }else{
                btnAddSugar1.setEnabled(false);
                btnAddSugar1.setVisibility(View.INVISIBLE);

            }
            type=2;
            etOpis.setEnabled(false);
            etCountOh.setEnabled(false);
            etInsulinCounter.setEnabled(false);
        }
        etSugar1.setFilters(new InputFilter[]{
                new DecimalDigitsFilter(),
                new InputFilter.LengthFilter(4)
        });
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog=alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_cancel).setVisibility(View.INVISIBLE);
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type==2) {
                    ((Activitys_record) current).appendSugar(novoDodaniSugar);
                    if (!etEndActivei.getText().toString().equals("Операція не завершена")) {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
                        try {
                            ((Activitys_record) current).setEndActivei(simpleDateFormat.parse(etEndActivei.getText().toString()));
                        } catch (ParseException e) {
                            Log.e("Error", "This should not happen.");
                        }
                    }
                    applicationDiabet.getUser().getAddRecord().set(id, current);
                }
                gridViewAdapter.notifyDataSetChanged();
                gridView.setAdapter(gridViewAdapter);
                alertDialog.cancel();
                closeMenu();
            }
        });
    }

    public void deleteAddRecord(ArrayList<Integer> arrayList){
        for(int i=0;i<arrayList.size();i++){
            applicationDiabet.getUser().getAddRecord().remove((int)arrayList.get(i));
        }
        gridViewAdapter.setLongClickFalse();
        gridViewAdapter=new GridViewAdapter(
                getApplicationContext(),
                activity,
                applicationDiabet.getUser().getAddRecordiFiltered(dateTimeBegin, dateTimeEnd)
        );
        gridView.setAdapter(gridViewAdapter);
    }

    private void filterDatePicker(final Activity activity, ViewGroup parent){
        LayoutInflater layoutInflater=(LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(activity);
        final View alertView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.layout_filter_date_picker,parent,false);
        alertDialogBuilder.setView(alertView);
        final AlertDialog alertDialog=alertDialogBuilder.create();
        final LocalDateTime localDateTime=new LocalDateTime();
        alertDialog.show();
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertView.findViewById(com.pirko.diabetolog.R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_od)).getText().toString().equals("") || ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_do)).getText().toString().equals("")){
                    Toast.makeText(activity,"Виберіть обидві дати!", Toast.LENGTH_SHORT).show();
                }else {
                    alertDialog.cancel();
                    Intent intent=new Intent(activity, ActivityStatistics.class);
                    intent.putExtra("datesOd",((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_od)).getText().toString());
                    intent.putExtra("datesDo",((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_do)).getText().toString());
                    startActivity(intent);
                }
            }
        });
        alertView.findViewById(com.pirko.diabetolog.R.id.et_od).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_od)).setText(dayOfMonth+"."+monthOfYear+"."+year);
                    }
                },localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                datePickerDialog.setTitle("Від:");
                datePickerDialog.show();
            }
        });
        alertView.findViewById(com.pirko.diabetolog.R.id.et_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog=new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ((EditText)alertView.findViewById(com.pirko.diabetolog.R.id.et_do)).setText(dayOfMonth+"."+monthOfYear+"."+year);
                    }
                },localDateTime.getYear(),localDateTime.getMonthOfYear(),localDateTime.getDayOfMonth());
                datePickerDialog.setTitle("До:");
                datePickerDialog.show();
            }
        });
    }

    private AddRecord fillAddRecord(String sugar1, String countOH, String insulinCounter, Calendar dates){
        AddRecord addrecord=new AddRecord(dates);
        addrecord.setSugar1(Double.valueOf(sugar1));
        if(!countOH.equals("")){
            addrecord.setOH(Double.valueOf(countOH));
        }else{
            addrecord.setOH(0d);
        }
        if(!insulinCounter.equals("")){
            addrecord.setInsulinCounter(Double.valueOf(insulinCounter));
        }else{
            addrecord.setInsulinCounter(0d);
        }
        return addrecord;
    }

    private Eat_record fillEat(String sugar1, String countOH, String insulinCounter, String opis, Integer position , Calendar dates, ArrayList<Products1> prod){
        Eat_record addrecord=new Eat_record(dates);
        if(!sugar1.equals("")) {
            addrecord.setSugar1(Double.valueOf(sugar1));
        }else{
            addrecord.setSugar1(null);
        }
        addrecord.setOH(Double.valueOf(countOH));
        if(!insulinCounter.equals("")){
            addrecord.setInsulinCounter(Double.valueOf(insulinCounter));
        }else{
            addrecord.setInsulinCounter(0d);
        }
        if(!opis.equals("")){
            addrecord.setOpis(opis);
        }else{
            addrecord.setOpis("");
        }
        if(!prod.equals("")){
            addrecord.setProd(prod);
        }else{
            addrecord.setOpis("");
        }
        addrecord.setTip(position);
        return addrecord;
    }

    private Activitys_record fillActive(String sugar1, String countOH, String insulinCounter, Calendar dates, String opis ){
        Activitys_record addrecord=new Activitys_record(dates);
        addrecord.setSugar1(Double.valueOf(sugar1));
         if(!opis.equals("")){
             addrecord.setOpiss(opis);
         }else{
             addrecord.setOpiss("");
         }

        if(!countOH.equals("")){
            addrecord.setOH(Double.valueOf(countOH));
        }else{
            addrecord.setOH(0d);
        }
        if(!insulinCounter.equals("")){
            addrecord.setInsulinCounter(Double.valueOf(insulinCounter));
        }
        else{
            addrecord.setInsulinCounter(0d);
        }
        return addrecord;
    }

    void openMenu(){
        isMenuOpen=true;
        final RelativeLayout rlActive =(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_active);
        final RelativeLayout rlEat=(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_eat);
        final RelativeLayout rlAddRecord=(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_addrecord);
        final FrameLayout frameLayout=(FrameLayout) findViewById(com.pirko.diabetolog.R.id.frame_layout);
        final FloatingActionButton fabActive=(FloatingActionButton)findViewById(com.pirko.diabetolog.R.id.fab_active);
        final FloatingActionButton fabEat=(FloatingActionButton)findViewById(com.pirko.diabetolog.R.id.fab_eat);
        final FloatingActionButton fabAddRecord=(FloatingActionButton) findViewById(com.pirko.diabetolog.R.id.fab_addrecord);
        final TextView tvActive=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_active);
        final TextView tvEat=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_eat);
        final TextView tvAddRecord=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_input);

        if(rlActive!=null && rlEat!=null && rlAddRecord!=null && frameLayout !=null &&
                fabActive!=null && fabEat!=null && fabAddRecord!=null &&
                tvActive!=null && tvEat!=null && tvAddRecord!=null) {
            frameLayout.setBackgroundColor(getResources().getColor(com.pirko.diabetolog.R.color.dimBackground,getTheme()));
            frameLayout.setEnabled(true);
            frameLayout.setClickable(true);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.frame_layout_open));

            rlAddRecord.setVisibility(View.VISIBLE);
            fabAddRecord.setVisibility(View.VISIBLE);
            tvAddRecord.setVisibility(View.VISIBLE);
            rlAddRecord.setClickable(true);
            rlAddRecord.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.fab_menu_open));

            rlEat.setVisibility(View.VISIBLE);
            fabEat.setVisibility(View.VISIBLE);
            tvEat.setVisibility(View.VISIBLE);
            rlEat.setClickable(true);
            rlEat.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.fab_menu_open));

            rlActive.setVisibility(View.VISIBLE);
            fabActive.setVisibility(View.VISIBLE);
            tvActive.setVisibility(View.VISIBLE);
            rlActive.setClickable(true);
            rlActive.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.fab_menu_open));
        }
    }

    void closeMenu(){
        isMenuOpen=false;
        final RelativeLayout rlActive =(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_active);
        final RelativeLayout rlEat=(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_eat);
        final RelativeLayout rlAddRecord=(RelativeLayout)findViewById(com.pirko.diabetolog.R.id.rl_addrecord);
        final FrameLayout frameLayout=(FrameLayout) findViewById(com.pirko.diabetolog.R.id.frame_layout);
        final FloatingActionButton fabActive=(FloatingActionButton)findViewById(com.pirko.diabetolog.R.id.fab_active);
        final FloatingActionButton fabEat=(FloatingActionButton)findViewById(com.pirko.diabetolog.R.id.fab_eat);
        final FloatingActionButton fabAddRecord=(FloatingActionButton) findViewById(com.pirko.diabetolog.R.id.fab_addrecord);
        final TextView tvActive=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_active);
        final TextView tvEat=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_eat);
        final TextView tvAddRecord=(TextView)findViewById(com.pirko.diabetolog.R.id.tv_input);

        if(rlActive!=null && rlEat!=null && rlAddRecord!=null && frameLayout !=null &&
                fabActive!=null && fabEat!=null && fabAddRecord!=null &&
                tvActive!=null && tvEat!=null && tvAddRecord!=null) {
            Animation fabMenuCloseAnimation =AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.fab_menu_close);
            fabMenuCloseAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    //Do Nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    rlActive.setVisibility(View.INVISIBLE);
                    fabActive.setVisibility(View.INVISIBLE);
                    tvActive.setVisibility(View.INVISIBLE);
                    rlActive.setClickable(false);


                    rlEat.setVisibility(View.INVISIBLE);
                    fabEat.setVisibility(View.INVISIBLE);
                    tvEat.setVisibility(View.INVISIBLE);
                    rlEat.setClickable(false);


                    rlAddRecord.setVisibility(View.INVISIBLE);
                    fabAddRecord.setVisibility(View.INVISIBLE);
                    tvAddRecord.setVisibility(View.INVISIBLE);
                    rlAddRecord.setClickable(false);


                    frameLayout.setEnabled(false);
                    frameLayout.setClickable(false);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    //Do Nothing
                }
            });
            rlActive.startAnimation(fabMenuCloseAnimation);
            rlEat.startAnimation(fabMenuCloseAnimation);
            rlAddRecord.startAnimation(fabMenuCloseAnimation);
            frameLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), com.pirko.diabetolog.R.anim.frame_layout_close));

        }
    }
}