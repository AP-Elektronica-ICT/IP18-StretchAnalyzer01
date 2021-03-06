#include <Wire.h>
#include <math.h>
#include <SoftwareSerial.h>
SoftwareSerial BT(10, 11);

#define CTRL_REG1 0x20
#define CTRL_REG2 0x21
#define CTRL_REG3 0x22
#define CTRL_REG4 0x23
#define CTRL_REG5 0x24

int L3G4200D_Address = 105; //I2C address of the L3G4200D

const int analogInPin0 = A0;  // x
const int analogInPin1 = A1;  // y
const int analogInPin2 = A2;  // z


int sensorValue0 = 0;        // x
int sensorValue1 = 0;        // y
int sensorValue2 = 0;        // z

double  kiihtyvyys_x = 0;  //x-akselin kiihtyvyys m/s^2
double  kiihtyvyys_y = 0;  //x-akselin kiihtyvyys m/s^2
double  kiihtyvyys_z = 0;  //x-akselin kiihtyvyys m/s^2
double  edellkiiht = 0;
double  edellMaksimi = -99;
double  edellMinimi = 99;
double  pMaksimi = -99;
double  pMinimi = 99;
double  edellpMaksimi = -99;
double  edellpMinimi = 99;
double  Maksimi = 0;
double  Minimi = 0;
double  suurinkulma = 0;
double  pieninkulma = 0;


unsigned long aika = 0;

int x;
int y;
int z;
int tila;
int edelltila = 0;
int ylataso = 0;
int alataso = 0;



int getTila() //tarkistaa tilan tai luo sen
{ 
  if(kiihtyvyys_x > 1)
    {
    tila = 1;
    return tila;
    }
    else if(kiihtyvyys_x <  -1)
    {
      tila = -1;
      return tila;
    }  
    else
    {
      tila = 0;
      return tila;
    }
}

double getTaso() //onko alataso vai ylataso
{     
      if(tila == 1 && edelltila == 1)
      {
         ylataso = 1;
         return ylataso;
      }
     else if(tila == -1 && edelltila == -1)
     {
      alataso = 1;
      return alataso;
     }
    else
    {
      ylataso = 0;
      alataso = 0;
    return alataso, ylataso;
    }
}

double  getMaksimi()
{
Maksimi = kiihtyvyys_x;/*
Serial.print(aika);
  Serial.print("\t");
Serial.print(Maksimi);
  Serial.print("\t");
*/
  if (Maksimi > 6 && Maksimi < 9.81)
  {
      pMinimi = kiihtyvyys_x;
      if (edellMaksimi < Maksimi)
      {
        edellpMinimi = pMinimi;
      double ylamuuttuja;
      double ylakulma;
      double ylakuluma = max (-9.81, edellpMinimi);
      ylamuuttuja = asin(ylakuluma/9.81)/3.14*180;
      ylakulma = ylamuuttuja + 90;
      if (suurinkulma < ylakulma){    //tähän joku parempi ehto, idea olisi että kulma olisi suurempi
          suurinkulma = ylakulma;
          }
      Serial.print(aika);
      Serial.print("\t");
      Serial.print(suurinkulma);
      Serial.println("");
      }
  }
  return Maksimi;
}

double   getMinimi()
{
   Minimi = kiihtyvyys_x;
   /*
   Serial.print(aika);
  Serial.print("\t");
   Serial.print(Minimi);
  Serial.print("\t");*/

  if (Minimi < -6 && Minimi > -9.81)
  {
    pMaksimi = kiihtyvyys_x;
       if (edellMinimi > Minimi)
       {
        edellpMaksimi = pMaksimi;
        double alamuuttuja = 99;
        double alakulma = 99;
        double alakuluma = max(-9.81, edellpMaksimi);
       // alamuuttuja = asin(min (-9.81, edellpMaksimi)/9.81)/3.14*180;

              alamuuttuja = asin(alakuluma/9.81)/3.14*180;
        alakulma = alamuuttuja - 90;
        if (pieninkulma > alakulma){ //tähän joku parempi ehto, idea olisi että kulma olisi pienempi
          pieninkulma = alakulma;
          }
        Serial.print(aika);
        Serial.print("\t");
        Serial.print(pieninkulma);
        Serial.println("");
           alamuuttuja = 0;
        alakulma = 0;
       }
      
  }

  
  return Minimi;
}

void setup()
{

  Wire.begin();
  Serial.begin(115200);

  Serial.println("starting up L3G4200D");
  setupL3G4200D(2000); // Configure L3G4200  - 250, 500 or 2000 deg/sec

  //setup hc-06 bluetooth module
  pinMode(13, OUTPUT); // set digital pin to control as an output
  
  BT.begin(9600);   // set the data rate for the SoftwareSerial port
  
  BT.println("Hello from Arduino"); // Send test message to other device

  delay(1500); //wait for the sensor to be ready 
}

void loop ()
{
  edellkiiht = kiihtyvyys_x;
  edelltila = tila;
  edellMaksimi = Maksimi;
  edellMinimi = Minimi;
  sensorValue0 = analogRead(analogInPin0);
  sensorValue1 = analogRead(analogInPin1);
  sensorValue2 = analogRead(analogInPin2);
   //jannite = sensorValue/1023.0 * 5.0;
  aika = millis();
  

    
  kiihtyvyys_x = 0.1401 * sensorValue0 -49.33;        //kiihtyvyydet
  kiihtyvyys_x = constrain(kiihtyvyys_x, -9.81, 9.81);
  
  kiihtyvyys_y = 0.1391 * sensorValue1 -48.726;
  kiihtyvyys_y = constrain(kiihtyvyys_y, -9.81, 9.81);
  
  kiihtyvyys_z = 0.1382 * sensorValue2 -49.093;
  kiihtyvyys_z = constrain(kiihtyvyys_z, -9.81, 9.81);  

if (edellkiiht > kiihtyvyys_x + 1.5 || edellkiiht < kiihtyvyys_x - 1.5)  //tilan tarkistus
    {
  getTila(); 
    }   

getTaso();    

if(ylataso == 1)
  {
  getMaksimi();
  }

if(alataso == 1) 
  {
  getMinimi();
  }


/*  Serial.print(aika);
  Serial.print("\t");
  Serial.print(kiihtyvyys_x);
  Serial.print("\t");
  /*Serial.print(kiihtyvyys_y);
  Serial.print("\t ");
  Serial.print(kiihtyvyys_z);
  Serial.print("\t ");
  Serial.print(tila);
  Serial.print("\t ");
  Serial.print(ylataso);
  Serial.print("\t ");
  Serial.print(alataso);
  Serial.println("\t ");*/
  delay (50);
suurinkulma = 0;
pieninkulma = 0;
}



void getGyroValues()
{

  byte xMSB = readRegister(L3G4200D_Address, 0x29);
  byte xLSB = readRegister(L3G4200D_Address, 0x28);
  x = ((xMSB << 8) | xLSB);

  byte yMSB = readRegister(L3G4200D_Address, 0x2B);
  byte yLSB = readRegister(L3G4200D_Address, 0x2A);
  y = ((yMSB << 8) | yLSB);

  byte zMSB = readRegister(L3G4200D_Address, 0x2D);
  byte zLSB = readRegister(L3G4200D_Address, 0x2C);
  z = ((zMSB << 8) | zLSB);
}

int setupL3G4200D(int scale){
  //From  Jim Lindblom of Sparkfun's code

  // Enable x, y, z and turn off power down:
  writeRegister(L3G4200D_Address, CTRL_REG1, 0b00001111);

  // If you'd like to adjust/use the HPF, you can edit the line below to configure CTRL_REG2:
  writeRegister(L3G4200D_Address, CTRL_REG2, 0b00000000);

  // Configure CTRL_REG3 to generate data ready interrupt on INT2
  // No interrupts used on INT1, if you'd like to configure INT1
  // or INT2 otherwise, consult the datasheet:
  writeRegister(L3G4200D_Address, CTRL_REG3, 0b00001000);

  // CTRL_REG4 controls the full-scale range, among other things:

  if(scale == 250){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00000000);
  }else if(scale == 500){
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00010000);
  }else{
    writeRegister(L3G4200D_Address, CTRL_REG4, 0b00110000);
  }

  // CTRL_REG5 controls high-pass filtering of outputs, use it
  // if you'd like:
  writeRegister(L3G4200D_Address, CTRL_REG5, 0b00000000);
}

void writeRegister(int deviceAddress, byte address, byte val) {
    Wire.beginTransmission(deviceAddress); // start transmission to device 
    Wire.write(address);       // send register address
    Wire.write(val);         // send value to write
    Wire.endTransmission();     // end transmission
}

int readRegister(int deviceAddress, byte address){

    int v;
    Wire.beginTransmission(deviceAddress);
    Wire.write(address); // register to read
    Wire.endTransmission();

    Wire.requestFrom(deviceAddress, 1); // read a byte

    v = Wire.read();
    return v;
}
