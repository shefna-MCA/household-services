

***Request Activity***


package com.nextgen.homeservice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nextgen.homeservice.Adapter.RequestAdapter;
import com.nextgen.homeservice.ModelClass.RequestModelClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {

    private static String TAG ="RequestActivity";

    RecyclerView requestRV;
    ArrayList<RequestModelClass> list;

    private GlobalPreference globalPreference;
    private String ip,uid;

    private ImageView backIV;
    private TextView titleTV;
    TextView noRequestsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        requestRV = findViewById(R.id.requestsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        requestRV.setLayoutManager(layoutManager);

        getRequests();

        backIV = findViewById(R.id.BackImageButton);
        titleTV = findViewById(R.id.appBarTitle);
        titleTV.setText("Requests");

        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bIntent = new Intent(RequestsActivity.this,ServiceHomeActivity.class);
                startActivity(bIntent);
            }
        });

        noRequestsTV = findViewById(R.id.noRequestsTextView);
    }

    private void getRequests() {

        list = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://"+ ip +"/home_service/api/getRequests.php?uid="+uid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if (response.equals("failed")){
                    //Toast.makeText(RequestActivity.this, "No requests Available", Toast.LENGTH_SHORT).show();

                    requestRV.setVisibility(View.GONE);
                    noRequestsTV.setVisibility(View.VISIBLE);
                }
                else{
                    requestRV.setVisibility(View.VISIBLE);
                    noRequestsTV.setVisibility(View.GONE);
                    try{
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i=0; i< jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String date = object.getString("booking_date");
                            String time = object.getString("booking_time");
                            String latitude = object.getString("latitude");
                            String longitude = object.getString("longitude");


                            list.add(new RequestModelClass(id,name,date,time,latitude,longitude));

                        }

                        RequestAdapter adapter = new RequestAdapter(list,RequestsActivity.this);
                        requestRV.setAdapter(adapter);

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
