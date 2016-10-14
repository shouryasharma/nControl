package nemi.in;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import printing.DrawerService;
import printing.Global;

/**
 * Created by Aman on 7/2/2016.
 */

class PrinterBluetoothHandler extends Handler {

    WeakReference<Context> mActivity;


    PrinterBluetoothHandler(Context activity) {
        mActivity = new WeakReference<Context>(activity);
    }


    @Override
    public void handleMessage(Message msg) {
        Context theActivity = mActivity.get();


        switch (msg.what) {
            case Global.MSG_WORKTHREAD_SEND_CONNECTBTRESULT: {

                int result = msg.arg1;


                if (result == 1) {

                    byte[] buf = FragmentPOS.buf;

                    if (DrawerService.workThread.isConnected() && buf != null) {

                        Bundle data = new Bundle();
                        data.putByteArray(Global.BYTESPARA1, buf);
                        data.putInt(Global.INTPARA1, 0);

                        data.putInt(Global.INTPARA2, buf.length);
                        DrawerService.workThread.handleCmd(Global.CMD_POS_WRITE, data);


                    } else if (DrawerService.workThread.isConnected()) {
                        Toast.makeText(theActivity, "Printer Connected", Toast.LENGTH_SHORT).show();
                        try {
                        FragmentPOS harry = new FragmentPOS();
                        harry.harry();
                        } catch(Exception e) {
                            Log.v("Bluetooth: ", String.valueOf(e));
                        }
                    } else {
                        Toast.makeText(theActivity, "Printer Connection Failed", Toast.LENGTH_SHORT).show();
                        try {
                        FragmentPOS harry = new FragmentPOS();
                        harry.harry();
                        } catch(Exception e) {}
                    }

                }

                if (result == 0) {
//                    FragmentPOS.disblePayButton();

                    Toast.makeText(theActivity, "Please connect to the printer", Toast.LENGTH_SHORT).show();
                }
                FragmentPOS.buf = null;
                break;
            }

        }
    }
}