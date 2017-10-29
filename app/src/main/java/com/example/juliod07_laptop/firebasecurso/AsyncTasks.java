package com.example.juliod07_laptop.firebasecurso;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JulioD07-LAPTOP on 8/29/2017.
 */

public class AsyncTasks extends AsyncTask<Void, Integer, Boolean> {


    @Override
    protected Boolean doInBackground(Void... params) {

        boolean success = false;
        try {
            URL url = new URL("https://google.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.connect();
            success = connection.getResponseCode() == 200;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;

    }


    protected void onPreExecute(Boolean success, Context ctx) {
        super.onPreExecute();
        if (success == false) {
            Toast.makeText(ctx, "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
        }
    }


    protected void onPostExecute(Boolean success, Context ctx) {
        super.onPostExecute(success);
        if (success == false) {
            Toast.makeText(ctx, "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }


}
