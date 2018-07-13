package Data;

import com.pirko.diabetolog.LocalData;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Notification {
    public Notification() {
    }
    public String notdate;
    public  boolean temp;

    public int count;
    public String about;
    public Notification(String notdate, boolean temp,  int count, String about) {
        this.notdate = notdate;
        this.temp = temp;
        this.count = count;
        this.about = about;
    }
    public void setNotdate(String notdate) {
        this.notdate = notdate;
    }

    public void setTemp(boolean temp) {
        this.temp = temp;
    }



    public void setCount(int count) {
        this.count = count;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormat.forPattern("HH:mm");
        if(notdate!=null) {
            return notdate + "," +
                    temp + "," +
                    count+ "," + about;
        }
        return null;
    }



    public String getNotdate() {
        return notdate;
    }

    public boolean isTemp() {
        return temp;
    }


    public int getCount() {
        return count;
    }

    public String getAbout() {
        return about;
    }




}