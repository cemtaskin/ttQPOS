package ttqrpos.com.ttposmobile;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hugin.common.lib.IntentConsts;
import hugin.common.lib.MessengerConsts;
import hugin.common.lib.d10.BankListResponse;
import hugin.common.lib.d10.ConfigurationResponse;
import hugin.common.lib.d10.DeviceInfoResponse;
import hugin.common.lib.d10.EndOfDayResponse;
import hugin.common.lib.d10.InfoQueryResponse;
import hugin.common.lib.d10.MaintenanceResponse;
import hugin.common.lib.d10.MessageBuilder;
import hugin.common.lib.d10.POSMessage;
import hugin.common.lib.d10.PaymentRequest;
import hugin.common.lib.d10.PaymentResponse;
import hugin.common.lib.d10.PrintRequest;
import hugin.common.lib.d10.PrintResponse;
import hugin.common.lib.d10.SlipCopyResponse;
import ttqrpos.com.ttposmobile.HuginTechPos.FreePrint;
import ttqrpos.com.ttposmobile.HuginTechPos.IIncomingD10MessageListener;
import ttqrpos.com.ttposmobile.HuginTechPos.OrtakPosServis;
import ttqrpos.com.ttposmobile.Models.QrContent;

public class MainActivity extends AppCompatActivity {

    PaymentResponse currentResponse;
    OrtakPosServis ortakPosServis;
    Context context;
    private int HuginN910MessageCount=0;

    int currentPosition=0;
    @BindView(R.id.txtValue)
    EditText txtValue;

    String value="";
    double numberValue=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context=this;
        init();

    }


    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.xml.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.posActs:
                startActivity(new Intent(this,PosActList.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init(){

        ortakPosServis =new OrtakPosServis(this, new IIncomingD10MessageListener() {
            @Override
            public void onResponse(POSMessage resp) {

                if (resp instanceof EndOfDayResponse) {
                    EndOfDayResponse endOfDayResponse =(EndOfDayResponse)resp;
                    Toast.makeText(context,"İŞLEM SONUCU " + endOfDayResponse.getEodResultList().get(0).getErrorCode(),Toast.LENGTH_SHORT).show();
                } else if(resp instanceof SlipCopyResponse) {
                    SlipCopyResponse slipCopyResponse =(SlipCopyResponse)resp;
                    Toast.makeText(context,"İŞLEM SONUCU " + slipCopyResponse.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
                else if (resp instanceof ConfigurationResponse) {
                    ConfigurationResponse configurationResponse = (ConfigurationResponse)resp;
                    Toast.makeText(context,"İŞLEM SONUCU" + configurationResponse.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
                else if (resp instanceof PaymentResponse) {
                    PaymentResponse paymentResponse =(PaymentResponse)resp;

                    if (paymentResponse.getErrorCode()==99){
                        Toast.makeText(context,"Kredi Kartı Tahsilatı Sırasında Hata!",Toast.LENGTH_SHORT).show();
                        return;
                    }else if (paymentResponse.getErrorCode()==0)
                    {
                        currentResponse=paymentResponse;
                        PrintRequest.Builder printRequest = new PrintRequest.Builder(paymentResponse.getSerialNo(),paymentResponse.getMessageNumber() + 1,paymentResponse.getAcquirerId());

                        QrContent qrContent=new QrContent().getFromHuginResponse(paymentResponse);
                        qrContent.save();

                        ortakPosServis.sendD10Message(printRequest.build());
                        //ortakPosServis.printJson(new FreePrint().getPosReceipt(paymentResponse));

                    }
                }
                else if (resp instanceof PrintResponse) {
                    PrintResponse printResponse = (PrintResponse) resp;
                    ortakPosServis.printJson(new FreePrint().getPosReceipt2(currentResponse));

                    //new AsynPrintQrCode().execute(currentResponse);
                    value="0";
                    refreshValue();

                    //ortakPosServis.printJson(new FreePrint().getPosReceipt2(currentResponse));

                    //sendMessage();
                }
                else if (resp instanceof BankListResponse) {
                    BankListResponse bankListResponse = (BankListResponse) resp;
                    if(bankListResponse.getBankAppList().size() > 0){
                        Toast.makeText(context,"BANKA SAYISI " + bankListResponse.getBankAppList().size(),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(context,"İŞLEM SONUCU BANKA YOK",Toast.LENGTH_SHORT).show();
                    }
                }
                else if (resp instanceof InfoQueryResponse) {
                    InfoQueryResponse infoQueryResponse = (InfoQueryResponse) resp;
                    if(infoQueryResponse.getBonusInfoList() != null){
                        Toast.makeText(context, "BONUS SAYISI : " + infoQueryResponse.getBonusInfoList().size(),Toast.LENGTH_SHORT).show();
                        PrintRequest.Builder printRequest = new PrintRequest.Builder(infoQueryResponse.getSerialNo(),infoQueryResponse.getMessageNumber() + 1,0);
                        ortakPosServis.sendD10Message(printRequest.build());
                    }
                    else{
                        Toast.makeText(context, "BONUS SAYISI : " + 0,Toast.LENGTH_SHORT).show();
                    }
                }
                else if (resp instanceof MaintenanceResponse) {
                    MaintenanceResponse maintenanceResponse = (MaintenanceResponse) resp;
                    Toast.makeText(context,"İŞLEM SONUCU"+ maintenanceResponse.getErrorCode(),Toast.LENGTH_SHORT).show();
                }
                else if (resp instanceof DeviceInfoResponse) {
                    DeviceInfoResponse deviceInfoResponse = (DeviceInfoResponse) resp;
                    Toast.makeText(context,
                            "İŞLEM SONUCU"+ deviceInfoResponse.getErrorCode() + "\n" +
                                    "BAGLANTI DURUMU : " + deviceInfoResponse.getNetworkState()+ "\n" +
                                    "KAĞIT DURUMU : " + deviceInfoResponse.getPrinterState()+ "\n" +
                                    "SERİ NO : " + deviceInfoResponse.getSerialNo() ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBarcodeResponse(String barcode) {

            }
        });
    }

    @OnClick({R.id.btn0,R.id.btn1,R.id.btn2,R.id.btn3,R.id.btn4,R.id.btn5,R.id.btn6,R.id.btn7,R.id.btn8,R.id.btn9})
    public void btnNumber (Button b) {
        String preValue = txtValue.getText().toString();

        if (currentPosition == 0 && b.getId() == R.id.btn0) {
            return;
        }

        currentPosition++;
        value+=b.getText().toString();
        refreshValue();
    }

    private void refreshValue(){
        numberValue=Double.valueOf(value)/100;
        txtValue.setText(String.format("%.2f",numberValue));
    }

    @OnClick(R.id.btnCancel)
    public void btnCancel (){
        currentPosition=0;
        value="";
        txtValue.setText("0.00");
    }

    @OnClick(R.id.btnBack)
    public void btnBack (){

        Log.d("BACK","back");
        Log.d("VALUE",value);

        if (currentPosition==0) return;

        currentPosition--;
        if (value.length()==1){
            value="";
        }else
        {
            value=value.substring(0,value.length()-1);
        }

        if (value==""){
            value="0";
        }
        refreshValue();
    }

    @OnClick(R.id.btnOK)
    public void btnOk(){
        if (!isRunningSfa()){
            Toast.makeText(context,"SFA Çalışmıyor. Lütfen TechPos uygulaması başlatın...",Toast.LENGTH_SHORT).show();
            return;
        }

        HuginN910MessageCount++;

        PaymentRequest.Builder builder=new PaymentRequest.Builder("HN7501579940",HuginN910MessageCount,numberValue);
        builder.setAcquirerId(1);
        builder.setIssuerId(1);
        builder.setCurrencyCode(1);
        builder.setDecimalPoint(2);
        builder.setTranType(1);

        ortakPosServis.sendD10Message(builder.build());

    }


    private boolean isRunningSfa() {
        boolean retVal = false;

        ArrayList<String> services = new ArrayList<>();
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                services.add(service.service.getClassName());
            }
        }
        if(services.contains(IntentConsts.NL_FISCAL_SERVICE))
        {
            retVal = true;
        }
        return retVal;
    }

    private class AsynPrintQrCode extends AsyncTask<PaymentResponse,Void,Void>{

        @Override
        protected Void doInBackground(PaymentResponse... paymentResponses) {
            //Log.d("TEST",paymentResponses[0].getRefNo());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


}