package com.example.kimheeyeon.beacontest;

import android.app.Activity;
import android.content.Intent;
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
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.distance.DistanceCalculator;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;




public class MainActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;

    //감지된 비콘들을 임시로 담을 리스트
    private List<Beacon> beaconList = new ArrayList<>();

    //textview
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Intent intent = new Intent(MainActivity.this, Speach.class);

            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }


        //비콘매니저 객체 초기화
        beaconManager = BeaconManager.getInstanceForApplication(this);
        textView = (TextView)findViewById(R.id.beaconList);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:23-24"));
        //위 숫자가 detect 숫자. 바꾸면 인식안됨
        beaconManager.bind(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
    @Override
    public void onBeaconServiceConnect() {
//        beaconManager.addMonitorNotifier(new MonitorNotifier() {
//            @Override
//            public void didEnterRegion(Region region) {
//                Log.i(TAG, "I just saw an beacon for the first time!");
//
//            }
//            @Override
//            public void didExitRegion(Region region) {
//                Log.i(TAG, "I no longer see an beacon");
//            }
//
//            @Override
//            public void didDetermineStateForRegion(int state, Region region) {
//                Log.i(TAG, "I have just switched from seeing/not seeing beacons: "+state);
//            }
//

        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for(Beacon beacon : beacons){
                        beaconList.add(beacon);
                    }
                    Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
//        });
//
//        try {
//            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
    }

    public void OnButtonClicked(View view){
        //아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초 간격. 한번 호출된다음부터는 1초마다!
        System.out.println("isin");
        handler.sendEmptyMessage(0);


    }

    Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Log.d("beacon is acting", "yes!!");
            textView.setText("");

            //비콘의 아이디와 거리를 측정하여 textvIEW에 띄움
            for(Beacon beacon : beaconList){

                Double accuracy = calculateDistance(beacon.getTxPower(),beacon.getRssi());

               //Log.d("beacon ID2", beacon.getId2());
               System.out.println("name : " + beacon.getBluetoothName() + " / " +
                       "ID2 : " + beacon.getId2() + " / " + String.valueOf(beacon.getDistance()));
               textView.append("name : " + beacon.getBluetoothName() + " / " +
                       "ID2 : " + beacon.getId2() + " / " + String.valueOf(accuracy) + "/" + "Accracy : " + getDistance(accuracy) + "m\n" );

            }


            handler.sendEmptyMessageDelayed(0,1000);
        }
    };

    public double calculateDistance(int txPower, double rssi) {
        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;

        Log.d("ratio " , String.valueOf(ratio));

        if (ratio < 1.0) {
            Log.d("pow!! " , String.valueOf(Math.pow(ratio,10)));
            return Math.pow(ratio,10);
        }
        else {
            double accuracy =  (0.89976)*Math.pow(ratio,7.7095) + 0.111;
            return accuracy;
        }
    }

    private String getDistance(double accuracy) {
        if (accuracy == -1.0) {
            return "Unknown";
        } else if (accuracy < 1) {
            return "Immediate";
        } else if (accuracy < 3) {
            return "Near";
        } else {
            return "Far";
        }
    }
}
