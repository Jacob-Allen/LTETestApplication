package com.clearc2.ltetestapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Context;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.speech.SpeechRecognizer;

import android.telephony.CellInfo;
import android.telephony.TelephonyManager;
import android.telephony.PhoneStateListener;
import android.telephony.CellIdentityCdma;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthLte;
import android.telephony.SignalStrength;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SignalStrengthListener {

    TelephonyManager tm;
    CustomPhoneStateListener phoneStateListener;
    public int MY_LOCATION_PERMISSION = 0;
    MobileInfoRecognizer mobileInfoRecognizer;
    List<CellInfo> cellInfos;
    CellInfoCdma cellInfoCdma;
    CellInfoLte cellInfoLte;
    CellInfoGsm cellInfoGsm;
    CellInfoWcdma cellInfoWcdma;
    TextView speechText;
    TextView cellInfoText;
    TextView radioText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mobileInfoRecognizer = new MobileInfoRecognizer();
        String speechAvailable = "Speech Recogition Available? " + Boolean.toString(SpeechRecognizer.isRecognitionAvailable(this));
        Log.d("JALLEN/Debug", speechAvailable);
        speechText = findViewById(R.id.JALLEN_SPEECH);
        speechText.setText(speechAvailable);

        cellInfoText = findViewById(R.id.JALLEN_CELLINFO);
        radioText = findViewById(R.id.JALLEN_RADIO);
        cellInfoText.setText("Waiting For Cell Info...");
        radioText.setText("Waiting For Radio Data...");
        this.phoneStateListener = new CustomPhoneStateListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_PERMISSION);
        } else {
            checkCellInfo();
            startRadioListen();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults[0] == -1) {
            Log.w("JALLEN/BAD", "Permission was denied. Without location permission LTE data is not available.");
        } else {
            startRadioListen();
            checkCellInfo();
        }
    }

    public void checkCellInfo() {

        String cellInfoString = "";

        if (tm != null) {
            cellInfos = tm.getAllCellInfo();
        } else {
            cellInfos = new ArrayList<>();
        }

        if (cellInfos != null) {
            Collections.reverse(cellInfos);

            for(CellInfo i : cellInfos) {
                if (i instanceof CellInfoLte) {cellInfoLte = (CellInfoLte) i;}
                else if (i instanceof CellInfoCdma) cellInfoCdma = (CellInfoCdma) i;
                else if (i instanceof CellInfoGsm) cellInfoGsm = (CellInfoGsm) i;
                else if (i instanceof CellInfoWcdma) cellInfoWcdma = (CellInfoWcdma) i;
            }

            if (cellInfos.size() > 0) {
                Log.d("JALLEN/CELLINFO", "vvvvvvvvvvvvvvvv START OF CELL INFO vvvvvvvvvvvvvvvv");
                for (int i = 0; i < cellInfos.size(); i++) {
                    if (mobileInfoRecognizer.getCellInfo(cellInfos.get(i)) != null) {
                        Log.d("JALLEN/CELLINFO", mobileInfoRecognizer.getCellInfo(cellInfos.get(i)));
                        cellInfoString = cellInfoString + mobileInfoRecognizer.getCellInfo(cellInfos.get(i)) + "\n\n";
                    }
                }
                if (cellInfoLte != null) {
                    CellIdentityLte lteIdentity = cellInfoLte.getCellIdentity();
                    int mcc = (lteIdentity.getMcc() == Integer.MAX_VALUE) ? -1 : lteIdentity.getMcc();
                    int mnc = (lteIdentity.getMnc() == Integer.MAX_VALUE) ? -1 : lteIdentity.getMnc();
                    int pci = (lteIdentity.getPci() == Integer.MAX_VALUE) ? -1 : lteIdentity.getPci();
                    int tac = (lteIdentity.getTac() == Integer.MAX_VALUE) ? -1 : lteIdentity.getTac();
                    int ci = (lteIdentity.getCi() == Integer.MAX_VALUE) ? -1 : lteIdentity.getCi();
                    int sector = (ci == -1) ? -1 : (ci & 0xFF);
                    int CI = ci >> 8;
                    CI &= 0xFFFFFF;
                    int market = (ci == -1) ? -1 : CI / 1000;
                    int enodeb = (ci == -1) ? -1 : CI % 1000;
                    Log.d("JALLEN/CELLINFO", "LTE MCC: " + mcc);
                    Log.d("JALLEN/CELLINFO", "LTE MNC: " + mnc);
                    Log.d("JALLEN/CELLINFO", "LTE PCI: " + pci);
                    Log.d("JALLEN/CELLINFO", "LTE TAC: " + tac);
                    Log.d("JALLEN/CELLINFO", "LTE CI: " + ci);
                    Log.d("JALLEN/CELLINFO", "LTE SECTOR: " + sector);
                    Log.d("JALLEN/CELLINFO", "LTE MARKET: " + market);
                    Log.d("JALLEN/CELLINFO", "LTE ENODEB: " + enodeb);
                    cellInfoString = cellInfoString + "LTE MCC: " + mcc + "\n";
                    cellInfoString = cellInfoString + "LTE MNC: " + mnc + "\n";
                    cellInfoString = cellInfoString + "LTE PCI: " + pci + "\n";
                    cellInfoString = cellInfoString + "LTE TAC: " + tac + "\n";
                    cellInfoString = cellInfoString + "LTE CI: " + ci + "\n";
                    cellInfoString = cellInfoString + "LTE SECTOR: " + sector + "\n";
                    cellInfoString = cellInfoString + "LTE MARKET: " + market + "\n";
                    cellInfoString = cellInfoString + "LTE ENODEB: " + enodeb + "\n";
                }
                if (cellInfoCdma != null) {
                    CellIdentityCdma cdmaIdentity = cellInfoCdma.getCellIdentity();
                    Log.d("JALLEN/CELLINFO", "CDMA BSID: " + cdmaIdentity.getBasestationId());
                    Log.d("JALLEN/CELLINFO", "CDMA SID: " + cdmaIdentity.getSystemId());
                    Log.d("JALLEN/CELLINFO", "CDMA NID: " + cdmaIdentity.getNetworkId());
                    cellInfoString = cellInfoString + "\n";
                    cellInfoString = cellInfoString + "CDMA BSID: " + cdmaIdentity.getBasestationId() + "\n";
                    cellInfoString = cellInfoString + "CDMA SID: " + cdmaIdentity.getSystemId() + "\n";
                    cellInfoString = cellInfoString + "CDMA NID: " + cdmaIdentity.getNetworkId() + "\n";
                }
                if (cellInfoGsm != null) {
                    CellIdentityGsm gsmIdentity = cellInfoGsm.getCellIdentity();
//                    Log.d("JALLEN/CELLINFO", "GSM ARFCN: " + gsmIdentity.getArfcn());
//                    Log.d("JALLEN/CELLINFO", "GSM BSIC: " + gsmIdentity.getBsic());
                    Log.d("JALLEN/CELLINFO", "GSM CID: " + gsmIdentity.getCid());
                    Log.d("JALLEN/CELLINFO", "GSM LAC: " + gsmIdentity.getLac());
                    Log.d("JALLEN/CELLINFO", "GSM MCC: " + gsmIdentity.getMcc());
                    Log.d("JALLEN/CELLINFO", "GSM MNC: " + gsmIdentity.getMnc());
                    cellInfoString = cellInfoString + "\n";
                    cellInfoString = cellInfoString + "GSM CID: " + gsmIdentity.getCid() + "\n";
                    cellInfoString = cellInfoString + "GSM LAC: " + gsmIdentity.getLac() + "\n";
                    cellInfoString = cellInfoString + "GSM MCC: " + gsmIdentity.getMcc() + "\n";
                    cellInfoString = cellInfoString + "GSM MNC: " + gsmIdentity.getMnc() + "\n";
                }
                if (cellInfoWcdma != null) {
                    CellIdentityWcdma wcdmaIdentity = cellInfoWcdma.getCellIdentity();
                    Log.d("JALLEN/CELLINFO", "WCDMA CID: " + wcdmaIdentity.getCid());
                    Log.d("JALLEN/CELLINFO", "WCDMA LAC: " + wcdmaIdentity.getLac());
                    Log.d("JALLEN/CELLINFO", "WCDMA MCC: " + wcdmaIdentity.getMcc());
                    Log.d("JALLEN/CELLINFO", "WCDMA MNC: " + wcdmaIdentity.getMnc());
                    Log.d("JALLEN/CELLINFO", "WCDMA PSC: " + wcdmaIdentity.getPsc());
//                    Log.d("JALLEN/CELLINFO", "WCDMA UARFCN: " + wcdmaIdentity.getUarfcn());
                    cellInfoString = cellInfoString + "\n";
                    cellInfoString = cellInfoString + "WCDMA CID: " + wcdmaIdentity.getCid() + "\n";
                    cellInfoString = cellInfoString + "WCDMA LAC: " + wcdmaIdentity.getLac() + "\n";
                    cellInfoString = cellInfoString + "WCDMA MCC: " + wcdmaIdentity.getMcc() + "\n";
                    cellInfoString = cellInfoString + "WCDMA MNC: " + wcdmaIdentity.getMnc() + "\n";
                    cellInfoString = cellInfoString + "WCDMA PSC: " + wcdmaIdentity.getPsc() + "\n";
                }
                Log.d("JALLEN/CELLINFO", "^^^^^^^^^^^^^^^^END OF CELL INFO^^^^^^^^^^^^^^^^");
            } else {
                cellInfoString = "NO CELL INFO FOUND";
                Log.w("JALLEN/BAD", "CURRENT CELL INFO: NONE FOUND " + cellInfos.toString());
            }
            cellInfoText.setText(cellInfoString);
        }
    }

    public void startRadioListen() {
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void signalUpdated(SignalStrength signalStrength) {
        checkCellInfo();
        String radioString = "";
        Log.d("JALLEN/RADIO", "vvvvvvvvvvvvvvvv START OF RADIO METRICS vvvvvvvvvvvvvvvv");
        String[] SignalParse = signalStrength.toString().split(" ");
        Log.d("JALLEN/RADIO", "MANUAL CDMA RSSI: " + Integer.toString(Integer.parseInt(SignalParse[3]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[3])));
        Log.d("JALLEN/RADIO", "MANUAL CDMA ECIO: " + Integer.toString(Integer.parseInt(SignalParse[4]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[4])));
        Log.d("JALLEN/RADIO", "=========");
        Log.d("JALLEN/RADIO", "MANUAL EVDO RSSI: " + Integer.toString(Integer.parseInt(SignalParse[5]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[5])));
        Log.d("JALLEN/RADIO", "MANUAL EVDO ECIO: " + Integer.toString(Integer.parseInt(SignalParse[6]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[6])));
        Log.d("JALLEN/RADIO", "MANUAL EVDO SNR: " + Integer.toString(Integer.parseInt(SignalParse[7]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[7])));
        Log.d("JALLEN/RADIO", "=========");
        Log.d("JALLEN/RADIO", "MANUAL LTE SS: " + Integer.toString(Integer.parseInt(SignalParse[8]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[8])));
        Log.d("JALLEN/RADIO", "MANUAL LTE RSRP: " + Integer.toString(Integer.parseInt(SignalParse[9]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[9])));
        Log.d("JALLEN/RADIO", "MANUAL LTE RSRQ: " + Integer.toString(Integer.parseInt(SignalParse[10]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[10])));
        Log.d("JALLEN/RADIO", "MANUAL LTE SNR: " + Integer.toString(Integer.parseInt(SignalParse[11]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[11])));
        Log.d("JALLEN/RADIO", "MANUAL LTE CQI: " + Integer.toString(Integer.parseInt(SignalParse[12]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[12])));
        Log.d("JALLEN/RADIO", "=========");
        Log.d("JALLEN/RADIO", "CDMA DBM: " + signalStrength.getCdmaDbm());
        Log.d("JALLEN/RADIO", "CDMA ECIO: " + signalStrength.getCdmaEcio());
        Log.d("JALLEN/RADIO", "=========");
        Log.d("JALLEN/RADIO", "EVDO RSSI: " + signalStrength.getEvdoDbm());
        Log.d("JALLEN/RADIO", "EVDO ECIO: " + signalStrength.getEvdoEcio());
        Log.d("JALLEN/RADIO", "EVDO SNR: " + signalStrength.getEvdoSnr());
        Log.d("JALLEN/RADIO", "=========");
        radioString = radioString + "MANUAL CDMA RSSI: " + Integer.toString(Integer.parseInt(SignalParse[3]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[3])) + "\n";
        radioString = radioString + "MANUAL CDMA ECIO: " + Integer.toString(Integer.parseInt(SignalParse[4]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[4])) + "\n";
        radioString = radioString + "\n";
        radioString = radioString + "MANUAL EVDO RSSI: " + Integer.toString(Integer.parseInt(SignalParse[5]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[5])) + "\n";
        radioString = radioString + "MANUAL EVDO ECIO: " + Integer.toString(Integer.parseInt(SignalParse[6]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[6])) + "\n";
        radioString = radioString + "MANUAL EVDO SNR: " + Integer.toString(Integer.parseInt(SignalParse[7]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[7])) + "\n";
        radioString = radioString + "\n";
        radioString = radioString + "MANUAL LTE SS: " + Integer.toString(Integer.parseInt(SignalParse[8]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[8])) + "\n";
        radioString = radioString + "MANUAL LTE RSRP: " + Integer.toString(Integer.parseInt(SignalParse[9]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[9])) + "\n";
        radioString = radioString + "MANUAL LTE RSRQ: " + Integer.toString(Integer.parseInt(SignalParse[10]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[10])) + "\n";
        radioString = radioString + "MANUAL LTE SNR: " + Integer.toString(Integer.parseInt(SignalParse[11]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[11])) + "\n";
        radioString = radioString + "MANUAL LTE CQI: " + Integer.toString(Integer.parseInt(SignalParse[12]) == Integer.MAX_VALUE ? -1 : Integer.parseInt(SignalParse[12])) + "\n";
        radioString = radioString + "\n";
        radioString = radioString + "CDMA DBM: " + signalStrength.getCdmaDbm() + "\n";
        radioString = radioString + "CDMA ECIO: " + signalStrength.getCdmaEcio() + "\n";
        radioString = radioString + "\n";
        radioString = radioString + "EVDO RSSI: " + signalStrength.getEvdoDbm() + "\n";
        radioString = radioString + "EVDO ECIO: " + signalStrength.getEvdoEcio() + "\n";
        radioString = radioString + "EVDO SNR: " + signalStrength.getEvdoSnr() + "\n";
        radioString = radioString + "\n";
        if (cellInfoLte != null) {
            CellSignalStrengthLte signalStrengthLte = cellInfoLte.getCellSignalStrength();
            Log.d("JALLEN/RADIO", "LTE CLASS DBM: " + signalStrengthLte.getDbm());
            Log.d("JALLEN/RADIO", "LTE CLASS ASU: " + signalStrengthLte.getAsuLevel());
            Log.d("JALLEN/RADIO", "LTE CLASS LEVEL: " + signalStrengthLte.getLevel());
            Log.d("JALLEN/RADIO", "LTE CLASS TIMING: " + (signalStrengthLte.getTimingAdvance() == Integer.MAX_VALUE ? -1 : signalStrengthLte.getTimingAdvance()));
//            Log.d("JALLEN/RADIO", "LTE CQI: " + signalStrengthLte.getCqi());
//            Log.d("JALLEN/RADIO", "LTE RSRP: " + signalStrengthLte.getRsrp());
//            Log.d("JALLEN/RADIO", "LTE RSRQ: " + signalStrengthLte.getRsrq());
//            Log.d("JALLEN/RADIO", "LTE RSSNR: " + signalStrengthLte.getRssnr());
            radioString = radioString + "LTE CLASS DBM: " + signalStrengthLte.getDbm() + "\n";
            radioString = radioString + "LTE CLASS ASU: " + signalStrengthLte.getAsuLevel() + "\n";
            radioString = radioString + "LTE CLASS RSSI: " + (signalStrengthLte.getAsuLevel()-140) + "\n";
            radioString = radioString + "LTE CLASS LEVEL: " + signalStrengthLte.getLevel() + "\n";
            radioString = radioString + "LTE CLASS TIMING: " + (signalStrengthLte.getTimingAdvance() == Integer.MAX_VALUE ? -1 : signalStrengthLte.getTimingAdvance()) + "\n";
            String[] LTEData = signalStrengthLte.toString().split(" ");
            for (int i = 0; i < LTEData.length; i++) {
                String[] data = LTEData[i].split("=");
                if (data.length == 2) {
                    String key = data[0];
                    Integer value = Integer.valueOf(data[1]);
                    value = (value == Integer.MAX_VALUE) ? -1 : value;
                    Log.d("JALLEN/RADIO", "LTE " + key.toUpperCase() + ": " + value);
                    radioString = radioString + "LTE " + key.toUpperCase() + ": " + value + "\n";
                }
            }
        } else {
            radioString = radioString + "LTE INFO UNAVAILABLE";
            Log.w("JALLEN/BAD", "\n LTE INFO UNAVAILABLE");
        }
        Log.d("JALLEN/RADIO", "^^^^^^^^^^^^^^^^END OF RADIO METRICS^^^^^^^^^^^^^^^^");
        radioText.setText(radioString);
    }
}
