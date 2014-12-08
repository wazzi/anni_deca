package wazza.ke.servista.utilities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by kelli on 10/26/14.
 */
public class Unit implements Parcelable {

    //Tags
    private static final String EXP_TAG = "UNIT_EXCEPTION";
    private static final String ERR_TAG = "UNIT_ERROR";
    private static final String INFO_TAG = "UNIT_INFO";

    public Unit() {
        //default constructor
    }

    private String id;
    private String fullName;
    private String code;
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() { return description;}

    public void setDescription(String description) { this.description = description;}


    //    //unserialization happens here (retrieving the unit data from the parcel object)...this will be invoked by the method createFromParcel(Parcel p) of the
    //object CREATOR
    public Unit(Parcel in) {
        Log.i(INFO_TAG, "creating object from parcel");
        this.code = in.readString();
        this.fullName = in.readString();
        this.description=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //serializing the object here...
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        Log.i(INFO_TAG, "Writting object to parcel..." + i);
        parcel.writeString(fullName);
        parcel.writeString(code);
        parcel.writeString(description);
    }

    public static final Creator<Unit> CREATOR = new Creator<Unit>() {
        public Unit createFromParcel(Parcel in) {
            Unit u = new Unit(in);
            return u;
        }

        public Unit[] newArray(int size) {
            return new Unit[size];
        }
    };


}
