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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final Handler handler;

    public MyBroadcastReceiver(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("DOWNLOAD_COMPLETE")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String stockName = intent.getStringExtra("stockName");

                    TableLayout table = (TableLayout) ((Activity)context).findViewById(R.id.tableLayout);
                    TableRow row = new TableRow(context);
                    TextView stock = new TextView(context);
                    stock.setText(stockName);
                    row.addView(stock);

                    TextView annReturn = new TextView(context);
                    annReturn.setText("NA");
                    row.addView(annReturn);

                    TextView volatility = new TextView(context);
                    volatility.setText("NA");
                    row.addView(volatility);
                    table.addView(row);
                }
            });
        }
    }
}

class StockRunnable implements Runnable{
    private String stockName;
    private Context context;
    public StockRunnable(String stockName, Context context){
        this.stockName = stockName;
        this.context = context;
    }
    @Override
    public void run() {
        TableLayout table = (TableLayout) ((Activity)context).findViewById(R.id.tableLayout);
        TableRow row = new TableRow(this.context);
        TextView stock = new TextView(this.context);
        stock.setText(this.stockName);
        row.addView(stock);

        TextView annReturn = new TextView(this.context);
        annReturn.setText("NA");
        row.addView(annReturn);

        TextView volatility = new TextView(this.context);
        volatility.setText("NA");
        row.addView(volatility);
        table.addView(row);

    }
}
