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
                    Uri CONTENT_URI = Uri.parse("content://com.example.serviceexample.HistoricalDataProvider/history");
                    TextView result = (TextView) ((Activity)context).findViewById(R.id.textview_result);
                    String[] columnNames = new String[1];
                    columnNames[0] = "distinct stockName";
                    Cursor cursor = context.getContentResolver().query(CONTENT_URI, columnNames,null, null, null);
                    List<String> stocks = new ArrayList<>();
                    if(cursor.moveToFirst()){
                        while(!cursor.isAfterLast()) {

                            stocks.add(cursor.getString(cursor.getColumnIndexOrThrow("stockName")));
                            cursor.moveToNext();
                        }
                    }

                    for(String stockName : stocks){
                        handler.post(new StockRunnable(stockName, context));
                    }

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
        annReturn.setText(String.format("%.4f", 0.005));
        row.addView(annReturn);

        TextView volatility = new TextView(this.context);
        volatility.setText(String.format("%.2f", 15.8));
        row.addView(volatility);
        table.addView(row);

    }
}
