package com.example.misensor20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.misensor20.model.Apidata;

import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link setting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class setting extends Fragment implements AdapterView.OnItemSelectedListener  {
    private static final String TAG = "Settings Data:";
    private FragmentSettings comunicador;

    public interface FragmentSettings{
        void onClickedBtnSave();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Spinner listaservidores;
    EditText edit_IPservidor;
    EditText edit_Key;
    EditText edit_channel;
    EditText edit_user;
    EditText edit_pss;
    Button guardar;
    String servidor = "";
    ArrayAdapter<CharSequence> adapter = null;


    private String SELECTEDSERVIDOR = "";
    private String USER_CHANNELS = "";
    private String APIKEY = "";
    private String IP = "";
    private String USUARIO = "";
    private String PASS = "";


    private final Apidata Settings = new Apidata();



    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public setting() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment setting.
     */
    // TODO: Rename and change types and number of parameters
    public static setting newInstance(String param1, String param2) {
        setting fragment = new setting();
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

        ReadDataOnMemory();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        edit_IPservidor = view.findViewById(R.id.edit_IPservidor);
        edit_Key = view.findViewById(R.id.edit_Key);
        edit_channel = view.findViewById(R.id.edit_channel);
        listaservidores = view.findViewById(R.id.servidores);
        guardar = view.findViewById(R.id.btn_guardar);

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.servidores, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listaservidores.setAdapter(adapter);
        listaservidores.setOnItemSelectedListener(this);

        edit_user = view.findViewById(R.id.edit_usuario);
        edit_pss = view.findViewById(R.id.edit_pass);




        EditSettings();

        guardar.setOnClickListener(v -> Guardar());

        edit_user.setText(USUARIO);
        edit_pss.setText(PASS);

        return view;
    }


    public void EditSettings(){
        if(!IP.isEmpty() && !APIKEY.isEmpty() && !USER_CHANNELS.isEmpty() && !SELECTEDSERVIDOR.isEmpty()){
            int IndexSelected = adapter.getPosition(SELECTEDSERVIDOR);
            listaservidores.setSelection(IndexSelected);
            edit_IPservidor.setText(IP);
            edit_channel.setText(USER_CHANNELS);
            edit_Key.setText(APIKEY);
        }else{
            edit_IPservidor.setText("");
            edit_Key.setText("");
            edit_channel.setText("");
        }
    }

    public void SaveData(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("IP", Settings.getIP());
        editor.putString("APIKEY", Settings.getAPIKEY());
        editor.putString("USER_CHANNELS", Settings.getUSER_CHANNELS());
        editor.putString("cantidad", Settings.getCantidad());
        editor.putString("ServidorOld", Settings.getServidorselected());
        String usuario = edit_user.getText().toString();
        String pass = edit_pss.getText().toString();
        editor.putString("Usuario", usuario);
        editor.putString("pass", pass);

        editor.apply();
    }

    private void Guardar(){
        String Ip = edit_IPservidor.getText().toString();
        String Key = edit_Key.getText().toString();
        String Channel = edit_channel.getText().toString();
        String usuario = edit_user.getText().toString();
        String pass = edit_pss.getText().toString();

        if(!Ip.isEmpty() && !Key.isEmpty() && !Channel.isEmpty()){
            Settings.setServidorselected(servidor);
            Settings.setUSER_CHANNELS(Channel);
            Settings.setAPIKEY(Key);
            Settings.setIP(Ip);
            Settings.setCantidad("60");
            SaveData();

            comunicador.onClickedBtnSave();

        }
        else{
            Toast.makeText(this.getContext(), "Campos vacios ", Toast.LENGTH_LONG).show();
        }
    }

    public void ReadDataOnMemory(){
        SharedPreferences preferences = this.getActivity().getSharedPreferences("DATA", Context.MODE_PRIVATE);
        IP = preferences.getString("IP","");
        APIKEY = preferences.getString("APIKEY","");
        USER_CHANNELS = preferences.getString("USER_CHANNELS","");
        SELECTEDSERVIDOR = preferences.getString("ServidorOld", "");
        USUARIO = preferences.getString("Usuario", "");
        PASS = preferences.getString("pass", "");

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        servidor = parent.getItemAtPosition(position).toString();
        selectedservidor();
    }

    private void selectedservidor() {
        if(!servidor.isEmpty()){
            if(servidor.equals("Seleccione un servidor")){
                edit_IPservidor.setText("");
                edit_Key.setText("");
                edit_channel.setText("");

                edit_IPservidor.setEnabled(false);
                edit_Key.setEnabled(false);
                edit_channel.setEnabled(false);
            }else{
                edit_IPservidor.setEnabled(true);
                edit_Key.setEnabled(true);
                edit_channel.setEnabled(true);
            }

            if(servidor.equals("thingspeak.com")){
                edit_IPservidor.setText("https://api.thingspeak.com/");
                edit_Key.setText("J8FR3PJMX71ZS990");
                edit_channel.setText("1279648");
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentSettings){
            comunicador = (FragmentSettings) context;
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