package keti.gto.android.bluetoothlegatt.music_folder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import keti.gto.android.bluetoothlegatt.R;

import java.io.IOException;

import keti.gto.android.bluetoothlegatt.ble_folder.AccSlidingCollection;
import keti.gto.android.bluetoothlegatt.ble_folder.BluetoothLeService;
import keti.gto.android.bluetoothlegatt.ble_folder.DeviceControlActivity;

import static android.content.ContentValues.TAG;

/**
 * Created by LSY1 on 2017-09-21.
 */

public class music_play3_activity extends Activity{
    SeekBar seekbar;
    MediaPlayer music;
    MediaPlayer music_wjsn;
    MediaPlayer music_lvlz;
    int i = 0;
    ImageView play1;

    AccSlidingCollection asc = new AccSlidingCollection();
    public static final int DUMP = -1;

    //TODO Gesture sensing variable

    public static String b_length;
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

    public static int count;

    private boolean mConnected = false;
    public static byte[] packet;
    private TextView mConnectionState;
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_play3);

        music = MediaPlayer.create(this, R.raw.twice_signal);
        music_wjsn = MediaPlayer.create(this, R.raw.apink_five);
        music_lvlz = MediaPlayer.create(this, R.raw.girlfriend_lovewhisper);

        music.setLooping(true);
        music_wjsn.setLooping(true);
        music_lvlz.setLooping(true);

        seekbar = (SeekBar) findViewById(R.id.seekBar1);
        seekbar.setMax(music.getDuration());
        seekbar.setMax(music_wjsn.getDuration());
        seekbar.setMax(music_lvlz.getDuration());

        //final ImageView basic = (ImageView) findViewById(R.id.basic);
        play1 = (ImageView) findViewById(R.id.play1);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub

                if(fromUser)
                    music.seekTo(progress);
                if(fromUser)
                    music_wjsn.seekTo(progress);
                if(fromUser)
                    music_lvlz.seekTo(progress);
            }

        });


    }

    public void Thread(){
        Runnable task = new Runnable(){
            public void run(){

                while(music.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    seekbar.setProgress(music.getCurrentPosition());
                }

                while(music_wjsn.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    seekbar.setProgress(music_wjsn.getCurrentPosition());
                }

                while(music_lvlz.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    seekbar.setProgress(music_lvlz.getCurrentPosition());
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void onClickPlay (View view) {
        music_lvlz.start();
        Thread();
        play1.setImageResource(R.drawable.pause_icon);
    }

    public void onClickPause (View view) {
        music_lvlz.pause();
        play1.setImageResource(R.drawable.play_icon);

    }

    public void onClickStop (View view) {
        music_lvlz.stop();
        try {
            music_lvlz.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        music_lvlz.seekTo(0);
        seekbar.setProgress(0);
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


    private String getStringPacket(byte[] packet) {

        if (packet [1] != -32) { //exception handling
            return null;
        }

        String hexString = "";

        for (byte b : packet) {  						//readBuf -> Hex
            hexString += Integer.toString((b & 0xF0) >> 4, 16);
            hexString += Integer.toString(b & 0x0F, 16);
        }
        int [] recv_sensors = new int [6];

        // TODO : GYRO X Y Z HEX
        b_gyro1 = hexString.substring(6, 10);
        b_gyro2 = hexString.substring(10, 14);
        b_gyro3 = hexString.substring(14, 18);

        // TODO : ACC X Y Z HEX
        b_acc1 = hexString.substring(18, 22);
        b_acc2 = hexString.substring(22, 26);
        b_acc3 = hexString.substring(26, 30);

        // TODO : ACC X Y Z Decimal
        b_ac_x = (short) Integer.parseInt(b_acc1, 16);
        b_ac_y = (short) Integer.parseInt(b_acc2, 16);
        b_ac_z = (short) Integer.parseInt(b_acc3, 16);

        // TODO : GYRO X Y Z Decimal
        b_gy_x = (short) Integer.parseInt(b_gyro1, 16);
        b_gy_y = (short) Integer.parseInt(b_gyro2, 16);
        b_gy_z = (short) Integer.parseInt(b_gyro3, 16);

        recv_sensors [0] = b_ac_x;
        recv_sensors [1] = b_ac_y;
        recv_sensors [2] = b_ac_z;

        recv_sensors [3] = b_gy_x;
        recv_sensors [4] = b_gy_x;
        recv_sensors [5] = b_gy_x;

        StringBuilder sb = new StringBuilder(packet.length * 2);

        int result_ = asc.SlidingCollectionInterface (recv_sensors);
        if (result_ != DUMP) {
            Log.d (TAG, "In IF branch");

            asc.gesture_count ++;
            //sb.append(String.valueOf(asc.gesture_count));
            //sb.append(" \t ");

            switch (result_)	{

                case LEFT:
                    System.out.println("+++++++++++++++++ LEFT +++++++++++++++++");
                    break;

                case RIGHT:
                    //sb.append(" <<<< SIDE >>>> \n");
                    System.out.println("+++++++++++++++++ RIGHT +++++++++++++++++");
                    break;

                case FRONT:
                    //sb.append ("[[[ FRONT ]\n");
                    System.out.println("+++++++++++++++++ FRONT +++++++++++++++++");
                    music_lvlz.start();
                    Thread();
                    play1.setImageResource(R.drawable.pause_icon);

                    break;

                case UP:
                    //sb.append (" ^^^^ UP\n");
                    System.out.println("+++++++++++++++++ UP +++++++++++++++++");

                    Runtime runtime3 = Runtime.getRuntime();
                    Process process3;
                    String res3 = "input keyevent 4";
                    music_lvlz.stop();
                    try {
                        music_lvlz.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    music_lvlz.seekTo(0);
                    seekbar.setProgress(0);
                    try {

                        process3 = runtime3.exec(res3); //2번 실행해야 되는 경우가 있음
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("Process Manager", "Unable to execute top command");
                    }



                    break;

                case CLOCK:
                    //sb.append (" **** CLOCK\n");
                    System.out.println("+++++++++++++++++ CLOCK +++++++++++++++++");

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

                    break;
                case ANTI_CLOCK:
                    //sb.append (" **** ANTI CLOCK\n");
                    System.out.println("+++++++++++++++++ ANTI CLOCK +++++++++++++++++");

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

                    break;

                default:
                    //sb.append (" CAN'T DETECT\n"); 	break;

            }	//switch


            // sb.append(String.valueOf(result_));
            //sb.append("\n");
        }
        return sb.toString();
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
                            break;

                        case RIGHT:
                            //sb.append(" <<<< SIDE >>>> \n");
                            System.out.println("+++++++++++++++++ RIGHT +++++++++++++++++");
                            break;

                        case FRONT:
                            //sb.append ("[[[ FRONT ]\n");
                            System.out.println("+++++++++++++++++ FRONT +++++++++++++++++");
                            music.start();
                            Thread();
                            play1.setImageResource(R.drawable.pause_icon);

                            break;

                        case UP:
                            //sb.append (" ^^^^ UP\n");
                            System.out.println("+++++++++++++++++ UP +++++++++++++++++");
                            if (DeviceControlActivity.final_can == 1 && DeviceControlActivity.final_wheel == 0) {
                                Runtime runtime3 = Runtime.getRuntime();
                                Process process3;
                                String res3 = "input keyevent 4";
                                music.stop();
                                try {
                                    music.prepare();
                                } catch (IllegalStateException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                music.seekTo(0);
                                seekbar.setProgress(0);
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
                            System.out.println("+++++++++++++++++ ANTI CLOCK +++++++++++++++++");
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
                Short HeartRate = (short)H_byteToShort(packet [4], packet [5]);


                break;
            case 2:
            /* ---------------------------------------------------------- Walking data */
                Short WalkingCount = (short)H_byteToShort(packet [4], packet [5]);

                break;
            case 3:
            /* ---------------------------------------------------------- Battery */
                Short Battery = (short)H_byteToShort(packet [4], packet [5]);

                break;
            case 4:
            /* ---------------------------------------------------------- Calory */
                Short Calory = (short)H_byteToShort(packet [4], packet [5]);

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
