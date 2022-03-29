package com.example.serviceexample;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewBuilder {
    public static void CreateStockRow(Context context, String stockName, String annualizedReturn, String annualizedVolatility){
        TableLayout table = (TableLayout) ((Activity)context).findViewById(R.id.tableLayout);
        TableRow row = new TableRow(context);
        row.setTag(stockName);
        TextView stock = new TextView(context);
        TableRow.LayoutParams stocklp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (float) 0.3
        );
        stock.setLayoutParams(stocklp);
        stock.setText(stockName);
        stock.setGravity(Gravity.CENTER);
        row.addView(stock);

        TextView annualizedReturnView = new TextView(context);
        TableRow.LayoutParams annualizedReturnlp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (float) 0.35
        );
        annualizedReturnView.setLayoutParams(annualizedReturnlp);
        annualizedReturnView.setText(annualizedReturn);
        annualizedReturnView.setGravity(Gravity.CENTER);
        row.addView(annualizedReturnView);

        TextView annualizedVolatilityView = new TextView(context);
        TableRow.LayoutParams annualizedVolatilitylp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (float) 0.35
        );
        annualizedVolatilityView.setLayoutParams(annualizedVolatilitylp);
        annualizedVolatilityView.setText(annualizedVolatility);
        annualizedVolatilityView.setGravity(Gravity.CENTER);
        row.addView(annualizedVolatilityView);

        table.addView(row);
    }
}
