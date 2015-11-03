package behnen.julia.makeyourownadventure.support;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Julia on 11/1/2015.
 */
public abstract class AbstractPostAsyncTask<J, K, L> extends AsyncTask<J, K, L> {
    protected String downloadUrl(String urlString, String urlParameters, String tag) throws IOException {
        InputStream is = null;

        int len = 4000;

        // Post request approach adapted from
        // http://stackoverflow.com/questions/4205980/java-sending-http-parameters-via-post-method-easily
        try {
            URL url = new URL(urlString);
            byte[] postData = urlParameters.getBytes();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setDoInput(true);
            conn.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            conn.connect();

            int response = conn.getResponseCode();
            Log.d(tag, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            Log.d(tag, "The string is: " + contentAsString);
            return contentAsString;
        } catch(Exception e) {
            Log.d(tag, "Something happened: " + e.getMessage());
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return null;
    }

    // Reads an InputStream and converts it to a String
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
