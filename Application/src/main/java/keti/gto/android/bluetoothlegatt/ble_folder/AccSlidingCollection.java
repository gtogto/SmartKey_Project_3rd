package keti.gto.android.bluetoothlegatt.ble_folder;

import android.util.Log;

public class AccSlidingCollection {
    public int gesture_count = 0;

    public void setBandFlag (byte band)   {
        this.band_flag = band;
    }
    private int bax = 40000;
    private int bay = 0;
    private int baz = 0;

    private int X_CALM = 0;
    private int Y_CALM = 0;
    private int Z_CALM = 0;

    public byte band_flag = -1;


    private int brk_term = 0;
    private boolean processFlag = false;

    public GestureRecognition GR = new GestureRecognition();


    private int [][] average_buffer = { {X_INIT, X_INIT, X_INIT, X_INIT}, {Y_INIT, Y_INIT, Y_INIT, Y_INIT}, {Z_INIT, Z_INIT, Z_INIT, Z_INIT}};
    private int [][] sensors = new int [GR.AXIS_NUM][GR.SAMPLE_NUM];



    private int counter = 0;

    ///////////////////////////////////////////////////////////////////////
    public int SlidingCollectionInterface (int [] recv)	{
        ///////////////////////////////////////////////////////////////////////
        int result_ = -1;

        if (bax == 40000)    {
            if (band_flag == 0) {//H_BAND
                bax = H_X_INIT;
                bay = H_Y_INIT;
                baz = H_Z_INIT;

                X_CALM = H_X_INIT;
                Y_CALM = H_Y_INIT;
                Z_CALM = H_Z_INIT;

                BRAKE_TERM = 15;

            } else if (band_flag == 1)  {   //PARTRON
                bax = X_INIT;
                bay = Y_INIT;
                baz = Z_INIT;

                X_CALM = X_INIT;
                Y_CALM = Y_INIT;
                Z_CALM = Z_INIT;

                BRAKE_TERM = 30;
            }

        }


        if (brk_term != 0)	{		//check moving term

            brk_term --;
            AverageCalculator (recv );
            return result_;
        }

        if (!processFlag  && ThresholdCheck(recv))	{	//hit the threshold
            processFlag = SaveSlidedElement ();
        } //if
        else
            AverageCalculator (recv );


        sensors [GR.ACC_X][counter] = recv [GR.ACC_X];
        sensors [GR.ACC_Y][counter] = recv [GR.ACC_Y];
        sensors [GR.ACC_Z][counter ++] = recv [GR.ACC_Z];



        if (counter == GR.SAMPLE_NUM)	{		//collect sample to SAMPLE_NUM
            counter = 0;

            if (processFlag)	{	// hit && collect
                processFlag = false;


                String hexString = "";
                StringBuilder sb = new StringBuilder();

                //THIS LOOP IS INSERTED JUST FOR DEBUGING
                for (int nLoop = 0; nLoop <= GR.ACC_Z; nLoop ++) {
                    for (int nLoop2 = 0; nLoop2 < GR.SAMPLE_NUM; nLoop2 ++) {
                        //Log.d (TAG, sensors [nLoop][nLoop2] + " ");
                        sb.append(sensors [nLoop][nLoop2] + " ");
                    }
                    sb.append("\n");

                }
                Log.d(TAG, sb.toString());

                GR.setBandFlag(band_flag);
                result_ = GR.gestureRecogInterface (sensors);

                brk_term = BRAKE_TERM;
            } //if

            bax = X_CALM;	//set the calm state value
            bay = Y_CALM;
            baz = Z_CALM;

        } //if

        return result_;
    } //function SlidingCollectionInterface




    ///////////////////////////////////////////////////////////////////////
    private boolean ThresholdCheck	(int [] recv)	{
        ///////////////////////////////////////////////////////////////////////
        boolean reutrn_ = false;

        if (((recv [GR.ACC_X] > X_CALM + MOVE_THRES) || (recv [GR.ACC_X] <  X_CALM - MOVE_THRES)) && ( Math.abs(Math.abs(recv [GR.ACC_X]) - Math.abs(bax)) > GAP_THRES))	{
            Log.d (TAG, "--------------------- X -------------- "+counter +" "+ Math.abs(Math.abs(recv [GR.ACC_X]) - Math.abs(bax)) + " " + bax);
            reutrn_ = true;
        }
        else if ((( recv [GR.ACC_Y] >  Y_CALM + MOVE_THRES) || (recv [GR.ACC_Y] < Y_CALM - MOVE_THRES)) && (Math.abs( Math.abs(recv [GR.ACC_Y]) - Math.abs(bay)) > GAP_THRES))	{
            Log.d (TAG, "--------------------- Y -------------- "+counter+" "+ Math.abs(Math.abs(recv [GR.ACC_Y]) - Math.abs(bay))+ " " + bay);
            reutrn_ = true;
        }
        else if (((recv [GR.ACC_Z] >  Z_CALM + MOVE_THRES) || (recv [GR.ACC_Z] < Z_CALM - MOVE_THRES)) && (Math.abs( Math.abs(recv [GR.ACC_Z]) - Math.abs(baz)) > GAP_THRES))	{
            Log.d (TAG, "--------------------- Z -------------- "+counter);
            reutrn_ = true;
        }

        return reutrn_;
    } // function ThresholdCheck



    ///////////////////////////////////////////////////////////////////////
    private boolean SaveSlidedElement ()	{
        ///////////////////////////////////////////////////////////////////////
        int nLoop, nLoop2;

        if (counter > BACKUP_SAMPLE)	{ 	//bigger than 5th sample
            for (nLoop = 0; nLoop < GR.AXIS_NUM; nLoop ++)	{
                for (nLoop2 = 0; nLoop2 < BACKUP_SAMPLE; nLoop2 ++)	{
                    sensors [nLoop][nLoop2] = sensors [nLoop][counter - BACKUP_SAMPLE + nLoop2];

                } //for nLoop2
            } //for nLoop
        } //else if

        else if (counter < BACKUP_SAMPLE)	{	//smaller than 5th sample

            for (nLoop = 0; nLoop < GR.AXIS_NUM; nLoop ++)	{

                for (nLoop2 = 1; nLoop2 <= counter; nLoop2 ++)	{
                    sensors [nLoop][BACKUP_SAMPLE - nLoop2] = sensors [nLoop][counter - nLoop2];
                } //for nLoop2
                int tempInt = BACKUP_SAMPLE - counter;
                for (nLoop2 = 0; nLoop2 < tempInt; nLoop2 ++)	{
                    sensors [nLoop][tempInt - nLoop2 - 1] = sensors [nLoop][GR.SAMPLE_NUM -1 - nLoop2];

                } //for nLoop2

            } //for nLoop
        } //else if

        counter  = BACKUP_SAMPLE;

        return true;
    } //function SaveSlidedElement
/*
    private float X_ESTt = X_INIT;
    private float Y_ESTt = Y_INIT;
    private float Z_ESTt = Z_INIT;



    private float X_Eest = 3000;
    private float Y_Eest = 3000;
    private float Z_Eest = 3000;


    private void KalmanFilter ()    {

        //Error in measurement = 200
        int Emea = 2000;
        float KG = 0;

        KG = X_Eest / (X_Eest + Emea);
        X_ESTt = X_ESTt + KG;
        //printf ("KG: %d(%d),", (int)(KG*100000), (int)((X_Eest / (X_Eest + 200))*10000));
        X_Eest = (1-KG) * X_Eest;

        KG = Y_Eest / (Y_Eest + Emea);
        Y_ESTt = Y_ESTt + KG;
        //printf ("%d(%d),", (int)(KG*100000), (int)((Y_Eest / (Y_Eest + 200))*10000));
        Y_Eest = (1-KG) * Y_Eest;


        KG = Z_Eest / (Z_Eest + Emea);
        Z_ESTt = Z_ESTt + KG;
       // printf ("%d(%d)\t", (int)(KG*100000), (int)((Z_Eest / (Z_Eest + 200))*10000));
        Z_Eest = (1-KG) * Z_Eest;

        X_CALM = (int)(X_ESTt);
        Y_CALM = (int)(Y_ESTt);
        Z_CALM = (int)(Z_ESTt);

      //  Log.d (TAG,  "KF: " + X_CALM + " " + Y_CALM + " "+ Z_CALM);
    }
*/



    private void AverageCalculator (int [] recv) {
        int ITER_VALUE = BACKUP_SAMPLE - 1;
        for (int nLoop = GR.ACC_X; nLoop <  ITER_VALUE; nLoop ++)  {
            average_buffer [GR.ACC_X][nLoop] = average_buffer [GR.ACC_X][nLoop +1];
            average_buffer [GR.ACC_Y][nLoop] = average_buffer [GR.ACC_Y][nLoop +1];
            average_buffer [GR.ACC_Z][nLoop] = average_buffer [GR.ACC_Z][nLoop +1];
        }
        average_buffer [GR.ACC_X][ITER_VALUE] = recv [GR.ACC_X];
        average_buffer [GR.ACC_Y][ITER_VALUE] = recv [GR.ACC_Y];
        average_buffer [GR.ACC_Z][ITER_VALUE] = recv [GR.ACC_Z];


        X_CALM = Y_CALM = Z_CALM = 0;
        for (int nLoop = GR.ACC_X; nLoop <=  ITER_VALUE; nLoop ++)  {
            X_CALM += average_buffer [GR.ACC_X][nLoop];
            Y_CALM += average_buffer [GR.ACC_Y][nLoop];
            Z_CALM += average_buffer [GR.ACC_Z][nLoop];;
        }
        X_CALM /=  BACKUP_SAMPLE;
        Y_CALM /=  BACKUP_SAMPLE;
        Z_CALM /=  BACKUP_SAMPLE;

        // Log.d (TAG,  "AVERAGE: " + X_CALM + " " + Y_CALM + " "+ Z_CALM);

    }



    ///////////////////////////////////////////////////////////////////////
    //static parameters
    ///////////////////////////////////////////////////////////////////////

    private static final String TAG = "ASC";

    private static int X_INIT = 0;
    private static int Y_INIT = 0;
    private static int Z_INIT = -16000;

    private static int H_X_INIT = 300;
    private static int H_Y_INIT = -3700;
    private static int H_Z_INIT = 7400;


    private static int MOVE_THRES = 2000;
    private static int GAP_THRES = 4000;

    private static int BRAKE_TERM = 0;
    private static int BACKUP_SAMPLE = 4;

}

// Partron Band data Function
/*
import android.util.Log;

public class AccSlidingCollection {
    public int gesture_count = 0;

    private int bax = X_INIT;
    private int bay = Y_INIT;
    private int baz = Z_INIT;

    private int X_CALM = X_INIT;
    private int Y_CALM = Y_INIT;
    private int Z_CALM = Z_INIT;


    private int brk_term = 0;
    private boolean processFlag = false;

    public GestureRecognition GR = new GestureRecognition();

    private int [][] average_buffer = { {X_INIT, X_INIT, X_INIT, X_INIT}, {Y_INIT, Y_INIT, Y_INIT, Y_INIT}, {Z_INIT, Z_INIT, Z_INIT, Z_INIT}};
    private int [][] sensors = new int [GR.AXIS_NUM][GR.SAMPLE_NUM];

    private int counter = 0;

    ///////////////////////////////////////////////////////////////////////
    public int SlidingCollectionInterface (int [] recv)	{
        ///////////////////////////////////////////////////////////////////////
        int result_ = -1;



        if (brk_term != 0)	{		//check moving term

            brk_term --;
            AverageCalculator (recv );
            return result_;
        }

        if (!processFlag  && ThresholdCheck(recv))	{	//hit the threshold
            processFlag = SaveSlidedElement ();
        } //if
        else
            AverageCalculator (recv );


        sensors [GR.ACC_X][counter] = recv [GR.ACC_X];
        sensors [GR.ACC_Y][counter] = recv [GR.ACC_Y];
        sensors [GR.ACC_Z][counter ++] = recv [GR.ACC_Z];



        if (counter == GR.SAMPLE_NUM)	{		//collect sample to SAMPLE_NUM
            counter = 0;

            if (processFlag)	{	// hit && collect
                processFlag = false;

                result_ = GR.gestureRecogInterface (sensors);

                brk_term = BRAKE_TERM;
            } //if

            bax = X_CALM;	//set the calm state value
            bay = Y_CALM;
            baz = Z_CALM;

        } //if

        return result_;
    } //function SlidingCollectionInterface




    ///////////////////////////////////////////////////////////////////////
    private boolean ThresholdCheck	(int [] recv)	{
        ///////////////////////////////////////////////////////////////////////
        boolean reutrn_ = false;

        if (((recv [GR.ACC_X] > X_CALM + MOVE_THRES) || (recv [GR.ACC_X] <  X_CALM - MOVE_THRES)) && ( Math.abs(Math.abs(recv [GR.ACC_X]) - Math.abs(bax)) > GAP_THRES))	{
            Log.d (TAG, "--------------------- X -------------- "+counter +" "+ Math.abs(Math.abs(recv [GR.ACC_X]) - Math.abs(bax)) + " " + bax);
            reutrn_ = true;
        }
        else if ((( recv [GR.ACC_Y] >  Y_CALM + MOVE_THRES) || (recv [GR.ACC_Y] < Y_CALM - MOVE_THRES)) && (Math.abs( Math.abs(recv [GR.ACC_Y]) - Math.abs(bay)) > GAP_THRES))	{
            Log.d (TAG, "--------------------- Y -------------- "+counter+" "+ Math.abs(Math.abs(recv [GR.ACC_Y]) - Math.abs(bay))+ " " + bay);
            reutrn_ = true;
        }
        else if (((recv [GR.ACC_Z] >  Z_CALM + MOVE_THRES) || (recv [GR.ACC_Z] < Z_CALM - MOVE_THRES)) && (Math.abs( Math.abs(recv [GR.ACC_Z]) - Math.abs(baz)) > GAP_THRES))	{
           Log.d (TAG, "--------------------- Z -------------- "+counter);
            reutrn_ = true;
        }

        return reutrn_;
    } // function ThresholdCheck



    ///////////////////////////////////////////////////////////////////////
    private boolean SaveSlidedElement ()	{
        ///////////////////////////////////////////////////////////////////////
        int nLoop, nLoop2;

        if (counter > BACKUP_SAMPLE)	{ 	//bigger than 5th sample
            for (nLoop = 0; nLoop < GR.AXIS_NUM; nLoop ++)	{
                for (nLoop2 = 0; nLoop2 < BACKUP_SAMPLE; nLoop2 ++)	{
                    sensors [nLoop][nLoop2] = sensors [nLoop][counter - BACKUP_SAMPLE + nLoop2];

                } //for nLoop2
            } //for nLoop
        } //else if

        else if (counter < BACKUP_SAMPLE)	{	//smaller than 5th sample

            for (nLoop = 0; nLoop < GR.AXIS_NUM; nLoop ++)	{

                for (nLoop2 = 1; nLoop2 <= counter; nLoop2 ++)	{
                    sensors [nLoop][BACKUP_SAMPLE - nLoop2] = sensors [nLoop][counter - nLoop2];
                } //for nLoop2
                int tempInt = BACKUP_SAMPLE - counter;
                for (nLoop2 = 0; nLoop2 < tempInt; nLoop2 ++)	{
                    sensors [nLoop][tempInt - nLoop2 - 1] = sensors [nLoop][GR.SAMPLE_NUM -1 - nLoop2];

                } //for nLoop2

            } //for nLoop
        } //else if

        counter  = BACKUP_SAMPLE;

        return true;
    } //function SaveSlidedElement

    private float X_ESTt = X_INIT;
    private float Y_ESTt = Y_INIT;
    private float Z_ESTt = Z_INIT;

    private float X_Eest = 3000;
    private float Y_Eest = 3000;
    private float Z_Eest = 3000;



    private void AverageCalculator (int [] recv) {
        int ITER_VALUE = BACKUP_SAMPLE - 1;
        for (int nLoop = GR.ACC_X; nLoop <  ITER_VALUE; nLoop ++)  {
            average_buffer [GR.ACC_X][nLoop] = average_buffer [GR.ACC_X][nLoop +1];
            average_buffer [GR.ACC_Y][nLoop] = average_buffer [GR.ACC_Y][nLoop +1];
            average_buffer [GR.ACC_Z][nLoop] = average_buffer [GR.ACC_Z][nLoop +1];
        }
        average_buffer [GR.ACC_X][ITER_VALUE] = recv [GR.ACC_X];
        average_buffer [GR.ACC_Y][ITER_VALUE] = recv [GR.ACC_Y];
        average_buffer [GR.ACC_Z][ITER_VALUE] = recv [GR.ACC_Z];


        X_CALM = Y_CALM = Z_CALM = 0;
        for (int nLoop = GR.ACC_X; nLoop <=  ITER_VALUE; nLoop ++)  {
            X_CALM += average_buffer [GR.ACC_X][nLoop];
            Y_CALM += average_buffer [GR.ACC_Y][nLoop];
            Z_CALM += average_buffer [GR.ACC_Z][nLoop];;
        }
        X_CALM /=  BACKUP_SAMPLE;
        Y_CALM /=  BACKUP_SAMPLE;
        Z_CALM /=  BACKUP_SAMPLE;

       // Log.d (TAG,  "AVERAGE: " + X_CALM + " " + Y_CALM + " "+ Z_CALM);

    }



    ///////////////////////////////////////////////////////////////////////
    //static parameters
    ///////////////////////////////////////////////////////////////////////

    private static final String TAG = "ASC";

    private static int X_INIT = 0;
    private static int Y_INIT = 0;
    private static int Z_INIT = -16000;

    private static int MOVE_THRES = 3000;
    private static int GAP_THRES = 5000;

    private static int BRAKE_TERM = 20;
    private static int BACKUP_SAMPLE = 4;

}
*/