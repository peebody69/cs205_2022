package com.example.serviceexample;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.serviceexample.*;

public class MainActivity extends AppCompatActivity{

    private Button start, calc;
    private TextView result;
    private EditText ticker;

//    Uri CONTENT_URI = Uri.parse("content://com.example.serviceexample.HistoricalDataProvider/history");
    private BroadcastReceiver myBroadcastReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up layout

        setContentView(R.layout.activitymain);

        start = (Button) findViewById(R.id.start_button);
        calc = (Button) findViewById(R.id.calc_button);
        result = (TextView) findViewById(R.id.textview_result);
        ticker = (EditText) findViewById(R.id.edit_ticker);

        // Initialise Table with data already inside the database
        TableLayout table = (TableLayout) findViewById(R.id.tableLayout);
        String[] columnNames = new String[1];
        columnNames[0] = "distinct stockName";
        Cursor cursor = getContentResolver().query(HistoricalDataProvider.CONTENT_URI, columnNames, null, null, null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                TableRow row = new TableRow(this);
                row.setTag(stockName);
                TextView stock = new TextView(this);
                stock.setText(stockName);
                row.addView(stock);

                TextView annReturn = new TextView(this);
                annReturn.setText("NA");
                row.addView(annReturn);

                TextView volatility = new TextView(this);
                volatility.setText("NA");
                row.addView(volatility);
                table.addView(row);

                cursor.moveToNext();
            }
        }


        // Start BroadcastReceiver
        myBroadcastReceiver = new MyBroadcastReceiver(new Handler(Looper.getMainLooper()));

        // start service, pass ticker info via an intent

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                registerReceiver(myBroadcastReceiver, new IntentFilter("DOWNLOAD_COMPLETE"));

                Intent intent = new Intent(getApplicationContext(), MyService.class);
                intent.putExtra("ticker", String.valueOf(ticker.getText()));
                startService(intent);
            }
        });

        // register broadcast receiver to get informed that data is downloaded so that we can calc

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                result.setText("Calculating");
                registerReceiver(myBroadcastReceiver, new IntentFilter("PERFORMANCE_CALCULATED"));
                Intent intent = new Intent(getApplicationContext(), PerformanceService.class);
                startService(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myBroadcastReceiver);
    }


}