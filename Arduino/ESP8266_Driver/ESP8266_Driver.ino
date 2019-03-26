#include <FirebaseESP8266.h>
#include <ESP8266WiFi.h>

//Constants
#define WIFI_SSID "NETGEAR50"
#define WIFI_PASSWORD "shinysparrow288"
#define FIREBASE_LINK "ez-serve-81804.firebaseio.com"
#define FIREBASE_SECRET "f8QvXMp36EcqZdNcvr64WvOBzmMnhIZ2peboCXjm"
#define TABLE "/Connection/42019/Status"

//Global variables
FirebaseData firebaseData;
int val = 0;
int buttonState = 0;
int lastButtonState = LOW;

void setup() {
  Serial.begin(115200);

  //Initialize pin modes
  pinMode(2, OUTPUT); 
  pinMode(0, INPUT);
  digitalWrite(2, LOW);

  //Connect to WiFi
  Serial.print("connecting");
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("connected: ");
  Serial.println(WiFi.localIP());

  //Connect to Firebase
  Firebase.begin(FIREBASE_LINK, FIREBASE_SECRET);
  Firebase.reconnectWiFi(true);

  //Visual cue of setup completed
  setupComplete();
}

void setupComplete() {
  digitalWrite(2, HIGH);
  delay(300);
  digitalWrite(2, LOW);
  delay(300);
  digitalWrite(2, HIGH);
  delay(300);
  digitalWrite(2, LOW);
  delay(300);
  digitalWrite(2, HIGH);
  delay(300);
  digitalWrite(2, LOW);
}

void loop() {
//BUTTON HANDLER
  buttonState = digitalRead(0);
  if (buttonState == HIGH && lastButtonState == LOW) {
    delay(100);
    if (val == 0) {
      digitalWrite(2, HIGH);
      val = 1;
      if (!Firebase.setString(firebaseData, TABLE, "ON")){
        Serial.println(firebaseData.errorReason());
      }
    } else {
      digitalWrite(2, LOW);
      val = 0;
      if (!Firebase.setString(firebaseData, TABLE, "OFF")){
        Serial.println(firebaseData.errorReason());
      }
    }
  }
  lastButtonState = buttonState;

  if (Firebase.getString(firebaseData, TABLE)) {   
    String LED_Status = firebaseData.stringData();
    Serial.println(LED_Status);
    delay(100);
    if (LED_Status == "ON"){
      digitalWrite(2, HIGH); 
      val = 1;
    } else if (LED_Status == "OFF") {
      digitalWrite(2, LOW);
      val = 0;
    }
  } else {
    Serial.println(firebaseData.errorReason());
  }
  delay(150);
}
