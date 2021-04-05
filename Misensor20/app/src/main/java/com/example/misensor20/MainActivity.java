package com.example.misensor20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.misensor20.API.API;
import com.example.misensor20.model.Apidata;
import com.example.misensor20.model.HumedadYTemperatura_model;
import com.example.misensor20.model.feeds;
import com.example.misensor20.Ajustes.sendlastupdate;
import com.google.android.material.tabs.TabLayout;
import com.example.misensor20.temperatura.getinfotemp;
import com.example.misensor20.humedad.getinfohum;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


//validar configuracion inicial --- I think done i need test in other device
//poner otra grafica de humedad
//navigation-swipe-view
//Agregar un edit para seleccionar cuantos datos traer

public class MainActivity extends AppCompatActivity implements setting.FragmentSettings, sendlastupdate, getinfotemp,getinfohum, Application.ActivityLifecycleCallbacks  {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private String SELECTEDSERVIDOR = "";
    private String USER_CHANNELS = "";
    private String APIKEY = "";
    private String IP = "";
    private String CANTIDAD = "";
    private Apidata Settings = new Apidata();
    private LoadingDialog loadingDialog;
    private String cantidadtmpp;
    private temperatura temperatura;
    private humedad humedad;
    private Ajustes ajustes;
    private List<feeds> sendlist = null; //referencia a la lista de datos cargada por retrofit

    private String usuario;
    private String pass;
    private static final int uniqueID = 45612;
    private MqttAndroidClient client = null;
    public String clientId = "Android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingDialog = new LoadingDialog(MainActivity.this);
        clientId = MqttClient.generateClientId();
        client =
                new MqttAndroidClient(MainActivity.this, "tcp://mqtt.thingspeak.com:1883",
                        clientId);
        cargar();
        ReadDataOnMemory();
    }

    private void getandsendlastupdate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

        String currentDateandTime = simpleDateFormat.format(new Date());
        ajustes.setlastupdates("Ultima actualizacion: "+currentDateandTime);
    }

    private void cargar() {

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        viewPager = findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),this.getApplicationContext());
        viewPager.setAdapter(adapter);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        temperatura = adapter.getTemperatura();
        humedad = adapter.getHumedad();
        ajustes = adapter.getAjustes();

        /*viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //cambio y ahora se encuentra la opsion 1
                        //Toast.makeText(getApplicationContext(),"Configuracion", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        //ajustes.graficaT = temperatura.graficaT;
                        //cambio y ahora se encuentra la opsion 2
                        //Toast.makeText(getApplicationContext(),"Temperatura", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        //cambio y ahora se encuentra la opsion 3
                        //Toast.makeText(getApplicationContext(),"Humedad", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        //temperatura.graficaT = ajustes.graficaT;
                        //cambio y ahora se encuentra la opsion 3
                        //Toast.makeText(getApplicationContext(),"Humedad", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });*/
    }

    public void getDataRetrofit(String cantidad){
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Settings.getIP())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            API getdataAPI = retrofit.create(API.class);
            Call<HumedadYTemperatura_model> call = getdataAPI.getData(Settings.getUSER_CHANNELS(),Settings.getAPIKEY(),cantidad);

            call.enqueue(new Callback<HumedadYTemperatura_model>() {
                @Override
                public void onResponse(Call<HumedadYTemperatura_model> call, Response<HumedadYTemperatura_model> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Error de respuesta de la API" + response.code(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    List<feeds> feeds = response.body().getfeeds();
                    sendlist = feeds;
                    if(temperatura != null && humedad != null){
                        temperatura.setLista(sendlist);
                        humedad.setLista(sendlist);
                        loadingDialog.dismissDialog();
                        cantidadtmpp = CANTIDAD;
                        connect();
                    }
                }

                @Override
                public void onFailure(Call<HumedadYTemperatura_model> call, Throwable t) {
                    Log.e(TAG, "onFailure: ", t);
                    loadingDialog.dismissDialog();
                }
            });


        }catch (Exception e){
            Log.d(TAG, "getDataRetrofit: Ocurrio un error al crear retrofit para traer los datos revisar su direccion IP " +e );
            loadingDialog.dismissDialog();
        }
    }


    public void ReadDataOnMemory(){
        loadingDialog.StartLoadingDialog();
        SharedPreferences preferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        IP = preferences.getString("IP","");
        APIKEY = preferences.getString("APIKEY","");
        USER_CHANNELS = preferences.getString("USER_CHANNELS","");
        CANTIDAD = preferences.getString("cantidad", "");
        SELECTEDSERVIDOR = preferences.getString("ServidorOld", "");
        Settings = new Apidata(APIKEY,USER_CHANNELS,CANTIDAD,SELECTEDSERVIDOR,IP);

        usuario = preferences.getString("Usuario", "");
        pass = preferences.getString("pass", "");
        if(SELECTEDSERVIDOR != null && USER_CHANNELS != null && APIKEY != null && IP != null && CANTIDAD != null){

            getDataRetrofit(CANTIDAD);
        }
    }

    @Override
    public void onClickedBtnSave() {
        ReadDataOnMemory();
        if(SELECTEDSERVIDOR != null && USER_CHANNELS != null && APIKEY != null && IP != null && CANTIDAD != null){
            try {
                if(client.isConnected()){
                    client.unsubscribe(topic);
                }
            }catch (MqttException e){
                Log.e(TAG, "closed: ", e);
            }
            getDataRetrofit(CANTIDAD);
            adapter.notifyDataSetChanged();
        }
        loadingDialog.dismissDialog();
    }

    @Override
    public void getlastupdate() {
        getandsendlastupdate();
    }

    @Override
    public void actualizarregistro() {
        SharedPreferences preferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        CANTIDAD = preferences.getString("cantidad", "");
        if(!CANTIDAD.equals(cantidadtmpp)){
            if(SELECTEDSERVIDOR != null && USER_CHANNELS != null && APIKEY != null && IP != null && CANTIDAD != null){
                getDataRetrofit(CANTIDAD);
            }
        }
        loadingDialog.dismissDialog();
    }

    @Override
    public void actualizartmp() {
        SharedPreferences preferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        try {
            float maxv = Float.parseFloat(preferences.getString("TmaxT", ""));
            float minv = Float.parseFloat(preferences.getString("TminT", ""));
            temperatura.calcular(minv, maxv);
        }catch (Exception e){  }
    }

    @Override
    public void actualizarhum() {
        loadingDialog.StartLoadingDialog();
        SharedPreferences preferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        try {
            float maxvx = Float.parseFloat(preferences.getString("TmaxH", ""));
            float minvx = Float.parseFloat(preferences.getString("TmixH", ""));
            humedad.calcular(minvx, maxvx);
        }catch (Exception e){}
        loadingDialog.dismissDialog();
    }

    @Override
    public void cargardatos() {
        actualizartmp();
    }

    @Override
    public void cargardatoshum() {
        actualizarhum();
    }


    private void connect(){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

        if(!usuario.isEmpty() && !pass.isEmpty()){
            char[] passsend = pass.toCharArray();
            options.setUserName(usuario);
            options.setPassword(passsend);

            try {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        Log.d(TAG, "Connected");
                        subscribe(client);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "onFailure connect");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private String topic = "";
    private void subscribe(MqttAndroidClient client) {
        topic = "channels/" + Settings.getUSER_CHANNELS() +"/subscribe/json";
        try {
            if(client.isConnected()){
                Log.d(TAG, "onClick: esta conectado");

                client.subscribe(topic,0);

                client.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable cause) {
                        lostconection(cause);
                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String response = new String(message.getPayload());
                        jsonConvert(response);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

            }
        }catch (MqttException e) {
            Log.e(TAG, "onClick: ", e);
        }
    }

    private void lostconection(Throwable cause) {
        Log.e(TAG, "connectionLost: ", cause);
        if(client.isConnected()){
            try {
                client.unsubscribe(topic);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        subscribe(client);
    }

    public JSONObject jsonConvert(String reponse){
        JSONObject obj = null;
        Log.d(TAG, "messageArrived: Nuevos Datos");
        try {
            obj = new JSONObject(reponse);



            int entry_id = Integer.parseInt(obj.getString("entry_id"));

            if(entry_id != sendlist.get(sendlist.size()-1).getEntryId()){
                feeds nuevo = new feeds(obj.getString("created_at"), entry_id ,  obj.getString("field1"),  obj.getString("field2"),  obj.getString("field3"));
                sendlist.remove(0);
                sendlist.add(nuevo);
                temperatura.setLista(sendlist);
                humedad.setLista(sendlist);
                getandsendlastupdate();
                alerta( Float.parseFloat(obj.getString("field1")) , Float.parseFloat(obj.getString("field2")));
            }
        } catch (JSONException e) {
            Log.e(TAG, "jsonConvert: ", e);
            Log.e(TAG, "jsonConvert:  Could not parse malformed JSON: " + reponse  );
        }

        return obj;
    }


    private NotificationManager notifManager;
    public void createNotification(String aMessage, String titleshow, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = String.valueOf(uniqueID); // default_channel_id
        String title = titleshow; // Default Channel
        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle(aMessage)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(context.getString(R.string.app_name)) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(aMessage))
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }



    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        //closed(client, topic);
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }

    private void closed(MqttAndroidClient client, String topic){
        try {
            if(client.isConnected()){
                client.unsubscribe(topic);
            }
        }catch (MqttException e){
            Log.e(TAG, "closed: ", e);
        }
    }

    private void alerta(float temperatura, float humedad){
        Log.d(TAG, "alerta: ");
        SharedPreferences preferences = this.getSharedPreferences("DATA", Context.MODE_PRIVATE);
       boolean maxT = preferences.getBoolean("CmaxT", false);
       boolean minT = preferences.getBoolean("CminT", false);
       boolean maxH =  preferences.getBoolean("CmaxH", false);
       boolean minH = preferences.getBoolean("CminH", false);

       String maxtval = preferences.getString("TmaxT", "");
       String mintval = preferences.getString("TminT", "");
       String maxhval = preferences.getString("TmaxH", "");
       String minhval = preferences.getString("TmixH", "");



       if(maxT && minT){
           if(temperatura >= Integer.parseInt(maxtval)) {
               createNotification("¡Alerta! El ultimo valor de la temperatura supera el máximo.", "Temperatura", this);
           }else if(temperatura <= Integer.parseInt(mintval)) {
               createNotification("¡Alerta! El ultimo valor de la temperatura supera el mínimo.", "Temperatura", this);
           }
       }else if(maxT){
           if(temperatura >= Integer.parseInt(maxtval)) {
               createNotification("¡Alerta! El ultimo valor de la temperatura supera el máximo.", "Temperatura", this);
           }
       }else if(minT){
           if(temperatura <= Integer.parseInt(mintval)) {
               createNotification("¡Alerta! El ultimo valor de la temperatura supera el mínimo.", "Temperatura", this);
           }
       }

       if(maxH && minH){
           if(humedad >= Integer.parseInt(maxhval)) {
               createNotification("¡Alerta! El ultimo valor de la humedad supera el máximo.", "Humedad", this);
           }else if(humedad <= Integer.parseInt(minhval)) {
               createNotification("¡Alerta! El ultimo valor de la humedad supera el mínimo.", "Humedad", this);
           }
       }else if(maxH){
           if(humedad >= Integer.parseInt(maxhval)) {
               createNotification("¡Alerta! El ultimo valor de la humedad supera el máximo.", "Humedad", this);
           }
       }else if(minH){
           if(humedad <= Integer.parseInt(minhval)) {
               createNotification("¡Alerta! El ultimo valor de la humedad supera el mínimo.", "Humedad", this);
           }
       }
    }
}