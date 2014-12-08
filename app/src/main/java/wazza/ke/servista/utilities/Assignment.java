package wazza.ke.servista.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by kelli on 11/17/14.
 */
public class Assignment implements Parcelable{

    //Tags
    private static final String EXP_TAG = "UNIT_EXCEPTION";
    private static final String ERR_TAG = "UNIT_ERROR";
    private static final String INFO_TAG = "UNIT_INFO";

    //
    private String title;
    private String description;
    private String dueDate;
    private String grade;
    private String submissionsAllowDate;


    public Assignment(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubmissionsAllowDate() {
        return submissionsAllowDate;
    }

    public void setSubmissionsAllowDate(String submissionsAllowDate) {
        this.submissionsAllowDate = submissionsAllowDate;
    }



    //    //unserialization happens here (retrieving the unit data from the parcel object)...this will be invoked by the method createFromParcel(Parcel p) of the
    //object CREATOR
    public Assignment(Parcel in) {
        Log.i(INFO_TAG, "creating object from parcel");
        this.title = in.readString();
        this.description = in.readString();
        this.grade = in.readString();
        this.submissionsAllowDate = in.readString();
        this.dueDate=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //serializing the object here...
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.i(INFO_TAG, "Writting object to parcel..." + i);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(grade);
        parcel.writeString(submissionsAllowDate);
        parcel.writeString(dueDate);
    }

    public static final Parcelable.Creator<Assignment> CREATOR = new Parcelable.Creator<Assignment>() {
        public Assignment createFromParcel(Parcel in) {
            Assignment assignment = new Assignment(in);
//            u.fullName=in.readString();
//            u.code=in.readString();
            String s = assignment.getTitle();
            Log.i(INFO_TAG, "Current unit: " + s);
            return assignment;
        }

        public Assignment[] newArray(int size) {
            return new Assignment[size];
        }
    };
}
