package io.github.septianrin.hidrocon;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.septianrin.hidrocon.helper.InputFilterMinMax;
import io.github.septianrin.hidrocon.model.DataHidro;
import io.github.septianrin.hidrocon.model.DataManual;
import io.github.septianrin.hidrocon.model.DataOto;
import io.github.septianrin.hidrocon.model.DataPH;
import io.github.septianrin.hidrocon.model.DataTinggi;
import io.github.septianrin.hidrocon.model.DataEC;
import io.github.septianrin.hidrocon.network.RetrofitClientInstance;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main Activity";
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future longRunningTaskFuture;
    LinearLayout mancontainer;
    Button manapply;
    Button btnOto;
    Button btnMan;
    ProgressDialog progressDialog;
    long referencedatesuhu = 0;
    long referencedatetds = 0;
    long referencedateph = 0;
    private LineChart mChartSuhu;
    private LineDataSet globalSetSuhu;
    private LineChart mChartTDS;
    private LineDataSet globalSetTDS;
    private LineChart mChartPH;
    private LineDataSet globalSetPH;
    private Thread thread;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10*1000; //Delay for 15 seconds.  One second = 1000 milliseconds.


    @Override
    protected void onResume() {
        //start handler as activity become visible

        handler.postDelayed( runnable = new Runnable() {
            public void run() {
                //do something
                refreshData();
                handler.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    // If onPause() is not included the threads will double up when you
    // reload the activity

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable); //stop handler when activity not visible
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
        //declare variable to oncreate

        //generateDataPlot();
        mancontainer = findViewById(R.id.containerman);
        manapply = findViewById(R.id.applyman);
        btnOto = findViewById(R.id.btn_oto);
        btnMan = findViewById(R.id.btn_manual);
        mancontainer.setVisibility(View.GONE);
        manapply.setVisibility(View.GONE);

        //TODO : make seekbar function so no need to rewrite code

        makeSeekbarWithEditText(R.id.sb_ph, R.id.et_ph, 14, 7);
        makeSeekbarWithEditText(R.id.sb_tds, R.id.et_tds, 1400, 1000);
        makeSeekbarWithEditText(R.id.sb_temp, R.id.et_temp, 35, 21);

        mChartSuhu = findViewById(R.id.plotSuhu);
        mChartSuhu.setTouchEnabled(true);
        mChartSuhu.setPinchZoom(true);

        mChartPH = findViewById(R.id.plotPH);
        mChartPH.setTouchEnabled(true);
        mChartSuhu.setPinchZoom(true);

        mChartTDS = findViewById(R.id.plotTDS);
        mChartTDS.setTouchEnabled(true);
        mChartTDS.setPinchZoom(true);

        //TODO : Runable here every 60 second to call api, delete last LineData and add new ones
        // or add the last api response with database.size()-1
        refreshData();
    }

    private void makeSeekbarWithEditText(int id_sb, int id_et, int max, int nilaidef) {
        final SeekBar seekBar = findViewById(id_sb);
        seekBar.setMax(max);
        seekBar.setProgress(nilaidef);
        final EditText seekBarValue = findViewById(id_et);
        // TODO : Make if to make inputfilter work
        seekBarValue.setFilters(new InputFilter[]{new InputFilterMinMax(0, max)});
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i > 0) {
                    seekBarValue.setText(String.valueOf(i));
                } else {
                    seekBarValue.setText("");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarValue.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                super.beforeTextChanged(s, start, count, after);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public synchronized void afterTextChanged(Editable s) {
                super.afterTextChanged(s);

                //seekBarValue.removeTextChangedListener(this);
                if (s.length() < 1) {
                    seekBar.setProgress(0);
                } else {
                    seekBar.setProgress(Integer.parseInt(s.toString()));
                }
            }
        });

    }

    private void refreshData() {
        RetroInterface service = RetrofitClientInstance.getRetrofitInstance().create(RetroInterface.class);
        Call<DataHidro> call = service.lihatdata();

        call.enqueue(new Callback<DataHidro>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DataHidro> call, Response<DataHidro> response) {
                progressDialog.dismiss();
                assert response.body() != null;
                Log.e(TAG, response.body().toString());
                ArrayList<DataTinggi> suhu = response.body().getDataTinggi();
                ArrayList<DataEC> tds = response.body().getDataEC();
                ArrayList<DataPH> ph = response.body().getDataPH();
                //Log.e(TAG, "onResponse: "+response.body().getDataSuhu().toString() );
                setDataLineSuhu(suhu);
                setDataLinePH(ph);
                setDataLineTDS(tds);
            }

            @Override
            public void onFailure(Call<DataHidro> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something When Wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: "+t );
                System.out.println(t);
            }
        });


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDataLineSuhu(ArrayList<DataTinggi> database) {

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < database.size(); i++) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
            long xdate = LocalDateTime.parse(database.get(i).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            referencedatesuhu = LocalDateTime.parse(database.get(0).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            values.add(new Entry(xdate - referencedatesuhu, database.get(i).getValue()));
        }

        if (mChartSuhu.getData() != null &&
                mChartSuhu.getData().getDataSetCount() > 0) {
            //if there is data on chart then add datapoints
            globalSetSuhu = (LineDataSet) mChartSuhu.getData().getDataSetByIndex(0);
            globalSetSuhu.setValues(values);
            mChartSuhu.getData().notifyDataChanged();
            mChartSuhu.notifyDataSetChanged();
            mChartSuhu.invalidate();
        } else {
            //create new line chart
            globalSetSuhu = new LineDataSet(values, "Sample Data");
            globalSetSuhu.setDrawIcons(false);
            globalSetSuhu.enableDashedLine(10f, 5f, 0f);
            globalSetSuhu.enableDashedHighlightLine(10f, 5f, 0f);
            globalSetSuhu.setColor(Color.DKGRAY);
            globalSetSuhu.setCircleColor(Color.DKGRAY);
            globalSetSuhu.setLineWidth(1f);
            globalSetSuhu.setCircleRadius(3f);
            globalSetSuhu.setDrawCircleHole(false);
            globalSetSuhu.setValueTextSize(9f);
            globalSetSuhu.setDrawFilled(true);
            globalSetSuhu.setFormLineWidth(1f);
            globalSetSuhu.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            globalSetSuhu.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                globalSetSuhu.setFillColor(Color.GREEN);
            } else {
                globalSetSuhu.setFillColor(Color.DKGRAY);
            }

            ValueFormatter xAxisFormatter = new MyValueFormatter(referencedatesuhu);
            XAxis xAxis = mChartSuhu.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);
            mChartSuhu.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(globalSetSuhu);
            LineData data = new LineData(dataSets);
            mChartSuhu.setData(data);
            mChartSuhu.animateXY(1000, 1000);
            mChartSuhu.notifyDataSetChanged();
            mChartSuhu.invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDataLinePH(ArrayList<DataPH> database) {


        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < database.size(); i++) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
            long xdate = LocalDateTime.parse(database.get(i).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            referencedateph = LocalDateTime.parse(database.get(0).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            values.add(new Entry(xdate - referencedateph, database.get(i).getValue()));
        }

        if (mChartPH.getData() != null &&
                mChartPH.getData().getDataSetCount() > 0) {
            //if there is data on chart then add datapoints
            globalSetPH = (LineDataSet) mChartPH.getData().getDataSetByIndex(0);
            globalSetPH.setValues(values);
            mChartPH.getData().notifyDataChanged();
            mChartPH.notifyDataSetChanged();
            mChartPH.invalidate();
        } else {
            //create new line chart
            globalSetPH = new LineDataSet(values, "Sample Data");
            globalSetPH.setDrawIcons(false);
            globalSetPH.enableDashedLine(10f, 5f, 0f);
            globalSetPH.enableDashedHighlightLine(10f, 5f, 0f);
            globalSetPH.setColor(Color.DKGRAY);
            globalSetPH.setCircleColor(Color.DKGRAY);
            globalSetPH.setLineWidth(1f);
            globalSetPH.setCircleRadius(3f);
            globalSetPH.setDrawCircleHole(false);
            globalSetPH.setValueTextSize(9f);
            globalSetPH.setDrawFilled(true);
            globalSetPH.setFormLineWidth(1f);
            globalSetPH.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            globalSetPH.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {

                globalSetPH.setFillColor(Color.GREEN);
            } else {
                globalSetPH.setFillColor(Color.DKGRAY);
            }

            ValueFormatter xAxisFormatter = new MyValueFormatter(referencedateph);
            XAxis xAxis = mChartPH.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);
            mChartPH.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //XAxis xAxis = mChart.getXAxis();
            //xAxis.setLabelCount(4,true);;

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(globalSetPH);
            LineData data = new LineData(dataSets);
            mChartPH.setData(data);
            mChartPH.animateXY(1000, 1000);
            mChartPH.notifyDataSetChanged();
            mChartPH.invalidate();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDataLineTDS(ArrayList<DataEC> database) {

        ArrayList<Entry> values = new ArrayList<>();
        for (int i = 0; i < database.size(); i++) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
            long xdate = LocalDateTime.parse(database.get(i).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            referencedatetds = LocalDateTime.parse(database.get(0).getDate(), dtf)
                    .atOffset(ZoneOffset.ofHours(0))
                    .toInstant()
                    .toEpochMilli();
            values.add(new Entry(xdate - referencedatetds, database.get(i).getValue()));
        }

        if (mChartTDS.getData() != null &&
                mChartTDS.getData().getDataSetCount() > 0) {
            //if there is data on chart then add datapoints
            globalSetTDS = (LineDataSet) mChartTDS.getData().getDataSetByIndex(0);
            globalSetTDS.setValues(values);
            mChartTDS.getData().notifyDataChanged();
            mChartTDS.notifyDataSetChanged();
            mChartTDS.invalidate();
        } else {
            //create new line chart
            globalSetTDS = new LineDataSet(values, "TDS");
            globalSetTDS.setDrawIcons(false);
            globalSetTDS.enableDashedLine(10f, 5f, 0f);
            globalSetTDS.enableDashedHighlightLine(10f, 5f, 0f);
            globalSetTDS.setColor(Color.DKGRAY);
            globalSetTDS.setCircleColor(Color.DKGRAY);
            globalSetTDS.setLineWidth(1f);
            globalSetTDS.setCircleRadius(3f);
            globalSetTDS.setDrawCircleHole(false);
            globalSetTDS.setValueTextSize(9f);
            globalSetTDS.setDrawFilled(true);
            globalSetTDS.setFormLineWidth(1f);
            globalSetTDS.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            globalSetTDS.setFormSize(15.f);
            if (Utils.getSDKInt() >= 18) {
                //Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_launcher_background);
                //set1.setFillDrawable(drawable);
                globalSetTDS.setFillColor(Color.rgb(201, 252, 130));
            } else {
                globalSetTDS.setFillColor(Color.DKGRAY);
            }

            ValueFormatter xAxisFormatter = new MyValueFormatter(referencedatetds);
            XAxis xAxis = mChartTDS.getXAxis();
            xAxis.setValueFormatter(xAxisFormatter);
            mChartTDS.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            //XAxis xAxis = mChart.getXAxis();
            //xAxis.setLabelCount(4,true);;

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(globalSetTDS);
            LineData data = new LineData(dataSets);
            mChartTDS.setData(data);
            mChartTDS.animateXY(1000, 1000);
            mChartTDS.notifyDataSetChanged();
            mChartTDS.invalidate();
        }
    }


    public void gotoManual(View view) {
        RetroInterface service = RetrofitClientInstance.getRetrofitInstance().create(RetroInterface.class);
        Call<DataManual> call = service.gomanual();

        call.enqueue(new Callback<DataManual>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DataManual> call, Response<DataManual> response) {
                assert response.body() != null;
                Log.e(TAG, response.body().toString());
                Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DataManual> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something When Wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: "+t );
                System.out.println(t);
            }
        });

        mancontainer.setVisibility(View.VISIBLE);
        manapply.setVisibility(View.VISIBLE);
        btnOto.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        btnOto.setTextColor(Color.parseColor("#918E85"));
        btnMan.setBackground(getResources().getDrawable(R.drawable.button_active));
        btnMan.setTextColor(Color.parseColor("#000000"));
    }

    public void gotoOtomatis(View view) {
        RetroInterface service = RetrofitClientInstance.getRetrofitInstance().create(RetroInterface.class);
        Call<DataOto> call = service.gootom();

        call.enqueue(new Callback<DataOto>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<DataOto> call, Response<DataOto> response) {
                assert response.body() != null;
                Log.e(TAG, response.body().toString());
                Toast.makeText(MainActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DataOto> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Something When Wrong", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: "+t );
                System.out.println(t);
            }
        });
        mancontainer.setVisibility(View.GONE);
        manapply.setVisibility(View.GONE);
        btnOto.setBackground(getResources().getDrawable(R.drawable.button_active));
        btnOto.setTextColor(Color.parseColor("#000000"));
        btnMan.setBackground(getResources().getDrawable(R.drawable.button_disabled));
        btnMan.setTextColor(Color.parseColor("#918E85"));

    }

}