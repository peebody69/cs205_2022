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
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.serviceexample.*;

public class MainActivity extends AppCompatActivity{

    private Button start, calc;
    private EditText ticker;

//    Uri CONTENT_URI = Uri.parse("content://com.example.serviceexample.HistoricalDataProvider/history");
    private BroadcastReceiver myBroadcastReceiver;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up layout
        setContentView(R.layout.activitymain);

        start = (Button) findViewById(R.id.start_button);
        calc = (Button) findViewById(R.id.calc_button);
        ticker = (EditText) findViewById(R.id.edit_ticker);

        // Start BroadcastReceiver
        myBroadcastReceiver = new MyBroadcastReceiver(new Handler(Looper.getMainLooper()));

        // start service, pass ticker info via an intent

        // Initialise Table with data already inside the database
        // Create String Array of 1 Location to place String
        String[] columnNames = new String[1];
        columnNames[0] = "distinct stockName";

        /**
         * getContentResolver() is method of class android.content.Context, to call it you need an instance of Context (e.g. Activity or Service)
         * A Cursor is used to select the input from the system that the user operates with the mouse
          */
        Cursor cursor = getContentResolver().query(HistoricalDataProvider.CONTENT_URI, columnNames, null, null, null);
        if(cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                String stockName = cursor.getString(cursor.getColumnIndexOrThrow("stockName"));
                // Invoke ViewBuilder.java
                ViewBuilder.CreateStockRow(this, stockName, "NA", "NA", this);
                cursor.moveToNext();
            }
        }

        // When click on the start_button aka "Download" button
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    Log.e("input",e.getMessage());
                }
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

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }


}