package be.eaict.stretchalyzer1;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static int REQUEST_ENABLE_BT = 1;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String address;
    BluetoothSocket btSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnOn = (Button)findViewById(R.id.btnOn);
        Button btnOff = (Button)findViewById(R.id.btnOff);

        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!myBluetoothAdapter.isEnabled()){
            Intent enableBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBT, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = myBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice device : pairedDevices){
                if(device.getName().equals("HC-05")){
                    address = device.getAddress();
                    Log.d("Address", address);
                }
            }
        }

        BluetoothDevice HC05 = myBluetoothAdapter.getRemoteDevice(address);//connects to the device's address and checks if it's available
        try {
            btSocket = HC05.createInsecureRfcommSocketToServiceRecord(myUUID);
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            Log.d("Connection", "SUCCES!");
        } catch (IOException e) {
            Log.d("Connection", e.toString());
        }

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOn();
            }
        });
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOff();
            }
        });
    }

    private void turnOn(){
        try
        {
            btSocket.getOutputStream().write('1');
            try
            {
                int bytesAvailable = btSocket.getInputStream().available();
                byte []packetBytes = new byte[bytesAvailable];
                if(bytesAvailable > 0){
                    btSocket.getInputStream().read(packetBytes);
                    String test = new String(packetBytes);
                    Log.d("receive", test);
                }
            }
            catch (Exception e)
            {
                Log.d("Error", "not received");
            }
        }
        catch (IOException e)
        {
            Log.d("Error", "failed");
        }
    }

    private void turnOff(){
        try
        {
            btSocket.getOutputStream().write('0');
        }
        catch (IOException e)
        {
            Log.d("Error","failed");
        }
    }

}
