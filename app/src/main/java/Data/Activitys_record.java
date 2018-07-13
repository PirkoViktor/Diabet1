package Data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Activitys_record extends AddRecord
{
    private Date endActivei;
    private ArrayList<Double> sugar;
    private String opiss;
    public Activitys_record(double sugar1, double OH, double insulinCounter, Calendar dates, String opis) {
        super(sugar1, OH, insulinCounter, dates);
        this.endActivei=null;
        this.sugar=new ArrayList<>();
        this.opiss=opis;
    }

    public Activitys_record(Calendar dates) {
        super(dates);
        this.endActivei=null;
        this.sugar=new ArrayList<>();
        this.opiss=null;
    }
    public String getOpiss() {
        return opiss;
    }

    public void setOpiss(String opiss) {
        this.opiss=opiss;
    }
    public String getEndActivei() {
        if(endActivei!=null){
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
            return simpleDateFormat.format(endActivei);
        } else {
            return "Операція не завершена";
        }

    }

    public void setEndActivei(Date endActivei) {
        this.endActivei=endActivei;
    }

    public ArrayList<Double> getSugar() {
        return sugar;
    }

    public String sugarToString(){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<sugar.size();i++){
            stringBuilder.append(",").append(sugar.get(i));
        }
        return stringBuilder.toString();
    }
    public void appendSugar(ArrayList<Double> arrayList){
        sugar.addAll(arrayList);
    }
}
