package behnen.julia.makeyourownadventure;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Julia on 11/13/2015.
 */
public abstract class AbstractDownloadImageTask extends AsyncTask<String, Void, Bitmap> {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Starts the image download process.
     * @param urls The URL of the image.
     * @return A string holding the result of the request.
     */
    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmap = null;
        for (String url : urls) {
            try {
                URL urlObject = new URL(url);
                InputStream is = new BufferedInputStream(urlObject.openStream());
                bitmap = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
//                Toast.makeText(getActivity(), "Unable to download the image, Reason: "
//                        + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return bitmap;
    }

    @Override
    protected abstract void onPostExecute(Bitmap bitmap);
}
