package com.example.veere.Main;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    String address = "98:D3:71:F5:DE:83";
    //String address = "94:B8:6D:3B:EB:E6";
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    Button[] btnList = new Button[9];
    private final int[] buttons_id= {R.id.button,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9};
    TextView outText, inText;
    Button eraseButton, setButton, cancelButton;
    volatile String password = "12123";
    volatile String usrIn;
    volatile boolean setMode = false;
/*    String password = "12123";
    String usrIn;*/
    boolean passCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
            //finish apk
            finish();
        }
        else
        {
            if (myBluetooth.isEnabled())
            { }
            else
            {
                //Ask to the user turn the bluetooth on
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon,1);
            }
        }

        new ConnectBT().execute();
        usrIn = "";
        outText = findViewById(R.id.textView2);
        inText = findViewById(R.id.textView);
        for(int i=0;i<9;i++){
            final int t = i;
            btnList[i] = findViewById(buttons_id[i]);
            btnList[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    numPressed(t+1);
                }
            });
        }
        eraseButton = findViewById(R.id.button10);
        setButton = findViewById(R.id.button11);
        cancelButton = findViewById(R.id.button12);

        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                erase();
            }
        });
        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassword();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }
    public void numPressed(int a) {

        usrIn += String.valueOf(a);
        inText.setText(usrIn);
        if (password.equals(usrIn) && setMode == false) {
            doorUnlock();
            usrIn="";
            inText.setText("");
        }else if (usrIn.length()==5 && password.equals(usrIn)==false){
            Toast.makeText(getApplicationContext(),"Wrong Password, Try again!",Toast.LENGTH_SHORT).show();
            usrIn="";
            inText.setText("");
        }
    }
    public void doorUnlock(){
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("A".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error");
            }
        }
        Toast.makeText(getApplicationContext(),"Door Unlocked",Toast.LENGTH_LONG).show();
    }
    public void erase(){
        if (usrIn.length()>0) {
            usrIn = usrIn.substring(0, usrIn.length() - 1);
            inText.setText(usrIn);
        }
    }
    public void setPassword(){
        setButton.setEnabled(false);
        setMode = true;
        usrIn="";
        outText.setText("Enter Current Password");


        Runnable r = new Runnable() {
            @Override
            public void run() {

                while(usrIn.equals(password)==false && setMode){ //setmode is checked to be true to see if someone cancelled while typing password

                }
                if(setMode){

                    usrIn="";

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            inText.setText("");
                            outText.setText("Enter New Password");
                        }
                    });
                }
                while(usrIn.length()<5 && setMode){

                }
                if(setMode){
                    password = usrIn;
                    usrIn = "";
                    setMode=false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setButton.setEnabled(true);
                            inText.setText("");
                            outText.setText("Enter Password");
                        }
                    });
                    //setButton.setEnabled(true);
                }
            }
        };
        Thread t1 = new Thread(r);
        t1.start();

    }
    public void cancel(){
        usrIn = "";
        inText.setText("");
        outText.setText("Input password");
        setButton.setEnabled(true);
        setMode=false;
    }
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }
/*    private class setP extends AsyncTask<Void, Void, Void>  // UI thread
    {
        @Override
        protected void onPreExecute()
        {
            //progress = ProgressDialog.show(getApplicationContext(), "Connecting...", "Please wait!!!");  //show a progress dialog
            outText.setText("Enter Current Password");
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            while(usrIn.equals(password)==false && setMode){ //setmode is checked to be true to see if someone cancelled while typing password

            }
            if(setMode){
                outText.setText("Enter New Password");
                usrIn="";
                inText.setText("");
            }
            while(usrIn.length()<5 && setMode){

            }
            if(setMode){
                password = usrIn;
                usrIn = "";
                inText.setText("");
                setMode=false;

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            outText.setText("Enter Password");
            setButton.setEnabled(true);
        }
    }*/
    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
        }
    }
}
