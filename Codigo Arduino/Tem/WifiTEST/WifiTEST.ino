#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>

#ifndef STASSID
#define STASSID "INFINITUM4804_2.4"      // "SSID-nombre Wifi" 
#define PASS "PpU7cUttBJ"            // Contraseña del Wifi
#endif

const char* ssid     = STASSID;
const char* password = PASS;

ESP8266WiFiMulti WiFiMulti;

boolean conectado = false; //variable para usar el loop cuando esta conectado al wifi


void setup() {
  Serial.begin(115200);
  pinMode(LED_BUILTIN, OUTPUT);
  delay(1000);
  Serial.println("Iniciando......");
  
  
  WiFi.mode(WIFI_STA);
  WiFiMulti.addAP(ssid, password);

  
  //el codigo solo va a esperar a conectar al wifi no tiene sentido seguir sin la coneccion al wifi por lo tanto solo retorna true o se mantiene en un siclo esperando conectar a la SSID
  if(conectarWiFi()){
    Serial.println("OKEY");
    delay(250);
    conectado = true;
  }
}

void loop() {
  if(conectado){
    digitalWrite(LED_BUILTIN, HIGH);  
    delay(100);                       
    digitalWrite(LED_BUILTIN, LOW);    
    delay(100);

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
