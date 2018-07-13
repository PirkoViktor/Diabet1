package com.pirko.diabetolog;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.text.DecimalFormat;
import java.util.ArrayList;

import Data.Activitys_record;
import Data.AddRecord;
import Data.Eat_record;

public class ActivityStatistics extends AppCompatActivity implements OnChartValueSelectedListener{
    Integer stevecAddRecordi=0, stevecEati=0, stevecActivei=0;
    private ArrayList<AddRecord> addrecordArrayList;
    private ApplicationDiabet applicationDiabet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.pirko.diabetolog.R.layout.activity_statistics);
        Toolbar toolbar=(Toolbar) findViewById(com.pirko.diabetolog.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        applicationDiabet =(ApplicationDiabet) getApplication();
        LocalDate dateTimeOd=LocalDate.parse(getIntent().getStringExtra("datesOd"),
                DateTimeFormat.forPattern("dd.MM.yyyy")
        );
        LocalDate dateTimeDo=LocalDate.parse(getIntent().getStringExtra("datesDo"),
                DateTimeFormat.forPattern("dd.MM.yyyy")
        );
        addrecordArrayList= applicationDiabet.getUser().getAddRecordiFiltered(dateTimeOd, dateTimeDo);
        if(addrecordArrayList.size()==0){
            Toast.makeText(getApplicationContext(),"Немає записів для вибраного періоду!",Toast.LENGTH_SHORT).show();
        }else{
            Log.i("Info-Statistics",addrecordArrayList.size()+"");
        }
        ((TextView)findViewById(com.pirko.diabetolog.R.id.tv_obdobje)).setText("Статистика за період від "+
                dateTimeOd.toString(DateTimeFormat.forPattern("dd.MM.yyyy"))+
                " до "
                + dateTimeDo.toString(DateTimeFormat.forPattern("dd.MM.yyyy"))
        );
        statisticsAll();
        statisticsAddRecordi();
        statisticsEati();
        statisticsActivei();
        setPieChart();
        setLineChart();
        findViewById(com.pirko.diabetolog.R.id.ll_addrecordi).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_eati).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_activei).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_all).setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        switch (((PieEntry)e).getLabel()){
            case "Записи":
                findViewById(com.pirko.diabetolog.R.id.ll_addrecordi).setVisibility(View.VISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_eati).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_activei).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_all).setVisibility(View.INVISIBLE);
                break;
            case "Їжа":
                findViewById(com.pirko.diabetolog.R.id.ll_addrecordi).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_eati).setVisibility(View.VISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_activei).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_all).setVisibility(View.INVISIBLE);
                break;
            case "Діяльність":
                findViewById(com.pirko.diabetolog.R.id.ll_addrecordi).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_eati).setVisibility(View.INVISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_activei).setVisibility(View.VISIBLE);
                findViewById(com.pirko.diabetolog.R.id.ll_all).setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onNothingSelected() {
        findViewById(com.pirko.diabetolog.R.id.ll_addrecordi).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_eati).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_activei).setVisibility(View.INVISIBLE);
        findViewById(com.pirko.diabetolog.R.id.ll_all).setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        applicationDiabet.save();
    }

    public void statisticsAll(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<addrecordArrayList.size();i++){
            Double sugar1=addrecordArrayList.get(i).getSugar1();
            if(sugar1!=null) {
                vsota += sugar1;
                stevec++;
                if (sugar1 < min) {
                    min=sugar1;
                }
                if (sugar1 > max) {
                    max=sugar1;
                }
                //If addrecord is Activitys_record you need to iterate throught the Sugar array and check the values aswell
                //and add them to the full sum
                if (addrecordArrayList.get(i).getClass().equals(Activitys_record.class)) {
                    ArrayList<Double> sugar=((Activitys_record) addrecordArrayList.get(i)).getSugar();
                    for (int j=0; j < sugar.size(); j++) {
                        sugar1=sugar.get(j);
                        vsota += sugar1;
                        stevec++;
                        if (sugar1 < min) {
                            min=sugar1;
                        }
                        if (sugar1 > max) {
                            max=sugar1;
                        }
                    }
                }
            }
        }
        DecimalFormat decimalFormat=new DecimalFormat("#.#");
        if(stevec!=0) {
            avg=vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_all)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_all)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_all)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_all)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_all)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_all)).setText(decimalFormat.format(avg));
        }

    }
    public void statisticsAddRecordi(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<addrecordArrayList.size();i++){
            if(addrecordArrayList.get(i).getClass().equals(AddRecord.class)){
                double sugar1=addrecordArrayList.get(i).getSugar1();
                vsota+=sugar1;
                stevec++;
                if(sugar1<min){
                    min=sugar1;
                }
                if(sugar1>max){
                    max=sugar1;
                }
            }
        }
        DecimalFormat decimalFormat=new DecimalFormat("#.#");
        if(stevec!=0) {
            avg=vsota / stevec;
        }
        stevecAddRecordi=(int)stevec;
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_addrecordi)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_addrecordi)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_addrecordi)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_addrecordi)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_addrecordi)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_addrecordi)).setText(decimalFormat.format(avg));
        }
    }
    public void statisticsEati(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<addrecordArrayList.size();i++){
            if(addrecordArrayList.get(i).getClass().equals(Eat_record.class)){
                Double sugar1=addrecordArrayList.get(i).getSugar1();
                if(sugar1!=null) {
                    vsota += sugar1;
                    stevec++;
                    if (sugar1 < min) {
                        min=sugar1;
                    }
                    if (sugar1 > max) {
                        max=sugar1;
                    }
                }
                stevecEati++;
            }
        }
        DecimalFormat decimalFormat=new DecimalFormat("#.#");
        if(stevec!=0) {
            avg=vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_eati)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_eati)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_eati)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_eati)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_eati)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_eati)).setText(decimalFormat.format(avg));
        }
    }
    public void statisticsActivei(){
        double min=Double.MAX_VALUE, max=Double.MIN_VALUE, avg=0, stevec=0, vsota=0;
        for(int i=0;i<addrecordArrayList.size();i++){
            if(addrecordArrayList.get(i).getClass().equals(Activitys_record.class)){
                double sugar1=addrecordArrayList.get(i).getSugar1();
                vsota+=sugar1;
                stevec++;
                if(sugar1<min){
                    min=sugar1;
                }
                if(sugar1>max){
                    max=sugar1;
                }
                ArrayList<Double> sugar=((Activitys_record)addrecordArrayList.get(i)).getSugar();
                for(int j=0;j<sugar.size();j++){
                    sugar1=sugar.get(j);
                    vsota+=sugar1;
                    stevec++;
                    if(sugar1<min){
                        min=sugar1;
                    }
                    if(sugar1>max){
                        max=sugar1;
                    }
                }
                stevecActivei++;
            }
        }
        DecimalFormat decimalFormat=new DecimalFormat("#.#");
        if(stevec!=0) {
            avg=vsota / stevec;
        }
        if(min==Double.MAX_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_activei)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_min_activei)).setText(decimalFormat.format(min));
        }
        if(max==Double.MIN_VALUE){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_activei)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_max_activei)).setText(decimalFormat.format(max));
        }
        if(avg==0){
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_activei)).setText("");
        } else {
            ((EditText)findViewById(com.pirko.diabetolog.R.id.et_avg_activei)).setText(decimalFormat.format(avg));
        }
    }
    public void setPieChart(){
        ArrayList<PieEntry> pieEntries=new ArrayList<>();
        if(stevecAddRecordi!=0) {
            pieEntries.add(new PieEntry(stevecAddRecordi, "Записи", stevecAddRecordi));
        }
        if(stevecEati!=0) {
            pieEntries.add(new PieEntry(stevecEati, "Їжа", stevecEati));
        }
        if(stevecActivei!=0) {
            pieEntries.add(new PieEntry(stevecActivei, "Діяльність", stevecActivei));
        }

        PieDataSet pieDataSet=new PieDataSet(pieEntries,"");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData pieData=new PieData(pieDataSet);
        ((PieChart)findViewById(com.pirko.diabetolog.R.id.pie_chart)).setData(pieData);
        Description description=new Description();
        description.setText("");
        description.setTextColor(Color.BLACK);
        PieChart pieChart=(PieChart)findViewById(com.pirko.diabetolog.R.id.pie_chart);
        pieChart.setDescription(description);
        pieChart.animateY(1000);
        pieChart.setOnChartValueSelectedListener(this);
    }
    public void setLineChart(){
        LineChart lineChart=(LineChart)findViewById(com.pirko.diabetolog.R.id.line_chart);
        LineChart lineChartAll=(LineChart)findViewById(com.pirko.diabetolog.R.id.line_chart_all);
        ArrayList<Entry> entryListAll=new ArrayList<>(),
                entryListAddRecordi=new ArrayList<>(),
                entryListEati=new ArrayList<>(),
                entryListActivei=new ArrayList<>(),
                a=new ArrayList<>();
        Entry entry;
        int MAX_STEVILO_GRAF=5;
        if(addrecordArrayList.size()!=0){
            int i=0,j;
            //Get MAX_STEVILO_GRAF or less of Sugar1 values from everything: Zadnjih MAX_STEVILO_GRAF sugar1jev
            while(i< MAX_STEVILO_GRAF && i<addrecordArrayList.size()){
                Double sugar1=addrecordArrayList.get(i).getSugar1();
                if(sugar1!=null) {
                    entry=new Entry((float) i, sugar1.floatValue());
                    entryListAll.add(entry);
                    if (addrecordArrayList.get(i).getClass().equals(Activitys_record.class)) {
                        ArrayList<Double> sugar=((Activitys_record) addrecordArrayList.get(i)).getSugar();
                        i++;
                        for (j=0; j < sugar.size(); j++) {
                            sugar1=sugar.get(j);
                            entry=new Entry((float) i, sugar1.floatValue());
                            entryListAll.add(entry);
                            i++;
                            if (i == 20) {
                                break;
                            }
                        }
                        i--;
                    }
                }
                i++;
            }

            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<addrecordArrayList.size()){
                if(addrecordArrayList.get(i).getClass().equals(AddRecord.class)) {
                    Double sugar1=addrecordArrayList.get(i).getSugar1();
                    entry=new Entry((float) j, sugar1.floatValue());
                    entryListAddRecordi.add(entry);
                    j++;
                }
                i++;
            }
            //Get MAX_STEVILO_GRAF or less of Sugar1 values for Products only
            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<addrecordArrayList.size()){
                if(addrecordArrayList.get(i).getClass().equals(Eat_record.class)) {
                    Double sugar1=addrecordArrayList.get(i).getSugar1();
                    if(sugar1!=null) {
                        entry=new Entry((float) j, sugar1.floatValue());
                        entryListEati.add(entry);
                        j++;
                    }
                }
                i++;
            }
            //Get MAX_STEVILO_GRAF or less of Sugar1 values for Activitys_record only
            i=0;
            j=0;
            while(j< MAX_STEVILO_GRAF && i<addrecordArrayList.size()){
                if(addrecordArrayList.get(i).getClass().equals(Activitys_record.class)){
                    Double sugar1=addrecordArrayList.get(i).getSugar1();
                    entry=new Entry((float)j,sugar1.floatValue());
                    entryListActivei.add(entry);
                    ArrayList<Double> sugar=((Activitys_record)addrecordArrayList.get(i)).getSugar();
                    j++;
                    for(int k=0;k<sugar.size();k++){
                        sugar1=sugar.get(k);
                        entry=new Entry((float)j,sugar1.floatValue());
                        entryListActivei.add(entry);
                        j++;
                        if(j== MAX_STEVILO_GRAF){
                            break;
                        }
                    }
                }
                i++;
            }
        }

        LineData lineData=new LineData();
        if(entryListAll.size()!=0) {
            LineDataSet lineDataSet=new LineDataSet(entryListAll, "Останні цукри");
            lineDataSet.setColor(Color.RED);
            lineDataSet.setCircleColor(Color.RED);
            lineData.addDataSet(lineDataSet);
        }
        Description description=new Description();
        description.setText("Цукор(max "+ MAX_STEVILO_GRAF +")");
        description.setTextColor(Color.BLACK);
        lineChart.setData(lineData);
        lineChart.setDescription(description);
        lineChart.setClickable(false);
        lineChart.getLegend().setWordWrapEnabled(true);
        lineChart.animateX(1500);
        lineChart.animateY(1500);
        lineChart.invalidate();

        lineData=new LineData();



        if(entryListAddRecordi.size()!=0) {
            LineDataSet lineDataSet2=new LineDataSet(entryListAddRecordi, "Цукор в крові");
            lineDataSet2.setColor(Color.GREEN);
            lineDataSet2.setCircleColor(Color.GREEN);
            lineData.addDataSet(lineDataSet2);
        }

        if(entryListEati.size()!=0) {
            LineDataSet lineDataSet3=new LineDataSet(entryListEati, "Цукор перед їжею");
            lineData.addDataSet(lineDataSet3);
        }

        if(entryListActivei.size()!=0) {
            LineDataSet lineDataSet4=new LineDataSet(entryListActivei, "Цукор перед активністю");
            lineDataSet4.setColor(Color.YELLOW);
            lineDataSet4.setCircleColor(Color.YELLOW);
            lineData.addDataSet(lineDataSet4);
        }

        description=new Description();
        description.setText("Цукор(max "+ MAX_STEVILO_GRAF +")");
        description.setTextColor(Color.BLACK);
        lineChartAll.setData(lineData);
        lineChartAll.setDescription(description);
        lineChartAll.setClickable(false);
        lineChartAll.getLegend().setWordWrapEnabled(true);
        lineChartAll.animateX(1500);
        lineChartAll.animateY(1500);
        lineChartAll.invalidate();

    }
}