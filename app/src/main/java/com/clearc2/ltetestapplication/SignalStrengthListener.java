package com.clearc2.ltetestapplication;

import android.telephony.SignalStrength;

public interface SignalStrengthListener {
    void signalUpdated(SignalStrength signalStrength);
}