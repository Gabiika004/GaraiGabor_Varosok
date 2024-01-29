package com.example.varosok;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {

    private int id;
    private String nev;
    private String orszag;
    private int lakossag;

    public City(String nev, String orszag, int lakossag) {
        this.id = id;
        this.nev = nev;
        this.orszag = orszag;
        this.lakossag = lakossag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getOrszag() {
        return orszag;
    }

    public void setOrszag(String orszag) {
        this.orszag = orszag;
    }

    public int getLakossag() {
        return lakossag;
    }

    public void setLakossag(int lakossag) {
        this.lakossag = lakossag;
    }

    protected City(Parcel in) {
        nev = in.readString();
        orszag = in.readString();
        lakossag = in.readInt();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel in) {
            return new City(in);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nev);
        dest.writeString(orszag);
        dest.writeInt(lakossag);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
