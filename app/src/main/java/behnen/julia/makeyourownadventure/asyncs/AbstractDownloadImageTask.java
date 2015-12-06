package behnen.julia.makeyourownadventure.asyncs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Downloads an image from the shared image folder for this app.
 *
 * @author Julia Behnen
 * @version December 6, 2015
 */
public abstract class AbstractDownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    /**
     * The URL for story element download requests from the shared image directory.
     */
    private static final String SHARED_IMAGES_URL =
            "http://cssgate.insttech.washington.edu/~jbehnen/myoa/images/shared/";

    /**
     * Starts the image download process.
     * @param urls The URL of the image.
     * @return A bitmap holding the result of the request.
     */
    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        for (String url : urls) {
            try {
                URL urlObject = new URL(SHARED_IMAGES_URL + url);
                InputStream is = new BufferedInputStream(urlObject.openStream());
                bitmap = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    @Override
    protected abstract void onPostExecute(Bitmap bitmap);
}
