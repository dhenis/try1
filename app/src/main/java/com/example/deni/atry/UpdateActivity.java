package com.example.deni.atry;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.deni.atry.api.RegisterAPI;
import com.example.deni.atry.model.Value;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateActivity extends AppCompatActivity {

    public static final String URL = "http://dhenis.com/mahasiswa/";
    private RadioButton radioJKButton;
    private ProgressDialog progress;
    String nim, nama, jurusan;


    // karena udah pake butter knife --> onclick mendjadi lebih simple

    @BindView(R.id.radioJK) RadioGroup radioGroup;
    @BindView(R.id.male) RadioButton radioButtonMale;
    @BindView(R.id.female) RadioButton RadioButtonFemale;
    @BindView(R.id.editTextNIM) EditText editTextNIM;
    @BindView(R.id.editTextJurusan) EditText ediTextJurusan;
    @BindView(R.id.editTextNama)  EditText editTextNama;

    // simple karna pake butter knife
    @OnClick(R.id.buttonUbah) void ubah(){

        //Untuk menampilkan progress dialog
        progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.setMessage("Loading...");
        progress.show();

        nim = editTextNIM.getText().toString();
        nama = editTextNama.getText().toString();
        jurusan = ediTextJurusan.getText().toString();

        int selectedId = radioGroup.getCheckedRadioButtonId();

        radioJKButton = (RadioButton) findViewById(selectedId);
        String jk = radioJKButton.getText().toString();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.ubah(nim,nama,jurusan,jk);

        call.enqueue(new Callback<Value>(){
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();

                if(value.equals("1")){
                    Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish(); // untuk kembali ke main activity

                }else{
                    Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(UpdateActivity.this,"Jaringan Eror",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ubah Data");

        Intent intent = getIntent();
        String nim = intent.getStringExtra("nim"); // updae akan terkirim ke
        String nama = intent.getStringExtra("nama"); // updae akan terkirim ke
        String jurusan = intent.getStringExtra("jurusan"); // updae akan terkirim ke
        String jk = intent.getStringExtra("jk"); // updae akan terkirim ke

        editTextNIM.setText(nim);
        editTextNama.setText(nama);
        ediTextJurusan.setText(jurusan);
        if(jk.equals("Laki - Laki")){
            radioButtonMale.setChecked(true);
        }else{
            RadioButtonFemale.setChecked(true);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_delete, menu);

        return true;
    }

    // saat memilih
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_delete:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Peringatan");
                alertDialogBuilder
                        .setMessage("Apakah Anda yakin ingin mengapus data ini?")
                        .setCancelable(false)
                        .setPositiveButton("Hapus",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                Gson gson = new GsonBuilder()
                                        .setLenient()
                                        .create();
                                String nim = editTextNIM.getText().toString();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(URL)
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
                                RegisterAPI api = retrofit.create(RegisterAPI.class);
                                Call<Value> call = api.hapus(nim);

                                call.enqueue(new Callback<Value>() {
                                    @Override
                                    public void onResponse(Call<Value> call, Response<Value> response) {
                                        String value = response.body().getValue();
                                        String message = response.body().getMessage();
//                                        Toast.makeText(UpdateActivity.this, finalResponse.body().getValue(), Toast.LENGTH_SHORT).show();
                                        if (value.equals("1")) {
                                            Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(UpdateActivity.this, message, Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Value> call, Throwable t) {
                                        t.printStackTrace(); // ini print error loh bro
                                        Log.d("errornya : " , ""+call);
                                        //                                            Log.d("passing: ", call.toString());
//                                           Toast.makeText(UpdateActivity.this,call.toString(), Toast.LENGTH_SHORT).show();

//
//                                           Toast.makeText(UpdateActivity.this, "Jaringan Error!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Batal",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}




























