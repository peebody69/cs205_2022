package com.example.serviceexample;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadService extends Service{
    private Looper serviceLooper;
    private ServiceHandler serviceHandler;

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private String token = BuildConfig.API_KEY; // put your own token

    private final class ServiceHandler extends Handler{
        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            // Obtain the name of the stock to download from the current message
            String ticker = (String) msg.obj;

            // Toast indicating initiation of download for current stock
            Toast.makeText(DownloadService.this, "Downloading in progress for: " + ticker, Toast.LENGTH_SHORT).show();

            // url to get historical data

            String stringUrl = "https://finnhub.io/api/v1/stock/candle?symbol="+ticker
                    +"&resolution=D&from=1625097601&to=1640995199&token="+token;
            String result = null;
            String inputLine;

            try {
                // Check if data already exists in database
                Cursor cursor = getContentResolver().query(HistoricalDataProvider.CONTENT_URI, null, "stockName=?", new String[]{ticker}, null);
                Cursor stockCount = getContentResolver().query(HistoricalDataProvider.CONTENT_URI, new String[] {"count(DISTINCT stockName) as stockCount"} ,null, null, null);
                if(cursor.moveToFirst()){
                    throw new StockExistsException(ticker);
                }
                if(stockCount.moveToFirst()){
                    int numStocks = stockCount.getInt(stockCount.getColumnIndexOrThrow("stockCount"));
                    if(numStocks >= 5){
                        throw new TooManyStocksException();
                    }
                }
                // make GET requests

                URL myUrl = new URL(stringUrl);
                HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                connection.connect();

                // store json string from GET response

                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }

                reader.close();
                streamReader.close();

                result = stringBuilder.toString();

            } catch(IOException e) {
                e.printStackTrace();
                result = null;
                Thread.currentThread().interrupt();
            } catch(StockExistsException e){
                e.printStackTrace();
                Toast.makeText(DownloadService.this, "Stock Already Exists", Toast.LENGTH_SHORT).show();
                return;
            } catch (TooManyStocksException e){
                e.printStackTrace();
                Toast.makeText(DownloadService.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            // parse the json string into 'close' and 'open' array

            JSONObject jsonObject = null;
            JSONArray jsonArrayClose = null;
            JSONArray jsonArrayOpen = null;

            try {
                jsonObject = new JSONObject(result);
                if(!(jsonObject.has("c") && jsonObject.has("o")))
                    throw new InvalidStockException(ticker);
                jsonArrayClose = jsonObject.getJSONArray("c");
                jsonArrayOpen = jsonObject.getJSONArray("o");

            } catch (JSONException e) {
                e.printStackTrace();
            } catch(InvalidStockException e){
                e.printStackTrace();
                Toast.makeText(DownloadService.this, "Invalid ticker", Toast.LENGTH_SHORT).show();
                return;
            }


//            Log.v("close", String.valueOf(jsonArrayClose.length()));
//            Log.v("open", String.valueOf(jsonArrayOpen.length()));

            // Insert rows into database
            try {
                for (int i = 0; i < jsonArrayClose.length(); i++) {
                    double close = jsonArrayClose.getDouble(i);
                    double open = jsonArrayOpen.getDouble(i);
                    Log.v("data", i + ":, c: " + close + " o: " + open);

                    ContentValues values = new ContentValues();
                    values.put(HistoricalDataProvider.STOCKNAME, ticker);
                    values.put(HistoricalDataProvider.ID, i);
                    values.put(HistoricalDataProvider.CLOSE, close);
                    values.put(HistoricalDataProvider.OPEN, open);
                    getContentResolver().insert(HistoricalDataProvider.CONTENT_URI, values);
                }
            }
            catch (JSONException e)
            {e.printStackTrace();}
            catch (NullPointerException e){
                // In the case of an invalid API Key, jsonArray will be null
                e.printStackTrace();
                Toast.makeText(DownloadService.this, "Invalid API Key", Toast.LENGTH_SHORT).show();
            }

            // Toast indicating completion of download for current stock
            Toast.makeText(DownloadService.this, "Downloading complete for: " + ticker, Toast.LENGTH_SHORT).show();

            // broadcast message that download is complete

            Intent intent = new Intent("DOWNLOAD_COMPLETE");
            intent.putExtra("stockName", ticker);
            sendBroadcast(intent);

            // Destroys service if arg1 matches the last startId that was started by service
            // ie. Destroy service if there are no longer any messages in queue
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
        Toast.makeText(this, "Download Starting", Toast.LENGTH_SHORT).show();

        // Obtain a message instance from pool of recycled objects
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent.getStringExtra("ticker");

        // Queue a new message
        serviceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onDestroy(){ Toast.makeText(this, "Download Completed", Toast.LENGTH_SHORT).show(); }
}

class StockExistsException extends Exception {
    public StockExistsException(String stockName){
        super(stockName + " already exists in database");
    }
}

class InvalidStockException extends Exception {
    public InvalidStockException(String stockName){
        super(stockName + " is invalid");
    }
}

class TooManyStocksException extends Exception {
    public TooManyStocksException() {
        super("There are already 5 stocks in the database, and you can't add more");
    }
}