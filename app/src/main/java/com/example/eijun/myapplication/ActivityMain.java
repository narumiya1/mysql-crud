package com.example.eijun.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityMain extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> array_id, array_nama, array_alamat, array_hobi;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_main);
        swipeRefreshLayout = findViewById(R.id.swipe_main);
        progressDialog = new ProgressDialog(this);

        recyclerView.hasFixedSize();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                scrollRefresh();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        scrollRefresh();
    }

    public void scrollRefresh() {

        progressDialog.setMessage("Mengambil data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 1200);
    }

    void getData() {
        initializeArray();
        AndroidNetworking.get("192.168.151.2/api-db-siswa/getData.php")
                .setTag("get data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();

                        try {
                            Boolean status = response.getBoolean("status");
                            if (status) {
                                JSONArray jsonArray = response.getJSONArray("result");
                                Log.d("respon", "" + jsonArray);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    array_id.add(jsonObject.getString("id"));
                                    array_nama.add(jsonObject.getString("nama"));
                                    array_alamat.add(jsonObject.getString("alamat"));
                                    array_hobi.add(jsonObject.getString("hobi"));
                                }

                                recyclerViewAdapter = new RecyclerViewAdapter(ActivityMain.this, array_id, array_nama, array_alamat, array_hobi);
                                recyclerView.setAdapter(recyclerViewAdapter);
                            } else {
                                Toast.makeText(ActivityMain.this, "gagal mengambil data", Toast.LENGTH_SHORT).show();
                                recyclerViewAdapter = new RecyclerViewAdapter(ActivityMain.this, array_id, array_nama, array_alamat, array_hobi);
                                recyclerView.setAdapter(recyclerViewAdapter);
                            }
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void initializeArray() {
        array_id = new ArrayList<String>();
        array_nama = new ArrayList<String>();
        array_alamat = new ArrayList<String>();
        array_hobi = new ArrayList<String>();

        array_id.clear();
        array_nama.clear();
        array_alamat.clear();
        array_hobi.clear();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menutambah, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent i = new Intent(ActivityMain.this, ActivityAdd.class);
            startActivityForResult(i, 1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
            if (resultCode == RESULT_OK) {
                scrollRefresh();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
            }
        }

        if (resultCode == 2) {
            if (resultCode == RESULT_OK) {
                scrollRefresh();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();

            }
        }
    }


}