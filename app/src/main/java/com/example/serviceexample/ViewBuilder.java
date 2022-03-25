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
    public static void CreateStockRow(Context context, String stockName, String annualReturn, String volatility){
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

        TextView annualReturnView = new TextView(context);
        TableRow.LayoutParams annualReturnlp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (float) 0.35
        );
        annualReturnView.setLayoutParams(annualReturnlp);
        annualReturnView.setText(annualReturn);
        annualReturnView.setGravity(Gravity.CENTER);
        row.addView(annualReturnView);

        TextView volatilityView = new TextView(context);
        TableRow.LayoutParams volatilitylp = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                (float) 0.35
        );
        volatilityView.setLayoutParams(volatilitylp);
        volatilityView.setText(volatility);
        volatilityView.setGravity(Gravity.CENTER);
        row.addView(volatilityView);

        table.addView(row);
    }
}
