package com.android.geolocalization;

/**
 * Created by Farcem on 05-Jun-16.
 */

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class Server extends AsyncTask<String, Void, JSONArray>
{
    String charset = "UTF-8";
    Handler h = new Handler(Looper.getMainLooper());

    protected JSONArray doInBackground(String... pUrl)
    {
        try
        {
            URLConnection connection = new URL(pUrl[0]).openConnection();
            connection.setDoOutput(true); // Triggers POST.
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output = connection.getOutputStream();
            output.write(pUrl[1].getBytes(charset));

            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String responseJSON = scanner.useDelimiter("\\n").next();
            return new JSONArray(responseJSON);
        }
        catch (final MalformedURLException e)
        {
            h.post(new Runnable()
            {
                public void run()
                {
                    e.printStackTrace();
                }
            });
        }
        catch (final IOException e)
        {
            h.post(new Runnable()
            {
                public void run()
                {
                    e.printStackTrace();
                }
            });
        } catch (final JSONException e)
        {
            h.post(new Runnable()
            {
                public void run()
                {
                    e.printStackTrace();
                }
            });
        }
        return null;
    }
}