
****BookingActivity****



package com.nextgen.homeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.homeservice.Adapter.BookingsAdapter;
import com.nextgen.homeservice.ModelClass.BookingsModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BookingsActivity extends AppCompatActivity {

    private static String TAG ="BookingsActivity";

    RecyclerView bookingsRV;
    ArrayList<BookingsModelClass> list;

    private GlobalPreference globalPreference;
    private String ip,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        bookingsRV = findViewById(R.id.bookingsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        bookingsRV.setLayoutManager(layoutManager);

        getBookings();
    }

    private void getBookings() {

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ ip +"/home_service/api/getBookings.php?uid="+uid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("failed")){
                    Toast.makeText(BookingsActivity.this, "No Bookings Available", Toast.LENGTH_SHORT).show();
                }
                else{
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String serviceId = object.getString("serviceId");
                            String serviceName = object.getString("name");
                            String location = object.getString("location");
                            String service_type = object.getString("service_type");
                            String image = object.getString("image");
                            String date = object.getString("date");
                            String time = object.getString("time");
                            String rate = object.getString("rate");
                            String status = object.getString("status");


                            list.add(new BookingsModelClass(id,serviceId,serviceName,location,service_type,image,date,time,rate,status));

                        }

                        BookingsAdapter adapter = new BookingsAdapter(list,BookingsActivity.this);
                        bookingsRV.setAdapter(adapter);

                    } catch(JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}