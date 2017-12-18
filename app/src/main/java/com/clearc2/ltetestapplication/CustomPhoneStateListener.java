package com.clearc2.ltetestapplication;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;


public class CustomPhoneStateListener extends PhoneStateListener {

    SignalStrengthListener listener;

    public CustomPhoneStateListener(SignalStrengthListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        listener.signalUpdated(signalStrength);
    }
}