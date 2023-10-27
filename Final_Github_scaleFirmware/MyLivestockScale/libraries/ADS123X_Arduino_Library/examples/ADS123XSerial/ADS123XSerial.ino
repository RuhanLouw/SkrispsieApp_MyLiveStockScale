#include "ADS123X.h"

#define SCALE_DOUT   19
#define SCALE_SCLK   18
#define SCALE_PDWN   17
#define SCALE_GAIN0  16
#define SCALE_GAIN1  4
#define SCALE_SPEED  0
#define SCALE_A0     25
#define SCALE_A1     26

// //Pin Definition
// #define pin_ADS_DATA    19
// #define pin_ADS_SCLK    18
// #define pin_ADS_NPOWER  17
// #define pin_ADS_SPD     16
// #define pin_ADS_A1      4
// #define pin_ADS_A0      0
// #define pin_GAIN0       25
// #define pin_GAIN1       26
ADS123X scale;


void setup() {
	
  Serial.begin(38400);
  Serial.println("ADS123X Demo");
  
  scale.begin(SCALE_DOUT, SCALE_SCLK, SCALE_PDWN, SCALE_GAIN0, SCALE_GAIN1, SCALE_SPEED, SCALE_A0, SCALE_A1, GAIN128, FAST);
  scale.power_down();
  delay(2000);
  scale.power_up();
  delay(1000);

  while(!scale.is_ready()){
    Serial.println("not ready");
    delay(10);
  }
  long value_long;
  ERROR_t err = scale.read(AIN1, value_long);

  Serial.println(err);
  Serial.println(value_long);			// print a raw reading from the ADC

  float value_double;
  Serial.print("read average: \t\t");
  err = scale.read_average(AIN1,value_double,20);
  Serial.println(err);
  Serial.println(value_long);  	// print the average of 20 readings from the ADC

  Serial.print("get value: \t\t");
  scale.get_value(AIN1,value_double,5);
  Serial.println(value_double);		// print the average of 5 readings from the ADC minus the tare weight (not set yet)

  Serial.print("get units: \t\t");
  scale.get_units(AIN1,value_double,5);
  Serial.println(value_double, 1);	// print the average of 5 readings from the ADC minus tare weight (not set) divided 
						// by the SCALE parameter (not set yet)  
  Serial.print("get units: \t\t");
  scale.get_units(AIN1,value_double,5);
  Serial.println(value_double, 1);
  Serial.print("get units: \t\t");
  scale.get_units(AIN1,value_double,5);
  Serial.println(value_double, 1);


  // scale.set_scale(AIN1,2280.f);        // this value is obtained by calibrating the scale with known weights; see the README for details
  // scale.tare(AIN1);				        // reset the scale to 0

  // Serial.println("After setting up the scale:");

  // Serial.print("read: \t\t");
  // scale.read(AIN1, value_long);
  // Serial.println(value_long);                 // print a raw reading from the ADC

  // Serial.print("read average: \t\t");
  // scale.read_average(AIN1,value_double,20);
  // Serial.println(value_long);       // print the average of 20 readings from the ADC

  // Serial.print("get value: \t\t");
  // scale.get_value(AIN1,value_double,5);
  // Serial.println(value_double);		// print the average of 5 readings from the ADC minus the tare weight, set with tare()

  // Serial.print("get units: \t\t");
  // scale.get_units(AIN1,value_double,5);
  // Serial.println(value_double, 1);        // print the average of 5 readings from the ADC minus tare weight, divided 
	// 					// by the SCALE parameter set with set_scale

  Serial.println("Readings:");
}

void loop() {
  //LC1
  float value1;
  Serial.print("1 one reading:\t");
  scale.get_units(AIN1,value1,1,true);
  Serial.print(value1, 1);
  float value_avg1;
  Serial.print("\t| average:\t");
  scale.get_units(AIN1,value_avg1,10,true);
  Serial.println(value_avg1, 1);

  float value2;
  Serial.print("2 one reading:\t");
  scale.get_units(AIN2,value1,1,true);
  Serial.print(value2, 1);
  float value_avg2;
  Serial.print("\t| average:\t");
  scale.get_units(AIN2,value_avg2,10,true);
  Serial.println(value_avg2, 1);

  float value3;
  Serial.print("3 one reading:\t");
  scale.get_units(AIN3,value3,1,true);
  Serial.print(value3, 1);
  float value_avg3;
  Serial.print("\t| average:\t");
  scale.get_units(AIN3,value_avg3,10,true);
  Serial.println(value_avg3, 1);

  float value4;
  Serial.print("4 one reading:\t");
  scale.get_units(AIN4,value4,1,true);
  Serial.print(value4, 1);
  float value_avg4;
  Serial.print("\t| average:\t");
  scale.get_units(AIN4,value_avg4,10,true);
  Serial.println(value_avg4, 1);
  //scale.power_down();			        // put the ADC in sleep mode
  //delay(250);
  //scale.power_up();
}
