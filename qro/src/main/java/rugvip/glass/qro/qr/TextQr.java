package rugvip.glass.qro.qr;

import com.google.zxing.Result;

public class TextQr extends Qr {
    private final String text;

    public TextQr(Result result) {
        super(result);
        this.text = result.getText();
    }

    public String getText() {
        return text;
    }
}
