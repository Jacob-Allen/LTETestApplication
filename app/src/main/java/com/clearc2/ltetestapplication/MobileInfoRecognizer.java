package com.clearc2.ltetestapplication;

import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;

public class MobileInfoRecognizer {
    public String getCellInfo(CellInfo cellInfo) {
        String additional_info;
        if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
            additional_info = "GSM CELL INFO FOUND: \n"
            + "GSM CELL IDENTITY: " + cellIdentityGsm.getCid() + "\n";
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
            additional_info = "LTE CELL INFO FOUND: \n"
            + "LTE CELL IDENTITY " + cellIdentityLte.getCi() + "\n"
            + "LTE MOBILE COUNTRY CODE " + cellIdentityLte.getMcc() + "\n"
            + "LTE MOBILE NETWORK CODE " + cellIdentityLte.getMnc() + "\n"
            + "LTE PHYSICAL CELL " + cellIdentityLte.getPci() + "\n"
            + "LTE TRACKING AREA CODE " + cellIdentityLte.getTac() + "\n";
        } else if (cellInfo instanceof CellInfoWcdma){
            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfo;
            CellIdentityWcdma cellIdentityWcdma = cellInfoWcdma.getCellIdentity();
            additional_info = "WCDMA CELL INFO FOUND: \n"
            + "CELL IDENTITY " + cellIdentityWcdma.getCid() + "\n";
        } else {
            additional_info = "ADDITIONAL CELL INFO FOUND BUT IT WAS NOT GSM, LTE, OR WCDMA";
        }
        return additional_info;
    }
}
