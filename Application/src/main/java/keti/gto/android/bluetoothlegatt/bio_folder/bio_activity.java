package keti.gto.android.bluetoothlegatt.bio_folder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import keti.gto.android.bluetoothlegatt.R;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import keti.gto.android.bluetoothlegatt.ble_folder.AccSlidingCollection;
import keti.gto.android.bluetoothlegatt.ble_folder.BluetoothLeService;
import keti.gto.android.bluetoothlegatt.ble_folder.DeviceControlActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by LSY1 on 2017-08-31.
 */

public class bio_activity extends Activity {

    AccSlidingCollection asc = new AccSlidingCollection();
    public static final int DUMP = -1;

    //TODO Gesture sensing variable
    public static String b_gyro1;
    public static String b_gyro2;
    public static String b_gyro3;

    public static String b_acc1;
    public static String b_acc2;
    public static String b_acc3;

    public static int b_ac_x;
    public static int b_ac_y;
    public static int b_ac_z;

    public static int b_gy_x;
    public static int b_gy_y;
    public static int b_gy_z;

    //TODO BPM info
    public static String b_sleep_bpm;
    public static String b_discount_bpm;

    public static int b_bpm_a;
    public static int b_bpm_b;

    private TimerTask myTask;
    private Timer timer;

    public static Short HeartRate;


    //TODO BLE HEX data receive
    private boolean mConnected = false;
    public static byte[] packet;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;


    ImageView normal_mode, sleep_mode, tired_mode;
    Button mode_btn, sleep_btn, tired_btn;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            asc.setBandFlag(H_BAND);
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            }

            else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final byte[] txValue = intent.getByteArrayExtra(mBluetoothLeService.EXTRA_DATA);
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            H_getStringPacket (txValue);
                        } catch (Exception e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }
        }
    };


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.bio_status);

        myTask = new TimerTask() {
            public void run() {

             new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView level = (TextView) findViewById(R.id.level_textView);
                                TextView bpm = (TextView) findViewById(R.id.bpm_textView);
                                TextView condition = (TextView) findViewById(R.id.condition_textView);
                                bpm.setText("h_band_HeartRate " + " bpm");

                            }
                        });
                    }
                }).start();
            }
        };
        timer = new Timer();
        timer.schedule(myTask, 0, 100); // 0초후 첫실행, 0.1초마다 계속실행

    }


    //TODO BLE Packet receive
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            //Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        Log.d("test", "onDstory()");
        timer.cancel();
        super.onDestroy();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    public void displayData(byte[] packet) {

        //getStringPacket(DeviceControlActivity.packet);

    }


    private int H_byteToShort (byte b1, byte b2)  {
        return (b1 & 0xff) << 8 | (b2 & 0xff);
    }

    private String H_getStringPacket(byte[] packet) {
        StringBuilder sb = new StringBuilder(packet.length * 2);

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
                            if (DeviceControlActivity.final_can == 1 && DeviceControlActivity.final_wheel == 0) {
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
                            //sb.append(" <<<< SIDE >>>> \n");
                            System.out.println("+++++++++++++++++ RIGHT +++++++++++++++++");
                            if (DeviceControlActivity.final_can == 1 && DeviceControlActivity.final_wheel == 0) {
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
                            //sb.append ("[[[ FRONT ]\n");
                            System.out.println("+++++++++++++++++ FRONT +++++++++++++++++");
                            if (DeviceControlActivity.final_can == 1 && DeviceControlActivity.final_wheel == 0) {
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
                            if (DeviceControlActivity.final_can == 1 && DeviceControlActivity.final_wheel == 0) {
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

                        case CLOCK:
                            //sb.append (" **** CLOCK\n");
                            System.out.println("+++++++++++++++++ CLOCK +++++++++++++++++");
                            if (DeviceControlActivity.cal_316 >= 10 || DeviceControlActivity.cal_2B0 >= 10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else if (DeviceControlActivity.cal_316 == -10 || DeviceControlActivity.cal_2B0 == -10){
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
                            //sb.append (" **** ANTI CLOCK\n");
                            if (DeviceControlActivity.cal_316 >= 10 || DeviceControlActivity.cal_2B0 >= 10) {
                                System.out.println("*************************** No event ***************************");
                                Toast toast = Toast.makeText(this, "                                                                                    .", Toast.LENGTH_SHORT);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                LinearLayout view = (LinearLayout) toast.getView();
                                ImageView image = new ImageView(getApplicationContext());
                                image.setImageResource(R.drawable.warning);
                                view.addView(image, 0);
                                toast.show();
                            }
                            else if (DeviceControlActivity.cal_316 == -10 || DeviceControlActivity.cal_2B0 == -10){
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
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_SEND_PACKET);
        return intentFilter;
    }

    public static final int FRONT = 0;
    public static final int BACK = FRONT + 1;	//1
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
