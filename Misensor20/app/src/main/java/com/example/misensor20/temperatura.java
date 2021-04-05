package com.example.misensor20;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.misensor20.API.API;
import com.example.misensor20.model.Apidata;
import com.example.misensor20.model.HumedadYTemperatura_model;
import com.example.misensor20.model.feeds;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link temperatura#newInstance} factory method to
 * create an instance of this fragment.
 */
public class temperatura extends Fragment implements SeekBar.OnSeekBarChangeListener, OnChartValueSelectedListener {
    private static final String TAG = "temperatura";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private getinfotemp comunicador;

    public interface getinfotemp{
        void cargardatos();
    }

    public LineChart graficaT;
    private List<feeds> Data;
    private TextView showitems;
    private Apidata Settings = null;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY;
    ArcProgress arcProgress;


    int progress = 0;
    public float mintmpd = -30;
    public float maxtmpd = 30;

    private TextView max, min, viewlasttemperatura;

    List<Entry> datatmp;
    List<String> xAxisValues;
    LineDataSet datasettmp;


    public temperatura() {
        Data = null;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment temperatura.
     */
    // TODO: Rename and change types and number of parameters
    public static temperatura newInstance(String param1, String param2) {
        temperatura fragment = new temperatura();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        datasettmp = new LineDataSet(datatmp, "Temperataura");
    }

    public void setLista(List<feeds> MainData){
        Data = MainData;
        setGraficaT(Data.size());
        seekBarX.setMax(Data.size());
        seekBarX.setProgress(Data.size());
        showitems.setText("Min: " + seekBarX.getProgress());
        comunicador.cargardatos();
        calcular(mintmpd,maxtmpd);
    }

    public void calcular(float minv, float maxv){
        mintmpd = minv;  maxtmpd = maxv;
        float lasttmp = Float.parseFloat(Data.get(Data.size()-1).getField1());
        float mintmp = minv;
        float maxtmp = maxv;
        max.setText("MAX " + maxtmp + "°");
        min.setText("MIN " +  mintmp + "°");
        viewlasttemperatura.setText("Temperatura: " + lasttmp + "°");
        float result = ( (lasttmp - mintmp) / (maxtmp - mintmp) ) * 100;
        progress = (int) result;
        if(progress > arcProgress.getMax()){
            arcProgress.setMax(progress + 1);
        }else if(100 > progress){
            arcProgress.setMax(100);
        }
        arcProgress.setProgress(progress);
    }

    private void setGraficaT(int countx){
        datatmp = new ArrayList<>();
        xAxisValues = new ArrayList<>(); // String hora ....
        datasettmp.setCircleColor(Color.GREEN);
        datasettmp.setColor(Color.GREEN);
        Calendar now = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));


        int valormax = Data.size();
        int valorinicial = 0;
        if(countx < Data.size()){
            valorinicial = valormax - countx;
        }

        for (int i = 0; i < countx; i++) {
            try {
                Date date = dateFormat.parse(Data.get(valorinicial).getCreatedAt());
                DateFormat formatter = new SimpleDateFormat("dd-MM HH:mm");
                formatter.setTimeZone(now.getTimeZone());
                String dateStr = formatter.format(date);
                xAxisValues.add(dateStr);
            }catch (Exception e){
                Log.e(TAG, "drawLineChartLine: ", e);
            }

            datatmp.add(new Entry(i+1, Float.parseFloat(Data.get(valorinicial).getField1())));
            valorinicial++;
        }

        try {
            if(graficaT.getData() != null){
                if (graficaT.getData().getDataSetCount() > 0) {
                    datasettmp = (LineDataSet) graficaT.getData().getDataSetByIndex(0);
                    datasettmp.setValues(datatmp);
                    datasettmp.notifyDataSetChanged();

                    XAxis xAxis = graficaT.getXAxis();
                    xAxis.setGranularity(1f);
                    xAxis.setCenterAxisLabels(true);
                    xAxis.setEnabled(true);
                    xAxis.setPosition(XAxis.XAxisPosition.TOP);
                    xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));

                    graficaT.getData().notifyDataChanged();
                    graficaT.notifyDataSetChanged();
                }
            }else{
                LineData data = new LineData(datasettmp);
                XAxis xAxis = graficaT.getXAxis();
                xAxis.setGranularity(1f);
                xAxis.setCenterAxisLabels(true);
                xAxis.setEnabled(true);
                xAxis.setPosition(XAxis.XAxisPosition.TOP);
                graficaT.animateX(1000);
                xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(xAxisValues));
                graficaT.setData(data);
                graficaT.invalidate();

            }
        }catch (Exception e){
            Log.e(TAG, "setGraficaT: Ocurrio un error al generar la grafica  ", e);
        }

        datasettmp.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_temperatura, container, false);
        graficaT = view.findViewById(R.id.grarficaT);
        showitems = view.findViewById(R.id.showitems);

        arcProgress = view.findViewById(R.id.arc_progress);
        seekBarX = view.findViewById(R.id.seekBar1);

        max = view.findViewById(R.id.max);
        min = view.findViewById(R.id.min);
        viewlasttemperatura = view.findViewById(R.id.temperaturaview);

        max.setText("MAX " + 0 + "°");
        min.setText("MIN " + 0 + "°");
        viewlasttemperatura.setText("Temperatura: " + 0 + "°");

        seekBarX.setOnSeekBarChangeListener(this);
        graficaT.setBackgroundColor(Color.WHITE);
        graficaT.setOnChartValueSelectedListener(this);
        graficaT.setDrawBorders(false);
        graficaT.getDescription().setEnabled(false);
        graficaT.setTouchEnabled(true);
        graficaT.setDragEnabled(true);
        graficaT.setScaleEnabled(true);
        graficaT.setPinchZoom(true);
        graficaT.setDrawGridBackground(true);
        graficaT.setExtraLeftOffset(2);
        graficaT.setExtraRightOffset(0);
        graficaT.getXAxis().setDrawGridLines(false);
        graficaT.getAxisLeft().setDrawGridLines(true);
        graficaT.getAxisRight().setDrawGridLines(false);
        arcProgress.setMax(100);
        arcProgress.setSuffixText("%");
        arcProgress.setFinishedStrokeColor(R.color.azul);
        arcProgress.setUnfinishedStrokeColor(R.color.blanco);
        arcProgress.setProgress(progress);
        arcProgress.invalidate();
        arcProgress.setBottomText("Temperatura");

        if(Data != null){
            seekBarX.setMax(Data.size());
            comunicador.cargardatos();
            calcular(mintmpd,maxtmpd);
        }

        return view;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setGraficaT(seekBarX.getProgress());
        showitems.setText("Min: " + seekBarX.getProgress());
        graficaT.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof getinfotemp){
            comunicador = (getinfotemp) context;
        }else{
            throw new RuntimeException(context.toString() + " Debe implementar FragmentSettings");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        comunicador = null;
    }
}