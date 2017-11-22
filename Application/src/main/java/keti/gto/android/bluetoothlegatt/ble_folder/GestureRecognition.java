package keti.gto.android.bluetoothlegatt.ble_folder;


import java.util.Arrays;
import android.util.Log;

public class GestureRecognition {


    GestureDataBase DB = new GestureDataBase();

    private int [][] WMA_SENSOR = new int [AXIS_NUM][WMA_SAMPLE_NUM];
    private int [][] SENSOR_tmep =  new int [AXIS_NUM][SAMPLE_NUM];

    private int [][] WMA_temp = new int [AXIS_NUM][WMA_SAMPLE_NUM];

    private byte [] WEIGHTED_AXIS = new byte [AXIS_NUM];
    private byte Motionflag_ = 0;
    public int [] gaps = {0,0,0,0,0,0};
    private int [] biggestValue;
    private int result_;
    public byte band_flag = 0;


    public void setBandFlag (byte band)   {
        this.band_flag = band;
    }



    ///////////////////////////////////////////////////////////////////////
    public int gestureRecogInterface (int [][] SENSOR)	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        result_ = UNKNOWN_;
        biggestValue = new int [GESTURE_NUM];
        int nLoop, nLoop2;

        //THIS LOOP IS INSERTED JUST FOR DEBUGING



        //GestureDB gesture;
        normalizingFunction(SENSOR);

        weightedMovingAverage(SENSOR_tmep);

 /*
        Log.d (TAG, "NORNALIZED ");
        StringBuilder sb1 = new StringBuilder();
        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < SAMPLE_NUM; nLoop2 ++)	{
                //    Log.d(TAG, "[" + nLoop + "][" +nLoop2 + "] " + SENSOR [nLoop][nLoop2]);
                sb1.append(SENSOR_tmep [nLoop][nLoop2]);
                sb1.append (" ");
            }
            sb1.append ("\n");
        }
        Log.d(TAG, sb1.toString());

        Log.d (TAG, "WEIGHTED ");
*/
        StringBuilder sb = new StringBuilder();
        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM; nLoop2 ++)	{
                //    Log.d(TAG, "[" + nLoop + "][" +nLoop2 + "] " + SENSOR [nLoop][nLoop2]);
                sb.append(WMA_SENSOR [nLoop][nLoop2]);
                sb.append (" ");
            }
            sb.append ("\n");
        }
        Log.d(TAG, sb.toString());

        validAxiesClassifier();

        result_ = groupClassifier();

        switch (result_)	{
            case DUMP:
                Log.d (TAG,"-------------------- DUMP--\n"); break;
            case LEFT:
                Log.i (TAG," <<<< LEFT <<< \n");
                break;
            case RIGHT:
                Log.i (TAG," >>>> RIGHT >>\n");
                break;
            case FRONT:
                Log.i (TAG,"[[[ FRONT ]\n");
                break;
            case BACK:
            case UP:
                Log.i (TAG," ^^^^ UP\n");
                break;
            /*case UP:
            case DOWN:
                result_ = UP;
                Log.i (TAG," ^^^^ UP\n");
                break;*/
            case CLOCK:
            case LOW_ANTI:
            case LOW_CLOCK:
                Log.i (TAG," **** CLOCK\n");
                break;
            case ANTI_CLOCK:
                Log.i (TAG," **** ANTI-CLOCK\n");
                break;
            case UNKNOWN_:
                Log.i (TAG," CAN'T DETECT\n"); 	break;
            default:
                Log.i (TAG," ELSE \n"); 	break;

        }	//switch



        return result_;
    } //function gestureRecogInterface



    ///////////////////////////////////////////////////////////////////////
    private void normalizingFunction (int [][] sensor){
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2;

        //Arrays.fill(SENSOR_tmep, 0);

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < SAMPLE_NUM; nLoop2 ++)	{

                SENSOR_tmep [nLoop][nLoop2] = (int)(((double)sensor [nLoop][nLoop2] / NORMALIZATION_FACTOR) * 1000);
                //Log.d(TAG, "["+nLoop+"/"+nLoop2+"]"+SENSOR_tmep [nLoop][nLoop2]);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////
    private void weightedMovingAverage (int [][] SENSOR)	{
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2, nLoop3;
        int temp_avr;

        // Arrays.fill(WMA_SENSOR, 0);

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM; nLoop2 ++)	{
                temp_avr = 0;
                for (nLoop3 = 0; nLoop3 < WMA_FACTOR; nLoop3 ++)	{
                    temp_avr +=  SENSOR [nLoop][nLoop2 + nLoop3];
                } //for nLoop3

                WMA_SENSOR [nLoop][nLoop2] = temp_avr / WMA_FACTOR;
            } //for nLoop2
        } //for nLoop
    }



    ///////////////////////////////////////////////////////////////////////
    private void validAxiesClassifier ()	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        int [] min_ = {10000,10000,30000, 10000, 10000, 10000};
        int [] max_ = {-10000, -10000,-1000, -10000, -10000,-1000};
        int [] minFlag = {0,0,0,0,0,0};
        int [] maxFlag = {0,0,0,0,0,0};
        int nLoop, nLoop2;

        Motionflag_ = 0;

        char temp_flag = 0;
        for (nLoop = ACC_X; nLoop <= ACC_Z; nLoop++ )	{

            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM; nLoop2 ++)	{

                if (min_[nLoop] > WMA_SENSOR [nLoop][nLoop2])	{
                    min_[nLoop] = WMA_SENSOR [nLoop][nLoop2];
                    minFlag [nLoop] |= 0x1;
                } else if (max_[nLoop] < WMA_SENSOR [nLoop][nLoop2])	{
                    max_[nLoop] = WMA_SENSOR [nLoop][nLoop2];
                    maxFlag [nLoop] |= 0x1;
                }
                minFlag[nLoop] = minFlag[nLoop] << 1;
                maxFlag[nLoop] = maxFlag[nLoop] << 1;

            } //for nLoop2

            gaps [nLoop] = max_ [nLoop] - min_ [nLoop];

            if (gaps [nLoop] > GAP_THRESHOLD)	{
                Motionflag_ |= 0x1;
                temp_flag ++;
                WEIGHTED_AXIS [nLoop] = 90;

            }

            Motionflag_ = (byte) (Motionflag_ << 1);

        } //for nLoop

        if (temp_flag == 3)	{
            int nLoop3 = 0, temp_min=2000, order = 0;
            for (nLoop3=0; nLoop3 < 3; nLoop3 ++ )	{

                if (gaps [nLoop3] < temp_min)	{
                    order = nLoop3;
                    temp_min = gaps [nLoop3];
                }

            }	//for nLoop3


            switch (order)	{
                case 0:
                    Motionflag_ = 0x6;
                    break;
                case 1:
                    Motionflag_ = 0xA;
                    WEIGHTED_AXIS [order] = 10;
                    temp_flag --;
                    break;
                case 2:
                    Motionflag_ = 0xC;
                    WEIGHTED_AXIS [order] = 10;
                    temp_flag --;
                    break;

            }	//switch
        }	//if

        if (temp_flag != 0) {
            for (nLoop = ACC_X; nLoop < AXIS_NUM; nLoop++) {
                WEIGHTED_AXIS[nLoop] /= temp_flag;
            }
        }

    }	//function validAxiesClassifier


    ///////////////////////////////////////////////////////////////////////
    private int groupClassifier ()	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        result_ = DUMP;
        int nLoop;

        // Arrays.fill(biggestValue, 0);


        if (Motionflag_ == 0)	{
            result_ = DUMP;
        }
        else {

            int temp_result=0, max=0;

            switch (Motionflag_ & 0x0E)	{

                case 0x0:
                    result_ = DUMP;
                    break;
                case 0x4:	//FRONT
                    Log.d(TAG, "flag is 4");
                    result_ = FRONT;
                    break;

                case 0x2:	//UP
                    Log.d(TAG, "flag is 2");
                    result_ = UP;
//                    result_ = assignDB_FRONT ();
                    break;
                case 0x8:		//SIDE
                    Log.d(TAG, "flag is 8");

                    result_ = RIGHT;
                    result_ = assignDB_RIGHT ();
                    result_ = FRONT;
                    result_ = assignDB_FRONT ();

                    for (nLoop = FRONT; nLoop <= UP; nLoop ++)	{
                        Log.d (TAG, "Value is " + biggestValue [nLoop]);

                        if (max < biggestValue [nLoop])	{
                            max = biggestValue [nLoop];
                            result_ = nLoop;
                        }
                    }

                    break;

                case 0xA:
                    Log.d(TAG, "flag is A");

                    if (gaps[0] + gaps[2] < 1700)	{
                        result_ = CLOCK;
                        temp_result = assignDB_CLOCK ();
                        result_ = FRONT;
                        temp_result = assignDB_FRONT ();
                        result_ = RIGHT;
                        temp_result = assignDB_RIGHT ();

                        for (nLoop = FRONT; nLoop < GESTURE_NUM; nLoop ++)	{
                            Log.d (TAG, "Value is" + biggestValue [nLoop]);

                            if (max < biggestValue [nLoop])	{
                                max = biggestValue [nLoop];
                                result_ = nLoop;
                            }
                        }
                    }
                    else	{
                        result_ = CLOCK;
                        result_ = assignDB_CLOCK ();
                    }

                    break;

                case 0xC:
                    Log.d(TAG, "flag is C");

                    result_ = FRONT;
                    temp_result  = assignDB_FRONT ();
                    result_ = RIGHT;
                    temp_result  = assignDB_RIGHT ();

                    for (nLoop = FRONT; nLoop < LEFT+1; nLoop ++)	{
                        Log.d (TAG, "Value is" + biggestValue [nLoop]);

                        if (max < biggestValue [nLoop])	{
                            max = biggestValue [nLoop];
                            result_ = nLoop;
                        } //if
                    }
                    break;

                default:

                    result_ = CLOCK;
                    temp_result = assignDB_CLOCK ();
                    result_ = RIGHT;
                    temp_result = assignDB_RIGHT ();
                    result_ = FRONT;
                    temp_result = assignDB_FRONT ();

                    for (nLoop = FRONT; nLoop < GESTURE_NUM; nLoop ++)	{
                        Log.d (TAG, "Value is" + biggestValue [nLoop]);
                        if (max < biggestValue [nLoop])	{
                            max = biggestValue [nLoop];
                            result_ = nLoop;
                        } //if
                    }

                    Log.d(TAG, "flag is else :"+ result_);

                    break;

            } //switch
        }	//else

        return result_;
    }	//function groupClassifier






    ///////////////////////////////////////////////////////////////////////
    private int assignDB_FRONT ()	{
        ///////////////////////////////////////////////////////////////////////

        switch (band_flag)  {
            case H_BAND:
                if (crossCorrelation(DB.H_BAND_FRONT_UP, DB.H_BAND_FRONT_UP_AVR, DB.H_BAND_FRONT_UP_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
            case PARTRON:
                if (crossCorrelation(DB.PARTRON_FRONT_UP, DB.PARTRON_FRONT_UP_AVR, DB.PARTRON_FRONT_UP_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
        }

        return result_;
    }

    ///////////////////////////////////////////////////////////////////////
    private int assignDB_RIGHT ()	{
        ///////////////////////////////////////////////////////////////////////
        switch (band_flag)  {
            case H_BAND:
                if (crossCorrelation(DB.H_BAND_LEFT_RIGHT, DB.H_BAND_LEFT_RIGHT_AVR, DB.H_BAND_LEFT_RIGHT_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
            case PARTRON:
                if (crossCorrelation(DB.PARTRON_LEFT_RIGHT, DB.PARTRON_LEFT_RIGHT_AVR, DB.PARTRON_LEFT_RIGHT_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
        }
        return result_;
    }
    ///////////////////////////////////////////////////////////////////////
    private int assignDB_CLOCK ()	{
        ///////////////////////////////////////////////////////////////////////

        switch (band_flag)  {
            case H_BAND:
                if (crossCorrelation(DB.H_BAND_CLOCK_ANTI, DB.H_BAND_CLOCK_ANTI_AVR, DB.H_BAND_CLOCK_ANTI_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
            case PARTRON:
                if (crossCorrelation(DB.PARTRON_CLOCK_ANTI, DB.PARTRON_CLOCK_ANTI_AVR, DB.PARTRON_CLOCK_ANTI_COV) == 1)
                    result_ = BACK;
                else
                    result_ = FRONT;
                break;
        }
        return result_;

    }




    ///////////////////////////////////////////////////////////////////////
    private int crossCorrelation (double [][][] db_sample, double [][] db_average, double [][] db_cov)	{
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2;

        // Arrays.fill(WMA_temp, 0);

        for (nLoop = -CROSS_FACTOR; nLoop < 1; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM + nLoop ; nLoop2 ++)	{
                WMA_temp [0][nLoop2] = WMA_SENSOR [0][-nLoop+ nLoop2];
                WMA_temp [1][nLoop2] = WMA_SENSOR [1][-nLoop+ nLoop2];
                WMA_temp [2][nLoop2] = WMA_SENSOR [2][-nLoop+ nLoop2];

            }
            compareOtherCorrelation (WMA_SAMPLE_NUM + nLoop, nLoop, db_sample, db_average, db_cov);
            // Arrays.fill(WMA_temp, 0);

        }
        for (nLoop = 1; nLoop < CROSS_FACTOR; nLoop ++)	{

            compareOtherCorrelation (WMA_SAMPLE_NUM, nLoop, db_sample, db_average, db_cov);
        }

        //printf ("%d vs. %d  ",  biggestValue [0], biggestValue [1]);

        return biggestValue [result_] > biggestValue [result_ + 1] ? 0 : 1;

    }



    ///////////////////////////////////////////////////////////////////////
    private void compareOtherCorrelation (int arrayLen, int flag_, double [][][] db_sample, double [][] db_average, double [][] db_cov)	{
        ///////////////////////////////////////////////////////////////////////

        int [][] WMA_SENSOR_local = new int [AXIS_NUM][WMA_SAMPLE_NUM];
        int nLoopLen = 0, nLoopStart = 0;//, db_start = 0, db_end = 0;
        double [] average = {0, 0, 0};
        int nLoop, nLoop2, nLoop3;
        double [][] cov =  {{0,0,0}, {0,0,0}};
        double [] cov_self = {0, 0, 0};
        double [][] corr =  {{0,0,0}, {0,0,0}};
        double [][] corr_int_local  = {{0,0,0}, {0,0,0}};

        //  Arrays.fill(WMA_SENSOR_local, 0);

        if (flag_ < 1)	{	//array copy to
            nLoopLen = arrayLen;
            nLoopStart = 0;
            WMA_SENSOR_local = WMA_temp.clone();
            //memcpy (WMA_SENSOR_local, WMA_temp, sizeof(WMA_SENSOR_local));
        }
        else	{
            nLoopLen = WMA_SAMPLE_NUM;
            nLoopStart = flag_;
            WMA_SENSOR_local = WMA_SENSOR.clone();
            //memcpy (WMA_SENSOR_local, WMA_SENSOR, izeof(WMA_SENSOR_local));
        }

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{	//average of incoming sensor value
            for (nLoop2 = 0; nLoop2 < nLoopLen ; nLoop2 ++)	{
                average [nLoop] += WMA_SENSOR_local [nLoop][nLoop2];
            } //for nLoop2

            average [nLoop] /= nLoopLen;

        } //for nLoop

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            double temp_double=0;
            for (nLoop2 = nLoopStart; nLoop2 < nLoopLen; nLoop2 ++)	{ //co-variance //분자계산
                temp_double = WMA_SENSOR_local [nLoop][nLoop2 - nLoopStart] - average [nLoop];

                for (nLoop3 = 0; nLoop3 < NUM_OF_DB; nLoop3 ++)
                    cov [nLoop3][nLoop] += (temp_double * (db_sample [nLoop3][nLoop][nLoop2] - db_average [nLoop3][nLoop]));


                cov_self [nLoop] += (temp_double * temp_double);
            } //for nLoop2

            for (nLoop3 = 0; nLoop3 < NUM_OF_DB; nLoop3 ++)
                cov [nLoop3][nLoop] /= (nLoopLen - nLoopStart);

            cov_self [nLoop] /= (nLoopLen - nLoopStart);

        } // for nLoop

        int [] tempSUM  = {0,0};

        for (nLoop = 0; nLoop <= 1; nLoop ++)	{	//correlation. //분모 계산

            for (nLoop2 = 0; nLoop2 < AXIS_NUM; nLoop2 ++)	{


                if (cov_self [nLoop2] * cov [nLoop][nLoop2] != 0)	{
                    corr [nLoop][nLoop2] = 0;
                    corr [nLoop][nLoop2] = cov [nLoop][nLoop2] / (mysqrt ((cov_self [nLoop2] * db_cov [nLoop][nLoop2])* 10000) / 100);
                }
                else
                    corr [nLoop][nLoop2] = 0;

                corr_int_local [nLoop][nLoop2] = 0;
                corr_int_local [nLoop][nLoop2] = (int)(corr [nLoop][nLoop2] *100);

                tempSUM [nLoop] += ((int)(corr_int_local [nLoop][nLoop2] * WEIGHTED_AXIS [nLoop2]) / 100);

            } //for nLoop2

            if (tempSUM[nLoop] < 20)
                tempSUM[nLoop] = 0;
            else
                tempSUM[nLoop] *= tempSUM[nLoop];


        } //for nLoop


        //------------------------------------------------ deviation --------------
        int [][] gap_mean = {{0,0,0}, {0,0,0}};
        int [][] gap_deviation = {{0,0,0}, {0,0,0}};

        int [] tempSUM_dev = {0,0};

        for (nLoop = 0; nLoop < NUM_OF_DB; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < AXIS_NUM; nLoop2 ++){
                for (nLoop3 = nLoopStart; nLoop3 < nLoopLen; nLoop3 ++){
                    gap_mean [nLoop][nLoop2] += Math.abs( (int)(db_sample [nLoop][nLoop2][nLoop3] * 1000) - (int) WMA_SENSOR_local[nLoop2][nLoop3]);
                } //for nLoop3
                gap_mean [nLoop][nLoop2] /= nLoopLen;

                for (nLoop3 = nLoopStart; nLoop3 < nLoopLen; nLoop3 ++){
                    gap_deviation [nLoop][nLoop2] += Math.abs( (int)(db_sample [nLoop][nLoop2][nLoop3] * 1000) - (int)WMA_SENSOR_local[nLoop2][nLoop3] - (int)gap_mean [nLoop][nLoop2] );
                } //for nLoop3

                gap_deviation [nLoop][nLoop2] /= nLoopLen;

                tempSUM_dev [nLoop] +=  gap_deviation [nLoop][nLoop2];

            } //for nLoop2

        } //for nLoop

        int temp_int;
        for (nLoop = 0; nLoop < NUM_OF_DB; nLoop ++)	{
            if (tempSUM [nLoop] > 0)	{
                temp_int = (int)((tempSUM [nLoop] * 1000) / tempSUM_dev [nLoop]);
                //temp_int = tempSUM[nLoop];
            }
            else	{
                temp_int = 0;
            } //else

            if(biggestValue [result_ + nLoop] < temp_int)
                biggestValue [result_ + nLoop] = temp_int;

        } //for nLoop

    }






    ///////////////////////////////////////////////////////////////////////
    double mysqrt(double d)
    ///////////////////////////////////////////////////////////////////////
    {
        int NUM_REPEAT = 16;
        int k;
        double t;
        double buf = (double)d;
        for(k=0,t=buf;k<NUM_REPEAT;k++)
        {
            if(t<1.0)
                break;
            t = (t*t+buf)/(2.0*t);
        }
        return t;
    }



    ///////////////////////////////////////////////////////////////////////
    //static parameters
    ///////////////////////////////////////////////////////////////////////
    private static final String TAG = "GestureRecognition";
    private static final int GAP_THRESHOLD = 300;

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
    public static final int DUMP = -1;
    public static final int UNKNOWN_ = 99;

    public static final int GESTURE_NUM = LOW_ANTI + 1;	//10

    public static final int AXIS_NUM = 3;
    public static final int SAMPLE_NUM = 25;

    private static final int WMA_FACTOR = 3;
    private static final int WMA_SAMPLE_NUM = SAMPLE_NUM - WMA_FACTOR + 1;
    private static final int NORMALIZATION_FACTOR = 32767;
    private static final int NUM_OF_DB = 2;

    private static final int CROSS_FACTOR = 3;

    public static final int ACC_X = 0;
    public static final int ACC_Y = 1;
    public static final int ACC_Z = 2;
    /*public static final int GY_X = 3;
    public static final int GY_Y = 4;
    public static final int GY_Z = 5;

    */
    public static final byte H_BAND = 0;
    public static final byte PARTRON = 1;
    public static final byte UCOMM = 2;




}

// Partron band data function
/*
import android.util.Log;

public class GestureRecognition {




    private int [][] WMA_SENSOR = new int [AXIS_NUM][WMA_SAMPLE_NUM];
    private int [][] SENSOR_tmep =  new int [AXIS_NUM][SAMPLE_NUM];

    private int [][] WMA_temp = new int [AXIS_NUM][WMA_SAMPLE_NUM];

    private byte [] WEIGHTED_AXIS = new byte [AXIS_NUM];
    private byte Motionflag_ = 0;
    public int [] gaps = {0,0,0,0,0,0};
    private int [] biggestValue;
    private int result_;






    ///////////////////////////////////////////////////////////////////////
    public int gestureRecogInterface (int [][] SENSOR)	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        result_ = UNKNOWN_;
        biggestValue = new int [GESTURE_NUM];
        int nLoop, nLoop2;

        //THIS LOOP IS INSERTED JUST FOR DEBUGING


        //GestureDB gesture;
        normalizingFunction(SENSOR);

        weightedMovingAverage(SENSOR_tmep);

        validAxiesClassifier();

        result_ = groupClassifier();

        switch (result_)	{
            case DUMP:
                Log.d (TAG,"-------------------- DUMP--\n"); break;
            case LEFT:
                Log.i (TAG," <<<< LEFT <<< \n");
                break;
            case RIGHT:
                Log.i (TAG," >>>> RIGHT >>\n");
                break;
            case FRONT:
            case BACK:
                result_ = FRONT;
                Log.i (TAG,"[[[ FRONT ]\n");
                break;
            case UP:
            case DOWN:
                result_ = UP;
                Log.i (TAG," ^^^^ UP\n");
                break;
            case CLOCK:
                result_ = CLOCK;
                Log.i (TAG," **** CLOCK\n");
                break;
            case ANTI_CLOCK:

                result_ = ANTI_CLOCK;
                Log.i (TAG," **** ANTI CLOCK\n");
                break;
            case UNKNOWN_:
                Log.i (TAG," CAN'T DETECT\n"); 	break;
            default:
                Log.i (TAG," ELSE \n"); 	break;

        }	//switch



        return result_;
    } //function gestureRecogInterface


    ///////////////////////////////////////////////////////////////////////
    private void normalizingFunction (int [][] sensor){
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2;

        //Arrays.fill(SENSOR_tmep, 0);

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < SAMPLE_NUM; nLoop2 ++)	{

                SENSOR_tmep [nLoop][nLoop2] = (int)(((double)sensor [nLoop][nLoop2] / NORMALIZATION_FACTOR) * 1000);
                //Log.d(TAG, "["+nLoop+"/"+nLoop2+"]"+SENSOR_tmep [nLoop][nLoop2]);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////
    private void weightedMovingAverage (int [][] SENSOR)	{
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2, nLoop3;
        int temp_avr;

       // Arrays.fill(WMA_SENSOR, 0);

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM; nLoop2 ++)	{
                temp_avr = 0;
                for (nLoop3 = 0; nLoop3 < WMA_FACTOR; nLoop3 ++)	{
                    temp_avr +=  SENSOR [nLoop][nLoop2 + nLoop3];
                } //for nLoop3

                WMA_SENSOR [nLoop][nLoop2] = temp_avr / WMA_FACTOR;
            } //for nLoop2
        } //for nLoop
    }



    ///////////////////////////////////////////////////////////////////////
    private void validAxiesClassifier ()	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        int [] min_ = {10000,10000,30000, 10000, 10000, 10000};
        int [] max_ = {-10000, -10000,-1000, -10000, -10000,-1000};
        int [] minFlag = {0,0,0,0,0,0};
        int [] maxFlag = {0,0,0,0,0,0};
        int nLoop, nLoop2;

        Motionflag_ = 0;

        char temp_flag = 0;
        for (nLoop = ACC_X; nLoop <= ACC_Z; nLoop++ )	{

            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM; nLoop2 ++)	{

                if (min_[nLoop] > WMA_SENSOR [nLoop][nLoop2])	{
                    min_[nLoop] = WMA_SENSOR [nLoop][nLoop2];
                    minFlag [nLoop] |= 0x1;
                } else if (max_[nLoop] < WMA_SENSOR [nLoop][nLoop2])	{
                    max_[nLoop] = WMA_SENSOR [nLoop][nLoop2];
                    maxFlag [nLoop] |= 0x1;
                }
                minFlag[nLoop] = minFlag[nLoop] << 1;
                maxFlag[nLoop] = maxFlag[nLoop] << 1;

            } //for nLoop2

            gaps [nLoop] = max_ [nLoop] - min_ [nLoop];

            if (gaps [nLoop] > GAP_THRESHOLD)	{
                Motionflag_ |= 0x1;
                temp_flag ++;
                WEIGHTED_AXIS [nLoop] = 90;

            }

            Motionflag_ = (byte) (Motionflag_ << 1);

        } //for nLoop

        if (temp_flag == 3)	{
            int nLoop3 = 0, temp_min=2000, order = 0;
            for (nLoop3=0; nLoop3 < 3; nLoop3 ++ )	{

                if (gaps [nLoop3] < temp_min)	{
                    order = nLoop3;
                    temp_min = gaps [nLoop3];
                }

            }	//for nLoop3


            switch (order)	{
                case 0:
                    Motionflag_ = 0x6;
                    break;
                case 1:
                    Motionflag_ = 0xA;
                    WEIGHTED_AXIS [order] = 10;
                    temp_flag --;
                    break;
                case 2:
                    Motionflag_ = 0xC;
                    WEIGHTED_AXIS [order] = 10;
                    temp_flag --;
                    break;

            }	//switch
        }	//if

        if (temp_flag != 0) {
            for (nLoop = ACC_X; nLoop < AXIS_NUM; nLoop++) {
                WEIGHTED_AXIS[nLoop] /= temp_flag;
            }
        }

    }	//function validAxiesClassifier


    ///////////////////////////////////////////////////////////////////////
    private int groupClassifier ()	{
        ///////////////////////////////////////////////////////////////////////
        // A factor: The array of sensor data
        // Return: NULL
        //////////////////////////////////////////////////////////////////////
        result_ = DUMP;
        int nLoop;

       // Arrays.fill(biggestValue, 0);


        if (Motionflag_ == 0)	{
            result_ = DUMP;
        }
        else {

            int temp_result=0, max=0;

            switch (Motionflag_ & 0x0E)	{

                case 0x0:
                    result_ = DUMP;
                    break;
                case 0x4:	//FRONT
                    Log.d(TAG, "flag is 4");
                    result_ = FRONT;
                    break;

                case 0x2:	//UP
                    Log.d(TAG, "flag is 2");
                    result_ = UP;
//                    result_ = assignDB_FRONT ();
                    break;
                case 0x8:		//SIDE
                    Log.d(TAG, "flag is 8");

                    result_ = RIGHT;
                    result_ = assignDB_RIGHT ();
                    break;

                case 0xA:
                    Log.d(TAG, "flag is A");

                    if (gaps[0] + gaps[2] < 1700)	{
                        result_ = CLOCK;
                        temp_result = assignDB_CLOCK ();
                        result_ = FRONT;
                        temp_result = assignDB_FRONT ();
                        result_ = RIGHT;
                        temp_result = assignDB_RIGHT ();

                        for (nLoop = RIGHT; nLoop < GESTURE_NUM; nLoop ++)	{
                            if (max < biggestValue [nLoop])	{
                                max = biggestValue [nLoop];
                                result_ = nLoop;
                            }
                        }
                    }
                    else	{
                        result_ = CLOCK;
                        result_ = assignDB_CLOCK ();
                    }

                    break;

                case 0xC:
                    Log.d(TAG, "flag is C");

                    result_ = FRONT;
                    temp_result  = assignDB_FRONT ();
                    result_ = RIGHT;
                    temp_result  = assignDB_RIGHT ();

                    for (nLoop = FRONT; nLoop < GESTURE_NUM; nLoop ++)	{
                        if (max < biggestValue [nLoop])	{
                            max = biggestValue [nLoop];
                            result_ = nLoop;
                        } //if
                    }
                    break;

                default:

                    result_ = CLOCK;
                    temp_result = assignDB_CLOCK ();
                    result_ = RIGHT;
                    temp_result = assignDB_RIGHT ();
                    result_ = FRONT;
                    temp_result = assignDB_FRONT ();

                    for (nLoop = FRONT; nLoop < GESTURE_NUM; nLoop ++)	{
                        if (max < biggestValue [nLoop])	{
                            max = biggestValue [nLoop];
                            result_ = nLoop;
                        } //if
                    }

                    Log.d(TAG, "flag is else :"+ result_);

                    break;

            } //switch
        }	//else

        return result_;
    }	//function groupClassifier






    ///////////////////////////////////////////////////////////////////////
    private int assignDB_FRONT ()	{
        ///////////////////////////////////////////////////////////////////////

        double [][][] db_sample  = {
                { //front
                        {-0.0028, -0.0031, -0.0058, -0.004, -0.0025, 0.0008, 0.0028, 0.0076, 0.0127, 0.0129, 0.005, -0.0015, -0.0094, -0.0083, -0.0079, -0.0012, -0.0021, 0.002, 0.0015, 0.0029, 0.0087, 0.0034, 0.0093},
                        { -0.0054, -0.0034, 0.0071, 0.022, 0.0314, 0.0291, 0.0199, 0.0104, -0.0006, -0.0177, -0.0285, -0.033, -0.0294, -0.027, -0.0242, -0.0231, -0.0244, -0.0239, -0.0205, -0.0118, -0.0016, 0.0076, 0.0147},
                        {-0.0518, -0.0535, -0.0556, -0.0606, -0.0662, -0.0665, -0.0582, -0.0458, -0.0358, -0.0332, -0.035, -0.0407, -0.0454, -0.0483, -0.0472, -0.0466, -0.0433, -0.0448, -0.0433, -0.0411, -0.0391, -0.0375, -0.0438}

                },
                {// UP
                        {-0.0035, -0.0055, -0.006, -0.0039, -0.0022, -0.0046, -0.0122, -0.0171, -0.0156, -0.0118, -0.0069, -0.0033, -0.0003, 0.0023, 0.0035, 0.0035, 0.0019, 0.0005, -0.0038, -0.0051, -0.0104, -0.0086, -0.0112, -0.005230435, 0.000034},
                        {-0.0034, -0.0044, -0.0069, -0.0113, -0.0196, -0.0282, -0.0325, -0.0307, -0.0216, -0.0092, 0.003, 0.0097, 0.0109, 0.0133, 0.0166, 0.0207, 0.0218, 0.0206, 0.0163, 0.0135, 0.0099, 0.0094, 0.0066, 0.000195652, 0.000301},
                        {-0.0431, -0.0473, -0.0612, -0.0795, -0.0943, -0.0998, -0.0957, -0.0817, -0.0577, -0.0266, -0.0032, 0.0096, 0.0096, 0.0061, 0.0025, -0.0005, -0.005, -0.0123, -0.0226, -0.0346, -0.0436, -0.0498, -0.0524, -0.038395652, 0.001283}
                }
        };
        double [][] db_average  = {
                {0.000913, -0.005752, -0.0471 },
                {-0.00523, 0.000195, -0.038395}

        };

        double [][] db_cov  = {
                {0.000040, 0.000413, 0.000088 },
                {0.000034, 0.000301, 0.001283}
        };


        if (crossCorrelation(db_sample, db_average, db_cov) == 1)
            result_ = UP;
        else
            result_ = FRONT;

        return result_;
    }

    ///////////////////////////////////////////////////////////////////////
    private int assignDB_RIGHT ()	{
        ///////////////////////////////////////////////////////////////////////

        double [][][] db_sample  = {
                { //right
                        {-0.0095, -0.0156, -0.0281, -0.0462, -0.0644, -0.0738, -0.0654, -0.0372, -0.0015, 0.028, 0.0397, 0.0395, 0.0344, 0.0319, 0.0299, 0.026, 0.0189, 0.0105, 0.0035, -0.0021, -0.0071, -0.0115, -0.0155},
                        {0.003, 0.0064, 0.0085, 0.0061, -0.0031, -0.0174, -0.0331, -0.042, -0.0413, -0.033, -0.025, -0.0204, -0.0181, -0.0145, -0.0107, -0.0074, -0.0082, -0.0112, -0.0154, -0.0152, -0.0154, -0.0139, -0.0139},
                        {-0.0487, -0.0505, -0.0525, -0.0522, -0.0492, -0.0454, -0.0424, -0.04, -0.0405, -0.0441, -0.0489, -0.051, -0.0498, -0.0479, -0.0463, -0.0457, -0.047, -0.0495, -0.0524, -0.0523, -0.0513, -0.0493, -0.0479}
                },
                { //left

                        {0.0012, 0.0088, 0.0211, 0.0367, 0.044, 0.0402, 0.0206, -0.0034, -0.027, -0.0449, -0.0508, -0.0474, -0.0352, -0.0252, -0.0205, -0.0197, -0.018, -0.0176, -0.0158, -0.0186, -0.0177, -0.0189, -0.0134},
                        {-0.0053, -0.0055, -0.0039, -0.0008, -0.0022, -0.0028, -0.0042, -0.0115, -0.014, -0.0197, -0.0128, -0.0084, -0.0022, 0.0005, 0.0028, 0.0039, 0.004, 0.0033, 0.0005, -0.0012, -0.0054, -0.0068, -0.0064},
                        {-0.0523, -0.0505, -0.0519, -0.0556, -0.0596, -0.0558, -0.0493, -0.0467, -0.0454, -0.0473, -0.047, -0.0492, -0.0492, -0.0469, -0.0433, -0.0429, -0.0459, -0.0489, -0.051, -0.0492, -0.0472, -0.0449, -0.0446}


                }
        };
        double [][] db_average = {
                {-0.0050, -0.0146, -0.0480  },
                {-0.0096, -0.0043, -0.0489}
        };

        double [][] db_cov  = {
                {0.001206, 0.000194, 0.000013 },
                {0.000730, 0.000037, 0.000017 }
        };


        if (crossCorrelation(db_sample, db_average, db_cov) == 1)
            result_ =LEFT ;
        else
            result_ = RIGHT;
        return result_;
    }
    ///////////////////////////////////////////////////////////////////////
    private int assignDB_CLOCK ()	{
        ///////////////////////////////////////////////////////////////////////

        double [][][] db_sample  = {
                { //CLOCK
                        {-0.0003, -0.0004, 0.0057, 0.0258, 0.0415, 0.0414, 0.0114, -0.0378, -0.0769, -0.0939, -0.078, -0.0511, -0.0251, 0.0059, 0.0372, 0.0675, 0.0763, 0.0657, 0.0425, 0.0185, -0.0027, -0.0178, -0.0244},
                        {-0.0049, -0.0067, -0.0072, 0.0004, 0.0045, -0.006, -0.0249, -0.0494, -0.0562, -0.0488, -0.0361, -0.0268, -0.0252, -0.0204, -0.0154, -0.0124, -0.0114, -0.0173, -0.0224, -0.0268, -0.0266, -0.0237, -0.0193},
                        {-0.0463, -0.0437, -0.0407, -0.0474, -0.0628, -0.0837, -0.0949, -0.0926, -0.0647, -0.0257, 0.008, 0.0239, 0.029, 0.0261, 0.0095, -0.0198, -0.0479, -0.0685, -0.0771, -0.0804, -0.0796, -0.0754, -0.0678}
                },
                {
                        {-0.0058, -0.0077, -0.0133, -0.0312, -0.0521, -0.0613, -0.0418, -0.0046, 0.0334, 0.0624, 0.0678, 0.055, 0.0324, 0.0129, -0.0116, -0.0475, -0.0759, -0.0932, -0.0928, -0.0812, -0.0574, -0.0209, 0.0088},
                        {0.0028, 0.0032, 0.0035, 0.0067, 0.0023, -0.0124, -0.0355, -0.0442, -0.0385, -0.0263, -0.0221, -0.0194, -0.0143, -0.0057, 0.001, 0.0016, -0.0065, -0.0201, -0.0292, -0.0323, -0.0308, -0.0301, -0.0281},
                        {-0.0455, -0.0416, -0.0374, -0.031, -0.0402, -0.0623, -0.088, -0.0998, -0.0901, -0.0719, -0.0391, -0.0038, 0.0272, 0.0443, 0.0456, 0.0354, 0.0122, -0.0197, -0.0472, -0.0652, -0.0692, -0.0722, -0.073}

                }
        };
        double [][] db_average  = {
                {0.0013, -0.0210, -0.0445 },
                {-0.0185, -0.0163, -0.0362 }
        };

        double [][] db_cov  = {
                {0.002222, 0.000245, 0.001570},
                {0.002332, 0.000255, 0.001929 }
        };


        if (crossCorrelation(db_sample, db_average, db_cov) == 1)
            result_ = ANTI_CLOCK;
        else
            result_ = CLOCK;
        return result_;

    }

    ///////////////////////////////////////////////////////////////////////
    private int crossCorrelation (double [][][] db_sample, double [][] db_average, double [][] db_cov)	{
        ///////////////////////////////////////////////////////////////////////

        int nLoop, nLoop2;

       // Arrays.fill(WMA_temp, 0);

        for (nLoop = -CROSS_FACTOR; nLoop < 1; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < WMA_SAMPLE_NUM + nLoop ; nLoop2 ++)	{
                WMA_temp [0][nLoop2] = WMA_SENSOR [0][-nLoop+ nLoop2];
                WMA_temp [1][nLoop2] = WMA_SENSOR [1][-nLoop+ nLoop2];
                WMA_temp [2][nLoop2] = WMA_SENSOR [2][-nLoop+ nLoop2];

            }
            compareOtherCorrelation (WMA_SAMPLE_NUM + nLoop, nLoop, db_sample, db_average, db_cov);
           // Arrays.fill(WMA_temp, 0);

        }
        for (nLoop = 1; nLoop < CROSS_FACTOR; nLoop ++)	{

            compareOtherCorrelation (WMA_SAMPLE_NUM, nLoop, db_sample, db_average, db_cov);
        }

        //printf ("%d vs. %d  ",  biggestValue [0], biggestValue [1]);

        return biggestValue [result_] > biggestValue [result_ + 1] ? 0 : 1;

    }



    ///////////////////////////////////////////////////////////////////////
    private void compareOtherCorrelation (int arrayLen, int flag_, double [][][] db_sample, double [][] db_average, double [][] db_cov)	{
        ///////////////////////////////////////////////////////////////////////

        int [][] WMA_SENSOR_local = new int [AXIS_NUM][WMA_SAMPLE_NUM];
        int nLoopLen = 0, nLoopStart = 0;//, db_start = 0, db_end = 0;
        double [] average = {0, 0, 0};
        int nLoop, nLoop2, nLoop3;
        double [][] cov =  {{0,0,0}, {0,0,0}};
        double [] cov_self = {0, 0, 0};
        double [][] corr =  {{0,0,0}, {0,0,0}};
        double [][] corr_int_local  = {{0,0,0}, {0,0,0}};

      //  Arrays.fill(WMA_SENSOR_local, 0);

        if (flag_ < 1)	{	//array copy to
            nLoopLen = arrayLen;
            nLoopStart = 0;
            WMA_SENSOR_local = WMA_temp.clone();
            //memcpy (WMA_SENSOR_local, WMA_temp, sizeof(WMA_SENSOR_local));
        }
        else	{
            nLoopLen = WMA_SAMPLE_NUM;
            nLoopStart = flag_;
            WMA_SENSOR_local = WMA_SENSOR.clone();
            //memcpy (WMA_SENSOR_local, WMA_SENSOR, izeof(WMA_SENSOR_local));
        }

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{	//average of incoming sensor value
            for (nLoop2 = 0; nLoop2 < nLoopLen ; nLoop2 ++)	{
                average [nLoop] += WMA_SENSOR_local [nLoop][nLoop2];
            } //for nLoop2

            average [nLoop] /= nLoopLen;

        } //for nLoop

        for (nLoop = 0; nLoop < AXIS_NUM; nLoop ++)	{
            double temp_double=0;
            for (nLoop2 = nLoopStart; nLoop2 < nLoopLen; nLoop2 ++)	{ //co-variance //분자계산
                temp_double = WMA_SENSOR_local [nLoop][nLoop2 - nLoopStart] - average [nLoop];

                for (nLoop3 = 0; nLoop3 < NUM_OF_DB; nLoop3 ++)
                    cov [nLoop3][nLoop] += (temp_double * (db_sample [nLoop3][nLoop][nLoop2] - db_average [nLoop3][nLoop]));


                cov_self [nLoop] += (temp_double * temp_double);
            } //for nLoop2

            for (nLoop3 = 0; nLoop3 < NUM_OF_DB; nLoop3 ++)
                cov [nLoop3][nLoop] /= (nLoopLen - nLoopStart);

            cov_self [nLoop] /= (nLoopLen - nLoopStart);

        } // for nLoop

        int [] tempSUM  = {0,0};

        for (nLoop = 0; nLoop <= 1; nLoop ++)	{	//correlation. //분모 계산

            for (nLoop2 = 0; nLoop2 < AXIS_NUM; nLoop2 ++)	{


                if (cov_self [nLoop2] * cov [nLoop][nLoop2] != 0)	{
                    corr [nLoop][nLoop2] = 0;
                    corr [nLoop][nLoop2] = cov [nLoop][nLoop2] / (mysqrt ((cov_self [nLoop2] * db_cov [nLoop][nLoop2])* 10000) / 100);
                }
                else
                    corr [nLoop][nLoop2] = 0;

                corr_int_local [nLoop][nLoop2] = 0;
                corr_int_local [nLoop][nLoop2] = (int)(corr [nLoop][nLoop2] *100);

                tempSUM [nLoop] += ((int)(corr_int_local [nLoop][nLoop2] * WEIGHTED_AXIS [nLoop2]) / 100);

            } //for nLoop2

            if (tempSUM[nLoop] < 20)
                tempSUM[nLoop] = 0;
            else
                tempSUM[nLoop] *= tempSUM[nLoop];


        } //for nLoop


        //------------------------------------------------ deviation --------------
        int [][] gap_mean = {{0,0,0}, {0,0,0}};
        int [][] gap_deviation = {{0,0,0}, {0,0,0}};

        int [] tempSUM_dev = {0,0};

        for (nLoop = 0; nLoop < NUM_OF_DB; nLoop ++)	{
            for (nLoop2 = 0; nLoop2 < AXIS_NUM; nLoop2 ++){
                for (nLoop3 = nLoopStart; nLoop3 < nLoopLen; nLoop3 ++){
                    gap_mean [nLoop][nLoop2] += Math.abs( (int)(db_sample [nLoop][nLoop2][nLoop3] * 1000) - (int) WMA_SENSOR_local[nLoop2][nLoop3]);
                } //for nLoop3
                gap_mean [nLoop][nLoop2] /= nLoopLen;

                for (nLoop3 = nLoopStart; nLoop3 < nLoopLen; nLoop3 ++){
                    gap_deviation [nLoop][nLoop2] += Math.abs( (int)(db_sample [nLoop][nLoop2][nLoop3] * 1000) - (int)WMA_SENSOR_local[nLoop2][nLoop3] - (int)gap_mean [nLoop][nLoop2] );
                } //for nLoop3

                gap_deviation [nLoop][nLoop2] /= nLoopLen;

                tempSUM_dev [nLoop] +=  gap_deviation [nLoop][nLoop2];

            } //for nLoop2

        } //for nLoop

        int temp_int;
        for (nLoop = 0; nLoop < NUM_OF_DB; nLoop ++)	{
            if (tempSUM [nLoop] > 0)	{
                temp_int = (int)((tempSUM [nLoop] * 1000) / tempSUM_dev [nLoop]);
                //temp_int = tempSUM[nLoop];
            }
            else	{
                temp_int = 0;
            } //else

            if(biggestValue [result_ + nLoop] < temp_int)
                biggestValue [result_ + nLoop] = temp_int;

        } //for nLoop

    }






    ///////////////////////////////////////////////////////////////////////
    double mysqrt(double d)
    ///////////////////////////////////////////////////////////////////////
    {
        int NUM_REPEAT = 16;
        int k;
        double t;
        double buf = (double)d;
        for(k=0,t=buf;k<NUM_REPEAT;k++)
        {
            if(t<1.0)
                break;
            t = (t*t+buf)/(2.0*t);
        }
        return t;
    }



    ///////////////////////////////////////////////////////////////////////
    //static parameters
    ///////////////////////////////////////////////////////////////////////
    private static final String TAG = "GestureRecognition";
    private static final int GAP_THRESHOLD = 600;

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
    public static final int DUMP = -1;
    public static final int UNKNOWN_ = 99;

    public static final int GESTURE_NUM = LOW_ANTI + 1;	//10

    public static final int AXIS_NUM = 3;
    public static final int SAMPLE_NUM = 25;

    private static final int WMA_FACTOR = 3;
    private static final int WMA_SAMPLE_NUM = SAMPLE_NUM - WMA_FACTOR + 1;
    private static final int NORMALIZATION_FACTOR = 32767;
    private static final int NUM_OF_DB = 2;

    private static final int CROSS_FACTOR = 3;

    public static final int ACC_X = 0;
    public static final int ACC_Y = 1;
    public static final int ACC_Z = 2;

}
*/