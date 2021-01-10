package apr.autismapp.activities;

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

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.ResourceLocation;
import apr.autismapp.data.TimeLocation;
import retrofit2.Call;


public class GeoFragment extends Fragment implements LocationListener {

    MapView map = null;
    TextView tv =null;
    String name = "";
    String username = "";
    String lastLoc = "";
    String phoneNo = null;
    String message = null;
    boolean smsSent = false;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =151 ;

    public GeoFragment(String name, String username){
        this.name = name;
        this.username = username;
    }

    public GeoFragment(String name, String username, String phoneNo, String message){
        this.name = name;
        this.username = username;
        this.phoneNo = phoneNo;
        this.message = message;
    }

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
        Call<ResourceLocation[]> call = AsynRestSensorData.init().getResourceLocation(username);
        AsynRestSensorData.MyCall<ResourceLocation[]> mycall=new AsynRestSensorData.MyCall<ResourceLocation[]>(
                (ResourceLocation [] e)->{
                    paintLocations(e);
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

            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getContext(),
                            "Permiso a SMS denegado. Esta función no está disponible", Toast.LENGTH_LONG).show();
                    return;
                }
            }
                break;
        }
    }

    private void sendSMS(Location loc){
        checkPermissionSMS();
        lastLoc="[" + loc.getLatitude() + "," + loc.getLongitude() + "] ";
        if(phoneNo!=null&&message!=null){
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, message+lastLoc, null, null);
                Toast.makeText(getContext(), "SMS enviado", Toast.LENGTH_LONG).show();
            } catch (Exception e){
                Toast.makeText(getContext(), "Ha denegado el permiso de envío de SMS", Toast.LENGTH_LONG).show();
            }
        }
        smsSent=true;
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
                        if (loc != null){
                            sendSMS(loc);
                            updateUI(loc);
                        }
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
                        if (loc != null) {
                            sendSMS(loc);
                            updateUI(loc);
                        }
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




    public void paintLocations(ResourceLocation [] locations){
        this.tv.setText("Painted locations ");
        for(ResourceLocation sensor:locations){
            GeoPoint startPoint = new GeoPoint(sensor.lat,sensor.lon);
            Marker startMarker = new Marker(map);
            startMarker.setTitle(sensor.name);
            startMarker.setPosition(startPoint);
            startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(startMarker);
        }


        if(locations.length>0){
            zoomToBounds(computeArea(locations));
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
            currentLocation.setTitle(name);
            currentLocation.setPosition(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            currentLocation.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);


            Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.pushpin, null);
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            Drawable dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int) (48.0f * getResources().getDisplayMetrics().density), (int) (48.0f * getResources().getDisplayMetrics().density), true));
            currentLocation.setIcon(d);


            map.getOverlays().add(currentLocation);

            map.getController().animateTo(new GeoPoint(loc.getLatitude(), loc.getLongitude()));
            map.getController().setZoom(18d);
            //getLocation();


            Call<TimeLocation[]> call = AsynRestSensorData.init().inserTimeLocation(username, (float) loc.getLatitude(), (float) loc.getLongitude());
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

    protected void checkPermissionSMS() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }



}
