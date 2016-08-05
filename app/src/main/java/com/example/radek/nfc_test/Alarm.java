package com.example.radek.nfc_test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Radek on 2016-05-27.
 */
public class Alarm implements Parcelable
{
    @Expose
    private String stringNotation;
    @Expose
    private Boolean alarmActive = true;
    @Expose
    private Calendar alarmTime = Calendar.getInstance();
    @Expose
    private Day[] days = {Day.MONDAY,Day.TUESDAY,Day.WEDNESDAY,Day.THURSDAY,Day.FRIDAY,Day.SATURDAY,Day.SUNDAY,};
    @Expose
    private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    @Expose
    private Boolean vibrate = true;
    @Expose
    private String alarmName = "Alarm Clock";
    @Expose
    private int ID;

    protected Alarm(Parcel in) {
        stringNotation = in.readString();
        alarmTonePath = in.readString();
        alarmName = in.readString();
        ID = in.readInt();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stringNotation);
        dest.writeString(alarmTonePath);
        dest.writeString(alarmName);
        dest.writeInt(ID);
    }

    public enum Day {
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY;

        @Override
        public String toString() {
            switch (this.ordinal()) {
                case 0:
                    return "Sunday";
                case 1:
                    return "Monday";
                case 2:
                    return "Tuesday";
                case 3:
                    return "Wednesday";
                case 4:
                    return "Thursday";
                case 5:
                    return "Friday";
                case 6:
                    return "Saturday";
            }
            return super.toString();
        }
    }

    public Alarm(){
        setStringNotation(alarmTime.get(Calendar.HOUR_OF_DAY), alarmTime.get(Calendar.MINUTE));
    }

    public Alarm(String hour)
    {
        setAlarmTime(hour);
    }

    public void setHour(int hour, int minutes) throws IllegalHourException
    {
        setStringNotation(hour, minutes);
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE,minutes);
    }

    public String getStringNotation() {
        return stringNotation;
    }

    private void setStringNotation(int hour,int minutes) {
        StringBuilder stringBuilder = new StringBuilder();
        if(hour / 9 == 0)
            stringBuilder.append("0");
        stringBuilder.append(hour).append(":");
        if(minutes % 9 == 0)
            stringBuilder.append("0");
        stringBuilder.append(minutes);
        this.stringNotation = stringBuilder.toString();
    }

    public void setAlarmTime(String alarmTime) {

        this.stringNotation = alarmTime;
        String[] timePieces = alarmTime.split(":");

        Calendar newAlarmTime = Calendar.getInstance();
        newAlarmTime.set(Calendar.HOUR_OF_DAY,
                Integer.parseInt(timePieces[0]));
        newAlarmTime.set(Calendar.MINUTE, Integer.parseInt(timePieces[1]));
        newAlarmTime.set(Calendar.SECOND, 0);
        setAlarmTime(newAlarmTime);
    }

    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
        setStringNotation(alarmTime.get(Calendar.HOUR_OF_DAY),alarmTime.get(Calendar.MINUTE));
    }

    public Calendar getAlarmTime() {
        if (alarmTime.before(Calendar.getInstance()))
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        while(!Arrays.asList(getDays()).contains(Day.values()[alarmTime.get(Calendar.DAY_OF_WEEK)-1])){
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }
        return alarmTime;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public Day[] getDays() {
        return days;
    }

    public void setDays(Day[] days) {
        this.days = days;
    }

    public void addDay(Day day){
        boolean contains = false;
        for(Day d : getDays())
            if(d.equals(day))
                contains = true;
        if(!contains){
            List<Day> result = new LinkedList<Day>();
            for(Day d : getDays())
                result.add(d);
            result.add(day);
            setDays(result.toArray(new Day[result.size()]));
        }
    }

    public void removeDay(Day day)
    {
        List<Day> result = new LinkedList<Day>();
        for(Day d : getDays())
            if(!d.equals(day))
                result.add(d);
        setDays(result.toArray(new Day[result.size()]));
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public Boolean isAlarmActive() {
        return alarmActive;
    }

    public void setAlarmActive(Boolean alarmActive) {
        this.alarmActive = alarmActive;
    }

    public Boolean getVibrate() {
        return vibrate;
    }

    public void setVibrate(Boolean vibrate) {
        this.vibrate = vibrate;
    }

    public String getAlarmTonePath() {
        return alarmTonePath;
    }

    public void setAlarmTonePath(String alarmTonePath) {
        this.alarmTonePath = alarmTonePath;
    }

    public int getID() {return ID;}

    public void setId(int id) {
        this.ID = id;
    }
    public String getRepeatDaysString() {
        StringBuilder daysStringBuilder = new StringBuilder();
        if(getDays().length == Day.values().length){
            daysStringBuilder.append("Every Day");
        }else{
            Arrays.sort(getDays(), new Comparator<Day>() {
                @Override
                public int compare(Day lhs, Day rhs) {

                    return lhs.ordinal() - rhs.ordinal();
                }
            });
            for(Day d : getDays()){
                switch(d){
                    case TUESDAY:
                    case THURSDAY:
//					daysStringBuilder.append(d.toString().substring(0, 4));
//					break;
                    default:
                        daysStringBuilder.append(d.toString().substring(0, 3));
                        break;
                }
                daysStringBuilder.append(',');
            }
            daysStringBuilder.setLength(daysStringBuilder.length()-1);
        }

        return daysStringBuilder.toString();
    }

    public String getTimeUntilNextAlarmMessage(){
        long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
        String alert = "Alarm will sound in ";
        if (days > 0) {
            alert += String.format(
                    "%d days, %d hours, %d minutes and %d seconds", days,
                    hours, minutes, seconds);
        } else {
            if (hours > 0) {
                alert += String.format("%d hours, %d minutes and %d seconds",
                        hours, minutes, seconds);
            } else {
                if (minutes > 0) {
                    alert += String.format("%d minutes, %d seconds", minutes,
                            seconds);
                } else {
                    alert += String.format("%d seconds", seconds);
                }
            }
        }
        return alert;
    }
    public void schedule(Context context) {
        setAlarmActive(true);

        Intent myIntent = new Intent(context, AlarmAlertBroadcastReceiver.class);
        myIntent.putExtra("alarm", this);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getAlarmTime().getTimeInMillis(), pendingIntent);
    }

}
