package com.example.eijun.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONObject;

public class ActivityEdit extends AppCompatActivity{

    com.rengwuxian.materialedittext.MaterialEditText et_id, et_nama, et_alamat, et_hobi ;
    String no_id, nama, alamat, hobi ;
    Button btn_submit ;
    ProgressDialog progressDialog ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        et_id = findViewById( R.id.et_id);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        et_hobi = findViewById(R.id.et_hobi);
        btn_submit = findViewById(R.id.btn_submit);

        progressDialog = new ProgressDialog(this);
        getDataIntent();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Menambahkan data.....");
                progressDialog.setCancelable(false);
                progressDialog.show();

                no_id = et_id.getText().toString();
                nama = et_nama.getText().toString();
                alamat = et_alamat.getText().toString();
                hobi = et_hobi.getText().toString();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validasiData();
                    }
                },1000);
            }
        });
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();

        if (bundle != null){
            et_id.setText(bundle.getString("no_id"));
            et_nama.setText(bundle.getString("nama"));
            et_alamat.setText(bundle.getString("alamat"));
            et_hobi.setText(bundle.getString("hobi"));
        }else {
            et_id.setText("");
            et_nama.setText("");
            et_alamat.setText("");
            et_hobi.setText("");
        }

    }

    private void validasiData() {

        if (no_id.equals("") || nama.equals("")| alamat.equals("") | hobi.equals("")){
            progressDialog.dismiss();
            Toast.makeText(ActivityEdit.this, "periksa kembali data masukan anda",Toast.LENGTH_SHORT).show();
        }else {
            updateData();
        }
    }

    private void updateData() {
        AndroidNetworking.post("192.168.151.2/api-db-siswa/updateSiswa.php")
                .addBodyParameter("no_id",""+no_id)
                .addBodyParameter("nama", ""+nama)
                .addBodyParameter("alamat", ""+alamat)
                .addBodyParameter("hobi",""+hobi)
                .setTag("update data")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        Log.d("responEdit",""+response);
                        try {
                            Boolean status = response.getBoolean("status");
                            if (status){
                                new AlertDialog.Builder(ActivityEdit.this)
                                        .setMessage("berhasil mengupdate data")
                                        .setCancelable(false)
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_OK,i);
                                                ActivityEdit.this.finish();
                                            }
                                        }).show();
                            }else {
                                new AlertDialog.Builder(ActivityEdit.this)
                                        .setMessage("gagal mengupdate data")
                                        .setCancelable(false)
                                        .setPositiveButton("kembali", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent i = getIntent();
                                                setResult(RESULT_CANCELED, i);
                                                ActivityEdit.this.finish();
                                            }
                                        });
                            }
                        }catch (Exception e){

                        }
                    }

                    @Override
                    public void onError(ANError anError) {


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
        if (id == R.id.menu_back) {
            this.finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
