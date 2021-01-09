package jmq.uja.org.mygeosensorapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import jmq.uja.org.mygeosensorapp.R;
import jmq.uja.org.mygeosensorapp.data.AsynRestSensorData;
import jmq.uja.org.mygeosensorapp.data.UserPass;
import retrofit2.Call;

public class SigninActivity extends AppCompatActivity {

    private TextView tUser;
    private TextView tName;
    private TextView tNumber;
    private TextView tPass;
    private Button bSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        tUser=(TextView)findViewById(R.id.editText3);
        tPass=(TextView)findViewById(R.id.editText4);
        tName=(TextView)findViewById(R.id.editText5);
        tNumber=(TextView)findViewById(R.id.editText6);


        bSignIn =(Button)findViewById(R.id.button3);


        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyGeo", "passs:"+tPass.getText().toString());
                String hash1=getHash(tPass.getText().toString());
                Log.d("MyGeo", "hash1:"+hash1);
                String hash2=getHash(tUser.getText().toString()+hash1);
                Log.d("MyGeo", "hash2:"+hash2);
                Log.d("MyGeo", "name:"+tName.getText().toString());
                Log.d("MyGeo", "emergency:"+tNumber.getText().toString());
                Call<UserPass> call = AsynRestSensorData.initLogin().addUser(tUser.getText().toString(),hash2,tName.getText().toString(),tNumber.getText().toString());
                AsynRestSensorData.MyCall<UserPass> mycall=new AsynRestSensorData.MyCall<>(
                        (UserPass e)->{
                            Log.d("MyGeo", "userpass:"+e);

                        }
                );
                mycall.execute(call);
                Intent intent = new Intent(SigninActivity.this, LoginActivity.class);
                Bundle b = new Bundle();
                startActivity(intent);
                finish();
            }
        });

    }


    // utility function
    private static String bytesToHexString(byte[] bytes) {
        // http://stackoverflow.com/questions/332079
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

// generate a hash

    private String getHash(String label){
    MessageDigest digest=null;
    String hash=null;
    try {
        digest = MessageDigest.getInstance("SHA-256");
        digest.update(label.getBytes());

        hash = bytesToHexString(digest.digest());

    } catch (NoSuchAlgorithmException e1) {


        e1.printStackTrace();
    }
    return hash;
    }
}
