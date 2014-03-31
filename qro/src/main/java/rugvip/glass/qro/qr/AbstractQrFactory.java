package rugvip.glass.qro.qr;

import com.google.zxing.Result;

public interface AbstractQrFactory {
    public Qr maybeCreate(Result result);
}
