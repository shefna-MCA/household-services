
***User Home***

package com.nextgen.homeservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.google.android.material.navigation.NavigationView;
import com.nextgen.homeservice.Service.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment = null;

    private GlobalPreference globalPreference;
    private String ip, uid;
    String latitude, longitude;

    private TextView headerUsernameTV;
    private TextView headerEmailTV;
    ImageView headerSettingsIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        globalPreference = new GlobalPreference(this);
        ip = globalPreference.getIP();
        uid = globalPreference.getID();
        latitude = globalPreference.getLatitude();
        longitude = globalPreference.getLongitude();

        updateLocation(latitude, longitude);

        Toast.makeText(this, "loc"+latitude, Toast.LENGTH_SHORT).show();

        displaySelectedScreen(R.id.nav_home);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Initializing values from the nav_header_home xml file
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);

        headerUsernameTV = hView.findViewById(R.id.headerUsernameTextView);
        headerEmailTV = hView.findViewById(R.id.headerEmailTextView);
        headerSettingsIV = hView.findViewById(R.id.headerProfileSettings);

        headerSettingsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UserHomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        if (!uid.equals("")) {
            getUserDetails();
        }
    }

    private void displaySelectedScreen(int itemId) {

        switch (itemId) {

            case R.id.nav_home:
                fragment = new HomeFragment();
                break;

            case R.id.nav_bookings:
                Intent bIntent = new Intent(UserHomeActivity.this, BookingsActivity.class);
                startActivity(bIntent);
                break;

            case R.id.nav_feedback:
                Intent fIntent = new Intent(UserHomeActivity.this, FeedbackActivity.class);
                startActivity(fIntent);
                break;


            case R.id.nav_logout:
                logout();
                break;


        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        displaySelectedScreen(id);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getUserDetails() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/home_service/api/getUserInfo.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject obj = new JSONObject(response);
                    JSONArray array = obj.getJSONArray("data");
                    JSONObject jsonObject = array.getJSONObject(0);

                    String name = jsonObject.getString("name");
                    String email = jsonObject.getString("email");

                    headerUsernameTV.setText(name);
                    headerEmailTV.setText(email);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UserHomeActivity.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(UserHomeActivity.this);
        requestQueue.add(stringRequest);
    }

    private void logout() {

        new AlertDialog.Builder(UserHomeActivity.this)
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(UserHomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateLocation(String latitude, String longitude) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://" + ip + "/home_service/api/updateLocation.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("TAG", "onLocResponse: " + response);

                Toast.makeText(UserHomeActivity.this, "Location Updated", Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("TAG", "onLocErrorResponse: " + error);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", latitude);
                params.put("longitude", longitude);
                params.put("uid", uid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }
}
