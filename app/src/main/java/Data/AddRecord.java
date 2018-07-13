package Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddRecord
{
    private Double sugar1;
    private Double OH;
    private Double insulinCounter;
    private Calendar dates;

    public AddRecord(double sugar1, double OH, double insulinCounter, Calendar dates) {
        this.sugar1=sugar1;
        this.OH=OH;
        this.insulinCounter=insulinCounter;
        this.dates=dates;
    }public AddRecord(double sugar1, double OH) {
        this.sugar1=sugar1;
        this.OH=OH;
    }
    public AddRecord(Calendar dates){
        this.dates=dates;
    }
    public Double getSugar1() {
        return sugar1;
    }

    public void setSugar1(Double sugar1) {
        this.sugar1=sugar1;
    }

    public Double getOH() {
        return OH;
    }

    public void setOH(Double OH) {
        this.OH=OH;
    }

    public Double getInsulinCounter() {
        return insulinCounter;
    }

    public void setInsulinCounter(Double insulinCounter) {
        this.insulinCounter=insulinCounter;
    }

    public Calendar getDates() {
        return dates;
    }

    public String getDatesString() {
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault());
        return simpleDateFormat.format(dates.getTime());
    }
}
