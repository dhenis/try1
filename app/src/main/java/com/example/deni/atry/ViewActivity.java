package com.example.deni.atry;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import android.widget.Toast;

import com.example.deni.atry.adapter.RecyclerViewAdapter;
import com.example.deni.atry.api.RegisterAPI;
import com.example.deni.atry.model.Mahasiswa;
import com.example.deni.atry.model.Value;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ViewActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, OnChartValueSelectedListener {





    public static final String URL = "http://dhenis.com/mahasiswa/";
    public static int iteration = 1;

    private List<Mahasiswa> mahasiswa  = new ArrayList<>();
    private RecyclerViewAdapter viewAdapter;

    @BindView(R.id.recycleView)RecyclerView recyclerView;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.button)Button btnviewAll;

    SoundPool mySound;
    int raygunID;
    MediaPlayer mp;

//    btnviewAll = (Button)findViewById(R.id.button);
//    public LineData data = mChart.getData();

    private LineChart mChart;
    private void playmp(float a) {
        float volume = ((a / (mChart.getYChartMax() - mChart.getYChartMin())));
        mySound.play(raygunID, 1, 1, 1, 0, volume);

    }
    private LineData data;

    private ArrayList<Entry> entries = new ArrayList<Entry>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mySound = new SoundPool(6, AudioManager.STREAM_NOTIFICATION, 0);
        raygunID = mySound.load(this, R.raw.p1, 1);


        ButterKnife.bind(this);
        viewAdapter = new RecyclerViewAdapter(this, mahasiswa);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(viewAdapter);

        loadDataMahasiswa(); // panggil fungsi yang dibawah

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);
        mChart.setDescription("");

        // add an empty data object
        mChart.setData(new LineData());
        mChart.setScaleEnabled(false);

//        mChart.getXAxis().setDrawLabels(false);
//        mChart.getXAxis().setDrawGridLines(false);

        mChart.invalidate();



        mp = MediaPlayer.create(this, R.raw.p1);
        btnviewAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(data != null){
                    entries.clear();
                    for(int i=0;i<data.getDataSetByIndex(0).getEntryCount();i++){
                        entries.add(data.getDataSetByIndex(0).getEntryForIndex(i));
                    }

                }
                // TODO Auto-generated method stub
                final Timer timer = new Timer();

                // Body Of Timer
                TimerTask time = new TimerTask() {

                    private int v = 0;

                    @Override
                    public void run() {

                        //Perform background work here
                        if (!mp.isPlaying()) {

                            playmp(entries.get(v++).getVal());
//                              playmp(data.getDataSetByIndex(0).getEntryForIndex(v++).getVal());


                            if (v >= entries.size())
                                timer.cancel();
                        }


                    }
                };
                //Starting Timer
                timer.scheduleAtFixedRate(time, 0, 500);



            }
        });
    }

    int[] mColors = ColorTemplate.VORDIPLOM_COLORS;

    private void addEntry(int masukan) {


        data = mChart.getData();
        if(data != null) {
//            int test = Integer.parseInt(editY.getText().toString()); masih off
            ILineDataSet set = data.getDataSetByIndex(0);

            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet(); //masih off
                data.addDataSet(set);
            }

            // add a new x-value first
            data.addXValue(set.getEntryCount() + "");

            // choose a random dataSet
            int randomDataSetIndex = (int) (Math.random() * data.getDataSetCount());
            System.out.println("randomDataSetIndex: "+randomDataSetIndex);

            // tambah dari disini dari db
            data.addEntry(new Entry((float) masukan, set.getEntryCount()) , 0); //masih off

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            mChart.setVisibleXRangeMaximum(6);
            mChart.setVisibleYRangeMaximum(15, YAxis.AxisDependency.LEFT);
//
//            // this automatically refreshes the chart (calls invalidate())
            mChart.moveViewTo(data.getXValCount()-7, 50f, YAxis.AxisDependency.LEFT);
        }
    }

    private void removeLastEntry() {

        LineData data = mChart.getData();

        if(data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set != null) {

                Entry e = set.getEntryForXIndex(set.getEntryCount() - 1);

                data.removeEntry(e, 0);
                // or remove by index
                // mData.removeEntry(xIndex, dataSetIndex);

                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        }
    }

    private void addDataSet() {

        data = mChart.getData();

        if(data != null) {

            int count = (data.getDataSetCount() + 1);

            // create 10 y-vals
            ArrayList<Entry> yVals = new ArrayList<Entry>();

            if(data.getXValCount() == 0) {
                // add 10 x-entries
                for (int i = 0; i < 10; i++) {
                    data.addXValue("" + (i+1));
                }
            }

            for (int i = 0; i < data.getXValCount(); i++) {
                yVals.add(new Entry((float) (Math.random() * 50f) + 50f * count, i));
            }

            LineDataSet set = new LineDataSet(yVals, "DataSet " + count);
            set.setLineWidth(2.5f);
            set.setCircleRadius(4.5f);

            int color = mColors[count % mColors.length];

            set.setColor(color);
            set.setCircleColor(color);
            set.setHighLightColor(color);
            set.setValueTextSize(10f);
            set.setValueTextColor(color);

            data.addDataSet(set);
            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    private void removeDataSet() {

        LineData data = mChart.getData();

        if(data != null) {

            data.removeDataSet(data.getDataSetByIndex(data.getDataSetCount() - 1));

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    } //end for chart



    @Override
    protected void onResume(){
        super.onResume();
        loadDataMahasiswa();
    }

    // untuk meload data mahasiswa
    private void loadDataMahasiswa(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.view();

        call.enqueue(new Callback<Value>(){
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                if (iteration == 1) { // make sure only once response

                    String value = response.body().getValue();


                    progressBar.setVisibility(View.GONE);

                    if (value.equals("1")) { // nilai satu means bisa menghubungi server

    //                    String data = "[{\"jk\":\"Laki - Laki\",\"jurusan\":\"\",\"nama\":\"\",\"nim\":\"0\"}, {\"jk\":\"Perempuan\",\"jurusan\":\"Teknik Informatika\",\"nama\":\"1\",\"nim\":\"151524001\"}, {\"jk\":\"Laki - Laki\",\"jurusan\":\"System Informasi1\",\"nama\":\"2\",\"nim\":\"151524029\"}, {\"jk\":\"Laki - Laki\",\"jurusan\":\"Kedokteran\",\"nama\":\"3\",\"nim\":\"151524030\"}, {\"jk\":\"Laki - Laki\",\"jurusan\":\"Ilmu Komputer\",\"nama\":\"4\",\"nim\":\"151524088\"}]";
                        String data = new Gson().toJson(response.body().getResult()).toString();
                        Log.v("data nya @@ : ", data);

    //[{"jk":"Perempuan","jurusan":"Teknik Informatika","nama":"1","nim":"151524001"},
    // {"jk":"Laki - Laki","jurusan":"System Informasi1","nama":"2","nim":"151524029"},
    // {"jk":"Laki - Laki","jurusan":"Kedokteran","nama":"3","nim":"151524030"},
    // {"jk":"Laki - Laki","jurusan":"Ilmu Komputer","nama":"4","nim":"151524088"}]

                        mahasiswa = response.body().getResult();
    //                    Log.d("dari value: ",response.body().toString());

                        viewAdapter = new RecyclerViewAdapter(ViewActivity.this, mahasiswa);
                        recyclerView.setAdapter(viewAdapter);

                        try {

                            JSONArray jsonArr = new JSONArray(data);


                            for (int i = 0; i < jsonArr.length(); i++) {

                                JSONObject jsonObj = jsonArr.getJSONObject(i);

                                System.out.println(i); // ini masuk ke chart
                                addEntry(Integer.parseInt(jsonObj.getString("nama")));

    //                                Log.d("dari array length: ",jsonObj.getString("nama"));

                            }
                            Log.d("dari array length: ", String.valueOf(iteration));

    //                        array = new JSONArray(new Gson().toJson(response.body().getResult()));


    //                        Log.d("dari array: ",array.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


    //                    Log.v("berapa iterasi ", String.valueOf(iter[0]));
    //                    Log.v("bentuk json: ", mahasiswa.toString());

    //                    Log.v("TAG", "response 33: "+new Gson().toJson(response.body().getResult()) );
                        //int coba = Integer.parseInt(viewAdapter);
    //                                        addEntry(coba);

    //                    [{"jk":"Laki - Laki","jurusan":"","nama":"","nim":"0"},
    //                     {"jk":"Perempuan","jurusan":"Teknik Informatika","nama":"1","nim":"151524001"},
                        // {"jk":"Laki - Laki","jurusan":"System Informasi1","nama":"2","nim":"151524029"},
                        // {"jk":"Laki - Laki","jurusan":"Kedokteran","nama":"3","nim":"151524030"},
                        // {"jk":"Laki - Laki","jurusan":"Ilmu Komputer","nama":"4","nim":"151524088"}]


                        //V/bentuk json::
                        // [com.example.deni.atry.model.Mahasiswa@5f2654,
                        // com.example.deni.atry.model.Mahasiswa@f2919fd,
                        // com.example.deni.atry.model.Mahasiswa@11956f2,
                        // com.example.deni.atry.model.Mahasiswa@173f143,
                        // com.example.deni.atry.model.Mahasiswa@eb253c0]
                        iteration++;
                    }
                 }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {

                Toast.makeText(ViewActivity.this,"Jaringan Eror",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setQueryHint("Cari Nama Mahasiswa");
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;



    }

    @Override
    public boolean onQueryTextChange(String newText) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RegisterAPI api = retrofit.create(RegisterAPI.class);
        Call<Value> call = api.search(newText);
        call.enqueue(new Callback<Value>() {
            @Override
            public void onResponse(Call<Value> call, Response<Value> response) {
                String value = response.body().getValue();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (value.equals("1")) {
                    mahasiswa = response.body().getResult();
                    viewAdapter = new RecyclerViewAdapter(ViewActivity.this, mahasiswa);
                    recyclerView.setAdapter(viewAdapter);
                }
            }

            @Override
            public void onFailure(Call<Value> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
        return true;
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        float volume= ((e.getVal()/(mChart.getYChartMax()-mChart.getYChartMin()))*5);
        //float volume= (e.getVal()/130)*5;
        mySound.play(raygunID, 1, 1, 1, 0, volume);
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }


    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "DataSet 1");
        set.setLineWidth(2.5f);
        set.setCircleRadius(4.5f);
        set.setColor(Color.rgb(240, 99, 99));
        set.setCircleColor(Color.rgb(240, 99, 99));
        set.setHighLightColor(Color.rgb(190, 190, 190));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextSize(10f);

        return set;
    }
}



























