package tk.stopbus.stopbusbeacon;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.minew.beacon.BeaconValueIndex;
import com.minew.beacon.BluetoothState;
import com.minew.beacon.MinewBeacon;
import com.minew.beacon.MinewBeaconManager;
import com.minew.beacon.MinewBeaconManagerListener;

import java.util.HashMap;
import java.util.List;

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
            }
        });

        mMinewBeaconManager.startScan();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mMinewBeaconManager.stopScan();
    }

    public void OnClearClicked(View view){
        textView.setText("");
    }

    public void OnMapClicked(View view){
        GoogleMapDialog dialog = GoogleMapDialog.newInstance("아주대.아주대학교병원", new LatLng(37.2785,127.0436));
        dialog.show(getSupportFragmentManager(), "dialog");
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
}
