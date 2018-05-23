package com.example.kimheeyeon.beacontest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;
import org.w3c.dom.Text;

import java.util.Collection;

public class MainActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        textView = (TextView)findViewById(R.id.beaconList);
        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
            }

        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {    }
    }

    public void OnButtonClicked(View view){
        handler.sendEmptyMessage(0);
    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            textView.setText("");

            //비콘의 아이디와 거리를 측정하여 textvIEW에 띄움

        }
    };
}
