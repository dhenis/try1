package com.example.deni.atry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.deni.atry.api.RegisterAPI;
import com.example.deni.atry.model.Value;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;




public class MainActivity extends AppCompatActivity {
    public static final String URL = "http://dhenis.com/mahasiswa/";
    private RadioButton radioJKButton;
    private ProgressDialog progress;
    String nim, nama, jurusan;


    // karena udah pake butter knife --> onclick mendjadi lebih simple

    @BindView(R.id.radioJK)  RadioGroup radioGroup;
    @BindView(R.id.editTextNIM)  EditText editTextNIM;
    @BindView(R.id.editTextJurusan) EditText ediTextJurusan;
    @BindView(R.id.editTextNama)  EditText editTextNama;


    // simple karna pake butter knife
    @OnClick(com.example.deni.atry.R.id.buttonDaftar) void daftar(){

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
        Call<Value> call = api.daftar(nim,nama,jurusan,jk);

        call.enqueue(new Callback<Value>(){
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                progress.dismiss();

                if(value.equals("1")){
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                t.printStackTrace();
                progress.dismiss();
                Toast.makeText(MainActivity.this,"Jaringan Eror",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.buttonLihat)void lihat(){
        Intent pindah = new Intent(MainActivity.this, ViewActivity.class);
        startActivity(pindah);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
}
