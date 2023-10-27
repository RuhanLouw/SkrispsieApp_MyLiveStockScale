/*
  V0.2
  ADS123X.cpp - Library for reading from an ADS1232 and ADS1234 24-bit ADC.
  Created by Hamid Saffari @ Jan 2018. https://github.com/HamidSaffari/ADS123X
  Released into the public domain.
*/

#include "ADS123X.h"

#if ARDUINO_VERSION <= 106
    // "yield" is not implemented as noop in older Arduino Core releases, so let's define it.
    // See also: https://stackoverflow.com/questions/34497758/what-is-the-secret-of-the-arduino-yieldfunction/34498165#34498165
    void yield(void) {};
#endif


ADS123X::ADS123X() {
}

ADS123X::~ADS123X() {
}

void ADS123X::begin(byte pin_DOUT, byte pin_SCLK, byte pin_PDWN, byte pin_GAIN0, byte pin_GAIN1, byte pin_SPEED, byte pin_A0, byte pin_A1_or_TEMP, Gain gain, Speed speed){
  _pin_DOUT = pin_DOUT;
  _pin_SCLK = pin_SCLK;
  _pin_PDWN = pin_PDWN;
  _pin_GAIN0 = pin_GAIN0;
  _pin_GAIN1 = pin_GAIN1;
  _pin_SPEED = pin_SPEED;
  _pin_A0 = pin_A0;
  _pin_A1_or_TEMP = pin_A1_or_TEMP;
  

  pinMode(_pin_DOUT,  INPUT_PULLUP);
  pinMode(_pin_SCLK, OUTPUT);
  pinMode(_pin_PDWN, OUTPUT);
  pinMode(_pin_GAIN0, OUTPUT);
  pinMode(_pin_GAIN1, OUTPUT);
  pinMode(_pin_SPEED, OUTPUT);
  pinMode(_pin_A0, OUTPUT);
  pinMode(_pin_A1_or_TEMP, OUTPUT);
  
  setGain(gain);
  setSpeed(speed);
  
  power_up();

}

bool ADS123X::is_ready(void)
{
  return digitalRead(_pin_DOUT) == LOW;
}

void ADS123X::setGain(Gain gain)
{
  switch(gain)
  {
    case GAIN1:
    {
      digitalWrite(_pin_GAIN1, LOW);
      digitalWrite(_pin_GAIN0, LOW);
      break;
    }
    case GAIN2:
    {
      digitalWrite(_pin_GAIN1, LOW);
      digitalWrite(_pin_GAIN0, HIGH);
      break;
    }
    case GAIN64:
    {
      digitalWrite(_pin_GAIN1, HIGH);
      digitalWrite(_pin_GAIN0, LOW);
      break;
    }
    case GAIN128:
    {
      digitalWrite(_pin_GAIN1, HIGH);
      digitalWrite(_pin_GAIN0, HIGH);
      break;
    }
  }
}

void ADS123X::power_up(void)
{
  // delayMicroseconds(10);
  // digitalWrite(_pin_PDWN, HIGH);
  // delayMicroseconds(26);
  // digitalWrite(_pin_PDWN, LOW);
  // delayMicroseconds(26);
  digitalWrite(_pin_PDWN, HIGH);
  // Set CLK low to get the ADS1231 out of suspend
  digitalWrite(_pin_SCLK, LOW);
}

void ADS123X::power_down(void)
{
  digitalWrite(_pin_PDWN, LOW);
  digitalWrite(_pin_SCLK, HIGH);
}


void ADS123X::setSpeed(Speed speed)
{
  _speed = speed;
  switch(speed)
  {
    case SLOW:
    {
      digitalWrite(_pin_SPEED, LOW);
      break;
    }
    case FAST:
    {
      digitalWrite(_pin_SPEED, HIGH);
      break;
    }
  }
 
}

void ADS123X::setChannel(Channel channel)
{
  switch(channel)
  {
    case AIN1:
    {
      digitalWrite(_pin_A1_or_TEMP, LOW);
      digitalWrite(_pin_A0, LOW);
      break;
    }
    case AIN2:
    {
      digitalWrite(_pin_A1_or_TEMP, LOW);
      digitalWrite(_pin_A0, HIGH);
      break;
    }
  #if defined ADS1232
    case TEMP:
    {
      digitalWrite(_pin_A1_or_TEMP, HIGH);
      digitalWrite(_pin_A0, LOW);
      break;
    }
  #elif defined ADS1234
	case AIN3:
    {
      digitalWrite(_pin_A1_or_TEMP, HIGH);
      digitalWrite(_pin_A0, LOW);
      break;
    }
	case AIN4:
    {
      digitalWrite(_pin_A1_or_TEMP, HIGH);
      digitalWrite(_pin_A0, HIGH);
      break;
    }
  #endif

  }
}


/*
 * Get the raw ADC value. Can block up to 100ms in normal operation.
 * Returns 0 on success, an error code otherwise.
 */
ERROR_t ADS123X::read(Channel channel,long& value, bool Calibrating)
{
    int i=0;
    unsigned long start;
	unsigned int waitingTime;
	unsigned int SettlingTimeAfterChangeChannel=0;

	if(channel!=lastChannel){
		setChannel(channel);
		
		if(_speed==FAST) SettlingTimeAfterChangeChannel=55;
		else SettlingTimeAfterChangeChannel=405;
		lastChannel=channel;
	}
	
    /* A high to low transition on the data pin means that the ADS1231
     * has finished a measurement (see datasheet page 13).
     * This can take up to 100ms (the ADS1231 runs at 10 samples per
     * second!).
     * Note that just testing for the state of the pin is unsafe.
     */
	 
	if(Calibrating){
		if(_speed==FAST) waitingTime=150;
		else waitingTime=850;
	}
	else{
		if(_speed==FAST) waitingTime=20;
		else waitingTime=150;
	}
	waitingTime+=SettlingTimeAfterChangeChannel;

	waitingTime+=600; //[ms] Add some extra time ( sometimes takes longer than what datasheet claims! )
	
    start=millis();
    while(digitalRead(_pin_DOUT) != HIGH)
    {
        if(millis()-start > waitingTime)
            return TIMEOUT_HIGH; // Timeout waiting for HIGH
    }

    start=millis();
    while(digitalRead(_pin_DOUT) != LOW)
    {
        if(millis()-start > waitingTime)
            return TIMEOUT_LOW; // Timeout waiting for LOW
    }

    // Read 24 bits
    for(i=23 ; i >= 0; i--) {
        digitalWrite(_pin_SCLK, HIGH);
        value = (value << 1) + digitalRead(_pin_DOUT);
        digitalWrite(_pin_SCLK, LOW);
    }

	
	if(Calibrating){
	// 2 extra bits for calibrating
		for(i=1 ; i >= 0; i--) {
			digitalWrite(_pin_SCLK, HIGH);
			digitalWrite(_pin_SCLK, LOW); 
		}
	}
	
	if(!Calibrating){
		/* The data pin now is high or low depending on the last bit that
		 * was read.
		 * To get it to the default state (high) we toggle the clock one
		 * more time (see datasheet).
		 */
		digitalWrite(_pin_SCLK, HIGH);
		digitalWrite(_pin_SCLK, LOW);
	}
    
    /* Bit 23 is acutally the sign bit. Shift by 8 to get it to the
     * right position (31), divide by 256 to restore the correct value.
     */
    value = (value << 8) / 256;

    return NoERROR; // Success
}

ERROR_t ADS123X::read_average(Channel channel, float& avg_val, float* values_raw, byte times, bool Calibrating) {

	long sum = 0;
  float vals_holder[times];
	ERROR_t err;
	for (byte i = 0; i < times; i++) {
		long val;
		err = read(channel, val, Calibrating);
		if(err!=NoERROR) return err;
		
		sum += val;
    //TODO: Calculate LLF with 95% confidence
    //print long then print float -- see the difference
    vals_holder[i] =  (float)val; //raw values

   Serial.printf("raw value float %d: %f\n", (int)i, (float)val);
   Serial.printf("raw sum value long: %ld\n", sum);
    values_raw[i] = vals_holder[i];

		yield();

	}//for i = 0; i < times; i++
	if(times==0) return DIVIDED_by_ZERO;
	avg_val = (float) sum / times;
  //Serial.printf("average value float: %f\n", avg_val);
  
	return NoERROR;
}

ERROR_t ADS123X::get_value(Channel channel, float& offsetted_value, float* offsetted_values_holder, float* values_raw, byte times, bool Calibrating) {
	float val = 0;
  float vals_raw[times];
  float vals_offset[times];
	ERROR_t err;
	err = read_average(channel, val, vals_raw, times, Calibrating);
	if(err!=NoERROR) return err;
  // Minus each value in vals_holder by OFFSET[channel-1]
  //TOdo: iets hier is nie lekker nie
  //Serial.printf("offset to minus value (float): %f\n", OFFSET[channel-1]);
  for (byte i = 0; i < times; i++) {
    vals_offset[i] = vals_raw[i] - OFFSET[channel - 1];
    //Serial.printf("offsetted value float %d: %f\n", (int)i, vals_offset[i]);
  }
  offsetted_value = val - OFFSET[channel-1];
  //Serial.printf("average offsetted value float: %f\n", offsetted_value);
  offsetted_values_holder = vals_offset;
  values_raw = vals_raw;
	return NoERROR;
}

ERROR_t ADS123X::get_units(Channel channel, float& unit_value, float* unit_values_holder, float* values_raw, byte times, bool Calibrating) {
	float val_offset = 0;
  float vals_raw[times];
  float vals_offset[times];
	ERROR_t err;
	err = get_value(channel, val_offset, vals_offset, vals_raw, times, Calibrating);
	if(err!=NoERROR) return err;
	if(SCALE[channel-1]==0) return DIVIDED_by_ZERO;

  // Divide each value in vals_holder by SCALE[channel-1]
  for (byte i = 0; i < times; i++) {
    unit_values_holder[i] = vals_offset[i] / SCALE[channel - 1];
    //Serial.printf("unit value float %d: %f\n", (int)i, (float)vals_offset[i]);

  }
  unit_value = val_offset / SCALE[channel-1];
  //Serial.printf("average unit value float: %f\n", unit_value);
  values_raw = vals_raw;
	return NoERROR;
}

ERROR_t ADS123X::tare(Channel channel, byte times, bool Calibrating) {
	ERROR_t err;
	float sum = 0;
  float vals_holder[times];
  float vals_raw[times];
	err = read_average(channel, sum, vals_holder, times, Calibrating);
	if(err!=NoERROR) return err;
  
	set_offset(channel, sum);
	return NoERROR;
}

void ADS123X::set_scale(Channel channel, float scale) {
	SCALE[channel-1] = scale;
}

float ADS123X::get_scale(Channel channel) {
	return SCALE[channel-1];
}

void ADS123X::set_offset(Channel channel, float offset) {
	OFFSET[channel-1] = offset;
}

float ADS123X::get_offset(Channel channel) {
	return OFFSET[channel-1];
}
