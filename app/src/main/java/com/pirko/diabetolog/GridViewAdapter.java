package com.pirko.diabetolog;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Color;
        import android.text.Spannable;
        import android.text.SpannableStringBuilder;
        import android.text.style.ForegroundColorSpan;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.LinearLayout;
        import android.widget.TextView;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.Spinner;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Locale;

        import android.widget.AdapterView.OnItemSelectedListener;

        import Data.Activitys_record;
        import Data.AddRecord;
        import Data.Eat_record;

class GridViewAdapter extends BaseAdapter
{    String[] cities = {"Сьогодні", "Вчора", "Тиждень", "Місяць"};
    private Context context;
    private Activity activity;
    private ArrayList<AddRecord> arrayList;
    private ArrayList<Integer> deleteArrayList;
    private boolean longClick=false;
    GridViewAdapter(Context context, Activity activity, ArrayList<AddRecord> arrayList) {
        this.activity=activity;
        this.arrayList=arrayList;
        this.context=context;
        deleteArrayList=new ArrayList<>();
    }


    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    //організація представлення даних в головному меню
    @Override
    public View getView(final int i, final View view, final ViewGroup viewGroup) {
        LayoutInflater layoutInflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        SimpleDateFormat dateFormatDates=new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        SimpleDateFormat dateFormatUra=new SimpleDateFormat("HH:mm", Locale.getDefault());
        ForegroundColorSpan foregroundColorSpan;
        SpannableStringBuilder spannableStringBuilder;


        if(view==null){
            gridView=layoutInflater.inflate(com.pirko.diabetolog.R.layout.gridviewlayout,null);
        }
        else {
            gridView=view;
        }
        TextView textViewSugar1=(TextView)gridView.findViewById(com.pirko.diabetolog.R.id.textViewSugar1);
        TextView textViewDates=(TextView)gridView.findViewById(com.pirko.diabetolog.R.id.textViewDates);
        TextView textViewUra=(TextView)gridView.findViewById(com.pirko.diabetolog.R.id.textViewUra);
        final LinearLayout linearLayoutGridViewLayout=(LinearLayout)gridView.findViewById(com.pirko.diabetolog.R.id.linearLayoutGridViewLayout);
        textViewDates.setText(dateFormatDates.format(arrayList.get(i).getDates().getTime()));
        textViewUra.setText(dateFormatUra.format(arrayList.get(i).getDates().getTime()));

    //змінення кольору запису залежно від цукру
        Double sugar1=arrayList.get(i).getSugar1();
        if(sugar1!=null) {
            spannableStringBuilder=new SpannableStringBuilder(String.valueOf(sugar1));
            if (sugar1 >= 9.5) {
                foregroundColorSpan=new ForegroundColorSpan(Color.RED);
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sugar1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (sugar1 <= 4.5) {
                foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#FFA500"));
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sugar1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#33CC33"));
                spannableStringBuilder.setSpan(foregroundColorSpan, 0, String.valueOf(sugar1).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            textViewSugar1.setText(spannableStringBuilder);
        }else{
            spannableStringBuilder=new SpannableStringBuilder("--.-");
            foregroundColorSpan=new ForegroundColorSpan(Color.parseColor("#000000"));
            spannableStringBuilder.setSpan(foregroundColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewSugar1.setText(spannableStringBuilder);
        }
        setBackground(linearLayoutGridViewLayout,i);
        final CheckBox checkBox=(CheckBox)gridView.findViewById(com.pirko.diabetolog.R.id.cb_delete);
        gridView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);
                deleteArrayList.add(i);
                setBackgroundGrey(linearLayoutGridViewLayout,i);
                longClick=true;
                return true;
            }
        });
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(longClick) {
                    if (checkBox.isChecked()) {
                        checkBox.setVisibility(View.INVISIBLE);
                        checkBox.setChecked(false);
                        deleteArrayList.remove((Integer) i);
                        setBackground(linearLayoutGridViewLayout,i);
                        if(deleteArrayList.size()==0){
                            longClick=false;
                        }
                    }else{
                        checkBox.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                        deleteArrayList.add(i);
                        setBackgroundGrey(linearLayoutGridViewLayout,i);
                    }
                }else {
                    ((ActivityMain)activity).infoAddRecord(activity,i,viewGroup);
                }
            }
        });
        return gridView;
    }

    int getDeleteCount(){
        if(deleteArrayList.size()!=0){
            return deleteArrayList.size();
        }
        return 0;
    }
    void deleteAddRecordi(){
        Collections.sort(deleteArrayList);
        Collections.reverse(deleteArrayList);
        ((ActivityMain)activity).deleteAddRecord(deleteArrayList);
        deleteArrayList.clear();
    }
    void setLongClickFalse(){
        longClick=false;
    }
    private void setBackground(LinearLayout linearLayout,int i){
        if (arrayList.get(i).getClass().equals(Eat_record.class)) {
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.eat96);
        } else if (arrayList.get(i).getClass().equals(Activitys_record.class)) {
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.active96);
        } else if (arrayList.get(i).getClass().equals(AddRecord.class)) {
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.addrecord96);
        }
        linearLayout.getBackground().setAlpha(40);
    }
    private void setBackgroundGrey(LinearLayout linearLayout,int i){
        if(arrayList.get(i).getClass().equals(Eat_record.class)) {
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.eat96_grey);
        } else if(arrayList.get(i).getClass().equals(Activitys_record.class)) {
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.active96_grey);
        } else if(arrayList.get(i).getClass().equals(AddRecord.class)){
            linearLayout.setBackgroundResource(com.pirko.diabetolog.R.drawable.addrecord96_grey);
        }
        linearLayout.getBackground().setAlpha(40);
    }
}