
*****ServiceHome***


package com.nextgen.homeservice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ServiceHomeActivity extends AppCompatActivity {

    CardView requestsCV;
    CardView bookingsCV;
    CardView paymentCV;
    CardView feedbacksCV;

    TextView serviceProviderTV;
    CircleImageView serviceProviderImageView;
    ImageView logoutIV;
    private GlobalPreference globalPreference;
    private String ip,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_home);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();

        serviceProviderTV = findViewById(R.id.providerNameTextView);
        serviceProviderImageView = findViewById(R.id.providerIconImageView);
        logoutIV = findViewById(R.id.logoutImageView);

        if (!uid.equals("")){
            getServiceProviderDetails();
        }

        requestsCV = findViewById(R.id.card_requests);
        bookingsCV = findViewById(R.id.card_bookings);
        //paymentCV = findViewById(R.id.card_payment);
        feedbacksCV = findViewById(R.id.card_feedback);


        requestsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceHomeActivity.this,RequestsActivity.class);
                startActivity(intent);
            }
        });

        bookingsCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceHomeActivity.this,MyBookingsActivity.class);
                startActivity(intent);
            }
        });

       /* paymentCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceHomeActivity.this,ViewPaymentsActivity.class);
                startActivity(intent);
            }
        });*/

        feedbacksCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ServiceHomeActivity.this,ViewFeedbacksActivity.class);
                startActivity(intent);
            }
        });


        logoutIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout();
            }
        });
    }

    private void getServiceProviderDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/home_service/api/getServiceProviderDetails.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject jsonObject = array.getJSONObject(0);

                    String name = jsonObject.getString("name");
                    String image = jsonObject.getString("image");

                    serviceProviderTV.setText(name);

                    if (!image.equals("")) {
                        Glide.with(getApplicationContext())
                                .load("http://" + ip + "/home_service/service_providers/uploads/" + image)
                                .into(serviceProviderImageView);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ServiceHomeActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("uid",uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(ServiceHomeActivity.this);
        requestQueue.add(stringRequest);
    }

    private void logout() {

        new AlertDialog.Builder(ServiceHomeActivity.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ServiceHomeActivity.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}

