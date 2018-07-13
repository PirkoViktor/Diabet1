package Data;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Eat_record extends AddRecord {
    private Type_record tip;
    private String opis;
    private ArrayList <Products1> prod;

    private int mass;
    public
    Eat_record(double sugar1, double OH, double insulinCounter, Calendar dates, Type_record tip, String opis) {
        super(sugar1, OH, insulinCounter, dates);
        this.tip=tip;
        this.opis=opis;
    }
    Eat_record(double sugar1, double OH, double insulinCounter, Calendar dates, Type_record tip, String opis, ArrayList <Products1> prod) {
        super(sugar1, OH, insulinCounter, dates);
        this.tip=tip;
        this.opis=opis;
        this.prod=prod;
    }
    public Eat_record(Calendar dates){
        super(dates);

    }
    public Type_record getTip() {
        return tip;
    }
public void setProd (ArrayList <Products1> prod)
{
    this.prod=prod;
}

    public void setTip(Integer position) {
        switch (position){
            case 0:
                this.tip= Type_record.Eat1;
                break;
            case 1:
                this.tip= Type_record.Eat2;
                break;
            case 2:
                this.tip= Type_record.Eat3;
                break;
            default:
                this.tip= Type_record.Enother;
                break;
        }
    }

    public String getOpis() {
        return opis;
    }
    public ArrayList <Products1>  getProd() {
        return prod;
    }

    public void setOpis(String opis) {
        this.opis=opis;
    }
}
