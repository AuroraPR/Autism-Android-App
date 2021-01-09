package apr.autismapp.activities;

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
import apr.autismapp.R;
import apr.autismapp.data.AsynRestSensorData;
import apr.autismapp.data.UserPass;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    private TextView tUser;
    private TextView tPass;
    private Button bLogin;
    private Button bSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        tUser=(TextView)findViewById(R.id.editText);
        tPass=(TextView)findViewById(R.id.editText2);

        bLogin=(Button)findViewById(R.id.button);
        bSignIn =(Button)findViewById(R.id.button2);

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
                                System.out.println(e.name);
                                b.putString("username", e.user);
                                b.putString("token", e.password); //Your id
                                b.putString("name",e.name);
                                b.putString("phone",e.emergencyNumber);
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

        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SigninActivity.class);
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
