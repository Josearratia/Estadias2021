
#include <DHT.h>

#define dataPin D2 //pin de datos
#define DHTTYPE DHT22 //Definir tipo de sensor

DHT dht(dataPin, DHTTYPE); //iniciar objeto pasando el pin de datos y el tipo de sensor

void setup(){
  Serial.begin(9600);
  pinMode(LED_BUILTIN, OUTPUT);
  dht.begin();

}

void loop(){
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

  delay(1000);
}
