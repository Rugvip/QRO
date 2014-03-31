package rugvip.glass.qro.qr;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

public abstract class Qr {
    private ResultPoint[] points;

    protected Qr(Result result) {
        points = result.getResultPoints();
    }

    public void update(Result result) {
        points = result.getResultPoints();
    }

    public ResultPoint bottom() {
        return points[0];
    }

    public ResultPoint middle() {
        return points[1];
    }

    public ResultPoint right() {
        return points[2];
    }
}
