package Data;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;

public class User {
    private ArrayList<AddRecord> addrecordi;

    public void setAddrecordi(ArrayList<AddRecord> addrecordi) {
        this.addrecordi = addrecordi;
    }

    public ArrayList<Notification> getNotify() {
        return notify;
    }

    private ArrayList<Notification> notify;

    public User() {
        addrecordi= new ArrayList<>();
        notify=new ArrayList<>();


    }


    public void setAddRecordi(ArrayList<AddRecord> addrecordi) {
        this.addrecordi=addrecordi;
    }





    public ArrayList<AddRecord> getAddRecord() {
        return addrecordi;
    }

    public void setNotification(ArrayList<Notification> notification) {
        this.notify = notification;
    }

    public void addNotification(Notification notification){
        notify.add(notification);
    }


    public ArrayList<AddRecord> getAddRecordiFiltered(LocalDate from, LocalDate to) {
        ArrayList<AddRecord> filteredArrayList=new ArrayList<>();
        Calendar dates;
        AddRecord currentAddRecord;
        for(int i=0;i<addrecordi.size();i++){
            currentAddRecord=addrecordi.get(i);
            dates=currentAddRecord.getDates();
            LocalDate dateCurrentAddRecord=new LocalDate(dates.getTimeInMillis());
            if((dateCurrentAddRecord.isBefore(to) && dateCurrentAddRecord.isAfter(from)) ||(dateCurrentAddRecord.equals(from) || dateCurrentAddRecord.equals(to))){
                filteredArrayList.add(currentAddRecord);
            }
        }
        return filteredArrayList;
    }
}
