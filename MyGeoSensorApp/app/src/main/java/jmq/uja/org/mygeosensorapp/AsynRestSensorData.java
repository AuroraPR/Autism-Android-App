package jmq.uja.org.mygeosensorapp;

import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class AsynRestSensorData {

    static public interface ServiceSensorData{

        @GET("insert_location/{user}/{lat}/{lon}")
        public Call<TimeLocation []> inserTimeLocation(
                @Path("user") String user,
                @Path("lat") Float lat,
                @Path("lon") Float lon
        );

        @GET("get_location_resources/{user}")
        public Call<ResourceLocation []> getResourceLocation(
                @Path("user") String user
        );


        @GET("get_cash_movement/{user}")
        public Call<CashMovement []> getCashMovement(
                @Path("user") String user
        );


        @GET("insert_cash_movement/{user}/{money}/{concept}")
        public Call<CashMovement []> insertCashMovement(
                @Path("user") String user,
                @Path("money") float money,
                @Path("concept") String concept
        );

        @GET("get_task/{user}/{currentTime}")
        public Call<Task []> getTask(
                @Path("user") String user,
                @Path("currentTime") Long currentTime
        );

        @GET("insert_task/{user}/{name}/{date}")
        public Call<Task []> insertTask(
                @Path("user") String user,
                @Path("name") String name,
                @Path("date") Date date
        );


    }



    static ServiceSensorData service0=null;
    static public ServiceSensorData init(){
      //  if(service0==null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://192.168.1.23:8092/MyGeoServlet/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            service0 = retrofit.create(ServiceSensorData.class);
     //   }
        return service0;
    }

    @FunctionalInterface
    static public interface Consume<T>{
        public void consume(T data);
    }

    static public class MyCall<T> extends AsyncTask<Call<T>,T,Boolean>{
        Consume<T> consumer;
        public MyCall(Consume<T> consumer){
            this.consumer=consumer;
        }
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
                Log.d("MyGeo", "class" + response.getClass().getName());
                this.consumer.consume(response);
            }
        }
    }

}


