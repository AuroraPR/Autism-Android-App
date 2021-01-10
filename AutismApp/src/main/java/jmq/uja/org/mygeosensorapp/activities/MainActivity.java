package apr.autismapp.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.Task;
import retrofit2.Call;

public class MainActivity extends AppCompatActivity implements FormDialogListener{
    BottomNavigationView bn;
    int prevSelected=0;
    private NfcAdapter mNfcAdapter;

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo = "658680447";
    String message = "PROBANDO DESDE LA APP";

    private TextView newTaskName;
    private Button newTaskDate;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Bundle b = getIntent().getExtras();
        String token =""; // or other values
        if(b != null)
            token = b.getString("token");
        AsynRestSensorData.token=token;

        bn=(BottomNavigationView) findViewById(R.id.bottomNavigation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        newTaskName = new TextView(this);
        newTaskDate = new Button(this);

        showSelectedFragment(new TasksFragment(newTaskName, newTaskDate));

        bn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId()==R.id.menu_task&&prevSelected!=R.id.menu_task){
                    Fragment actualFragment=new TasksFragment(newTaskName, newTaskDate);
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
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNo, null, message, null, null);
                        Toast.makeText(getApplicationContext(), "SMS enviado", Toast.LENGTH_LONG).show();
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(), "Ha denegado el permiso de envío de SMS", Toast.LENGTH_LONG).show();
                    }
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onNewIntent(Intent intent) {
        //if(prevSelected==R.id.menu_rewards) {
        super.onNewIntent(intent);
        CashFragment fragInfo = new CashFragment();
        fragInfo.resolveIntent(intent);
        prevSelected = R.id.menu_rewards;
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
                            "Permiso a SMS denegado. Esta función no está disponible", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    @Override
    public void update(String taskName, String taskDate) {
        newTaskName.setText("");
        newTaskDate.setText("");

        Call<Task[]> call = AsynRestSensorData.init().insertTask("aurora",taskName,Long.parseLong(taskDate));
        AsynRestSensorData.MyCall<Task[]> mycall=new AsynRestSensorData.MyCall<>(
                (Task [] e)->{showSelectedFragment(new TasksFragment(newTaskName, newTaskDate));}
        );
        mycall.execute(call);
    }

}
