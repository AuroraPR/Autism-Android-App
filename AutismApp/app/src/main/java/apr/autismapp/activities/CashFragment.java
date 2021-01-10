package apr.autismapp.activities;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.CashMovement;
import apr.autismapp.data.Utils;
import apr.autismapp.views.CashMovementsAdapter;
import retrofit2.Call;


public class CashFragment extends Fragment {

    ListView listView;
    List<CashMovement> data;
    CashMovementsAdapter adapter;
    TextView tCash;
    Intent onNewIntent;
    String name="";
    String username="";
    TextView tName;

    public CashFragment(String name, String username){
        this.name = name;
        this.username = username;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_cash, container, false);
        Bundle args=this.getArguments();
        if(args!=null)
            onNewIntent=args.getParcelable("onNewIntent");

        tName = (TextView) root.findViewById(R.id.tName);
        tName.setText(name);
        tCash = (TextView) root.findViewById(R.id.tCash);
        listView = (ListView) root.findViewById(R.id.sMovimientos);
        data = new LinkedList<CashMovement>();
        listView.setAdapter(adapter = new CashMovementsAdapter(root.getContext(), data));


        //mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());

        Call<CashMovement[]> call = AsynRestSensorData.init().getCashMovement(username);
        AsynRestSensorData.MyCall<CashMovement[]> mycall = new AsynRestSensorData.MyCall<CashMovement[]>(
                (CashMovement[] e) -> {
                    paintCashMovements(e);
                }
        );
        mycall.execute(call);

        return root;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void paintCashMovements(CashMovement [] e){
        Log.d("MyGeo", "RECEIVED:"+ e);
        float total=0f;
        for(CashMovement cm:e)
            total+=cm.money;
        tCash.setText(Utils.round(total,2)+" puntos");



        data= Arrays.asList(e);
        Comparator<CashMovement> comparator = Comparator.comparing(CashMovement::getTime);
        Collections.sort(data, comparator.reversed());


        adapter.update(data);
        Utils.setDynamicHeight(listView);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void parseNFC(String text){

        float money=Float.parseFloat(text.substring(0,text.indexOf("@")));
        String concept=text.substring(text.indexOf("@")+1);

        Log.d("MyGeo", "money:"+money);
        Log.d("MyGeo", "concept:"+concept);
        Call<CashMovement []> call = AsynRestSensorData.init().insertCashMovement(username,money,concept);
        AsynRestSensorData.MyCall<CashMovement[]> mycall=new AsynRestSensorData.MyCall<CashMovement[]>(
                (CashMovement [] e)->{
                    paintCashMovements(e);
                }
        );
        mycall.execute(call);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void resolveIntent(Intent onNewIntent){
        Tag tag = onNewIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        patchTag(tag);
        if (tag != null) {
            readFromNFC(tag, onNewIntent);
        }
    }


    public Tag patchTag(Tag oTag) {
        if (oTag == null)
            return null;

        String[] sTechList = oTag.getTechList();

        Parcel oParcel, nParcel;

        oParcel = Parcel.obtain();
        oTag.writeToParcel(oParcel, 0);
        oParcel.setDataPosition(0);

        int len = oParcel.readInt();
        byte[] id = null;
        if (len >= 0) {
            id = new byte[len];
            oParcel.readByteArray(id);
        }
        int[] oTechList = new int[oParcel.readInt()];
        oParcel.readIntArray(oTechList);
        Bundle[] oTechExtras = oParcel.createTypedArray(Bundle.CREATOR);
        int serviceHandle = oParcel.readInt();
        int isMock = oParcel.readInt();
        IBinder tagService;
        if (isMock == 0) {
            tagService = oParcel.readStrongBinder();
        } else {
            tagService = null;
        }
        oParcel.recycle();

        int nfca_idx = -1;
        int mc_idx = -1;

        for (int idx = 0; idx < sTechList.length; idx++) {
            if (sTechList[idx] == NfcA.class.getName()) {
                nfca_idx = idx;
            } else if (sTechList[idx] == MifareClassic.class.getName()) {
                mc_idx = idx;
            }
        }

        if (nfca_idx >= 0 && mc_idx >= 0 && oTechExtras[mc_idx] == null) {
            oTechExtras[mc_idx] = oTechExtras[nfca_idx];
        } else {
            return oTag;
        }

        nParcel = Parcel.obtain();
        nParcel.writeInt(id.length);
        nParcel.writeByteArray(id);
        nParcel.writeInt(oTechList.length);
        nParcel.writeIntArray(oTechList);
        nParcel.writeTypedArray(oTechExtras, 0);
        nParcel.writeInt(serviceHandle);
        nParcel.writeInt(isMock);
        if (isMock == 0) {
            nParcel.writeStrongBinder(tagService);
        }
        nParcel.setDataPosition(0);
        Tag nTag = Tag.CREATOR.createFromParcel(nParcel);
        nParcel.recycle();

        return nTag;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void readFromNFC(Tag tag, Intent intent) {

        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();

                if (ndefMessage != null) {
                    /*String message = new String(ndefMessage.getRecords()[0].getPayload());
                    Log.d(TAG, "NFC found.. "+"readFromNFC: "+message );
                    tvNFCMessage.setText(message);*/

                    Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

                    if (messages != null) {
                        NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                        for (int i = 0; i < messages.length; i++) {
                            ndefMessages[i] = (NdefMessage) messages[i];
                        }
                        NdefRecord record = ndefMessages[0].getRecords()[0];

                        byte[] payload = record.getPayload();
                        String text = new String(payload);

                        Log.d("MyGeo", "vahid  -->  " + text);
                        parseNFC(text);

                        ndef.close();

                    }

                } else {
                    Toast.makeText(getActivity(), "Not able to read from NFC, Please try again...", Toast.LENGTH_LONG).show();

                }
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        NdefMessage ndefMessage = ndef.getNdefMessage();

                        if (ndefMessage != null) {
                            String message = new String(ndefMessage.getRecords()[0].getPayload());
                            Log.d("MyGeo", "NFC found.. " + "readFromNFC: " + message);

                            ndef.close();
                        } else {
                            Toast.makeText(getActivity(), "Not able to read from NFC, Please try again...", Toast.LENGTH_LONG).show();

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "NFC is not readable", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}