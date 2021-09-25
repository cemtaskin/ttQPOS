package ttqrpos.com.ttposmobile.HuginTechPos;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import hugin.common.lib.d10.PaymentResponse;
import ttqrpos.com.ttposmobile.Models.QrContent;

public class FreePrint {
    public String type="TEXT";
    public String value="";
    public FreePrintAttr attr;

    public static String format(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    public static String formatTime(GregorianCalendar calendar) {
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());

        return dateFormatted;
    }

    public FreePrint(String type,String value,FreePrintAttr attr){
        this.type=type;
        this.value=value;
        this.attr=attr;
    }

    public FreePrint(){

    }

    public String getPosReceipt(double amount,String bacthNo){
        List<FreePrint> r=new ArrayList<>();
        /*
        r.add(new FreePrint("TEXT","HUGIN",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 1",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 2",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","ISTANBUL",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","4640507963",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","TEST BANKASI",new FreePrintAttr("center","small",true,"normal",0,0,0)));

        r.add(new FreePrint("TEXT","Tarih : 30.12.2020                   Saat: 11:01",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","ISYERI : 00000000000002              Pos: 1001147",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","Batch No :" + bacthNo + "             L11",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","SN : HGN7501579940     HGN/HN9     Ver : 1.00.90",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","SATIŞ",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
*/

        r.add(new FreePrint("IMAGE","/storage/emulated/0/Android/data/hugin.droid.techpos/files/logos/qr_code.png",new FreePrintAttr("center","normal",true,"bold",0,140,140)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));


        return new Gson().toJson(r);
    }

    public String getPosReceipt(PaymentResponse response){

        String fileName="";
        try {
            fileName=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + response.getRefNo() + ".png";
        }catch(Exception ex){

        }

        String responseContent = "";
        QrContent qrContent=new QrContent().getFromHuginResponse(response);
        responseContent=new Gson().toJson(qrContent);

        Log.d("RESPONSE CONTENT",responseContent);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmp = barcodeEncoder.encodeBitmap(responseContent, BarcodeFormat.QR_CODE, 800, 800);

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(Exception e) {

        }



        List<FreePrint> r=new ArrayList<>();
        //r.add(new FreePrint("IMAGE",fileName,new FreePrintAttr("center","normal",true,"bold",0,140,140)));

        //r.add(new FreePrint("TEXT",response.getRefNo(),new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        //r.add(new FreePrint("TEXT",responseContent,new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        /*
        r.add(new FreePrint("TEXT","HUGIN",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 1",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 2",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","ISTANBUL",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","4640507963",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","TEST BANKASI",new FreePrintAttr("center","small",true,"normal",0,0,0)));

        r.add(new FreePrint("TEXT",String.format("ISYERI NO: %15s    POS NO: %10s",response.getMerchantId(),response.getTerminalId()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("ISLEM NO: %06d                BATCH NO: %06d",response.getMessageNumber(),response.getBatchNo()),new FreePrintAttr("left","small",true,"normal",0,0,0)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String dateString=dateFormat.format(response.getTranDate().getTime());
        String timeString=timeFormat.format(response.getTranDate().getTime());


        r.add(new FreePrint("TEXT",String.format("TARİH: %s                   SAAT: %s",dateString,timeString),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("AID: %14s               10%s",response.getAID(),response.getCardType()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","SATIŞ",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));

        String cardNo = response.getCardNo();
        String cardInfo= cardNo.substring(0,4) + " **** **** " + cardNo.substring(12,15);
        r.add(new FreePrint("TEXT",cardInfo,new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        r.add(new FreePrint("TEXT","YURTİÇİ MASTERCARD KREDİ",new FreePrintAttr("center","normal",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","İŞLEM TUTARI",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("%.2f TL",response.getAmount()),new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        r.add(new FreePrint("TEXT",String.format("ONAY KODU: %6s    RRN: %12s",response.getAuthCode(),response.getRefNo()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","TUTAR KARŞILIĞI MAL VEYA HİZMET ALDIM",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","MALİ DEĞERİ YOKTUR",new FreePrintAttr("center","small",true,"normal",0,0,0)));


        */

        return new Gson().toJson(r);
    }
    public String getPosReceipt2(PaymentResponse response){

        String fileName="";
        try {
            fileName=Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + response.getRefNo() + ".jpg";
        }catch(Exception ex){

        }

        String responseContent = "";

        responseContent=String.format("%s-%s-%s-%.2f",response.getRefNo(),response.getTerminalId(),response.getAuthCode(),response.getAmount());

        Log.d("RESPONSE CONTENT",responseContent);


        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bmp = barcodeEncoder.encodeBitmap(responseContent, BarcodeFormat.QR_CODE, 250, 250);

            try (FileOutputStream out = new FileOutputStream(fileName)) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch(Exception e) {

        }


        //Log.d("QR",fileName);
        List<FreePrint> r=new ArrayList<>();
        r.add(new FreePrint("IMAGE",fileName,new FreePrintAttr("center","normal",true,"bold",0,280,280)));

        //r.add(new FreePrint("TEXT",response.getRefNo(),new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        //r.add(new FreePrint("TEXT",responseContent,new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        /*
        r.add(new FreePrint("TEXT","HUGIN",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 1",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","HUGIN TEST 2",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","ISTANBUL",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","4640507963",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","TEST BANKASI",new FreePrintAttr("center","small",true,"normal",0,0,0)));

        r.add(new FreePrint("TEXT",String.format("ISYERI NO: %15s    POS NO: %10s",response.getMerchantId(),response.getTerminalId()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("ISLEM NO: %06d                BATCH NO: %06d",response.getMessageNumber(),response.getBatchNo()),new FreePrintAttr("left","small",true,"normal",0,0,0)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        String dateString=dateFormat.format(response.getTranDate().getTime());
        String timeString=timeFormat.format(response.getTranDate().getTime());


        r.add(new FreePrint("TEXT",String.format("TARİH: %s                   SAAT: %s",dateString,timeString),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("AID: %14s               10%s",response.getAID(),response.getCardType()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","SATIŞ",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","",new FreePrintAttr("left","small",true,"normal",0,0,0)));

        String cardNo = response.getCardNo();
        String cardInfo= cardNo.substring(0,4) + " **** **** " + cardNo.substring(12,15);
        r.add(new FreePrint("TEXT",cardInfo,new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        r.add(new FreePrint("TEXT","YURTİÇİ MASTERCARD KREDİ",new FreePrintAttr("center","normal",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","İŞLEM TUTARI",new FreePrintAttr("center","normal",true,"bold",0,0,0)));
        r.add(new FreePrint("TEXT",String.format("%.2f TL",response.getAmount()),new FreePrintAttr("center","normal",true,"bold",0,0,0)));

        r.add(new FreePrint("TEXT",String.format("ONAY KODU: %6s    RRN: %12s",response.getAuthCode(),response.getRefNo()),new FreePrintAttr("left","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","TUTAR KARŞILIĞI MAL VEYA HİZMET ALDIM",new FreePrintAttr("center","small",true,"normal",0,0,0)));
        r.add(new FreePrint("TEXT","MALİ DEĞERİ YOKTUR",new FreePrintAttr("center","small",true,"normal",0,0,0)));


        */

        return new Gson().toJson(r);
    }
}