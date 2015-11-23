package behnen.julia.makeyourownadventure.asyncs;

import android.graphics.Bitmap;
import android.os.AsyncTask;

/**
 * Created by Julia on 11/13/2015.
 */
public abstract class AbstractDownloadImageTask extends AsyncTask<String, Void, Bitmap> {




    @Override
    protected abstract void onPostExecute(Bitmap bitmap);
}
