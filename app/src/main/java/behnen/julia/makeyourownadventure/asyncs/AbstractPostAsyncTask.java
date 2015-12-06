package behnen.julia.makeyourownadventure.asyncs;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous POST request.
 *
 * @author Julia Behnen
 * @version November 4, 2015
 */
public abstract class AbstractPostAsyncTask<J, K, L> extends AsyncTask<J, K, L> {

    /**
     * Downloads and returns content from the specified URL using a POST request.
     * @param urlString The URL target of the POST request.
     * @param urlParameters The parameters of the POST request formatted as a GET query (?q=p&r=t)
     * @param tag The tag used for logging output.
     * @return The POST response as a string; null if failure.
     * @throws IOException
     */
    protected String downloadUrl(String urlString, String urlParameters, String tag)
            throws IOException {
        InputStream is = null;

        // Cutoff for characters returned
        int len = 4000;

        // Post request approach adapted from
        // http://stackoverflow.com/questions/4205980/
        // java-sending-http-parameters-via-post-method-easily
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

    /**
     * Reads an InputStream and converts it to a String with a set maximum length.
     * @param stream The stream to be read.
     * @param len The maximum characters in the returned string; the cutoff value
     *            for the amount of the stream that is read.
     * @return A string containing the contents of the input stream up to length len.
     * @throws IOException
     */
    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
}
