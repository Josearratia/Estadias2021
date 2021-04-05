package com.example.misensor20.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.example.misensor20.API.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Apidata implements Parcelable {
    private static final String TAG = "Apidata";
    private String APIKEY; //J8FR3PJMX71ZS990
    private String USER_CHANNELS; //1279648
    private String cantidad;
    private String servidorselected;
    private String IP;

    private List<feeds> Data;
    private Context context;

    //save the context recievied via constructor in a local variable

    protected Apidata(Parcel in) {
        APIKEY = in.readString();
        USER_CHANNELS = in.readString();
        cantidad = in.readString();
        servidorselected = in.readString();
        IP = in.readString();
        Data = in.createTypedArrayList(feeds.CREATOR);
    }


    public void YourNonActivityClass(Context context){
        this.context = context;
    }

    public Apidata(){
    }

    public Apidata(String APIKEY, String USER_CHANNELS, String cantidad, String servidorselected, String IP) {
        this.APIKEY = APIKEY;
        this.USER_CHANNELS = USER_CHANNELS;
        this.cantidad = cantidad;
        this.servidorselected = servidorselected;
        this.IP = IP;
    }

    public String getAPIKEY() {
        return APIKEY;
    }

    public void setAPIKEY(String APIKEY) {
        this.APIKEY = APIKEY;
    }

    public String getUSER_CHANNELS() {
        return USER_CHANNELS;
    }

    public void setUSER_CHANNELS(String USER_CHANNELS) {
        this.USER_CHANNELS = USER_CHANNELS;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getServidorselected() {
        return servidorselected;
    }

    public void setServidorselected(String servidorselected) {
        this.servidorselected = servidorselected;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public boolean NotEmpty(){
        if(IP.isEmpty() || APIKEY.isEmpty() || USER_CHANNELS.isEmpty()){
            return false;
        }else {
            return true;
        }
    }


    public static String getTAG() {
        return TAG;
    }

    public void setData(List<feeds> data) {
        Data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(USER_CHANNELS);
        dest.writeString(APIKEY);
        dest.writeString(cantidad);
        dest.writeString(servidorselected);
        dest.writeString(IP);
    }

    public static final Parcelable.Creator<Apidata> CREATOR = new Parcelable.Creator<Apidata>()
    {
        public Apidata createFromParcel(Parcel in)
        {
            return new Apidata(in);
        }
        public Apidata[] newArray(int size)
        {
            return new Apidata[size];
        }
    };
}
