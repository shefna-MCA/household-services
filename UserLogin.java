****** user Login *****

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
import com.nextgen.homeservice.Service.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLoginActivity extends AppCompatActivity {

    EditText usernameET;
    EditText passwordET;
    Button loginBT;
    TextView signUpTV;

    private GlobalPreference globalPreference;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        Intent lIntent = new Intent(UserLoginActivity.this, LocationService.class);
        startService(lIntent);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();

        usernameET = findViewById(R.id.usernameEditText);
        passwordET = findViewById(R.id.passwordEditText);
        loginBT = findViewById(R.id.userLoginButton);
        signUpTV = findViewById(R.id.uSignUpTextView);

        usernameET.setText("anandu");
        passwordET.setText("555");


        loginBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (usernameET.getText().toString().isEmpty() || passwordET.getText().toString().isEmpty()){
                    Toast.makeText(UserLoginActivity.this, "Please Fill the Fields", Toast.LENGTH_SHORT).show();
                }else{
                    login();

                }

            }
        });

        signUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserLoginActivity.this,UserRegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ ip +"/home_service/api/userLogin.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: "+response);

                if(response.equals("failed")){
                    Toast.makeText(UserLoginActivity.this, "Incorrect Username or Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String id = jsonObject.getString("id");
                        globalPreference.saveID(id);
                        String name = jsonObject.getString("name");
                        globalPreference.saveName(name);

                        Intent intent = new Intent(UserLoginActivity.this,UserHomeActivity.class);
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
                params.put("username",usernameET.getText().toString());
                params.put("password",passwordET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}