package com.example.serviceexample;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class ViewBuilder {
    public static void CreateStockRow(Context context, String stockName, String annualizedReturn, String annualizedVolatility, Context context_button){
        TableLayout table = (TableLayout) ((Activity)context).findViewById(R.id.tableLayout);
        TableRow row = new TableRow(context);
        row.setTag(stockName);
        TextView stock = new TextView(context);
        Button button = new Button(context_button);
        button.setText("Delete");
        button.setTag(stockName);
        button.getBackground().setColorFilter(ContextCompat.getColor(context, R.color.off_white), PorterDuff.Mode.MULTIPLY);
        button.setTextColor(ContextCompat.getColor(context, R.color.design_default_color_error));
        button.setLayoutParams(new
                TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

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
        row.addView(button);

        table.addView(row);
    }
}
