package com.example.serviceexample;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PerformanceService extends Service {
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Uri CONTENT_URI = HistoricalDataProvider.CONTENT_URI;
            String[] columnNames = new String[1];
            columnNames[0] = "distinct stockName";
            Cursor cursor = getContentResolver().query(CONTENT_URI, columnNames,null, null, null);
            if(cursor.moveToFirst()){
                while(!cursor.isAfterLast()) {
                    String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                    this.post(new StockRunnable(PerformanceService.this, stockName));
                    cursor.moveToNext();
                }
            }

            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate(){
        HandlerThread thread = new HandlerThread("Service", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(this, "Calculations Commence", Toast.LENGTH_SHORT).show();

        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy(){ Toast.makeText(this, "Calculations Complete", Toast.LENGTH_SHORT).show(); }
}

class StockRunnable implements Runnable {
    Context context = null;
    String stockName = null;
    public StockRunnable(Context context, String stockName){
        this.context = context;
        this.stockName = stockName;
    }
    @Override
    public void run() {
        String selection = "stockName=?";
        String[] selectionArgs = new String[1];
        selectionArgs[0] = this.stockName;
        // the query method return a Cursor object, which is positioned before the first entry
        Cursor cursor = context.getContentResolver().query(HistoricalDataProvider.CONTENT_URI, null, selection, selectionArgs, null, null);
        double totalReturn = 0.0;
        double mean = 0.0;
        double standardDeviation = 0.0;
        int count = 0;
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int id = cursor.getColumnIndex("id");
                double close = cursor.getDouble(cursor.getColumnIndexOrThrow("close"));
                double open = cursor.getDouble(cursor.getColumnIndexOrThrow("open"));
                totalReturn += (close - open)/open;
                count++;
                cursor.moveToNext();
            }
        }

        Log.v(stockName + " row count ", String.valueOf(count));

        mean = totalReturn/count;
        double annualizedReturn = (250*(totalReturn/count))*100.0;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            int id = cursor.getColumnIndex("id");
            double close = cursor.getDouble(cursor.getColumnIndexOrThrow("close"));
            double open = cursor.getDouble(cursor.getColumnIndexOrThrow("open"));
            standardDeviation += Math.pow(((close - open)/open) - mean, 2);
            cursor.moveToNext();
        }

        double sdDailyReturn = Math.sqrt(standardDeviation/count);
        double annualizedVolatility = (Math.sqrt(250.0) * sdDailyReturn)*100.0;
        Intent intent = new Intent("PERFORMANCE_CALCULATED");
        intent.putExtra("stockName", this.stockName);
        intent.putExtra("annualizedReturn", annualizedReturn);
        intent.putExtra("annualizedVolatility", annualizedVolatility);
        this.context.sendBroadcast(intent);
    }
}
