package ttqrpos.com.ttposmobile.HuginTechPos;

import hugin.common.lib.d10.POSMessage;

public interface IIncomingD10MessageListener {
    void onResponse(POSMessage posMessage);
    void onBarcodeResponse(String barcode);
}
