package com.example.eijun.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

class ActivityAdd extends AppCompatActivity {

    com.rengwuxian.materialedittext.MaterialEditText et_id, et_nama, et_alamat, et_hobi;
    Button btn_submit;
    String id, nama, alamat, hobi;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        et_id = findViewById(R.id.et_id);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_hobi = findViewById(R.id.et_hobi);
        btn_submit = findViewById(R.id.btn_submit);

        progressDialog = new ProgressDialog(this);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("menambahkan data....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                id = et_id.getText().toString();
                nama = et_nama.getText().toString();
                alamat = et_alamat.getText().toString();
                hobi = et_hobi.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                }, 1000);

            }
        });
    }

    void validasiData() {
        if (id.equals("") || nama.equals("") || alamat.equals("") | hobi.equals("")) {
            progressDialog.dismiss();
            Toast.makeText(ActivityAdd.this, "periksa kembali data masukan anda", Toast.LENGTH_SHORT).show();
        } else {
            sendData();
        }
    }

    private void sendData() {
        AndroidNetworking.post("192.168.151.2/api-db-siswa/tambahSiswa.php")
                .addBodyParameter("id", "+" + id)
                .addBodyParameter("nama", "" + nama)
                .addBodyParameter("alamat", "" + alamat)
                .addBodyParameter("hobi", "" + hobi)
                .setPriority(Priority.MEDIUM)
                .setTag("tambah data")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("cekTambah", "" + response);
                        try {
                            Boolean status = response.getBoolean("status");
                            String pesan = response.getString("result");
                            Toast.makeText(ActivityAdd.this, "" + pesan, Toast.LENGTH_SHORT).show();
                            Log.d("status", "" + status);
                            if (status) {
                                new AlertDialog.Builder(ActivityAdd.this)
                                        .setMessage("berhasil menambahkan data")
                                        .setCancelable(false)
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK, i);
                                                ActivityAdd.this.finish();
                                            }
                                        })
                                        .show();
                            }else {
                                new AlertDialog.Builder(ActivityAdd.this)
                                        .setMessage("gagal menambahkan data")
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED, i);
                                                ActivityAdd.this.finish();
                                            }
                                        })
                                        .setCancelable(false)
                                        .show();


                            }

                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                        Log.d("error tambahdata","" +anError.getErrorBody());
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_back){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
