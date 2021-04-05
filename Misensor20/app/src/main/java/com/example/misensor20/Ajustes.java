package com.example.misensor20;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.example.misensor20.model.Apidata;
import com.github.mikephil.charting.charts.LineChart;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Ajustes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Ajustes extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String TAG = "Ajustes";

    private TextView lastupdate;
    private sendlastupdate comunicador;

    private String CANTIDAD = "0"; //cantidad a traer del servidor


    //Valores Max y min de temperatura Edittext y Textview
    private TextView TmaxT,TminT;
    private EditText EmaxT,EminT;


    //Valores Max y min de humedad Edittext y Textview
    private TextView TmaxH,TminH;
    private EditText EmaxH,EminH;


    //boton para guardar
    private Button guardar;


    //alertas

    //Temperatura
    private CheckBox CmaxT, CminT;

    //Humedad
    private CheckBox CmaxH, CminH;

    //Registros maximos a traer
    private EditText registros;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public interface sendlastupdate{
        void getlastupdate();
        void actualizarregistro();
        void actualizartmp();
        void actualizarhum();
    }

    public Ajustes() {
        // Required empty public constructor
    }


    public void setlastupdates(String hora){
        if(lastupdate != null){
            lastupdate.setText(hora);
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Ajustes.
     */
    // TODO: Rename and change types and number of parameters
    public static Ajustes newInstance(String param1, String param2) {
        Ajustes fragment = new Ajustes();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);
        //ultima actualizacion realizada
        lastupdate = view.findViewById(R.id.lastupdate);
        //Textview Temperatura min y max
        TmaxT = view.findViewById(R.id.textmaxtemp);
        TminT = view.findViewById(R.id.textmintemp);
        //EditText Temperatura min y max
        EmaxT = view.findViewById(R.id.tmpmax);
        EminT = view.findViewById(R.id.tmpmin);

        //Textview Humeedad min y max
        TmaxH = view.findViewById(R.id.textmaxhum);
        TminH = view.findViewById(R.id.textminhum);

        //EditText Humedad min y max
        EmaxH = view.findViewById(R.id.hummax);
        EminH = view.findViewById(R.id.hummin);

        //Checkbox Alertas
        //Temperatura
        CmaxT = view.findViewById(R.id.tmpmaxcheck);
        CminT = view.findViewById(R.id.tmpmincheck);
        //Humedad
        CmaxH = view.findViewById(R.id.hummaxcheck);
        CminH = view.findViewById(R.id.hummincheck);

        //Registros a traer
        registros = view.findViewById(R.id.registrosmax);

        //Boton para guardar cambios
        guardar = view.findViewById(R.id.button_guardar);


        registros.setText(CANTIDAD);



        CmaxT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificacionTemperaturaMax(isChecked);
            }
        });


        CminT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificacionTemperaturaMin(isChecked);
            }
        });

        CmaxH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificacionHumedadMax(isChecked);
            }
        });

        CminH.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificacionHumedadMin(isChecked);
            }
        });
        //notifica a MainActivity que ya puede hacer uso de los elementos del layout
        comunicador.getlastupdate();
        //intentar traer maximo de regritros actual
        ReadDataOnMemory();

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });



        return view;
    }

    public void notificacionTemperaturaMax(Boolean check){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CmaxT", check);
        editor.apply();
    }
    public void notificacionTemperaturaMin(Boolean check){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CminT", check);
        editor.apply();
    }
    public void notificacionHumedadMax(Boolean check){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CmaxH", check);
        editor.apply();
    }
    public void notificacionHumedadMin(Boolean check){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("CminH", check);
        editor.apply();
    }

    public void save(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(!CANTIDAD.isEmpty() && !CANTIDAD.equals("0")){
            editor.putString("cantidad", registros.getText().toString());
            registros.setText(registros.getText().toString());
        }

        if(!EmaxT.getText().toString().isEmpty() && !EmaxT.getText().equals(TmaxT.getText())){
            editor.putString("TmaxT", EmaxT.getText().toString());
            TmaxT.setText(EmaxT.getText().toString()+ "°");
            EmaxT.setText("");
        }

        if(!EminT.getText().toString().isEmpty() && !EminT.getText().equals(TminT.getText())){
            editor.putString("TminT", EminT.getText().toString());
            TminT.setText(EminT.getText().toString()+ "°");
            EminT.setText("");
        }

        if(!EminH.getText().toString().isEmpty() && !EminH.getText().equals(TminH.getText())){
            editor.putString("TmixH", EminH.getText().toString());
            TminH.setText(EminH.getText().toString());
            EminH.setText("");
        }


        if(!EmaxH.getText().toString().isEmpty() && !EmaxH.getText().equals(TmaxH.getText())){
            editor.putString("TmaxH", EmaxH.getText().toString());
            TmaxH.setText(EmaxH.getText().toString());
            EmaxH.setText("");
        }


        editor.apply();
        comunicador.actualizartmp();
        comunicador.actualizarregistro();
        comunicador.actualizarhum();
    }


    public void ReadDataOnMemory(){

        SharedPreferences preferences = getContext().getSharedPreferences("DATA", Context.MODE_PRIVATE);

        CANTIDAD = preferences.getString("cantidad", "");

        TmaxT.setText(preferences.getString("TmaxT", "") + "°");
        TminT.setText(preferences.getString("TminT", "") + "°");

        TmaxH.setText(preferences.getString("TmaxH", ""));
        TminH.setText(preferences.getString("TmixH", ""));

        CmaxT.setChecked(preferences.getBoolean("CmaxT", false));
        CminT.setChecked(preferences.getBoolean("CminT", false));

        CminH.setChecked(preferences.getBoolean("CminH", false));
        CmaxH.setChecked(preferences.getBoolean("CmaxH", false));

        if(!CANTIDAD.equals("0")){
            registros.setText(CANTIDAD);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof sendlastupdate){
            comunicador = (sendlastupdate) context;
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