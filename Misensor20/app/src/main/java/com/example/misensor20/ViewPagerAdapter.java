package com.example.misensor20;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.misensor20.API.API;
import com.example.misensor20.model.Apidata;
import com.example.misensor20.model.HumedadYTemperatura_model;
import com.example.misensor20.model.feeds;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "ViewPagerAdapter";

    private ListDatos obj;
    Context context = null;
    private List<feeds> Data = null;
    private temperatura temperatura;
    private humedad humedad;
    private Ajustes ajustes;

    public ViewPagerAdapter(@NonNull FragmentManager fm, Context c) {
        super(fm);
        this.context = c;
        temperatura = new temperatura();
        humedad = new humedad();
        ajustes = new Ajustes();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                setting settingFragment = new setting();
                return settingFragment;
            case 1:
                /*Bundle bundleTemperatura = new Bundle();
                bundleTemperatura.putSerializable("Datos", obj);
                temperatura.setArguments(bundleTemperatura);*/
                return temperatura;
            case 2:
                return humedad;
            case 3:
                return ajustes;
            default:
                return null;
        }
    }

    public temperatura getTemperatura() {
        return temperatura;
    }

    public humedad getHumedad() {
        return humedad;
    }

    public Ajustes getAjustes() {
        return ajustes;
    }



    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String output = "";
        switch (position){
            case 0:
                output = "Configuracion";
                break;
            case 1:
                output = "Temperatura";
                break;
            case 2:
                output = "Humedad";
                break;
            case 3:
                output = "Ajustes";
                break;
            default:
                output = "Error Name";
                break;
        }
       return output;
    }
}
