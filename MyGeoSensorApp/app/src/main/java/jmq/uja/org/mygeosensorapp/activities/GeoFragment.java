package jmq.uja.org.mygeosensorapp.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import jmq.uja.org.mygeosensorapp.R;
import jmq.uja.org.mygeosensorapp.data.AsynRestSensorData;
import jmq.uja.org.mygeosensorapp.data.ResourceLocation;
import jmq.uja.org.mygeosensorapp.data.TimeLocation;
import retrofit2.Call;


public class GeoFragment extends Fragment implements LocationListener {

    MapView map = null;
    TextView tv =null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_geo, container, false);
        Context ctx = getActivity().getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        map = (MapView) root.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        tv=(TextView) root.findViewById(R.id.userName);
        tv.setText("Ejemplo de vista con mapa!");
        initGPS();

        // get map controller

        //       addCenterMarker();
        Call<ResourceLocation[]> call = AsynRestSensorData.init().getResourceLocation("aurora");
        AsynRestSensorData.MyCall<ResourceLocation[]> mycall=new AsynRestSensorData.MyCall<ResourceLocation[]>(
                (ResourceLocation [] e)->{
                    paintSensorProperty(e);
                }
        );
        mycall.execute(call);
        return root;
    }

    private void initGPS(){
        locationManager = (LocationManager) getActivity().getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d("MyGeo", "Connection off");
            showSettingsAlert();
            //    getLastLocation();
        } else {
            Log.d("MyGeo", "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d("MyGeo", "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }
    }
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d("MyGeo", provider);
            Log.d("MyGeo", location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;


    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    private final static int ALL_PERMISSIONS_RESULT = 101;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d("MyGeo", "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d("MyGeo", "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2;
    private static final long MIN_TIME_BW_UPDATES = 100 * 60 * 1;
    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d("MyGeo", "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d("MyGeo", "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d("MyGeo", "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d("MyGeo", "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    GeoFragment getMe(){
        return this;
    }




    public void paintSensorProperty(ResourceLocation [] properties){
        this.tv.setText("Painted sensors ");
        for(ResourceLocation sensor:properties){
            GeoPoint startPoint = new GeoPoint(sensor.lat,sensor.lon);
            Marker startMarker = new Marker(map);
            startMarker.setTitle(sensor.name);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
        }


        if(properties.length>0){
            zoomToBounds(computeArea(properties));
        }
    }

    public void zoomToBounds(final BoundingBox box) {
        if (map.getHeight() > 0) {
            map.zoomToBoundingBox(box, true);

        } else {
            ViewTreeObserver vto = map.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    map.zoomToBoundingBox(box, true);
                    ViewTreeObserver vto2 = map.getViewTreeObserver();

                    vto2.removeOnGlobalLayoutListener(this);

                }
            });
        }
    }
    public BoundingBox computeArea(ResourceLocation [] properties) {

        double nord = 0, sud = 0, ovest = 0, est = 0;

        int i=0;
        for(ResourceLocation sensor:properties){


            double lat = sensor.lat;
            double lon = sensor.lon;

            if ((i == 0) || (lat > nord)) nord = lat;
            if ((i == 0) || (lat < sud)) sud = lat;
            if ((i == 0) || (lon < ovest)) ovest = lon;
            if ((i == 0) || (lon > est)) est = lon;

            i++;
        }

        return new BoundingBox(nord, est, sud, ovest);

    }

    public BoundingBox computeArea(Location location) {

        double nord = 0, sud = 0, ovest = 0, est = 0;


        return new BoundingBox(nord, est, sud, ovest);

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            updateUI(location);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }


    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    Marker currentLocation=null;
    private void updateUI(Location loc) {
        if(this.isVisible()) {
            Log.d("MyGeo", "updateUI");


            this.tv.setText("[" + loc.getLatitude() + "," + loc.getLongitude() + "] ");

            if (currentLocation != null)
                this.map.getOverlays().remove(currentLocation);
            currentLocation = new Marker(map);
            currentLocation.setTitle("Aurora");
            currentLocation.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            currentLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);


            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ap, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) (48.0f * getResources().getDisplayMetrics().density), (int) (48.0f * getResources().getDisplayMetrics().density), true));
            currentLocation.setIcon(d);


            map.getOverlays().add(currentLocation);

            map.getController().animateTo(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            map.getController().setZoom(18d);
            //getLocation();


            Call<TimeLocation[]> call = AsynRestSensorData.init().inserTimeLocation("aurora", (float) loc.getLatitude(), (float) loc.getLongitude());
            AsynRestSensorData.MyCall<TimeLocation[]> mycall = new AsynRestSensorData.MyCall<TimeLocation[]>(
                    (TimeLocation[] e) -> {
                        Log.d("MyGeo", "all tracks..." + e.length);
                        paintTrack(e);
                    }
            );
            mycall.execute(call);
        }

    }

    public void paintTrack(TimeLocation [] tracks){
        this.tv.setText("Painted TimeLocations ");
        for(TimeLocation ll:tracks){
            GeoPoint startPoint = new GeoPoint(ll.lat,ll.lon);
            Marker startMarker = new Marker(map);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.apmin, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            startMarker.setIcon(d);
            map.getOverlays().add(startMarker);
        }
    }

}
