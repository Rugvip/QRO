package rugvip.glass.qro.qr;

import com.google.zxing.Result;

import java.net.MalformedURLException;
import java.net.URL;

public class ImageQrFactory implements AbstractQrFactory {
    public Qr maybeCreate(Result result) {
        URL url = null;

        try {
            url = new URL(result.getText());
        } catch (MalformedURLException e) {
            return null;
        }

        return new ImageQr(result, url);
    }
}
