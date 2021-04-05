
#include <DHT.h>
#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

//Sensor
#define dataPin D2 //pin de datos
#define DHTTYPE DHT22 //Definir tipo de sensor
DHT dht(dataPin, DHTTYPE); //iniciar objeto pasando el pin de datos y el tipo de sensor
//end sensor

//wifi
#ifndef STASSID
#define STASSID "INFINITUM4804_2.4"   // "SSID-nombre Wifi" 
#define PASS "PpU7cUttBJ"            // Contraseña del Wifi
#endif

const char* ssid     = STASSID;
const char* password = PASS;
boolean conectado = false; //variable para usar el loop cuando esta conectado al wifi
//endwifi

//thingspeak.com
String KEY = "7C6KXFQOPO3H85C9"; //KEY de la API de thingspeak
const char* host = "api.thingspeak.com";
const uint16_t port = 80;
ESP8266WiFiMulti WiFiMulti;
//endthingspeak.com


void setup() {
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  delay(1000);
  Serial.println("Iniciando......");
  dht.begin();
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(ssid, password);

  if(conectarWiFi()){
    Serial.println("OKEY");
    delay(250);
    conectado = true;
  }
}

void loop() {
  if(conectado){
    digitalWrite(LED_BUILTIN, HIGH);  
    delay(2000);
      
    float t = dht.readTemperature();
    float h = dht.readHumidity();
    float f = dht.readTemperature(true);

    if (isnan(h) || isnan(t)) {
      Serial.println("No se pudo leer datos del sensor DHT!");
      return;
    }
  
    Serial.print("Humedad: ");
    Serial.print(h);
    Serial.print(" %\t");
    Serial.print("Temperatura: ");
    Serial.print(t);
    Serial.println(" *C ");

    delay(500);
                         
    digitalWrite(LED_BUILTIN, LOW);    
    delay(2000);

    if(WiFiMulti.run() == WL_CONNECTED){//sigue conectado al WIFI ON
        senddata(t,h); //envia los datos por protocolo tcp
        delay(60000); //Actualiza cada 1 min
    }else{
      Serial.println("Ha perdido la conexion"); //WIFI OFF cuando vuelva a enconrar la SSID intentara reconectar
      delay(500);
    }
  }
}

void senddata(float temperatura, float humedad){
  Serial.print("conectado a");
  Serial.print(host);
  Serial.print(':');
  Serial.println(port);

  // Use WiFiClient class to create TCP connections
  WiFiClient client;

  if (!client.connect(host, port)) {
    //Este fallo ocurre cuando no tiene salida a internet pero si conexión pero no salia a Internet o que el sitio web este caido y no se pueda acceder 
    //No puedo acceder al host (sitio web thingspeak.com)
    //Agregar envio a servidor alterno o esperar reconectar
    Serial.println("La conexión falló");
    Serial.println("Intentando reenviar en 1 min.");
    delay(500);
    return;
  }

  Serial.println("Conexión exitosa!");
  String datasend = "field1=" + String(temperatura) + "&field2=" + String(humedad); //datos a enviar
  Serial.println("Datos salientes: "+datasend);//envio final
  
  //si esta conectado prepara datos a enviar a thingspeak.com
  if (client.connected()) {
      client.print("GET /update HTTP/1.1\n"); //Método HTTP - RUTA UPDATE / versión HTTP 1.1 
      client.print("Host: api.thingspeak.com\n"); //Dominio
      client.print("Connection: close\n");
      client.print("X-THINGSPEAKAPIKEY: " + KEY + " \n"); //Key de la API
      client.print("Content-Type: application/x-www-form-urlencoded\n"); 
      client.print("Content-Length: ");
      client.print(datasend.length());
      client.print("\n\n");
      client.print(datasend);//Datos a enviar   
  }
 
  Serial.println("Cerrando conexión con el sitio");
  client.stop();

  Serial.println("Espere 1 min...");
  delay(500);
  
}

boolean conectarWiFi(){
  Serial.print("Esperando conectar a ");
  Serial.print(STASSID);
  Serial.print(" ...");
  while (WiFiMulti.run() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("");
  Serial.println("WiFi conectado");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  return true;
  delay(500);
}
