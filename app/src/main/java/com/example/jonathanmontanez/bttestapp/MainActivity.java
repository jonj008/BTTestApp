package com.example.jonathanmontanez.bttestapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.widget.ListView;
import android.widget.TextView;
import java.io.IOException;
import android.widget.Toast;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class MainActivity extends AppCompatActivity {

    //widgets

    Button enableBT_btn; // Button for enabling bluetooth.
    Button scanBT_btn; // Bluetooth scan devices.
    Button pairedDevices_btn; // Button to query paired devices.
    ArrayAdapter<String> pArrayAdapter;
    ArrayAdapter<String> dArrayAdapter;
    ListView deviceList;
    ListView pairedList;


    //others

    public BluetoothAdapter BTAdapter;


    // Intent request codes

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(); // function to initialize widgets and some others



        enableBT_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!BTAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

                    Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(discoverableIntent);

                } else if (BTAdapter.isEnabled()) {

                    displayToast("Bluetooth Already Enabled");

                }
            }
        });

        pairedDevices_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Set<BluetoothDevice> pairedDevices = BTAdapter.getBondedDevices();
            // If there are paired devices
                if (pairedDevices.size() > 0) {
                    // Loop through paired devices
                    for (BluetoothDevice device : pairedDevices) {
                        // Add the name and address to an array adapter to show in a ListView
                        pArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    }
                } else {
                    String noDevices = "No paired Devices";
                    pArrayAdapter.add(noDevices);
                }
            }
        });

        scanBT_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if (BTAdapter.isDiscovering()) {
                    BTAdapter.cancelDiscovery();
                }

                BTAdapter.startDiscovery();
                if(!dArrayAdapter.isEmpty()) {

                    dArrayAdapter.clear();
                }


                setProgressBarIndeterminateVisibility(true);
                setTitle("Scanning for devices...");


                // Create a BroadcastReceiver for ACTION_FOUND
                 final BroadcastReceiver mReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        // When discovery finds a device
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                            displayToast("Device Found");
                            // Get the BluetoothDevice object from the Intent
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            // Add the name and address to an array adapter to show in a ListView
                            dArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }

                        if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                            setProgressBarIndeterminateVisibility(false);
                            setTitle("Done Scanning");
                    }
                };

                // Register the BroadcastReceiver
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy



            }
        });


    }//end of onCreate


    public void displayToast(String msg){
    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
}


    private void init() {

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        enableBT_btn = (Button)findViewById(R.id.buttonBT);
        scanBT_btn = (Button)findViewById(R.id.buttonScan);
        pairedDevices_btn = (Button)findViewById(R.id.buttonPaired);
        pArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        dArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        pairedList = (ListView)findViewById(R.id.pairedlistView);
        deviceList = (ListView)findViewById(R.id.devicelistView);
        deviceList.setAdapter(dArrayAdapter);
        pairedList.setAdapter(pArrayAdapter);



    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {

                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth Enabled",Toast.LENGTH_SHORT ).show();
                }
                }
        }
    }

