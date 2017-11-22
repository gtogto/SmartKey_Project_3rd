package keti.gto.android.bluetoothlegatt.ble_folder;

/**
 * Created by LSY1 on 2017-08-11.
 */

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import keti.gto.android.bluetoothlegatt.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

public class can_main_activity  extends Activity{

    public static int final_can;	// 마지막 SPEED 값 저장 <전역번수>
    public static int before_can;	// 마지막 이전 SPEED 값 저장 <전역번수>

    public static int before_wheel; // 마지막 WHEEL 값 저장 <전역번수>
    public static int final_wheel;  // 마지막 이전 WHEEL 값 저장 <전역번수>

    public static int light_CAN;    // 마지막 LIGHT 값 저장 <전역번수>

    public static int obd_1;    //  433#01  engine
    public static int obd_2;    //  433#00.01   brake
    public static int obd_3;    //  433#00.00.01    light
    public static int obd_4;    //  433#00.00.00.01 accelerator
    public static int obd_5;    //  433#00.00.00.00.01  tire

    public static int open_door_state;
    public static int close_door_state;

    public static int cal_2B0;
    public static int cal_316;

    public final static String TAG = "SM";

    //TODO Check CAN value {speed, wheel, light}
    private TextView speedValue;
    private TextView wheelValue;
    private TextView lightValue;

    //TODO Check for state
    private TextView engineState;
    private TextView accState;
    private TextView wheelState;
    private TextView lightState;
    private TextView door_states;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.can_main);

        final ImageView ecu_all;
        ecu_all = (ImageView) findViewById(R.id.all_ecu);

        final ImageButton btnBreak, btnPower, btnLight, btnAcc, btnTire;

        btnBreak = (ImageButton) findViewById(R.id.btnBreak);
        btnPower = (ImageButton) findViewById(R.id.btnPower);
        btnLight = (ImageButton) findViewById(R.id.btnLight);
        btnAcc = (ImageButton) findViewById(R.id.btnAcc);
        btnTire = (ImageButton) findViewById(R.id.btnTire);

        //TODO: Run to Check_state function
        TimerTask myTask = new TimerTask() {
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        check_state();
                    }
                }).start();
            }
        };
        Timer timer = new Timer();
        timer.schedule(myTask, 0, 5000); // 0초후 첫실행, 5초마다 계속실행

        //TODO: CHECK OBD Status Button
        //Brake state
        btnBreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obd_2 == 1) {
                    ecu_all.setImageResource(R.drawable.err_can_break_ecu_img);
                }
                else {
                    ecu_all.setImageResource(R.drawable.can_break_ecu_img);
                }
            }
        });

        //Powertrain value
        btnPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obd_1 == 1) {
                    ecu_all.setImageResource(R.drawable.err_can_power_ecu_img);
                }
                else {
                    ecu_all.setImageResource(R.drawable.can_power_ecu_img);
                }

            }
        });

        //Headlight value
        btnLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obd_3 == 1) {
                    ecu_all.setImageResource(R.drawable.err_can_light_ecu_img);
                }
                else {
                    ecu_all.setImageResource(R.drawable.can_light_ecu_img);
                }
            }
        });

        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obd_4 == 1) {
                    ecu_all.setImageResource(R.drawable.err_can_acc_ecu_img);
                }
                else {
                    ecu_all.setImageResource(R.drawable.can_acc_ecu_img);
                }
            }
        });

        btnTire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obd_5 == 1) {
                    ecu_all.setImageResource(R.drawable.err_can_tire_ecu_img);
                }
                else {
                    ecu_all.setImageResource(R.drawable.can_tire_ecu_img);
                }
            }
        });

    }


    public void onClickStart (View v ) {

        speedValue.setText("" + final_can);
        wheelValue.setText("" + final_wheel);
        can_start();
    }

    public void can_start() {

    }

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
            //System.out.println("마지막 speed : "+final_can);
            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

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
            //System.out.println("마지막 이전 speed : "+before_can);
            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

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
                //System.out.println("HEX 1 : "+ b_data_1);
                int b_hex1 = Integer.parseInt(b_data_1, 16);
                before_wheel = b_hex1;
            }
            //System.out.println("마지막 이전 wheel : "+before_wheel);
            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }

    }

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
            //System.out.println("마지막 wheel : "+final_wheel);
            br.close();
        }

        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }

    }

    private void light_OnOff ()	{
        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
            String cmd = "./light_can.sh";
            process1 = runtime1.exec(cmd);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));
            String line1 = "";
            if ((line1 = br.readLine()) != null)
            {
                String b_data_1 = line1.substring(19, 21);
                int b_hex1 = Integer.parseInt(b_data_1, 16);
                /*
                System.out.println("light value : "+ b_data_1);
                if (b_hex1 == 0) {
                    System.out.println("------------------------- Light Off !");
                }
                if (b_hex1 == 1){
                    System.out.println("------------------------- Light On !");
                }*/
                light_CAN = b_hex1;

            }
            //System.out.println("마지막 wheel : "+final_wheel);
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }
    }

    private void check_state ()	{
        Runtime runtime1 = Runtime.getRuntime();
        Process process1;

        try
        {
            String cmd = "./check_state.sh";
            process1 = runtime1.exec(cmd);
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(process1.getInputStream()));
            String line1 = "";
            if ((line1 = br.readLine()) != null)
            {
                String b_data_1 = line1.substring(19, 21);      // offset 0 engine value
                String b_data_2 = line1.substring(22, 24);      // offset 1 brake value
                String b_data_3 = line1.substring(25, 27);      // offset 2 light value
                String b_data_4 = line1.substring(28, 30);      // offset 3 accelerator value
                String b_data_5 = line1.substring(31, 33);      // offset 4 tire value

                int b_hex1 = Integer.parseInt(b_data_1, 16);
                int b_hex2 = Integer.parseInt(b_data_2, 16);
                int b_hex3 = Integer.parseInt(b_data_3, 16);
                int b_hex4 = Integer.parseInt(b_data_4, 16);
                int b_hex5 = Integer.parseInt(b_data_5, 16);

                obd_1 = b_hex1;
                obd_2 = b_hex2;
                obd_3 = b_hex3;
                obd_4 = b_hex4;
                obd_5 = b_hex5;

            }
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

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
                String b_data_1 = line1.substring(22, 24);      // normal hex data
                String b_data_2 = line1.substring(25, 27);      // error hex data
                int b_hex1 = Integer.parseInt(b_data_1, 16);    // normal int data
                int b_hex2 = Integer.parseInt(b_data_2, 16);    // error int data
                //System.out.println("04 : "+ b_hex1);
                //System.out.println("뒤 HEX : "+ b_hex2);

                /*
                //System.out.println("normal state value : "+ b_hex1);
                //System.out.println("error state value : "+ b_hex2);

                if (b_hex1 == 0 && b_hex2 == 0) {
                    System.out.println("------------------------- Unrecognizable !!");
                }

                else if (b_hex1 == 1 && b_hex2 == 0) {
                    System.out.println("------------------------- ENGINE OK");
                }

                else if (b_hex1 == 1 && b_hex2 == 1) {
                    System.out.println("------------------------- ENGINE Error");
                }

                else if (b_hex1 == 2 && b_hex2 == 0) {
                    System.out.println("------------------------- Accelerator OK");
                }

                else if (b_hex1 == 2 && b_hex2 == 1) {
                    System.out.println("------------------------- Accelerator Error");
                }

                else if (b_hex1 == 3 && b_hex2 == 0) {
                    System.out.println("------------------------- Wheel OK");
                }

                else if (b_hex1 == 3 && b_hex2 == 1) {
                    System.out.println("------------------------- Wheel Error");
                }

                else if (b_hex1 == 4 && b_hex2 == 0) {
                    System.out.println("------------------------- Light OK");
                }

                else if (b_hex1 == 4 && b_hex2 == 1) {
                    System.out.println("------------------------- Light Error");
                }

                else {
                    System.out.println("failed to read check_state.sh!!");
                }
                */
                open_door_state = b_hex1;
                close_door_state = b_hex2;
                //System.out.println("door body 1: " + open_door_state);
                //System.out.println("door body 2: " + close_door_state);

            }
            //System.out.println("마지막 wheel : "+final_wheel);
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            //Log.e("Process Manager", "Unable to execute top command");
        }
    }


    private void calcul_316 () {

        if (final_can == 0) {
            cal_316 = 0;
        }
        else {
            cal_316 = final_can - before_can;
            System.out.println("Speed_GAP: "+ cal_316);
        }
    }

    private void calcul_2B0 () {

        if (final_wheel == 0) {
            cal_2B0 = 0;
        }
        else {
            cal_2B0 = final_wheel - before_wheel;
            System.out.println("Wheel_GAP: "+ cal_2B0);
        }

    }


}
