package tk.stopbus.stopbusbeacon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity{
    protected static final String TAG = "mjin1220";
    TextView textView;

    /* MinewBeacon */
    MinewBeaconManager mMinewBeaconManager;
    HashMap<String, KalmanFilter> mRssiMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView)findViewById(R.id.Textview);

        mMinewBeaconManager = MinewBeaconManager.getInstance(this);
        mMinewBeaconManager.setDeviceManagerDelegateListener(new MinewBeaconManagerListener() {
            public void onDisappearBeacons(List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onDisappearBeacons() called");
                for (MinewBeacon minewBeacon : minewBeacons) {
                    if(minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue().equals("N/A"))
                        continue;
                    String deviceName = minewBeacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    Toast.makeText(getApplicationContext(), deviceName + "  out range", Toast.LENGTH_SHORT).show();
                }
            }

            public void onAppearBeacons(List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onAppearBeacons() called");
            }

            @Override
            public void onUpdateState(BluetoothState state) {
                Log.d(TAG, "onUpdateState() called");
                switch (state) {
                    case BluetoothStatePowerOn:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOn", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothStatePowerOff:
                        Toast.makeText(getApplicationContext(), "BluetoothStatePowerOff", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            public void onRangeBeacons(final List<MinewBeacon> minewBeacons) {
                Log.d(TAG, "onRangeBeacons() called");
                textView.setText("");

                String name;
                double rssi;
                for(int i = 0 ; i < minewBeacons.size() ; i++){
                    name = minewBeacons.get(i).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue();
                    if(name.equals("N/A"))
                        continue;

                    Log.d(TAG, "result: " + mRssiMap.containsKey(name) + "");
                    rssi = minewBeacons.get(i).getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue();
                    if(!mRssiMap.containsKey(name)) {
                        mRssiMap.put(name, new KalmanFilter(0.0f));
                    }
                    rssi = mRssiMap.get(name).update(rssi);

                    textView.append("NAME : " + name + "\n"
                            + "RSSI : " + rssi + "\n"
                            + "Distance : " + calculateDistance(rssi)+ "\n");
                    textView.append("\n");
                }

//                for(MinewBeacon beacon: minewBeacons){
//                    if(beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue().equals("N/A"))
//                        continue;
//                    textView.append("NAME : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Name).getStringValue() + "\n"
//                            + "Humidity : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Humidity).getStringValue() + "\n"
//                            + "BatteryLevel : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_BatteryLevel).getStringValue() + "\n"
//                            + "InRage : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_InRage).getStringValue() + "\n"
//                            + "MAC : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_MAC).getStringValue() + "\n"
//                            + "Major : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Major).getStringValue() + "\n"
//                            + "Minor : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Minor).getStringValue() + "\n"
//                            + "Temperature : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_Temperature).getStringValue() + "\n"
//                            + "TxPower : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_TxPower).getStringValue() + "\n"
//                            + "UUID : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_UUID).getStringValue() + "\n"
//                            + "RSSI : " + beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getStringValue() + "\n"
//                            + "Distance : " + calculateDistance(beacon.getBeaconValue(BeaconValueIndex.MinewBeaconValueIndex_RSSI).getFloatValue())+ "\n");
//                    textView.append("\n");
//                }
            }
        });

        mMinewBeaconManager.startScan();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMinewBeaconManager.stopScan();
    }

    public void OnButtonClicked(View view){
        textView.setText("");
    }

    public double calculateDistance(double rssi) {
        int txPower = -59;

        if (rssi == 0) {
            return -1.0; // if we cannot determine accuracy, return -1.
        }
        double ratio = rssi*1.0/txPower;

        if (ratio < 1.0) {
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
