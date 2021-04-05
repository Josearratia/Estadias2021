
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
#define STASSID "INFINITUM4804_2.4"      // "SSID-nombre Wifi" 
#define PASS "PpU7cUttBJ"            // Contraseña del Wifi
#endif

const char* ssid     = STASSID;
const char* password = PASS;

ESP8266WiFiMulti WiFiMulti;

boolean conectado = false; //variable para usar el loop cuando esta conectado al wifi

//endwifi


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

    if(WiFiMulti.run() == WL_CONNECTED){//si la coneccion se pierde intentara reconectar mientras muestra el mensaje
      Serial.println("Aun conectado!");
      delay(500);
    }else{
      Serial.println("Ha perdido la conexion"); 
      delay(500);
    }
  }
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
