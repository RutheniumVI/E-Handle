char command;
String string;
boolean ledon = false;
#define out1 7
#define out2 8

void setup()
{
  Serial1.begin(9600);
  pinMode(out1, OUTPUT);
  pinMode(out2, OUTPUT);
}

void loop()
{
  if (Serial1.available() > 0){
    
    while(Serial1.available() > 0)
    {
      command = Serial1.read();
      
      delay(1);
    }
    if(string == "F")
    {
      forward();
    }
    
    if(command =="R")
    {
      reverse();    
    }
    if(command=="S"){
      off();
    }
    if(command=="A"){
      forward();
      delay(7000);
      brake();
      delay(4000);
      reverse();
      delay(5000);
      off();
    }
  } 
}
void forward(){
  digitalWrite(out1, HIGH);
  digitalWrite(out2, LOW);
}
void reverse(){
  digitalWrite(out1, LOW);
  digitalWrite(out2, HIGH);
}
void off(){
  digitalWrite(out1, LOW);
  digitalWrite(out2, LOW);  
}
void brake(){
  digitalWrite(out1, HIGH);
  digitalWrite(out2, HIGH);  
}
 

    
