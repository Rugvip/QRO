package rugvip.glass.qro.qr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.google.zxing.Result;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageQr extends Qr {
    private final URL url;
    private Bitmap bitmap;

    public ImageQr(Result result, URL url) {
        super(result);

        this.url = url;

        new BitmapFetchTask(url) {
            @Override
            protected void onPostExecute(Bitmap downloadedBitmap) {
                bitmap = downloadedBitmap;
            }
        }.execute();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return url.toString();
    }

    private static class BitmapFetchTask extends AsyncTask<Void, Void, Bitmap> {
        private final URL url;

        private BitmapFetchTask(URL url) {
            this.url = url;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                BufferedInputStream in = new BufferedInputStream((InputStream) url.getContent(), 80192);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                int len;

                byte[] buffer = new byte[4096];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.close();
                in.close();
                byte[] data = out.toByteArray();
                return BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
