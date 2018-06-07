package tk.stopbus.stopbusbeacon;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by myeon on 2018-06-07.
 */

public class GoogleMapDialog extends DialogFragment implements View.OnClickListener{
    private static final String TAG = "GoogleMapDialog";

    MapView mapView;
    GoogleMap googleMap;

    private String stationName;
    private LatLng latlng;

    public static GoogleMapDialog newInstance(String stationName, LatLng latlng) {
        GoogleMapDialog dialog = new GoogleMapDialog();
        Bundle args = new Bundle();

        args.putString("stationName", stationName);
        args.putDouble("latitude", latlng.latitude);
        args.putDouble("longitude", latlng.longitude);

        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            stationName = getArguments().getString("stationName");
            latlng = new LatLng(getArguments().getDouble("latitude"), getArguments().getDouble("longitude"));
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.google_map, null);
        ((TextView)view.findViewById(R.id.dialogTitle)).setText(stationName);

        /* Google Map Setting */
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();

        if (mapView != null) {
            Log.d(TAG, "NOT NULL!");
            mapView.getMapAsync(new OnMapReadyCallback(){
                @Override
                public void onMapReady(GoogleMap map) {
                    Log.d(TAG, "onMapReady() called in getMapAsync()");
                    googleMap = map;

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16.9f));
                    googleMap.addMarker(new MarkerOptions().position(latlng).title(stationName));
                }
            });
//            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
//            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return view;
//            }
//            googleMap.setMyLocationEnabled(true);
//            googleMap.getUiSettings().setZoomControlsEnabled(true);
//            MapsInitializer.initialize(this.getActivity());
//            LatLngBounds.Builder builder = new LatLngBounds.Builder();
//            builder.include(new LatLng(55.854049, 13.661331));
//            LatLngBounds bounds = builder.build();
//            int padding = 0;
//            // Updates the location and zoom of the MapView
//            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//            googleMap.moveCamera(cameraUpdate);
        } else {
            Log.d(TAG, "NULL!");
        }

        builder.setView(view);
        Dialog dialog = builder.create();

        return dialog;
    }

    private void dismissDialog() {
        this.dismiss();
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() called");
        ((TextView)v.findViewById(R.id.dialogTitle)).setText("TEST");
    }

//    @Override
//    public void onMapReady(GoogleMap map) {
//        Log.d(TAG, "onMapReady() called");
//        googleMap = map;
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(this.latlng));
//        /* Google Map Permission */
//        if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            googleMap.setMyLocationEnabled(true);
//        } else {
//            Toast.makeText(this.getActivity(), "You have to accept to enjoy all app's services!", Toast.LENGTH_LONG).show();
//            if (ContextCompat.checkSelfPermission(this.getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                googleMap.setMyLocationEnabled(true);
//            }
//        }
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
//        googleMap.addMarker(new MarkerOptions()
//                .position(this.latlng)
//                .title(this.stationName));
//    }

}
