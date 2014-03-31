package rugvip.glass.qro.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QrDetector {
    private List<AbstractQrFactory> factories;
    private final Map<String, Qr> codes = new HashMap<String, Qr>();
    private final Set<QrDetectionListener> detectionListeners = new HashSet<QrDetectionListener>();
    private final Set<QrMotionListener> motionListeners = new HashSet<QrMotionListener>();

    public QrDetector(AbstractQrFactory... factories) {
        this.factories = Arrays.asList(factories);
    }

    public void addDetectionListener(QrDetectionListener listener) {
        detectionListeners.add(listener);
    }

    public void addMotionListener(QrMotionListener listener) {
        motionListeners.add(listener);
    }

    public void removeDetectionListener(QrDetectionListener listener) {
        detectionListeners.remove(listener);
    }

    public void removeMotionListener(QrMotionListener listener) {
        motionListeners.remove(listener);
    }

    private void notifyDetectionListeners(Qr code) {
        for (QrDetectionListener listener : detectionListeners) {
            listener.onQrDetected(code);
        }
    }

    private void notifyMotionListeners(Qr code) {
        for (QrMotionListener listener : motionListeners) {
            listener.onQrMoved(code);
        }
    }

    private static final Map<DecodeHintType,Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
    {
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.QR_CODE));
        hints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
    }

    public void consume(byte[] data, int width, int height) {
        LuminanceSource source = new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        QRCodeMultiReader multiReader = new QRCodeMultiReader();

        Result[] results = null;

        try {
            results = multiReader.decodeMultiple(new BinaryBitmap(new HybridBinarizer(source)), hints);
        } catch (NotFoundException ignored) {
        }

        if (results != null) {
            for (Result result : results) {
                handleResult(result);
            }
        }
    }

    private void handleResult(Result result) {
        Qr code = codes.get(result.getText());

        if (code == null) {
            code = createCode(result);
            codes.put(result.getText(), code);

            notifyDetectionListeners(code);
        } else {
            code.update(result);
            notifyMotionListeners(code);
        }
    }

    private Qr createCode(Result result) {
        for (AbstractQrFactory factory : factories) {
            Qr code = factory.maybeCreate(result);

            if (code != null) {
                return code;
            }
        }

        throw new RuntimeException("no suitable factory found");
    }
}
