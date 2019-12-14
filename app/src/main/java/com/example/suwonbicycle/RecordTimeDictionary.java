package com.example.suwonbicycle;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordTimeDictionary implements Parcelable {

    private String title;
    private String time;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public RecordTimeDictionary(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public RecordTimeDictionary(String title) {
        this.title = title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(time);
    }

    public static final Parcelable.Creator<RecordTimeDictionary> CREATOR
            = new Parcelable.Creator<RecordTimeDictionary>() {
        public RecordTimeDictionary createFromParcel(Parcel in) {
            return new RecordTimeDictionary(in);
        }

        @Override
        public RecordTimeDictionary[] newArray(int size) {
            return new RecordTimeDictionary[size];
        }
    };

    private RecordTimeDictionary(Parcel in) {
        title = in.readString();
        time = in.readString();
    }
    }
