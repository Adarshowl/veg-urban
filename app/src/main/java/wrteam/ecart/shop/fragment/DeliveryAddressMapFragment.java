package wrteam.ecart.shop.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import wrteam.ecart.shop.R;
import wrteam.ecart.shop.activity.MainActivity;
import wrteam.ecart.shop.helper.Constant;

public class DeliveryAddressMapFragment extends Fragment implements OnMapReadyCallback,
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyAudWLtLNHEjuCZUGJiDEbEMBjwKlbkO6c";
    static ArrayList resultList = null;
    private static String newAddressFromApi = "";
    // flag for GPS status
    public boolean isGPSEnabled = false;
    protected LocationManager locationManager;
    int count = 0;
    String place_name = "";
    String locality = "";
    String postal_code = "";
    String fullAddress = "";

    SupportMapFragment mapFragment;
    String newAdd = "";
    Location mLastLocation;
    Marker mCurrLocationMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    FloatingActionButton fabCurrent;
    ProgressDialog mProgressDialog;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    private View root;
    private GoogleMap mMap;
    private TextView tvLocation, tvUseLocation;
    private ProgressBar progressBar;

    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;


    private Bundle mBundle;


    private double mLastLatitude = 0.0;
    private double mLastLongitude = 0.0;

    public static void autocompleteAddress(double lat, double longitude) {
        resultList = null;
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append("" + lat + "," + longitude);
            sb.append("&key=" + API_KEY);
            Log.d(LOG_TAG, "autocomplete: sb " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");
            // Extract the Place descriptions from the results
            Log.d(LOG_TAG, "autocomplete: predsJsonArray " + predsJsonArray);
            newAddressFromApi = predsJsonArray.getJSONObject(4).getString("formatted_address");
        } catch (JSONException e) {
            Log.e(LOG_TAG, "autocomplete: predsJsonArray", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_delivery_address_map, container, false);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        fabCurrent = root.findViewById(R.id.fabCurrent);
        tvLocation = root.findViewById(R.id.tvLocation);
        showProgressDialog();

        // get lat long from last screen
        mBundle = getArguments();
        mLastLatitude = mBundle.getDouble("lastLatitude");
        mLastLongitude = mBundle.getDouble("lastLongitude");
        Log.d("TAG", "onCreateView: mLastLatitude map " + mLastLatitude + " *** "+mLastLongitude);

        tvUseLocation = root.findViewById(R.id.tvUseLocation);
        mapFragment.getMapAsync(this);
        fabCurrent.setOnClickListener(v -> {
            moveToCurrent(mLastLocation);
        });
        place_name = getArguments().getString("place_name");
        if (place_name != "") {
//            Location location = new Location("");
//            LatLng latLng = getLocationFromAddress(place_name);
//            location.setLatitude(latLng.latitude);
//            location.setLongitude(latLng.longitude);
//            onLocationChanged(location);
//            getCompleteAddressString(latLng.latitude, latLng.longitude);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Log.d("location tag -> ", mLastLocation.getLatitude() + " ** " + mLastLocation.getLongitude());
            lastLatitude = mLastLocation.getLatitude();
            lastLongitude = mLastLocation.getLongitude();
//            throw new JSONException("JSON exception arise");
        } catch (Exception e) {
            Log.d("location tag exc -> ", e.getMessage());
            lastLatitude = mLastLatitude;
            lastLongitude = mLastLongitude;
        }

        tvUseLocation.setOnClickListener(v -> {
            Constant.CURRENT_USER_LOCATION = fullAddress + ", " + postal_code;
//            Constant.CURRENT_USER_LOCATION = newAddressFromApi;
            Intent intent = new Intent(getContext(), MainActivity.class)
                    .putExtra(Constant.FROM, "").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        // getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        return root;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage(getActivity().getString(R.string.please_wait));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Constant.TOOLBAR_TITLE = getString(R.string.select_address);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("TAG", "onMapReady onLocationChanged: " + " **** place -> " + place_name);
//        if (place_name == "") {
        showProgressDialog();
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        // Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(latLng);
//        markerOptions.title("Current Position");
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);
        Log.d("TAG", "onLocationChanged: " + latLng.latitude + " ******** " + latLng.longitude + " **** place -> " + place_name);
        //move map camera
//        String address = getCompleteAddressString(location.getLatitude(), location.getLongitude());
//        tvLocation.setText(address + "");
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        hideProgressDialog();
//        } else {
//
//            Log.d("TAG", "onMapReady location change: else");
//
//
//            showProgressDialog();
//
//            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
////        MarkerOptions markerOptions = new MarkerOptions();
////        markerOptions.position(latLng);
////        markerOptions.title("Current Position");
////        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
////        mCurrLocationMarker = mMap.addMarker(markerOptions);
//            Log.d("TAG", "onLocationChanged: " + latLng.latitude + " ******** " + latLng.longitude);
//            //move map camera
//            String address = getCompleteAddressString(location.getLatitude(), location.getLongitude());
//            tvLocation.setText(address + "");
//            if (mMap != null) {
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
//            }
//            //stop location updates
//            if (mGoogleApiClient != null) {
//                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            }
//            hideProgressDialog();
//
//        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
//        if (place_name == "" || place_name == null) {
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                Log.d("TAG", "onCameraMove: ");

            }
        });

        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.d("TAG", "onCameraMove idle 111111111 => " + mMap.getCameraPosition().target.latitude + " ******** " +
                        mMap.getCameraPosition().target.longitude);
                count++;
                if (count > 1) {
                    String address = getCompleteAddressString(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
                    tvLocation.setText(address + "");
                }
//                Log.d("TAG", "onCameraIdle: address -> " + address);

//                MarkerOptions marker = new MarkerOptions().position(mMap.getCameraPosition().target).title("");
//                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//
//                mMap.addMarker(marker);

            }
        });


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                LatLng latLng = new LatLng(lastLatitude, lastLongitude);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                String address = getCompleteAddressString(lastLatitude, lastLongitude);
                tvLocation.setText(address + "");
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(lastLatitude, lastLongitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
            String address = getCompleteAddressString(lastLatitude, lastLongitude);
            tvLocation.setText(address + "");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, 5000);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    public void moveToCurrent(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    public void moveToPlaceLatLng(double latitude, double longitude) {
        Log.d("TAG", "moveToPlaceLatLng: on top " + latitude + " *** " + longitude + " *** " + place_name);

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(latitude, longitude);
        if (mMap != null) {
            Log.d("TAG", "moveToPlaceLatLng: " + latitude + " *** " + longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(18));

            //stop location updates
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        } else {
            Log.d("TAG", "moveToPlaceLatLng: else " + latitude + " *** " + longitude);

        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                Log.d("TAG", "onCameraIdle: address returnedAddress =-> : " + returnedAddress);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    locality = returnedAddress.getLocality();
                    postal_code = returnedAddress.getPostalCode();
                    Constant.CURRENT_USER_LATITUDE = returnedAddress.getLatitude();
                    Constant.CURRENT_USER_LONGITUDE = returnedAddress.getLongitude();
                    String o = returnedAddress.getAddressLine(i);
                    String[] arr = o.split(" ", 2);
                    String[] arr1 = o.split(",", 4);
//                    for (int j = 0; j < arr1.length; j++) {
//                        Log.d("TAG", "getCompleteAddressString: jjjj " + arr1[j]);
//                    }
                    fullAddress = arr1[1];
//                    Log.d("TAG", "getCompleteAddressString: ad" + arr[1]);
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n"); // commented on 15 mar
                    strReturnedAddress.append(arr[1]).append("\n");
                }
//                newAdd = getCompleteAddressString(returnedAddress.getLatitude(), returnedAddress.getLongitude());
//                autocompleteAddress(returnedAddress.getLatitude(), returnedAddress.getLongitude());
                strAdd = strReturnedAddress.toString();
                Log.d("TAG", "getCompleteAddressString strAdd => : " + strAdd);
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getContext());
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                } else {
                    Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


}