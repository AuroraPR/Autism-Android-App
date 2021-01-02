package jmq.uja.org.mygeosensorapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bn;
    int prevSelected=0;
    private NfcAdapter mNfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bn=(BottomNavigationView) findViewById(R.id.bottomNavigation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        //showSelectedFragment(new GeoFragment());

        bn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId()==R.id.menu_task&&prevSelected!=R.id.menu_task){
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
            //R.color.bnv_tab_item_foreground.
            bn.setSelectedItemId(R.id.menu_rewards);
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

}
