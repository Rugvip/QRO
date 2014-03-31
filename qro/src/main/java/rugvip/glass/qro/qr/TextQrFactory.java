package rugvip.glass.qro.qr;

import com.google.zxing.Result;

public class TextQrFactory implements AbstractQrFactory {
    @Override
    public Qr maybeCreate(Result result) {
        return new TextQr(result);
    }
}
