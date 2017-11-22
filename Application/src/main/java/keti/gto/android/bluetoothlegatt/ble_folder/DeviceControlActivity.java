/*
 * Copyright 2017 The Android AVN Project
 *
 *      Korea Electronics Technology Institute
 *
 *      http://keti.re.kr/
 *
 */

package keti.gto.android.bluetoothlegatt.ble_folder;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import keti.gto.android.bluetoothlegatt.Common.CommonData;
import keti.gto.android.bluetoothlegatt.R;
import keti.gto.android.bluetoothlegatt.bio_folder.bio_activity;
import keti.gto.android.bluetoothlegatt.map_folder.map_main_activity;
import keti.gto.android.bluetoothlegatt.music_folder.music_main_activity;
import keti.gto.android.bluetoothlegatt.smartKey_folder.smartKey_activity;


public class DeviceControlActivity extends Activity implements View.OnClickListener {
    private final static String TAG = "DCA";

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    public static final int DUMP = -1;

    private static final boolean D = true;

    private TextView mConnectionState;
    private SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm:ss");

    private TextView mDataTextView;
    private ScrollView mDataScrollView;

    //TODO: GTO code
    //TODO: Band read data
    public static String h_acc_x;
    public static String h_acc_y;
    public static String h_acc_z;
    public static String h_gyro_x;
    public static String h_gyro_y;
    public static String h_gyro_z;

    public static int h_ac_x;
    public static int h_ac_y;
    public static int h_ac_z;

    public static int h_gy_x;
    public static int h_gy_y;
    public static int h_gy_z;

    public static String h_band_heart;
    public static int h_int_heart;

    private TimerTask myTask;
    private Timer timer;

    //TODO: CAN read data
    public static int final_can;	// 마지막 CAN data 값 저장 <전역번수>
    public static int before_can;	// 마지막 이전 CAN data 값 저장 <전역번수>
    public static int before_wheel;
    public static int final_wheel;
    public static int cal_316;
    public static int cal_2B0;
    public static int door_state;
    public static int door_opcl;

    //TODO: EXTRA_DATA -> HEX
    public static byte[] packet;

    // TODO: URBAN
    private Button mManboSyncBtn;
    private Button mPPGSyncBtn;
    private Button mSleepSyncBtn;
    private Button mPPGBtn;
    private Button mBaroBtn;

    // TODO: EXERCISE
    private Button mEXStartBtn;
    private Button mEXStopBtn;
    private Button mEXSyncBtn;
    private Button mEXUpdateBtn;

    // TODO: SETTING
    private Button mConnectionBtn;
    private Button mRTCBtn;
    private Button mUserProfileBtn;
    private Button mLanguageBtn;
    private Button mUnitBtn;
    private Button mVersionBtn;
    private Button mUserPPGBtn;
    private Button mSleepTimeBtn;
    private Button mPPGIntervalBtn;
    private Button mEXDisplayItemBtn;

    // TODO: NOTI
    private Button mCallBtn;
    private Button mAcceptCallBtn;
    private Button mSMSBtn;
    private Button mGoalBtn;
    private Button mAppNotiBtn;

    private String mDeviceName;
    private String mDeviceAddress;

    public static Short HeartRate;

    //final String band_41a6 = "CD:25:C5:30:41:A6";
    final String band_41a6_1 = "CD25C53041A6";

    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    AccSlidingCollection asc = new AccSlidingCollection();

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (mDeviceName.equals("H_Fit"))    {
                asc.setBandFlag(H_BAND);

                final Intent mIntent = intent;
                //*********************//
                if (action.equals(mBluetoothLeService.ACTION_GATT_CONNECTED)) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            mBluetoothLeService.mState = mBluetoothLeService.UART_PROFILE_CONNECTED;
                        }
                    });
                }

                //*********************//
                if (action.equals(mBluetoothLeService.ACTION_GATT_DISCONNECTED)) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            mBluetoothLeService.mState = mBluetoothLeService.UART_PROFILE_DISCONNECTED;
                            mBluetoothLeService.close();
                        }
                    });
                }


                //*********************//
                if (action.equals(mBluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {

                    mBluetoothLeService.enableTXNotification();

                }
                //*********************//
                if (action.equals(mBluetoothLeService.ACTION_DATA_AVAILABLE)) {

                    final byte[] txValue = intent.getByteArrayExtra(mBluetoothLeService.EXTRA_DATA);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                H_getStringPacket (txValue);

                                String text = new String(txValue, "UTF-8");

                                //System.out.println("original H-Band data : " + txValue);
                                String hexString = "";
                                for (byte b : txValue) {  						//readBuf -> Hex
                                    hexString += Integer.toString((b & 0xF0) >> 4, 16);
                                    hexString += Integer.toString(b & 0x0F, 16);
                                }
                                //System.out.println(hexString);
                                int cnt = hexString.length();

                                if (cnt == 36) {
                                    h_acc_x = hexString.substring(8, 12);
                                    h_acc_y = hexString.substring(12, 16);
                                    h_acc_z = hexString.substring(16, 20);

                                    h_gyro_x = hexString.substring(20, 24);
                                    h_gyro_y = hexString.substring(24, 28);
                                    h_gyro_z = hexString.substring(28, 32);

                                    //System.out.println("subString H-Band data : " + h_acc_x + " " + h_acc_y + " " + h_acc_z + " " + h_gyro_x + " " + h_gyro_y + " " + h_gyro_z);

                                    h_ac_x = (short) Integer.parseInt(h_acc_x, 16);
                                    h_ac_y = (short) Integer.parseInt(h_acc_y, 16);
                                    h_ac_z = (short) Integer.parseInt(h_acc_z, 16);

                                    // TODO : GYRO X Y Z Decimal
                                    h_gy_x = (short) Integer.parseInt(h_gyro_x, 16);
                                    h_gy_y = (short) Integer.parseInt(h_gyro_y, 16);
                                    h_gy_z = (short) Integer.parseInt(h_gyro_z, 16);

                                    System.out.println("Integer H-Band data : " + h_ac_x + " " + h_ac_y + " " + h_ac_z + " " + h_gy_x + " " + h_gy_y + " " + h_gy_z);

                                    //System.out.println("length : "+cnt);
                                }
                                if (cnt == 16) {
                                    h_band_heart = hexString.substring(10, 12);
                                    h_int_heart = (short) Integer.parseInt(h_band_heart, 16);
                                    System.out.println("heart Rate : "+h_int_heart);
                                }


                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                            }
                        }
                    });
                }
                //*********************//
                if (action.equals(mBluetoothLeService.DEVICE_DOES_NOT_SUPPORT_UART)) {
                    Log.d(TAG, "Device doesn't support UART. Disconnecting");
                    mBluetoothLeService.disconnect();
                }

            }

            else    {

                if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                    mConnected = true;
                    updateConnectionState(R.string.connected);
                    invalidateOptionsMenu();
                } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                    mConnected = false;
                    updateConnectionState(R.string.disconnected);
                    invalidateOptionsMenu();
                } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    // Show all the supported services and characteristics on the user interface.


                    displayGattServices(mBluetoothLeService.getSupportedGattServices());

                    final BluetoothGattCharacteristic notifyCharacteristic = getNottifyCharacteristic();
                    if (notifyCharacteristic == null) {
                        Toast.makeText(getApplication(), "gatt_services can not supported", Toast.LENGTH_SHORT).show();
                        mConnected = false;
                        return;
                    }
                    final int charaProp = notifyCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBluetoothLeService.setCharacteristicNotification(
                                notifyCharacteristic, true);
                    }

                } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {

                    byte[] packet = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    displayData(packet);
                }
            }
        }

    };

    public static String stringToHex(String s) {
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("%02X ", (int) s.charAt(i));
        }

        return result;
    }

    public static String stringToHex0x(String s) {  // hex 데이터 0x 붙여서 사용
        String result = "";

        for (int i = 0; i < s.length(); i++) {
            result += String.format("0x%02X ", (int) s.charAt(i));
        }

        return result;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

        setContentView(R.layout.gatt_services_characteristics);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataTextView = (TextView) findViewById(R.id.send_data_tv);
        mDataScrollView = (ScrollView) findViewById(R.id.sd_scroll);

        // TODO: URBAN
        mManboSyncBtn = (Button) findViewById(R.id.ManboUpdateBtn);
        mPPGSyncBtn = (Button) findViewById(R.id.PPGUpdateBtn);
        mSleepSyncBtn = (Button) findViewById(R.id.SleepUpdateBtn);
        mPPGBtn = (Button) findViewById(R.id.PPGBtn);
        mBaroBtn = (Button) findViewById(R.id.BaroBtn);
        // TODO: EXERCISE
        mEXStartBtn = (Button) findViewById(R.id.ExerciseStartBtn);
        mEXStopBtn = (Button) findViewById(R.id.ExerciseStopBtn);
        mEXSyncBtn = (Button) findViewById(R.id.ExerciseSyncBtn);
        mEXUpdateBtn = (Button) findViewById(R.id.ExerciseUpdateBtn);
        // TODO: SETTING
        mConnectionBtn = (Button) findViewById(R.id.ConnectionBtn);
        mRTCBtn = (Button) findViewById(R.id.RTCBtn);
        mUserProfileBtn = (Button) findViewById(R.id.UserProfileBtn);
        mLanguageBtn = (Button) findViewById(R.id.LanguageBtn);
        mUnitBtn = (Button) findViewById(R.id.UnitBtn);
        mVersionBtn = (Button) findViewById(R.id.VersionBtn);
        mUserPPGBtn = (Button) findViewById(R.id.UserPPGBtn);
        mSleepTimeBtn = (Button) findViewById(R.id.SleepTimeBtn);
        mPPGIntervalBtn = (Button) findViewById(R.id.PPGIntervalBtn);
        mEXDisplayItemBtn = (Button) findViewById(R.id.ExerciseDisplayBtn);
        // TODO: NOTI
        mCallBtn = (Button) findViewById(R.id.CallBtn);
        mAcceptCallBtn = (Button) findViewById(R.id.CallAcceptBtn);
        mSMSBtn = (Button) findViewById(R.id.SMSBtn);
        mGoalBtn = (Button) findViewById(R.id.GoalBtn);
        mAppNotiBtn = (Button) findViewById(R.id.AppNotiBtn);

        mDataTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == true) {
                    mDataScrollView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mDataScrollView.smoothScrollBy(0, 800);
                        }
                    }, 100);
                }
            }
        });

        // TODO: URBAN
        mManboSyncBtn.setOnClickListener(this);
        mPPGSyncBtn.setOnClickListener(this);
        mSleepSyncBtn.setOnClickListener(this);
        mPPGBtn.setOnClickListener(this);
        mBaroBtn.setOnClickListener(this);
        // TODO: EXERCISE
        mEXStartBtn.setOnClickListener(this);
        mEXStopBtn.setOnClickListener(this);
        mEXSyncBtn.setOnClickListener(this);
        mEXUpdateBtn.setOnClickListener(this);
        // TODO: SETTING
        mConnectionBtn.setOnClickListener(this);
        mRTCBtn.setOnClickListener(this);
        mUserProfileBtn.setOnClickListener(this);
        mLanguageBtn.setOnClickListener(this);
        mUnitBtn.setOnClickListener(this);
        mVersionBtn.setOnClickListener(this);
        mUserPPGBtn.setOnClickListener(this);
        mSleepTimeBtn.setOnClickListener(this);
        mPPGIntervalBtn.setOnClickListener(this);
        mEXDisplayItemBtn.setOnClickListener(this);
        // TODO: NOTI
        mCallBtn.setOnClickListener(this);
        mAcceptCallBtn.setOnClickListener(this);
        mSMSBtn.setOnClickListener(this);
        mGoalBtn.setOnClickListener(this);
        mAppNotiBtn.setOnClickListener(this);

        //getActionBar().setTitle(mDeviceName);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        myTask = new TimerTask() {
            @Override
            public void run() {
                final ImageView gesture = (ImageView)findViewById(R.id.image3);
                final Button case1 = (Button)findViewById(R.id.case1);

                case1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String band_41a6 = "CD:25:C5:30:41:A6";
                        String band_0cb9 = "FA:9A:08:4E:0C:B9";

                        String h_band_810c = "F0:EB:B9:AE:18:B8";
                        String h_band_4494 = "E5:F2:C5:C8:44:94";

                        if (mDeviceAddress.equals(band_41a6)) {
                            //gesture.setImageResource(R.drawable.none_gesture_icon);
                            if (cal_316 >= 10 || cal_2B0 >= 10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (final_can == 0 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else if (final_can == 1 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else {
                                gesture.setImageResource(R.drawable.gesture_step_2);
                            }
                        }

                        else if (mDeviceAddress.equals(band_0cb9)) {
                            //gesture.setImageResource(R.drawable.none_gesture_icon);
                            if (cal_316 >= 10 || cal_2B0 >= 10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (final_can == 0 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else if (final_can == 1 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else {
                                gesture.setImageResource(R.drawable.gesture_step_2);
                            }
                        }

                        else if (mDeviceAddress.equals(h_band_4494)) {
                            //gesture.setImageResource(R.drawable.none_gesture_icon);
                            if (cal_316 >= 10 || cal_2B0 >= 10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (final_can == 0 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else if (final_can == 1 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else {
                                gesture.setImageResource(R.drawable.gesture_step_2);
                            }
                        }

                        else if (mDeviceAddress.equals(h_band_810c)) {
                            //gesture.setImageResource(R.drawable.none_gesture_icon);
                            if (cal_316 >= 10 || cal_2B0 >= 10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                //System.out.println(" MAC address is same");
                                gesture.setImageResource(R.drawable.gesture_step_3);
                            }
                            else if (final_can == 0 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else if (final_can == 1 && final_wheel ==0){
                                gesture.setImageResource(R.drawable.gesture_icon);
                            }
                            else {
                                gesture.setImageResource(R.drawable.gesture_step_2);
                            }
                        }

                        else {
                            //System.out.println(" MAC address is not to same");
                            gesture.setImageResource(R.drawable.none_gesture_icon);
                        }

                    }
                });

                final_can();
                before_can();
                final_wheel();
                before_wheel();
                can_gap();

                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String band_41a6 = "CD:25:C5:30:41:A6";
                                String band_0cb9 = "FA:9A:08:4E:0C:B9";

                                if (mDeviceAddress.equals(band_41a6)) {
                                    case1.performClick();
                                }
                                else if (mDeviceAddress.equals(band_0cb9)) {
                                    case1.performClick();
                                }
                                else {
                                    case1.performClick();
                                }
                            }
                        });
                    }
                }).start();
            }
        };
        timer = new Timer();

        timer.schedule(myTask, 0, 500);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mManboSyncBtn)) {
            mBluetoothLeService.writeGattCharacteristic(getWriteGattCharacteristic(), CommonData.URBAN_INFO_SYNC_START_REQ);
        }


    }
    private void showMessage(String msg) {
        Log.e(TAG, msg);
    }
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private String getCurrentTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        return mFormat.format(date);
    }

    private void displayData(byte[] packet) {
        //System.out.println("displayData start function packet >>> " + packet);
        //H_getStringPacket(DeviceControlActivity.packet);

    }

    private void autoScrollView(String text) {
        if (!text.isEmpty())
            mDataTextView.append(text);
        mDataScrollView.post(new Runnable() {
            @Override
            public void run() {
                mDataScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    //TODO: H-BAND Function >>> 2017.10.10 by.gto version 1.0
    private int H_byteToShort (byte b1, byte b2)  {
        return (b1 & 0xff) << 8 | (b2 & 0xff);
    }

    private String H_getStringPacket(byte[] packet) {
        Log.d (TAG,"hear ***************");
        StringBuilder sb = new StringBuilder(packet.length * 2);
        //System.out.println("Packet : " + packet);

        switch (packet [3]) {
            case 0:
            /* ---------------------------------------------------------- gesture recognition */
                int [] recv_sensors = new int [6];

                recv_sensors [0] = (short)H_byteToShort(packet [4], packet [5]);
                recv_sensors [1] = (short)H_byteToShort(packet [6], packet [7]);
                recv_sensors [2] = (short)H_byteToShort(packet [8], packet [9]);

                int result_ = asc.SlidingCollectionInterface (recv_sensors);

                if (result_ != DUMP) {
                    Log.d (TAG, "In IF branch");

                    asc.gesture_count ++;
                    sb.append(String.valueOf(asc.gesture_count));
                    sb.append(" \t ");

                    switch (result_)	{

                        case LEFT:
                            System.out.println("+++++++++++++++++ LEFT +++++++++++++++++");
                            if (final_can == 1 && final_wheel == 0) {
                                System.out.println("difference 2");
                                Runtime runtime0 = Runtime.getRuntime();
                                Process process0;
                                String res0 = "input keyevent 21";
                                try {
                                    process0 = runtime0.exec(res0); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                            }
                            else {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            break;

                        case RIGHT:
                            System.out.println("+++++++++++++++++ RIGHT +++++++++++++++++");
                            System.out.println("+++++++++++++++++ RIGHT +++++++++++++++++");
                            if (final_can == 1 && final_wheel == 0) {
                                Runtime runtime1 = Runtime.getRuntime();
                                Process process1;
                                String res = "input keyevent 22";
                                try {
                                    process1 = runtime1.exec(res); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                            }
                            else {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            break;

                        case FRONT:
                            System.out.println("+++++++++++++++++ FRONT +++++++++++++++++");
                            System.out.println("+++++++++++++++++ FRONT +++++++++++++++++");
                            if (final_can == 1 && final_wheel == 0) {
                                Runtime runtime2 = Runtime.getRuntime();
                                Process process2;
                                String res2 = "input keyevent 66";
                                try {

                                    process2 = runtime2.exec(res2); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                            }
                            else {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            break;

                        case UP:
                            //sb.append (" ^^^^ UP\n");
                            System.out.println("+++++++++++++++++ UP +++++++++++++++++");
                                /*
                                Runtime runtime3 = Runtime.getRuntime();
                                Process process3;
                                String res3 = "input keyevent 4";
                                try {

                                    process3 = runtime3.exec(res3); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                                */

                            break;

                        case CLOCK:
                            System.out.println("+++++++++++++++++ CLOCK +++++++++++++++++");
                            if (cal_316 >=10 || cal_2B0 >= 10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else {
                                Runtime runtime4 = Runtime.getRuntime();
                                Process process4;
                                String res4 = "input keyevent 24";
                                try {

                                    process4 = runtime4.exec(res4); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                            }

                            break;

                        case ANTI_CLOCK:
                            System.out.println("+++++++++++++++++ ANTI CLOCK +++++++++++++++++");
                            if (cal_316 >=10 || cal_2B0 >= 10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else if (cal_316 == -10 || cal_2B0 == -10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else {
                                Runtime runtime5 = Runtime.getRuntime();
                                Process process5;
                                String res5 = "input keyevent 25";
                                try {

                                    process5 = runtime5.exec(res5); //2번 실행해야 되는 경우가 있음
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                    Log.e("Process Manager", "Unable to execute top command");
                                }
                            }

                            break;

                        default:

                    }	//switch


                    // sb.append(String.valueOf(result_));
                    sb.append("\n");
                }

                break;

            case 1:
            /* ---------------------------------------------------------- Heart rate */
                HeartRate = (short)H_byteToShort(packet [4], packet [5]);
                System.out.println("HeartRate : "+HeartRate);


                break;
            case 2:
            /* ---------------------------------------------------------- Walking data */
                Short WalkingCount = (short)H_byteToShort(packet [4], packet [5]);
                System.out.println("WalkingCount : "+WalkingCount);

                break;
            case 3:
            /* ---------------------------------------------------------- Battery */
                Short Battery = (short)H_byteToShort(packet [4], packet [5]);
                System.out.println("Battery : "+Battery);

                break;
            case 4:
            /* ---------------------------------------------------------- Calory */
                Short Calory = (short)H_byteToShort(packet [4], packet [5]);
                System.out.println("Calory : "+Calory);

                break;
            default:
            /* ---------------------------------------------------------- Unknown */

                break;
        }

        return sb.toString();


    }

    private BluetoothGattCharacteristic getNottifyCharacteristic(){
        BluetoothGattCharacteristic notifyCharacteristic = null;
        if(mGattCharacteristics == null || mGattCharacteristics.size() == 0){
            return null;
        }
        for (int i = 0; i < mGattCharacteristics.size() ; i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size() ; j++) {
                notifyCharacteristic =  mGattCharacteristics.get(i).get(j);
                if(notifyCharacteristic.getUuid().equals(BluetoothLeService.FFF4_RATE_MEASUREMENT)){
                    return notifyCharacteristic;
                }
            }
        }
        return null;
    }

    private BluetoothGattCharacteristic getWriteGattCharacteristic(){
        BluetoothGattCharacteristic writeGattCharacteristic = null;
        if(mGattCharacteristics == null || mGattCharacteristics.size() == 0){
            return null;
        }

        for (int i = 0; i < mGattCharacteristics.size() ; i++) {
            for (int j = 0; j < mGattCharacteristics.get(i).size() ; j++) {
                writeGattCharacteristic =  mGattCharacteristics.get(i).get(j);
                if(writeGattCharacteristic. getUuid().equals(BluetoothLeService.FFF3_RATE_MEASUREMENT)){
                    return writeGattCharacteristic;
                }
            }
        }
        return null;
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_SEND_PACKET);
        return intentFilter;
    }

    //TODO move activity ImgButon
    public void onClickMap(View v) {        //Map info Activity     //Map Button

        final Intent i = new Intent(this, map_main_activity.class);
        startActivity(i);

    }

    public void onClickMusic(View v) {        //Music info Activity     //Music Button

        final Intent i = new Intent(this, music_main_activity.class);
        startActivity(i);

    }

    public void onClickCAN(View v) {        //Read to CAN DATA Activity     //CAN Button

        final Intent i = new Intent(this, can_main_activity.class);
        startActivity(i);

    }

    public void onClickKey(View v) {        //Read to CAN DATA Activity     //CAN Button

        final Intent i = new Intent(this, smartKey_activity.class);
        startActivity(i);

    }

    public void onClickBio(View v) {        //Read to CAN DATA Activity     //CAN Button

        final Intent i = new Intent(this, bio_activity.class);
        startActivity(i);

    }


    //TODO ＃Read to CAN DATA Function

    //TODO read to last CAN DATA(Speed)
    private void final_can ()	{

        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
     String cmd = "./final_can.sh";
            process1 = runtime1.exec(cmd);

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));

            String line1 = "";

            while ((line1 = br.readLine()) != null)
            {

                String data_1 = line1.substring(19, 21);
                int hex1 = Integer.parseInt(data_1, 16);

                final_can = hex1;

            }

            //System.out.println("마지막 속도: " + final_can);

            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

    //TODO read to last before CAN DATA(Speed)
    private void before_can ()	{

        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
            String cmd = "./before_can.sh";
            process1 = runtime1.exec(cmd);

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));

            String line1 = "";
            if ((line1 = br.readLine()) != null)
            {

                String b_data_1 = line1.substring(19, 21);
                int b_hex1 = Integer.parseInt(b_data_1, 16);

                before_can = b_hex1;

            }
            //System.out.println("마지막 이전 속도: " + before_can);

            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

    //TODO read to last CAN DATA(Wheel)
    private void final_wheel ()	{

        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {

            String cmd = "./wheel_final.sh";
            process1 = runtime1.exec(cmd);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));

            String line1 = "";

            if ((line1 = br.readLine()) != null)
            {
          String b_data_1 = line1.substring(19, 21);

                int b_hex1 = Integer.parseInt(b_data_1, 16);

                 final_wheel = b_hex1;

            }
            //System.out.println("마지막 휠: " + final_wheel);

            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

    //TODO read to last before CAN DATA(Wheel)
    private void before_wheel ()	{

        Runtime runtime1 = Runtime.getRuntime();
        Process process1;
        try
        {

           String cmd = "./wheel_before.sh";
           process1 = runtime1.exec(cmd);

            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));

            String line1 = "";

            if ((line1 = br.readLine()) != null)
            {
            String b_data_1 = line1.substring(19, 21);
                int b_hex1 = Integer.parseInt(b_data_1, 16);


                before_wheel = b_hex1;

            }

            //System.out.println("마지막 이전 휠: " + before_wheel);

            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

    //TODO read to last CAN DATA(MN_band info)
    private void door_state ()	{
        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
            String cmd = "./door_state.sh";
            process1 = runtime1.exec(cmd);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));
            String line1 = "";
            if ((line1 = br.readLine()) != null)
            {
                String b_data_1 = line1.substring(19, 21);      // normal hex data
                String b_data_2 = line1.substring(22, 24);      // error hex data
                int b_hex1 = Integer.parseInt(b_data_1, 16);    // normal int data
                int b_hex2 = Integer.parseInt(b_data_2, 16);    // error int data

                /*
                if (b_hex1 == 0 && b_hex2 == 0) {
                    //System.out.println("------------------------- Door CLOSE");
                    Toast toast = Toast.makeText(this, "                                                                                   11", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) toast.getView();
                    ImageView image = new ImageView(getApplicationContext());
                    image.setImageResource(R.drawable.open_door_toast);
                    //imageCodeProject.setImageResource(R.drawable.icon);
                    view.addView(image, 0);
                    toast.show();
                }

                else if (b_hex1 == 1 && b_hex2 == 0) {
                    //System.out.println("------------------------- Door CLOSE");
                    Toast toast = Toast.makeText(this, "                                                                                   11", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) toast.getView();
                    ImageView image = new ImageView(getApplicationContext());
                    image.setImageResource(R.drawable.open_door_toast);
                    //imageCodeProject.setImageResource(R.drawable.icon);
                    view.addView(image, 0);
                    toast.show();
                }

                else if (b_hex1 == 1 && b_hex2 == 1) {
                    //System.out.println("------------------------- Door OPEN");
                    //open_door.setImageResource(R.drawable.close_door_icon);
                    Toast toast = Toast.makeText(this, "                                                                                   11", Toast.LENGTH_SHORT);

                    toast.setGravity(Gravity.CENTER, 0, 0);
                    LinearLayout view = (LinearLayout) toast.getView();
                    ImageView image = new ImageView(getApplicationContext());
                    image.setImageResource(R.drawable.close_door_toast);
                    //imageCodeProject.setImageResource(R.drawable.icon);
                    view.addView(image, 0);
                    toast.show();
                }

                else {
                    System.out.println("failed to read door_state.sh!!");
                }
                */

                door_state = b_hex1;  // 533#00 , 533#01 <- door state
                door_opcl = b_hex2;   // 533#01.00 , 533#01.01 <- open, close state

            }
            /*
            final ImageView open_door = (ImageView) findViewById(R.id.image1);
            final Button case1 = (Button) findViewById(R.id.case1);

            case1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (door_state == 0 && door_opcl == 0) {
                        open_door.setImageResource(R.drawable.close_icon);
                    } else {
                        open_door.setImageResource(R.drawable.open_icon);
                    }
                }

            });

            if (door_state == 0 && door_opcl == 0) {
                //System.out.println("------------------------- Door CLOSE");
                case1.performClick();
            } else if (door_state == 1 && door_opcl == 0) {
                //System.out.println("------------------------- Door CLOSE");
                case1.performClick();
            } else if (door_state == 1 && door_opcl == 1) {
                //System.out.println("------------------------- Door OPEN");
                case1.performClick();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (door_state == 0 && door_opcl == 0) {
                                //System.out.println("------------------------- Door CLOSE");
                                case1.performClick();
                            } else if (door_state == 1 && door_opcl == 0) {
                                //System.out.println("------------------------- Door CLOSE");
                                case1.performClick();
                            } else if (door_state == 1 && door_opcl == 1) {
                                //System.out.println("------------------------- Door OPEN");
                                case1.performClick();
                            }
                        }
                    });
                }
            }).start();
            */
            //System.out.println("마지막 wheel : "+final_wheel);
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }
    }

    //TODO read to last CAN DATA(ucomm_band info)
    private void door_state_ucomm ()	{
        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
            String cmd = "./ucomm_band.sh";
            process1 = runtime1.exec(cmd);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));
            String line1 = "";
            if ((line1 = br.readLine()) != null)
            {
                String b_data_1 = line1.substring(19, 21);      // normal hex data
                String b_data_2 = line1.substring(22, 24);      // error hex data
                int b_hex1 = Integer.parseInt(b_data_1, 16);    // normal int data
                int b_hex2 = Integer.parseInt(b_data_2, 16);    // error int data

                door_state = b_hex1;  // 533#00 , 533#01 <- door state
                door_opcl = b_hex2;   // 533#01.00 , 533#01.01 <- open, close state

            }
            /*
            final ImageView open_door = (ImageView) findViewById(R.id.image1);
            final Button case1 = (Button) findViewById(R.id.case1);

            case1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (door_state == 0 && door_opcl == 0) {
                        open_door.setImageResource(R.drawable.close_icon);
                    } else {
                        open_door.setImageResource(R.drawable.open_icon);
                    }
                }

            });

            if (door_state == 0 && door_opcl == 0) {
                //System.out.println("------------------------- Door CLOSE");
                case1.performClick();
            } else if (door_state == 1 && door_opcl == 0) {
                //System.out.println("------------------------- Door CLOSE");
                case1.performClick();
            } else if (door_state == 1 && door_opcl == 1) {
                //System.out.println("------------------------- Door OPEN");
                case1.performClick();
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (door_state == 0 && door_opcl == 0) {
                                //System.out.println("------------------------- Door CLOSE");
                                case1.performClick();
                            } else if (door_state == 1 && door_opcl == 0) {
                                //System.out.println("------------------------- Door CLOSE");
                                case1.performClick();
                            } else if (door_state == 1 && door_opcl == 1) {
                                //System.out.println("------------------------- Door OPEN");
                                case1.performClick();
                            }
                        }
                    });
                }
            }).start();
            */
            //System.out.println("마지막 wheel : "+final_wheel);
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }
    }

    private void can_gap() {
        cal_316 = final_can - before_can;
        cal_2B0 = final_wheel - before_wheel;

        //System.out.println("현재 스피드 : " + final_can);
        //System.out.println("이전 스피드 : " + before_can);
        //System.out.println("현재 조향각 : " + final_wheel);
        //System.out.println("이전 조향각 : " + before_wheel);

    }



    public static final int FRONT = 0;    public static final int BACK = FRONT + 1;	//1
    public static final int RIGHT = BACK + 1;   //2
    public static final int LEFT = RIGHT + 1;	//3
    public static final int UP = LEFT + 1;	//4
    public static final int DOWN = UP + 1;	//5
    public static final int	CLOCK = DOWN + 1;	//6
    public static final int ANTI_CLOCK = CLOCK + 1;	//7
    public static final int LOW_CLOCK = ANTI_CLOCK + 1;	//8
    public static final int LOW_ANTI = LOW_CLOCK + 1;	//9
    public static final int UNKNOWN_ = 99;

    public static final byte H_BAND = 0;
    public static final byte PARTRON = 1;
    public static final byte UCOMM = 2;

    public static final int GESTURE_NUM = LOW_ANTI + 1;	//10


}
