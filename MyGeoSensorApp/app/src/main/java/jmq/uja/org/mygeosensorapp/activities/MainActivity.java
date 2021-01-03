package jmq.uja.org.mygeosensorapp.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Toast;

import jmq.uja.org.mygeosensorapp.R;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bn;
    int prevSelected=0;
    private NfcAdapter mNfcAdapter;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo = "657019300";
    String message = "PROBANDO DESDE LA APP";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bn=(BottomNavigationView) findViewById(R.id.bottomNavigation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        showSelectedFragment(new TasksFragment());

        bn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId()==R.id.menu_task&&prevSelected!=R.id.menu_task){
                    Fragment actualFragment=new TasksFragment();
                    showSelectedFragment(actualFragment);
                    prevSelected=R.id.menu_task;
                }
                if (menuItem.getItemId()==R.id.menu_rewards&&prevSelected!=R.id.menu_rewards){
                    Fragment actualFragment=new CashFragment();
                    showSelectedFragment(actualFragment);
                    prevSelected=R.id.menu_rewards;
                }
                if (menuItem.getItemId()==R.id.menu_maps&&prevSelected!=R.id.menu_maps){
                    Fragment actualFragment=new GeoFragment();
                    showSelectedFragment(actualFragment);
                    prevSelected=R.id.menu_maps;
                }
                if (menuItem.getItemId()==R.id.menu_emergency){
                    checkPermissionSMS();
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS enviado",
                    Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });
    }

    private void showSelectedFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        //if(prevSelected==R.id.menu_rewards) {
            CashFragment fragInfo = new CashFragment();
            fragInfo.resolveIntent(intent);
            prevSelected=R.id.menu_rewards;
            bn.setSelectedItemId(prevSelected);
            showSelectedFragment(fragInfo);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if(prevSelected==R.id.menu_rewards) {
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            IntentFilter[] nfcIntentFilter = new IntentFilter[]{techDetected, tagDetected, ndefDetected};

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            if (mNfcAdapter != null)
                mNfcAdapter.enableForegroundDispatch(this, pendingIntent, nfcIntentFilter, null);
        //}
    }
    @Override
    public void onPause() {
        super.onPause();

        //if(prevSelected==R.id.menu_rewards) {
            if (mNfcAdapter != null)
                mNfcAdapter.disableForegroundDispatch(this);
        //}
    }

    protected void checkPermissionSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Ha fallado, prueba de nuevo", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }



}
