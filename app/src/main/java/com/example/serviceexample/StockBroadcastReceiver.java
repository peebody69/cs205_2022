package com.example.serviceexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StockBroadcastReceiver extends BroadcastReceiver {

    private final Handler handler;

    public StockBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // On receiving a broadcast with intent action "DOWNLOAD_COMPLETE"
        if (intent.getAction().equals("DOWNLOAD_COMPLETE")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String stockName = intent.getStringExtra("stockName");
                    ViewBuilder.CreateStockRow(context, stockName, "NA", "NA", context);
                }
            });
        }

        if(intent.getAction().equals("PERFORMANCE_CALCULATED")){
            String tag = intent.getStringExtra("stockName");

            // Identifying the tablelayout on activitymain.xml
            TableLayout table = (TableLayout) ((Activity)context).findViewById(R.id.tableLayout);

            // Identifying the row corresponding to the stock whose performance we have calculated
            TableRow row = (TableRow) table.findViewWithTag(tag);

            double annualizedReturn = intent.getDoubleExtra("annualizedReturn", -1);
            double annualizedVolatility = intent.getDoubleExtra("annualizedVolatility", -1 );
            /**
             * Setting the value of the Annualized Return & Annualized Volatility
             */
            if(row != null){
                TextView tv0 = (TextView) row.getChildAt(1);
                TextView tv1 = (TextView) row.getChildAt(2);
                tv0.setText(String.format("%.2f%%", annualizedReturn));
                tv1.setText(String.format("%.2f%%", annualizedVolatility));
            }


        }
    }
}

