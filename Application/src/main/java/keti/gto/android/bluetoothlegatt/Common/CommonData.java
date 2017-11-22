package keti.gto.android.bluetoothlegatt.Common;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Administrator on 2016-01-29.
 */
public class CommonData {
    private final static String TAG = "KT";

    public static final int CONNECTION_REQ			        =	0x09;
    public static final int CONNECTION_CFM			        =	0x0A;

    public static final int URBAN_INFO_REQ			        =	0x10;
    public static final int URBAN_INFO_CFM			        =	0x11;
    public static final int URBAN_INFO_DB_REQ               =   0x12;
    public static final int URBAN_INFO_DB_CFM               =   0x13;
    public static final int URBAN_PPG_REQ                   =   0x14;
    public static final int URBAN_PPG_CFM                   =   0x15;
    public static final int URBAN_PPG_DB_REQ                =   0x16;
    public static final int URBAN_PPG_DB_CFM                =   0x17;
    public static final int URBAN_CONDITION_REQ             =   0x18;
    public static final int URBAN_CONDITION_CFM             =   0x19;
    public static final int URBAN_GOAL_REQ                  =   0x1A;
    public static final int URBAN_GOAL_CFM                  =   0x1B;
    public static final int URBAN_SLEEP_DB_REQ              =   0x1C;
    public static final int URBAN_SLEEP_DB_CFM              =   0x1D;
    public static final int URBAN_SLEEP_WU_REQ              =   0x1E;
    public static final int URBAN_SLEEP_WU_CFM              =   0x1F;


    public static final int URBAN_INFO_SYNC_START_REQ       =   0x20;
    public static final int URBAN_INFO_SYNC_START_CFM       =   0x21;
    public static final int URBAN_INFO_SYNC_DATA_REQ        =   0x22;
    public static final int URBAN_INFO_SYNC_DATA_CFM        =   0x23;
    public static final int URBAN_PPG_SYNC_START_REQ        =   0x24;
    public static final int URBAN_PPG_SYNC_START_CFM        =   0x25;
    public static final int URBAN_PPG_SYNC_DATA_REQ         =   0x26;
    public static final int URBAN_PPG_SYNC_DATA_CFM         =   0x27;
    public static final int URBAN_SLEEP_SYNC_START_REQ      =   0x28;
    public static final int URBAN_SLEEP_SYNC_START_CFM      =   0x29;
    public static final int URBAN_SLEEP_SYNC_DATA_REQ       =   0x2A;
    public static final int URBAN_SLEEP_SYNC_DATA_CFM       =   0x2B;
    public static final int URBAN_PPG_START_REQ             =   0x2C;
    public static final int URBAN_PPG_START_CFM             =   0x2D;
    public static final int URBAN_BARO_START_REQ            =   0x2E;
    public static final int URBAN_BARO_START_CFM            =   0x2F;

    public static final int EXERCISE_BtoA_START_REQ		    =	0x30;
    public static final int EXERCISE_BtoA_START_CFM		    =	0x31;
    public static final int EXERCISE_BtoA_STOP_REQ          =   0x32;
    public static final int EXERCISE_BtoA_STOP_CFM          =   0x33;
    public static final int EXERCISE_AtoB_START_REQ         =   0x34;
    public static final int EXERCISE_AtoB_START_CFM         =   0x35;
    public static final int EXERCISE_AtoB_STOP_REQ          =   0x36;
    public static final int EXERCISE_AtoB_STOP_CFM          =   0x37;
    public static final int EXERCISE_INFO_REQ               =   0x38;
    public static final int EXERCISE_INFO_CFM               =   0x39;
    public static final int EXERCISE_PPG_REQ                =   0x3A;
    public static final int EXERCISE_PPG_CFM                =   0x3B;
    public static final int EXERCISE_SYNC_REQ               =   0x3C;
    public static final int EXERCISE_SYNC_CFM               =   0x3D;

    public static final int EXERCISE_SYNC_START_REQ         =   0x40;
    public static final int EXERCISE_SYNC_START_CFM         =   0x41;

    public static final int EXERCISE_SYNC_DATA_REQ          =   0x42;
    public static final int EXERCISE_SYNC_DATA_CFM          =   0x43;

    public static final int EXERCISE_SYNC_DATA_INFO_REQ         =   0x48;
    public static final int EXERCISE_SYNC_DATA_INFO_CFM         =   0x49;
    public static final int EXERCISE_SYNC_DATA_HRM_REQ          =   0x4A;
    public static final int EXERCISE_SYNC_DATA_HRM_CFM          =   0x4B;
    public static final int EXERCISE_SYNC_DATA_ALTITUDE_REQ     =   0x4C;
    public static final int EXERCISE_SYNC_DATA_ALTITUDE_CFM     =   0x4D;

    public static final int RTC_REQ					        =	0x50;
    public static final int RTC_CFM					        =	0x51;
    public static final int USERPROFILE_REQ                 =   0x52;
    public static final int USERPROFILE_CFM                 =   0x53;
    public static final int LANGUAGE_REQ                    =   0x54;
    public static final int LANGUAGE_CFM                    =   0x55;
    public static final int UNIT_REQ                        =   0x56;
    public static final int UNIT_CFM                        =   0x57;
    public static final int VERSION_REQ                     =   0x58;
    public static final int VERSION_CFM                     =   0x59;
    public static final int USER_PPG_REQ                    =   0x5A;
    public static final int USER_PPG_CFM                    =   0x5B;
    public static final int SLEEP_TIME_REQ                  =   0x5C;
    public static final int SLEEP_TIME_CFM                  =   0x5D;
    public static final int PPG_INTERVAL_REQ                =   0x5E;
    public static final int PPG_INTERVAL_CFM                =   0x5F;

    public static final int EXERCISE_DISPLAY_ITEM_REQ       =   0x60;
    public static final int EXERCISE_DISPLAY_ITEM_CFM       =   0x61;

    public static final int CALL_REQ					    =	0x70;
    public static final int CALL_CFM					    =	0x71;
    public static final int CALL_ACCEPT_REQ                 =   0x72;
    public static final int CALL_ACCEPT_CFM                 =   0x73;
    public static final int SMS_REQ                         =   0x74;
    public static final int SMS_CFM                         =   0x75;
    public static final int GOAL_BtoA_REQ                   =   0x76;
    public static final int GOAL_BtoA_CFM                   =   0x77;
    public static final int GOAL_AtoB_REQ                   =   0x78;
    public static final int GOAL_AtoB_CFM                   =   0x79;
    public static final int APP_NOTI_REQ                    =   0x7A;
    public static final int APP_NOTI_CFM                    =   0x7B;

    public static final int CUSTOM_CONTENTS_START_REQ       =   (byte)0x90;
    public static final int CUSTOM_CONTENTS_START_CFM       =   (byte)0x91;
    public static final int CUSTOM_CONTENTS_DATA_REQ        =   (byte)0x92;
    public static final int CUSTOM_CONTENTS_DATA_CFM        =   (byte)0x93;

    public static final int  OTA_START_REQ					=   (byte)0x94;
    public static final int  OTA_START_CFM					=	(byte)0x95;
    public static final int  OTA_DATA_REQ					=   (byte)0x96;
    public static final int  OTA_DATA_CFM					=   (byte)0x97;

    public static final byte EXERCISE_WALKING               =   0x00;
    public static final byte EXERCISE_RUNNING               =   0x01;
    public static final byte EXERCISE_CLIMBING              =   0x02;
    public static final byte EXERCISE_BICYCLE               =   0x03;

    public static final int OTA_REQ_TIMER					=	1;
    public static final int OTA_REQ_TIMER_INTERVAL		    =	500;

    public static byte[] SendData(int msgReq, int msgLen, byte[] payloadData) {
        int index = 0, checkSum = 0;
        byte[] value = null;
        value = new byte[7+msgLen];

        value[index++] = (byte) 0xAA;
        value[index++] = (byte) msgReq;
        value[index++] = (byte) msgLen;

        for (int i=0; i<msgLen; i++) {
            value[index++] = payloadData[i];
        }

        for (int i = 1; i < index; i++) {
            checkSum += value[i];
        }

        value[index++] = (byte) checkSum;
        value[index++] = (byte) 0xA5;
        value[index++] = (byte) 0x5A;
        value[index++] = (byte) 0x7E;
        return value;
    }

    public static byte[] DecToBCDArray(long num) {
        //Log.d(TAG, "num : " + num);
        if(num == 0){
            byte bcd[] = new byte[1];
            bcd[0] = (byte)0x00;
            return bcd;
        }

        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;

        byte bcd[] = new byte[byteLen];


        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i == digits - 1 && isOdd)
                bcd[i / 2] = tmp;
            else if (i % 2 == 0)
                bcd[i / 2] = tmp;
            else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        return bcd;
    }

    public static int stringToSubString(String str, int start, int end){
        return Integer.parseInt(str.substring(start,end));
    }

    public static byte[] DateToByteArray(){
        byte dateByte[] = new byte[8];
        Calendar calendar = Calendar.getInstance();
        byte[] year =  DecToBCDArray((long) stringToSubString(String.valueOf(calendar.get(calendar.YEAR)), 1, 4));
        byte[] month = DecToBCDArray((long)calendar.get(calendar.MONTH) + 1);
        byte[] day = DecToBCDArray((long)calendar.get(calendar.DAY_OF_MONTH));
        byte[] hour;
        if(calendar.get(calendar.HOUR) == 0){
            hour = DecToBCDArray((long)12);
        }else{
            hour = DecToBCDArray((long)calendar.get(calendar.HOUR));
        }
        byte[] min = DecToBCDArray((long)calendar.get(calendar.MINUTE));
        byte[] sec = DecToBCDArray((long)calendar.get(calendar.SECOND));
        byte[] week = DecToBCDArray((long)calendar.get(calendar.DAY_OF_WEEK));
        byte[] am_pm = DecToBCDArray((long)calendar.get(calendar.AM_PM));

        dateByte[0] = year[0];
        dateByte[1] = month[0];
        dateByte[2] = day[0];
        dateByte[3] = hour[0];
        dateByte[4] = min[0];
        dateByte[5] = sec[0];
        dateByte[6] = week[0];
        dateByte[7] = am_pm[0];

        Log.d(TAG, "Date: " + dateByte[0]+"-"+dateByte[1]+"-"+dateByte[2]+" "+dateByte[3]+":"+dateByte[4]+":"+dateByte[5]
                +"["+dateByte[6]+"/"+dateByte[7]+"]");

        return dateByte;
    }

    public static void writeLog(String str) {
        String dir = "";
        if(str == null || TextUtils.isEmpty(str)){
            return;
        }

        dir = Environment.getExternalStorageDirectory()+ File.separator+"BLE_LOG";

        File folder = new File(dir);
        if (folder.mkdir() || folder.isDirectory()) {
            File  mLogFile = new File(dir, "log_"+System.currentTimeMillis()+".log");
            try {
                if (mLogFile.createNewFile()) {
                    BufferedWriter bfw = new BufferedWriter(new FileWriter(mLogFile,true));
                    bfw.write(str);
                    bfw.write("\n");
                    bfw.flush();
                    bfw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
