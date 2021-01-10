package apr.autismapp.data;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class AsynRestSensorData {
    static public interface ServiceLogin{
        @GET("login")
        public Call<UserPass> login(
                @Query("user") String user,
                @Query("password") String password
        );
        @GET("add")
        public Call<UserPass> addUser(
                @Query("user") String user,
                @Query("password") String password,
                @Query("name") String name,
                @Query("emergencyNumber") String emergencyNumber
        );
    }


    static public interface ServiceSensorData{

        @GET("insert_location/{user}/{lat}/{lon}")
        public Call<TimeLocation[]> inserTimeLocation(
                @Path("user") String user,
                @Path("lat") Float lat,
                @Path("lon") Float lon
        );

        @GET("get_location_resources/{user}")
        public Call<ResourceLocation[]> getResourceLocation(
                @Path("user") String user
        );


        @GET("get_cash_movement/{user}")
        public Call<CashMovement[]> getCashMovement(
                @Path("user") String user
        );


        @GET("insert_cash_movement/{user}/{money}/{concept}")
        public Call<CashMovement []> insertCashMovement(
                @Path("user") String user,
                @Path("money") float money,
                @Path("concept") String concept
        );

        @GET("get_task/{user}/{currentDay}")
        public Call<Task[]> getTask(
                @Path("user") String user,
                @Path("currentDay") Long currentDay
        );

        @GET("insert_task/{user}/{name}/{date}")
        public Call<Task []> insertTask(
                @Path("user") String user,
                @Path("name") String name,
                @Path("date") long date
        );


        @GET("modify_task/{user}/{name}/{date}/{check}")
        public Call<Task []> modifyTask(
                @Path("user") String user,
                @Path("name") String name,
                @Path("date") long date,
                @Path("check") boolean check
        );
    }


    static public String baseURL="http://192.168.1.23:8092";
    static public String token="wep";

    static public ServiceSensorData init(){
        return init(token);
    }
    static public ServiceSensorData init(String token0){
      //  if(service0==null) {
        token=token0;
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("Authorization", "Bearer"+token).build();
                return chain.proceed(request);
            }
        });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL+"/MyGeoServlet/")
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        ServiceSensorData service0 = retrofit.create(ServiceSensorData.class);
     //   }
        return service0;
    }

    static public ServiceLogin initLogin(){

        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(baseURL+"/usuario/").build();

        ServiceLogin serviceAouth0 = retrofit.create(ServiceLogin.class);
        //   }
        return serviceAouth0;
    }




    @FunctionalInterface
    static public interface Consume<T>{
        public void consume(T data);
    }
    @SuppressLint("NewApi")
    static public class MyCall<T> extends AsyncTask<Call<T>,T,Boolean>{
        Consume<T> consumer;
        public MyCall(Consume<T> consumer){
            this.consumer=consumer;
        }

        @SuppressLint("NewApi")
        @Override
        protected Boolean doInBackground(Call<T>... calls) {
            for(Call<T> call:calls){
                Log.d("MyGeo", "calling"+call);
                try {
                    Response<T> response=call.execute();
                    Log.d("MyGeo", "body"+response.message());
                    publishProgress(response.body());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(T... responses) {
            for(T response:responses) {
                Log.d("MyGeo", "response:" + response);
             //   Log.d("MyGeo", "class" + response.getClass().getName());
                this.consumer.consume(response);
            }
        }
    }

}


