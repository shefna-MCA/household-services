

****ServiceLogin*****

package com.nextgen.homeservice;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServiceLoginActivity extends AppCompatActivity {

    EditText emailET;
    EditText passwordET;
    Button loginBT;
    TextView signUpTV;

    private GlobalPreference globalPreference;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_login);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();

        emailET = findViewById(R.id.serviceEmailEditText);
        passwordET = findViewById(R.id.servicePasswordEditText);
        loginBT = findViewById(R.id.serviceLoginButton);
        signUpTV = findViewById(R.id.serviceSignUpTextView);

        emailET.setText("amal@gmail.com");
        passwordET.setText("888");

        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()){
                    Toast.makeText(ServiceLoginActivity.this, "Please Fill the Fields", Toast.LENGTH_SHORT).show();
                }else{
                    login();

                }

            }
        });

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ServiceLoginActivity.this,ServiceRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/home_service/api/serviceLogin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);

                if(response.equals("failed")){

                    Toast.makeText(ServiceLoginActivity.this, "Incorrect Email or Password", Toast.LENGTH_SHORT).show();

                } else if (response.equals("notverified")){

                    Toast.makeText(ServiceLoginActivity.this, "Not Verified", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String id = jsonObject.getString("id");
                        globalPreference.saveID(id);
                        String name = jsonObject.getString("name");
                        globalPreference.saveName(name);

                        Intent intent = new Intent(ServiceLoginActivity.this,ServiceHomeActivity.class);
                        startActivity(intent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse:"+error);
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",emailET.getText().toString());
                params.put("password",passwordET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }
}
