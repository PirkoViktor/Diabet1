package com.pirko.diabetolog;

import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.ClipboardManager;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import Data.Eat_record;
import Data.Notification;
import Data.Products;
import Data.Products1;

import static com.pirko.diabetolog.R.id.timerSwitch2;

public class ActivitySettings extends AppCompatActivity {
    private static ApplicationDiabet applicationDiabet;


    LinearLayout ll_set_time, ll_terms;
    public static ActivitySettings set;
    int hour, min;

    ClipboardManager myClipboard;

    public int counter=0;
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
    String TAG = "RemindMe";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.pirko.diabetolog.R.layout.activity_settings);

        applicationDiabet =((ApplicationDiabet)getApplication());
        set=this;
        Toolbar toolbar=(Toolbar) findViewById(com.pirko.diabetolog.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Create the addAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections addAdapter.
        ViewPager mViewPager=(ViewPager) findViewById(com.pirko.diabetolog.R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout=(TabLayout) findViewById(com.pirko.diabetolog.R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabet.save();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Фрагмент placeholder що містить представлення.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         *Аргумент фрагмента представляє номер секції меню налаштувань
         */
        private static final String ARG_SECTION_NUMBER="section_number";

        public PlaceholderFragment() {
        }

        /**
         * Повертає новий екземпляр цього фрагмента для вибраного розділу
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment=new PlaceholderFragment();
            Bundle args=new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
        public List<View> alLlayout;
        private List<Products> tempList;
        private AddAdapter addAdapter;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            final View rootView;


            if(getArguments().getInt(ARG_SECTION_NUMBER) == 2){
                //вибір сторінки з сповіщеннями

                rootView=inflater.inflate(com.pirko.diabetolog.R.layout.fragment_activity_notification,
                        container,
                        false);
                Button save =(Button)  rootView.findViewById(R.id.save_notify);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ArrayList<Notification> not=new ArrayList<>();
                        for(int i = 0; i <alLlayout.size(); i++) {
                            try {
                                NotificationScheduler.cancelReminder(alLlayout.get(i).getContext(), AlarmReceiver.class);
                                final SwitchCompat reminderSwitch = (SwitchCompat)  alLlayout.get(i).findViewById(timerSwitch2);
                                final LocalData localData = new LocalData( alLlayout.get(i).getContext());
                                final TextView tvTime = (TextView)  alLlayout.get(i).findViewById(R.id.tv_reminder_time_desc1);
                                final TextInputEditText about = (TextInputEditText)  alLlayout.get(i).findViewById(R.id.aboutins);
                                final TextInputEditText count = (TextInputEditText) alLlayout.get(i).findViewById(R.id.insulinCounter);


                                final LocalData localDat = new LocalData(alLlayout.get(i).getContext());
                                String b=tvTime.getText().toString();
                                String a=b.substring(3,5);
                                b=b.substring(0,2);
                                localDat.set_hour(Integer.parseInt(b));
                                localDat.set_min(Integer.parseInt(a));




                                NotificationScheduler.setReminder(alLlayout.get(i).getContext(), AlarmReceiver.class, localDat.get_hour(), localDat.get_min());
                            }
                            catch (Exception e)
                            {
                                Toast.makeText(rootView.getContext(), "Будь ласка, заповніть дані!", Toast.LENGTH_SHORT).show();
                            }

                        }




                    }
                });
                Button addButton=(Button) rootView.findViewById(com.pirko.diabetolog.R.id.notifi);
                //ініціалізація масиву з Edittext

/*
            //пошук layout

for(int i=0;i<applicationDiabet.getUser().getNotify().size();i++)
{

    LayoutInflater li=LayoutInflater.from(rootView.getContext());
    final  View view=li.inflate(com.pirko.diabetolog.R.layout.custom_edittext_layout1, null);

    final TextView tvTime = (TextView) view.findViewById(R.id.tv_reminder_time_desc1);
    final TextInputEditText about = (TextInputEditText) view.findViewById(R.id.aboutins);
    final SwitchCompat reminderSwitch = (SwitchCompat) view.findViewById(timerSwitch2);
    final TextInputEditText count = (TextInputEditText) view.findViewById(R.id.insulinCounter);
    Button delete=(Button) view.findViewById(com.pirko.diabetolog.R.id.button2);
    delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                ((LinearLayout) view.getParent()).removeView(view);
                alLlayout.remove(view);
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }
    });
    Notification n=new Notification();
    n=applicationDiabet.getUser().getNotify().get(i);
    tvTime.setText(n.notdate);
    about.setText(n.about);
    count.setText(n.count);
    reminderSwitch.setChecked(n.temp);

       alLlayout.add(view);

    linear.addView(view);


}
*/
                final LinearLayout linear=(LinearLayout) rootView.findViewById(com.pirko.diabetolog.R.id.linear1);

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alLlayout =new ArrayList<View>();


                        LayoutInflater li=LayoutInflater.from(v.getContext());
                        final  View view1=li.inflate(com.pirko.diabetolog.R.layout.custom_edittext_layout1, null);


                        Button delete=(Button) view1.findViewById(com.pirko.diabetolog.R.id.button2);
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view1.getParent()).removeView(view1);
                                    alLlayout.remove(view1);
                                } catch (IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        final LocalData localData = new LocalData(view1.getContext());
                        final TextView tvTime = (TextView) view1.findViewById(R.id.tv_reminder_time_desc1);
                        final TextInputEditText about = (TextInputEditText) view1.findViewById(R.id.aboutins);
                        final TextInputEditText count = (TextInputEditText) view1.findViewById(R.id.insulinCounter);
                        if (!localData.getReminderStatus())
                            tvTime.setAlpha(0.4f);
                        final  String TAG = " ";
                        final SwitchCompat reminderSwitch = (SwitchCompat) view1.findViewById(timerSwitch2);

                        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                localData.setReminderStatus(isChecked);
                                if (isChecked) {
                                    Log.d(TAG, "onCheckedChanged: true");
                                    NotificationScheduler.setReminder(view1.getContext(), AlarmReceiver.class, localData.get_hour(), localData.get_min());
                                    tvTime.setAlpha(1f);
                                } else {
                                    Log.d(TAG, "onCheckedChanged: false");
                                    NotificationScheduler.cancelReminder(view1.getContext(), AlarmReceiver.class);
                                    tvTime.setAlpha(0.4f);
                                }

                            }
                        });

                        tvTime.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {

                                applicationDiabet.showTimePickerDialog(localData.get_hour(), localData.get_min(), view1);
                            }
                        });

                        alLlayout.add(view1);

                        linear.addView(view1);



                    }

                });
            }
            else {
                //вибір сторінки калькулятора
                rootView=inflater.inflate(com.pirko.diabetolog.R.layout.fragment_activity_caclulation,
                        container,
                        false);
                Button addButton=(Button) rootView.findViewById(com.pirko.diabetolog.R.id.notifi);

                alLlayout =new ArrayList<View>();

                final LinearLayout linear=(LinearLayout) rootView.findViewById(com.pirko.diabetolog.R.id.linear);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        LayoutInflater li=LayoutInflater.from(v.getContext());
                        final  View view=li.inflate(com.pirko.diabetolog.R.layout.products_layout, null);
                        Button deleteField=(Button) view.findViewById(com.pirko.diabetolog.R.id.button2);
                        deleteField.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ((LinearLayout) view.getParent()).removeView(view);
                                    alLlayout.remove(view);
                                } catch(IndexOutOfBoundsException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        AutoCompleteTextView text=(AutoCompleteTextView) view.findViewById(com.pirko.diabetolog.R.id.timeinput);

                        alLlayout.add(view);

                        linear.addView(view);
                        tempList =retrievePeople();

                        text.setThreshold(1);
                        addAdapter =new AddAdapter(rootView.getContext(), com.pirko.diabetolog.R.layout.products_layout, com.pirko.diabetolog.R.id.lbl_products, tempList);
                        text.setAdapter(addAdapter);
                        EditText OH=(EditText) view.findViewById(com.pirko.diabetolog.R.id.aboutins);
                        EditText massa=(EditText) view.findViewById(com.pirko.diabetolog.R.id.massa);


                    }
                });
                Button showResult=(Button) rootView.findViewById(com.pirko.diabetolog.R.id.button3);
                showResult.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String [] name=new String[alLlayout.size()];
                        int [] OH=new int[alLlayout.size()];
                        int [] mass=new int[alLlayout.size()];
                        String temp;
                        LayoutInflater li=LayoutInflater.from(v.getContext());
                        final  View view=li.inflate(com.pirko.diabetolog.R.layout.products_layout, null);
                        //запис в масив
                        for(int i = 0; i < alLlayout.size(); i++) {
                            try {
                                name[i]=((EditText) alLlayout.get(i).findViewById(com.pirko.diabetolog.R.id.timeinput)).getText().toString();

                                OH[i]=Integer.parseInt(name[i].replaceAll("\\D+", "")) / 12;

                                mass[i]=Integer.parseInt(((EditText) alLlayout.get(i).findViewById(com.pirko.diabetolog.R.id.massa)).getText().toString());

                                OH[i]=(mass[i] * OH[i] / 100);
                                temp=String.valueOf(OH[i]);
                                ((EditText) alLlayout.get(i).findViewById(com.pirko.diabetolog.R.id.aboutins)).setText(temp);

                            }
                            catch (Exception e)
                            {
                                Toast.makeText(rootView.getContext(), "Будь ласка, заповніть дані!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

            };
            return rootView;
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {

            return 2;
        }
        private Notification fillNotifi(String tv, Boolean Sw, int count , String about){
            Notification addrecord=new Notification();
            if(!tv.equals("")) {
                addrecord.setNotdate(tv);
            }else{
                addrecord.setNotdate(null);
            }

            if(!Sw.equals("")){
                addrecord.setTemp(Sw);
            }else{
                addrecord.setTemp(false);
            }
            if(count!=0){
                addrecord.setCount(count);
            }else{
                addrecord.setCount(0);
            }
            if(!about.equals("")){
                addrecord.setAbout(about);
            }else{
                addrecord.setAbout("");
            }

            return addrecord;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Калькулятор";
                case 1:
                    return "Інсулін";
            }
            return null;
        }
    }
}
