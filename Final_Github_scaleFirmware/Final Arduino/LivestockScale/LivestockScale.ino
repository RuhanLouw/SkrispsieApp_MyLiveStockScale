/*  
    Writen by R. Louw
    Mechatronics Skripsie
    MyLiveStock Scale
    ver 1.0 2023
*/

#include <ADS1234.h>
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

//Pin Definition
#define pin_ADS_DATA    19
#define pin_ADS_SCLK    18
#define pin_ADS_NPOWER  17
#define pin_ADS_SPD     16
#define pin_ADS_A1      4
#define pin_ADS_A0      0
#define pin_GAIN0       25
#define pin_GAIN1       26

//Objects
  BluetoothSerial SerialBT;
  ADS1234 ads1234;
//

//Variables
  bool calibrating = false; //calibrate internal offsets on ads1234
  Speed _speed = SLOW;
  static uint8_t timesReadChannel = 10; //how many times must each channel be read (set multiple readings per channel, once per measurement)
  // float scale[] = {71582.84,71582.84,71582.84,71582.84}; //set scale raw value modifier for each channel to get "human readable" values, ie 'kg'. See conversion design for more detail.
  float scale[] = {1,1,1,1};
  char terminator = '\n';
  bool performTask = false;

  char Rx_buffer[16];  // 'taskCode', 'taskCode_var', '\n'
  uint8_t i_rx = 0; //1 to 3, 0 for init
  char taskCode;
  char taskCode_var;
  bool addCalibrate;
  bool adcSleeping;
  String response;
//

// Regression Model
  bool useCalCurve = false;
  uint8_t numOfWeights = 5;
  float a[4] = {0.0, 0.0, 0.0, 0.0};  
  float b[4] = {0.0, 0.0, 0.0, 0.0};  
  float c[4] = {0.0, 0.0, 0.0, 0.0};
//

//Functions
  String get_measurement(String type, bool calibration);
  void setScale(float calibration_const1, float calibration_const2, float calibration_const3, float calibration_const4); //this is the calibration const that the adc's value will be devided by to get to human readable values
  String setTare(String var, bool calibration);
  void RespondToTask(String response);
  void goToSleep(bool adcSleeping);
  bool isError (ERROR_t err1, ERROR_t err2, ERROR_t err3, ERROR_t err4);
  void calibrateLoadCell(int i, float input_weight[], float output_raw_offsetted_value[], uint8_t n);

//

/////////////////////////////////////////////////////////////////////////////////////////////////////////
void setup() {
  Serial.begin(115200);
  SerialBT.begin("Sheep Scale"); //Bluetooth device name
  Serial.printf("\nThe Sheep Scale started, now you can pair it with bluetooth!\n");
  
  adcSleeping = false;
  addCalibrate = false;
  response = "0";

  delay(5);//make sure PSU transients has been damped
  ads1234.begin(pin_ADS_DATA, pin_ADS_SCLK, pin_ADS_NPOWER, pin_GAIN0, pin_GAIN1, pin_ADS_SPD, pin_ADS_A0, pin_ADS_A1, GAIN128, _speed);
      // goToSleep(adcSleeping);
      // delay(100);
      // goToSleep(adcSleeping);

      // while(!ads1234.is_ready()){
      //   Serial.println("DATA PIN HIGH");
      // };
  String init_success;
  setScale(scale[0], scale[1], scale[2], scale[3]); //first-order regression model calibration contstants
  init_success = setTare("3", false); //tare
  Serial.print(init_success);

}
/////////////////////////////////////////////////////////////////////////////////////////////////////////
void loop() {

// READ BT MESSAGE
  //if ready to perform task, performTask = true
  //store incomming meassage; Rx_buffer[0] = taskCode, Rx_buffer[1] = taskCode_var, Rx_buffer[2] = '\n'
  if (SerialBT.available()>0) {
    Rx_buffer[i_rx] = SerialBT.read();
    i_rx++;
    if(Rx_buffer[2] == '\n'){
      Serial.write(Rx_buffer);
      taskCode = Rx_buffer[0];
      taskCode_var = Rx_buffer[1];
      Rx_buffer[0] = ' ';
      Rx_buffer[1] = ' ';
      Rx_buffer[2] = ' ';
      performTask = true; //-> if ready to perform task, performTask = true
      i_rx = 0;
      //check taskCode_var
      if(taskCode_var == '1'){
        addCalibrate = true;
      }else if(taskCode_var == '0') addCalibrate = false;
    }
  }//SerialBT.available() 

// TASK
  // Perfrom the Requested Task
  if (performTask){
    // each function for the taskCode has responding
    switch(taskCode){
      case '1': // Measure
        response = get_measurement("1", addCalibrate);
        RespondToTask(response); // send the response to the app
        taskCode = '0';
        taskCode_var = '0';
        response = "0";
        performTask = false;
        break;
      case '2': // Continuous Measurement
        response = get_measurement("2", false);
        RespondToTask(response);
        //do not reset taskCode
        response = "0";
        break;
      case '3': // Tare
        response = setTare("3", addCalibrate); //get and set the offset of the tare weight 
        RespondToTask(response); // send the response to the app
        taskCode = '0';
        taskCode_var = '0';
        response = "0";
        performTask = false;
        break;
      case '4': // Sleep/WakeUp
        goToSleep(adcSleeping); //if adcSleeping is false, then put the adc into sleep, else wake up the adc1234
        Serial.print("config sleep");
        taskCode = '0';
        taskCode_var = '0';
        response = "0";
        performTask = false;
        break;
      default:
        Serial.println("Error; no such task code");
        performTask = false;
        break;
    };//switch
  };//performTask
  delay(1); // Take out Delay


}
////////////////////////////////////////////////////////////////////////////////////////////////////////

// user functions //////////////////////////////////////////////////////////////////////////////////////
String get_measurement(String type, bool calibration) {
  String msg = "";
  String measurement_string = "";
  ERROR_t err[4];
  float raw_avg[4];
  float raw_avg_offsetted[4]; // input to regression model
  float raw_offset[4] = {ads1234.get_offset(AIN1), ads1234.get_offset(AIN2), ads1234.get_offset(AIN3), ads1234.get_offset(AIN4)}; 

  float unit_avg[4]; //output of regression model

  Serial.println("Measurements Started");
  err[0] = ads1234.read_average(AIN1, raw_avg[0], timesReadChannel, addCalibrate);
  err[1] = ads1234.read_average(AIN2, raw_avg[1], timesReadChannel, addCalibrate);
  err[2] = ads1234.read_average(AIN3, raw_avg[2], timesReadChannel, addCalibrate);
  err[3] = ads1234.read_average(AIN4, raw_avg[3], timesReadChannel, addCalibrate);

  if(isError(err[0],err[1],err[2],err[3])){
    msg ="0:000.0";
  }else {
    
    Serial.printf("raw_avg\t%f\t%f\t%f\t%f\n", raw_avg[0], raw_avg[1], raw_avg[2], raw_avg[3]);
    Serial.printf("raw_offset\t%f\t%f\t%f\t%f\n", raw_offset[0], raw_offset[1], raw_offset[2], raw_offset[3]);

    raw_avg_offsetted[0] = raw_avg[0] - raw_offset[0];
    raw_avg_offsetted[1] = raw_avg[1] - raw_offset[1];
    raw_avg_offsetted[2] = raw_avg[2] - raw_offset[2];
    raw_avg_offsetted[3] = raw_avg[3] - raw_offset[3];
    Serial.printf("raw_avg_offsetted\t%f\t%f\t%f\t%f\n", raw_avg_offsetted[0], raw_avg_offsetted[1], raw_avg_offsetted[2], raw_avg_offsetted[3]);

    if(useCalCurve){
      float weights[4];
      float weight_total;
      for (int i = 0; i < 4; i++) {
        weights[i] = a[i] * raw_avg_offsetted[i] * raw_avg_offsetted[i] + b[i] * raw_avg_offsetted[i] + c[i];
      }
      Serial.printf("weight\t%f\t%f\t%f\t%f\n", weights[0], weights[1], weights[2], weights[3]);
      weight_total = weights[0] + weights[1] + weights[2] + weights[3];
      Serial.printf("Weight total:\t%f\n", weight_total);
      measurement_string = String(weight_total,2);

    }else{
      float raf_total = raw_avg_offsetted[0] + raw_avg_offsetted[1] + raw_avg_offsetted[2] + raw_avg_offsetted[3];
      Serial.printf("Raf total\t%f\n", raf_total);
      measurement_string = String(raf_total,2);
    } // if use Calibration Curve

  } // if error, else measure

  Serial.println("Measurements Ended");
  msg = type+":"+measurement_string;
  return msg;
};//get_measurement()

String setTare(String var, bool calibration) {
  String msg;
  ERROR_t err[4];

    err[0] = ads1234.tare(AIN1, timesReadChannel, calibration);
    err[1] = ads1234.tare(AIN2, timesReadChannel, calibration);
    err[2] = ads1234.tare(AIN3, timesReadChannel, calibration);
    err[3] = ads1234.tare(AIN4, timesReadChannel, calibration);

  if(isError(err[0],err[1],err[2],err[3])){
    msg = "0:000.0";
  }else {
    float tare_values[5] = {ads1234.get_offset(AIN1), ads1234.get_offset(AIN2), ads1234.get_offset(AIN3), ads1234.get_offset(AIN4),0};
    tare_values[4] = tare_values[0]+tare_values[1]+tare_values[2]+tare_values[3];
    Serial.printf("Tare 1: %f \n",tare_values[0]);
    Serial.printf("Tare 2: %f \n",tare_values[1]);
    Serial.printf("Tare 3: %f \n",tare_values[2]);
    Serial.printf("Tare 4: %f \n",tare_values[3]);
    msg = var+":000.0";
  };
  return msg;
}; //setTare()




// background functions ////////////////////////////////////////////////////////////////////////////////

  //Build the message, that is being sent to the app, with parameters gathered in the loop
void RespondToTask(String response) {
  uint8_t arrayLength = response.length();
  char charArray[(int)(arrayLength+1)];

  response.toCharArray(charArray, sizeof(charArray), 0);
  charArray[(int)arrayLength] = '\n';

  Serial.println("MSG:");
  Serial.print(charArray);

  uint8_t bytesSent = 0; // Variable to track the number of bytes sent
  for (uint8_t i = 0; i <= arrayLength; i++) {
    if (SerialBT.write(charArray[(int)i])) {
      bytesSent++;
    };
  };

  // Check if all bytes were sent successfully
  if (bytesSent == (arrayLength+1) ) {
    Serial.printf("Sent successfully Length: %d \n", (int)arrayLength);
  } else {
    Serial.printf("Failed to send Length: %d \n", (int)arrayLength);
  };
  return;
}; //RespondToTask()


bool isError (ERROR_t err1, ERROR_t err2, ERROR_t err3, ERROR_t err4) {
  // typedef enum ERROR_t {
    // NoERROR=0,
    // TIMEOUT_HIGH,     // Timeout waiting for HIGH
    // TIMEOUT_LOW,      // Timeout waiting for LOW
    // WOULD_BLOCK,      // weight not measured, measuring takes too long
    // STABLE_TIMEOUT,   // weight not stable within timeout
    // DIVIDED_by_ZERO    
  // };
  ERROR_t error[4] = {err1,err2,err3,err4};
  for (int i = 0; i < 4; i++) {
    if (error[i] != NoERROR){
      Serial.printf("Error: %d \n",(int)error[i]);
      return true;
    };
  };
  return false;
};

void setScale(float calibration_const1, float calibration_const2, float calibration_const3, float calibration_const4) {
  //TODO
  ads1234.set_scale(AIN1, calibration_const1);
  ads1234.set_scale(AIN2, calibration_const2);
  ads1234.set_scale(AIN3, calibration_const3);
  ads1234.set_scale(AIN4, calibration_const4);
  return;
};

void goToSleep(bool adcSleeping){
  if(adcSleeping) {
    adcSleeping = false;
    ads1234.power_up();
    Serial.printf("adc power up\n");
  }else {
    adcSleeping = true;
    ads1234.power_down();
    Serial.printf("adc power down\n");
  };
  return;
};







