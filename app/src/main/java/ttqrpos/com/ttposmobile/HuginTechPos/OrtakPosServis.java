package ttqrpos.com.ttposmobile.HuginTechPos;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.NonNull;

import hugin.common.lib.IntentConsts;
import hugin.common.lib.MessengerConsts;
import hugin.common.lib.d10.BankListResponse;
import hugin.common.lib.d10.ConfigurationResponse;
import hugin.common.lib.d10.DeviceInfoResponse;
import hugin.common.lib.d10.EndOfDayResponse;
import hugin.common.lib.d10.InfoQueryResponse;
import hugin.common.lib.d10.MaintenanceResponse;
import hugin.common.lib.d10.MessageBuilder;
import hugin.common.lib.d10.MessageConstants;
import hugin.common.lib.d10.MessageTypes;
import hugin.common.lib.d10.POSMessage;
import hugin.common.lib.d10.PaymentResponse;
import hugin.common.lib.d10.PrintResponse;
import hugin.common.lib.d10.SlipCopyResponse;

public class OrtakPosServis{

    private Activity activity;
    private boolean bound = false;
    private Messenger serviceMessenger = null;
    private final Messenger clientMessenger;
    private ServiceConnection fiscalConn;
    private Runnable runOnConnect = null;

    public interface IServiceListener {
        void onConnected();
        void onDisconnected();
    }

    private static class IncomingHandler extends Handler {

        IIncomingD10MessageListener listener;

        // TODO add interface to update ui
        IncomingHandler(IIncomingD10MessageListener listener) {
            this.listener = listener;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            Bundle data = msg.getData();
            switch (msg.what) {
                case MessengerConsts.ACTION_SCAN_MESSAGE:

                    int errorCode = data.getInt(IntentConsts.EXTRA_ERROR_CODE);
                    String barcode = "";
                    if (errorCode == 0) {
                        barcode = data.getString(IntentConsts.EXTRA_SCAN_RESULT_MESSAGE);
                        listener.onBarcodeResponse(barcode);
                    }
                    listener.onBarcodeResponse("0");
                    break;

                case MessengerConsts.ACTION_D10_MESSAGE:

                    byte[] responseMes = data.getByteArray(IntentConsts.EXTRA_D10_MESSAGE);
                    int messageType = findD10MessageType(responseMes);
                    POSMessage parsedResponse;
                    switch(messageType) {
                        case MessageTypes.RESP_ENDOFDAY:
                            parsedResponse = new EndOfDayResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                        case MessageTypes.RESP_CONFIG:
                            parsedResponse = new ConfigurationResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                        case MessageTypes.RESP_PAYMENT:
                            parsedResponse = new PaymentResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                        case MessageTypes.RESP_SLIP_COPY:
                            SlipCopyResponse parsedSlipCopyResp = new SlipCopyResponse.Builder(responseMes).build();
                            listener.onResponse(parsedSlipCopyResp);
                            break;
                        case MessageTypes.RESP_PRINT:
                            PrintResponse printResponse = new PrintResponse.Builder(responseMes).build();
                            listener.onResponse(printResponse);
                            break;
                        case MessageTypes.RESP_BANK_LIST:
                            BankListResponse bankListResponse = new BankListResponse.Builder(responseMes).build();
                            listener.onResponse(bankListResponse);
                            break;
                        case MessageTypes.RESP_INFO_QUERY:
                            parsedResponse = new InfoQueryResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                        case MessageTypes.RESP_MAINTENANCE:
                            parsedResponse = new MaintenanceResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                        case MessageTypes.RESP_DEVICE_INFO:
                            parsedResponse = new DeviceInfoResponse.Builder(responseMes).build();
                            listener.onResponse(parsedResponse);
                            break;
                    }
            }
        }

        private int findD10MessageType(byte [] bytes) {
            int index = 3;
            index+= MessageConstants.LEN_SERIAL;
            int messageType = MessageBuilder.byteArrayToHex(bytes, index, 3);

            return messageType;
        }
    }

    public OrtakPosServis( Activity activity,IIncomingD10MessageListener listener) {
        this.activity = activity;
        this.clientMessenger = new Messenger(new IncomingHandler(listener));
        fiscalConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("FiscalConn","Service Connected");
                serviceMessenger = new Messenger(service);
                bound = true;
                if (runOnConnect != null) {
                    runOnConnect.run();
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("FiscalConn","Service Disconnected");
                serviceMessenger = null;
                bound = false;
            }
        };

        bindService(null);
    }



    public void sendScanRequest (){
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_SCAN_MESSAGE, 0, 0);
            Bundle data = new Bundle();
            data.putInt(IntentConsts.EXTRA_SCAN_TYPE, 0);
            msg.setData(data);
            try {
                msg.replyTo = clientMessenger;
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }
    }

    public void printJson (String json){
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_PRINT_FREE_FORMAT, 0, 0);
            Bundle data = new Bundle();
            data.putString(IntentConsts.EXTRA_JSON_CONTENT, json);
            msg.setData(data);
            try {
                msg.replyTo = clientMessenger;
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }

    }


    public void sendScanRequest2 (){
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_SCAN_MESSAGE, 0, 0);
            Bundle data = new Bundle();
            data.putInt(IntentConsts.EXTRA_SCAN_TYPE, 1);
            msg.setData(data);
            try {
                msg.replyTo = clientMessenger;
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }
    }

    /**
     * Method to send D10 protocol messages to service
     *
     * @param posMessage abstract POS message class, other public
     *                   methods can type check or take related
     *                   parameters and call this method to send D10
     *                   messages to service. (e.g. public void sendPaymentRequest(PaymentRequest payReq))
     */

    public void sendD10Message(POSMessage posMessage) {
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_D10_MESSAGE, 0, 0);
            Bundle data = new Bundle();
            data.putByteArray("D10", posMessage.getMessage());
            msg.setData(data);
            try {
                msg.replyTo = clientMessenger;
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }
    }

    /*N910 için başka

    public void isSfaReady(IIncomingMessageListener incomingHandler){
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_SFA_READY, 0, 0);
            Bundle data = new Bundle();
            msg.setData(data);
            try {
                msg.replyTo = new Messenger(new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        incomingHandler.onResponse(msg);
                    }
                });
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }
    }

     */

    ///- N710 için  ne olacak belli değil..

    public void isSfaReady(IIncomingMessageListener incomingHandler){
        Runnable runnable = () -> {
            Message msg = Message.obtain(null, MessengerConsts.ACTION_TERMINAL_INFO, 0, 0);
            Bundle data = new Bundle();
            msg.setData(data);
            try {
                msg.replyTo = new Messenger(new Handler(){
                    @Override
                    public void handleMessage(@NonNull Message msg) {
                        incomingHandler.onResponse(msg);
                    }
                });
                serviceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        };
        if (bound) {
            // Create and send a message to the service, using a supported 'what' value
            runnable.run();
        } else {
            bindService(runnable);
        }
    }


    private void bindService(Runnable runnable) {
        if (activity != null) {
            Intent intent = new Intent()
                    .setComponent(new ComponentName(IntentConsts.NL_FISCAL_PACKAGE, IntentConsts.NL_FISCAL_SERVICE))
                    .putExtra(IntentConsts.ORIGIN_TAG, IntentConsts.ORIGIN_HUGIN_SDK);
            activity.bindService(intent, fiscalConn, Context.BIND_AUTO_CREATE);
            runOnConnect = runnable;
        }
    }

    public void unbindService() {
        if (bound) {
            activity.unbindService(fiscalConn);
            activity = null;
        }
    }

}
