package com.example.eijun.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context context ;
    private ArrayList<String> array_id, array_nama, array_alamat, array_hobi ;
    ProgressDialog progressDialog;

    public RecyclerViewAdapter(Context context, ArrayList<String> array_id, ArrayList<String> array_nama, ArrayList<String> array_alamat,  ArrayList<String> array_hobi ){
        this.context = context ;
        this.array_id = array_id ;
        this.array_alamat = array_alamat;
        this.array_hobi = array_hobi ;

        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.item_rv, parent, false) ;
        return new RecyclerViewAdapter.MyViewHolder(itemView) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.tv_id.setText(array_id.get(position));
        holder.tv_nama.setText(array_nama.get(position));
        holder.tvalamat.setText(array_alamat.get(position));
        holder.tv_hobby.setText(array_hobi.get(position));

        holder.cv_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ActivityEdit.class);
                intent.putExtra("id", array_id.get(position)) ;
                intent.putExtra("nama", array_nama.get(position)) ;
                intent.putExtra("alamat", array_alamat.get(position));
                intent.putExtra("hobi", array_hobi.get(position));

                ((ActivityMain)context).startActivityForResult(intent, 2);
            }
        });

        holder.cv_main.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder((ActivityMain)context)
                        .setMessage("ingin mengahapus nomor induk" +array_id+ "?" )
                        .setCancelable(false)
                        .setPositiveButton("ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.setMessage("menghapus");
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                AndroidNetworking.post("192.168.151.2/api-db-siswa/getData.php")
                                        .addBodyParameter("id",""+array_id.get(position))
                                        .setPriority(Priority.MEDIUM)
                                        .build()
                                        .getAsJSONObject(new JSONObjectRequestListener() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                progressDialog.dismiss();
                                                try {
                                                    Boolean status = response.getBoolean("status");
                                                    Log.d("status", ""+status);
                                                    String result = response.getString("result");
                                                    if (status){
                                                        if (context instanceof ActivityMain) {
                                                            ((ActivityMain)context).scrollRefresh();
                                                        }
                                                    }else {
                                                        Toast.makeText(context,""+result,Toast.LENGTH_SHORT).show();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onError(ANError anError) {

                                            }
                                        });

                            }
                        })
                        .setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();



                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return array_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_id, tv_nama, tvalamat, tv_hobby ;
        private CardView cv_main ;

        public MyViewHolder(View itemView) {

            super(itemView);

            cv_main = itemView.findViewById(R.id.cv_main);
            tv_id = itemView.findViewById(R.id.tv_id ) ;
            tv_nama = itemView.findViewById(R.id.tv_nama);
            tvalamat = itemView.findViewById(R.id.tv_alamat);
            tv_hobby = itemView.findViewById(R.id.tv_hobi) ;

            progressDialog = new ProgressDialog(context);

        }
    }
}
