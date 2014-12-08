package wazza.ke.servista.utilities;

/**
 * Created by kelli on 10/15/14.
 */

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This class will handle all web service call form this android application.
 */
public class MoodleWS_Engine {

    private final static String EXP_TAG = "MWS_EXCEPTION";
    private final static String ERR_TAG = "moodle_ws.error";
    private final static String INFO_TAG = "MWS_INFO";

    //check if internet connection is available
    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.i(INFO_TAG, "Device connected to internet");
            return true;
        } else
            Log.e(ERR_TAG,"No internet connection");
            return false;

    }

    /**
     * Create url for any web service method in the application
     *
     * @param token
     * @param user
     * @param password
     * @param functionName
     * @param map
     * @return the created url
     */
    /**
     * Create a reusable inputstream for all web service connections
     *
     * @param functionUrl
     * @return inputsream
     */
    public static InputStream getWSMethodConnection(String functionUrl) {

        InputStream inputStream = null;
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(functionUrl);
        try {
            HttpResponse httpResponse = client.execute(get);
            inputStream = httpResponse.getEntity().getContent();
        } catch (ClientProtocolException cpre) {
            Log.d(MoodleWS_Engine.EXP_TAG, cpre.getMessage());
            cpre.printStackTrace();
        } catch (IOException io) {
            Log.d(MoodleWS_Engine.EXP_TAG, io.getMessage());
            io.printStackTrace();
        }
        Log.i(INFO_TAG, "Moodle Webservice connection is OK");
        return inputStream;
    }

    public static String getStreamContent(InputStream in) {

        String results = "";
        String liner;
        BufferedReader reader;
        try {

            reader = new BufferedReader(new InputStreamReader(in));

            while ((liner = reader.readLine()) != null) {
                results += liner;

            }
        } catch (IOException ioe) {
            Log.d(MoodleWS_Engine.EXP_TAG, ioe.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d(MoodleWS_Engine.EXP_TAG, e.getMessage());
                }
            }
        }
        Log.i(INFO_TAG, String.valueOf(results.length()));
        Log.i(INFO_TAG, "Reading stream content OK");
        return results;
    }
}
