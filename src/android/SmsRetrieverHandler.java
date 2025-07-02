package com.outsystems.smsretriever;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

/**
 * Created by pcamilo on 10/10/2019
 */
public class SmsRetrieverHandler {

    private Activity activity;
    private static String TAG = SmsRetrieverHandler.class.getSimpleName();

    public SmsRetrieverHandler(Activity activity) {
        TAG = this.getClass().getSimpleName();
        this.activity = activity;
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return mSmsBroadcastReceiver;
    }

    void startBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        this.activity.registerReceiver(mSmsBroadcastReceiver, intentFilter);
    }

    protected OtpReceivedInterface<String> onOtpReceived;

    public void setOtpReceivedCallback(OtpReceivedInterface<String> callback) {
        onOtpReceived = callback;
    }

    private BroadcastReceiver mSmsBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: ");
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                if (extras == null) {
                    Log.e(TAG, "extras es null");
                    return;
                }
                Status mStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (mStatus == null) {
                    Log.e(TAG, "Status es null");
                    return;
                }

                switch (mStatus.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents'
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    if (message != null) {
                        Log.d(TAG, "SMS message: " + message);
                        if (onOtpReceived != null) {
                            try {
                                String otpMessage = message.replace("<#> Your otp code is: ", "");
                                String[] lines = otpMessage.split("\n");
                                String otp = lines.length > 0 ? lines[0] : "";
                                onOtpReceived.onOtpReceived(otp);
                            } catch (Exception e) {
                                Log.e(TAG, "Error extracting OTP: " + e.getMessage());
                                onOtpReceived.onOtpReceived(""); // o manejar el error de otra forma
                            }
                        }
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    // Waiting for SMS timed out (5 minutes)
                    Log.d(TAG, "onReceive: failure");
                    if (onOtpReceived != null) {
                        onOtpReceived.onOtpTimeout();
                    }
                    break;
                }
            }
        }

        @Override
        public void onPause(boolean multitasking) {
            super.onPause(multitasking);
            try {
                this.cordova.getActivity().unregisterReceiver(smsRetrieverHandler.getBroadcastReceiver());
                Log.d("SmsRetrieverPlugin", "BroadcastReceiver desregistrado en onPause");
            } catch (IllegalArgumentException e) {
                Log.w("SmsRetrieverPlugin", "Receiver ya estaba desregistrado o nunca fue registrado");
            }
        }
    };
}
