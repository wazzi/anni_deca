package wazza.ke.servista.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.Date;

/**
 * Created by kelli on 11/20/14.
 */
public class CalendarEvent implements Parcelable {

    private static final String EXP_TAG = "CALENDAR_EVENTS_VIEW_EXCEPTION";
    private static final String ERR_TAG = "CALENDAR_EVENTS_VIEW_ERROR";
    private static final String INFO_TAG = "CALENDAR_EVENTS_VIEW_INFO";

    private String eventName;
    private String eventId;
    private Long eventStartDate;
    private Long eventEndDate;


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getEventStartDate() {
        return eventStartDate;
    }

    public void setEventStartDate(Long eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public Long getEventEndDate() {
        return eventEndDate;
    }

    public void setEventEndDate(Long eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    //    //unserialization happens here (retrieving the unit data from the parcel object)...this will be invoked by the method createFromParcel(Parcel p) of the
    //object CREATOR
    public CalendarEvent(Parcel in) {
        Log.i(INFO_TAG, "creating object from parcel");
        this.eventName = in.readString();
        this.eventId = in.readString();
        this.eventStartDate=in.readLong();
        this.eventEndDate=in.readLong();
    }

    public CalendarEvent(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    //serializing the object here...
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.i(INFO_TAG, "Writting event object to parcel..." + i);
        parcel.writeString(eventName);
        parcel.writeString(eventId);
        parcel.writeLong(eventStartDate);
        parcel.writeLong(eventEndDate);
    }

    public static final Parcelable.Creator<CalendarEvent> CREATOR = new Parcelable.Creator<CalendarEvent>() {
        public CalendarEvent createFromParcel(Parcel in) {
            CalendarEvent e = new CalendarEvent(in);
//            u.fullName=in.readString();
//            u.code=in.readString();
            String s = e.getEventName();
            Log.i(INFO_TAG, "Current event: " + s);
            return e;
        }

        public CalendarEvent[] newArray(int size) {
            return new CalendarEvent[size];
        }
    };
}
