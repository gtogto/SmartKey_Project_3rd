package keti.gto.android.bluetoothlegatt.smartKey_folder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import keti.gto.android.bluetoothlegatt.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by LSY1 on 2017-09-01.
 */

public class smartKey_activity extends Activity{

    ImageView close_door_icon;
    Button close_btn, open_btn;
    int i = 0;

    //TODO: CAN read data
    public static int mn_door_handle;
    public static int mn_door_open;
    public static int mn_door_close;
    public static int ucomm_door_open;
    public static int ucomm_door_close;
    private TextView door_states_mn;
    private TextView door_states_ucomm;

    private TimerTask myTask;
    private Timer timer;


    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.smart_key);

        close_door_icon = (ImageView) findViewById(R.id.door_close_icon);

        myTask = new TimerTask() {
            public void run() {

                door_state();
                door_state_ucomm();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mn_door_handle == 0 && mn_door_open == 04) {
                                    if (mn_door_close == 32) {
                                        close_door_icon.setImageResource(R.drawable.door_closing);
                                    }
                                    else if (mn_door_close == 16) {
                                        close_door_icon.setImageResource(R.drawable.door_opening);
                                    }
                                }

                                else if (ucomm_door_open == 02) {
                                    if (ucomm_door_close == 32) {
                                        close_door_icon.setImageResource(R.drawable.door_closing);
                                    }
                                    else if (ucomm_door_close == 80) {
                                        close_door_icon.setImageResource(R.drawable.door_opening);
                                    }
                                }

                                else if (mn_door_handle == 10 && mn_door_open == 04) {
                                    System.out.println("input door_handle: " + mn_door_handle);
                                    close_door_icon.setImageResource(R.drawable.door_opening);
                                }

                                else    close_door_icon.setImageResource(R.drawable.door_closing);

                            }
                        });
                    }
                }).start();
            }
        };
        timer = new Timer();
        timer.schedule(myTask, 0, 1000); // 0초후 첫실행, 1초마다 계속실행

    }

    @Override
    protected void onDestroy() {
        Log.d("test", "onDstory()");
        timer.cancel();
        super.onDestroy();
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
                String b_data_0 = line1.substring(19, 21);
                String b_data_1 = line1.substring(22, 24);      // normal hex data
                String b_data_2 = line1.substring(25, 27);      // error hex data

                int b_hex0 = Integer.parseInt(b_data_0, 16);    //mn handle data
                int b_hex1 = Integer.parseInt(b_data_1, 16);    // normal int data
                int b_hex2 = Integer.parseInt(b_data_2, 16);    // error int data

                mn_door_handle = b_hex0;
                mn_door_open = b_hex1;
                mn_door_close = b_hex2;

            }
            //System.out.println(" readr to ./door_state.sh " + " -> " + "mn_open value : "+mn_door_open + " **** " +"mn_close value : "+mn_door_close);

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //TODO read to last CAN DATA(ucomm_band info)
    private void door_state_ucomm()	{
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
                String b_data_1 = line1.substring(22, 24);      // normal hex data
                String b_data_2 = line1.substring(25, 27);      // error hex data
                int b_hex1 = Integer.parseInt(b_data_1, 16);    // normal int data
                int b_hex2 = Integer.parseInt(b_data_2, 16);    // error int data

                ucomm_door_open = b_hex1;  // 533#00 , 533#01 <- door state
                ucomm_door_close = b_hex2;   // 533#01.00 , 533#01.01 <- open, close state

                System.out.println("line1 : " + line1);
                System.out.println(" readr to ./ucomm_band.sh " + " -> " + "ucomm_open value : "+b_hex1 + " **** " +"ucomm_close value : "+b_hex2);

            }

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }
    }



}
