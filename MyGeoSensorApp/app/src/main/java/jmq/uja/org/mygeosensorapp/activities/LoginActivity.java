package jmq.uja.org.mygeosensorapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.appcompat.app.AppCompatActivity;
import jmq.uja.org.mygeosensorapp.R;
import jmq.uja.org.mygeosensorapp.data.AsynRestSensorData;
import jmq.uja.org.mygeosensorapp.data.Task;
import jmq.uja.org.mygeosensorapp.data.UserPass;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private TextView tUser;
    private TextView tPass;
    private Button bLogin;
    private Button bCreate;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tUser=(TextView)findViewById(R.id.editText);
        tPass=(TextView)findViewById(R.id.editText2);

        bLogin=(Button)findViewById(R.id.button);
        bCreate=(Button)findViewById(R.id.button2);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyGeo", "passs:"+tPass.getText().toString());
                String hash1=getHash(tPass.getText().toString());
                Log.d("MyGeo", "hash1:"+hash1);
                String hash2=getHash(tUser.getText().toString()+hash1);
                Log.d("MyGeo", "hash2:"+hash2);
                Call<UserPass> call = AsynRestSensorData.initLogin().login(tUser.getText().toString(),hash2);
                AsynRestSensorData.MyCall<UserPass> mycall=new AsynRestSensorData.MyCall<>(
                        (UserPass e)->{
                            Log.d("MyGeo", "userpass:"+e);
                            if(e!=null) {
                                Toast.makeText(getApplicationContext(), "Token:" + e.password, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                Bundle b = new Bundle();

                                b.putString("token", e.password); //Your id
                                intent.putExtras(b); //Put your id to your next Intent
                                startActivity(intent);
                                finish();
                            }else
                                Toast.makeText(getApplicationContext(), "El usuario no existe", Toast.LENGTH_LONG).show();

                        }
                );
                mycall.execute(call);
            }
        });

        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyGeo", "passs:"+tPass.getText().toString());
                String hash1=getHash(tPass.getText().toString());
                Log.d("MyGeo", "hash1:"+hash1);
                String hash2=getHash(tUser.getText().toString()+hash1);
                Log.d("MyGeo", "hash2:"+hash2);
                Call<UserPass> call = AsynRestSensorData.initLogin().add(tUser.getText().toString(),hash2);
                AsynRestSensorData.MyCall<UserPass> mycall=new AsynRestSensorData.MyCall<>(
                        (UserPass e)->{
                            Log.d("MyGeo", "userpass:"+e);

                        }
                );
                mycall.execute(call);
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
