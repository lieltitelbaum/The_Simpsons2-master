
package lielTitel.thesimpsons;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Delayed;

public class FiveLanes extends AppCompatActivity{

    int numOfLanes = 5;
    int nedSpeed = 20;
    int donutSpeed = 15;
    int counter = 0;
    int score = 0;

    private FrameLayout frame;
    private RelativeLayout gameLayout;
    private ImageView homer;
    private ImageView pauseBtn;
    private ImageView ned1;
    private ImageView ned2;
    private ImageView ned3;
    private ImageView ned4;
    private ImageView ned5;
    private ImageView heart1;
    private ImageView heart2;
    private ImageView heart3;
    private ImageView donut1;
    private ImageView donut2;
    private ImageButton left;
    private ImageButton right;

    //Score
    private TextView scoreLabel;

    //positions
    private float homerX;
    private float ned1Y;
    private float ned2Y;
    private float ned3Y;
    private float ned4Y;
    private float ned5Y;

    //checking actions
    private boolean startPause = false;
    private boolean pauseLeft = false;
    private boolean pauseRight = false;
    private boolean isRandFirstTime = true;

    //sensors
    private SensorManager sensors;
    private Sensor movementSensor;
    private SensorEventListener sensorEventListener;

    //Timers and handler
    private Handler handler = new Handler();
    private Timer timer;
    private  Timer moveDonuts;
    private Timer scoreThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_lanes);

        findViews();
        //set homer x to the left side of the screen
        homerX = 2 * (gameLayout.getWidth() / numOfLanes);
        homer.setX(homerX);

        //score label
        scoreLabel.setText(" "+ score);

        int isSensor = getIntent().getIntExtra(Constants.KEY_IS_SENSOR,0);
        Log.d("vvvSensorsKey","is sensor activated: " + isSensor);
        if (isSensor == 0)//if sensors off, btns on
        {
            //actions
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pauseLeft)
                        moveLeftHomer();
                }
            });
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!pauseRight)
                        moveRightHomer();
                }
            });
        }
        else if (isSensor == 1) {
            left.setVisibility(View.INVISIBLE);
            right.setVisibility(View.INVISIBLE);

            sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];

                    if (x < -4.5 && homerX != 4 * (gameLayout.getWidth() / numOfLanes)) {
                        homerX = 4 * (gameLayout.getWidth() / numOfLanes);
                        homer.setX(homerX);
                    } else if (x < -1.5 && x > -3 && homerX != 3 * (gameLayout.getWidth() / numOfLanes)) {
                        homerX = 3 * (gameLayout.getWidth() / numOfLanes);
                        homer.setX(homerX);
                    } else if (x > -1.5 && x < 1.5 && homerX != 2 * (gameLayout.getWidth() / numOfLanes)) {
                        homerX = 2 * (gameLayout.getWidth() / numOfLanes);
                        homer.setX(homerX);
                    } else if (x > 1.5 && x < 3 && homerX != (gameLayout.getWidth() / numOfLanes)) {
                        homerX = gameLayout.getWidth() / numOfLanes;
                        homer.setX(homerX);
                    } else if (x > 4.5 && homerX != 0) {
                        homerX = 0;
                        homer.setX(homerX);
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };
        }

        pauseBtn.setOnClickListener (new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if (startPause) //if user click again and replay the game
                {
                    pauseBtn.setBackgroundResource(R.drawable.pause1_icon);
                    countScore();
                    startMovement();
                    startMoveDonuts();
                    startPause = false;
                    pauseRight = false;
                    pauseLeft = false;
                } else {
                    pauseBtn.setBackgroundResource(R.drawable.play_icon);
                    timer.cancel();
                    moveDonuts.cancel();
                    scoreThread.cancel();
                    startPause = true;
                    pauseRight = true;
                    pauseLeft = true;
                }
            }
        });
    }

    private void findViews() {
        gameLayout = findViewById(R.id.startLayout);
        frame = findViewById(R.id.frameGame);
        scoreLabel = findViewById(R.id.scoreLabel);
        homer = findViewById(R.id.homer);
        ned1 = findViewById(R.id.ned1);
        ned2 = findViewById(R.id.ned2);
        ned3 = findViewById(R.id.ned3);
        ned4 = findViewById(R.id.ned4);
        ned5 = findViewById(R.id.ned5);
        pauseBtn =findViewById(R.id.pause);
        heart1 = findViewById(R.id.heart1);
        heart2 = findViewById(R.id.heart2);
        heart3 = findViewById(R.id.heart3);
        donut1 = findViewById(R.id.donut1);
        donut2 = findViewById(R.id.donut2);
        left = findViewById(R.id.leftBtn);
        right = findViewById(R.id.rightBtn);

        //sensors
        sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        movementSensor = sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }


    private void startMoveDonuts() {
        moveDonuts = new Timer();
        moveDonuts.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        moveDonuts();
                    }
                });
            }
        }, 0, 20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeUnvisNed();
        startMovement();
        startMoveDonuts();
        countScore();
        //donut animation
        rotateAnimation(donut1);
        rotateAnimation(donut2);
        if (movementSensor != null)
            sensors.registerListener(sensorEventListener, movementSensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (movementSensor != null)
            sensors.unregisterListener(sensorEventListener, movementSensor);

    }

    public void startMovement(){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            moveNeds();
                        }
                    });
                }
            }, 0, 20);
        }

    public void countScore() {
        scoreThread = new Timer();
        scoreThread.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        score += 5;
                        scoreLabel.setText("" + score);
                    }
                });
            }
        }, 20, 700);
    }

    public void moveNeds()
    {
        int frameHeight = frame.getHeight();

        //set x positions
        ned1.setX(0);
        ned2.setX(frame.getWidth()/numOfLanes);
        ned3.setX(2*frame.getWidth()/numOfLanes);
        ned4.setX(3*frame.getWidth()/numOfLanes);
        ned5.setX(4*frame.getWidth()/numOfLanes);

        //set y positions
        ned1Y += nedSpeed;
        ned2Y += nedSpeed;
        ned3Y += nedSpeed;
        ned4Y += nedSpeed;
        ned5Y += nedSpeed;

        //if out of border, moveDonuts them to the top of the screen
        if (ned2Y > frameHeight || ned1Y > frameHeight || ned3Y > frameHeight|| ned4Y > frameHeight || ned5Y > frameHeight) {
            ned1Y = -300;
            ned2Y = -300;
            ned3Y = -300;
            ned4Y = -300;
            ned5Y = -300;
            makeUnvisNed();
        }

        ned1.setY(ned1Y);
        ned2.setY(ned2Y);
        ned3.setY(ned3Y);
        ned4.setY(ned4Y);
        ned5.setY(ned5Y);

        checkNedsHits();
    }

    public void moveDonuts()
    {
        int frameHeight = frame.getHeight();

        if (isRandFirstTime) {
            positionRandDonuts();
            isRandFirstTime = false;
        }
        donut1.setY(donut1.getY() + donutSpeed);
        donut2.setY(donut2.getY() + donutSpeed);

        //if out of screen borders
        if (donut2.getY() > frameHeight || donut1.getY() > frameHeight) {
            donut1.setY(-350);
            donut2.setY(-350);
        }
        if(donut1.getY() <= -300 || donut1.getY() == 0)
            positionRandDonuts();

        checkDonutHits();

    }

    private void positionRandDonuts() {
        int max=4, min=0;
        int rand1 = (int) (Math.random() * ((max - min) + 1) + min);
        int rand2 = (int) (Math.random() * ((max - min) + 1) + min);

//        //in order to have different positions
        while (rand1 == rand2)
            rand2 = (int) (Math.random() * ((max - min) + 1) + min);

        //set x positions
        donut1.setX(rand1*(frame.getWidth()/numOfLanes));
        donut2.setX(rand2*(frame.getWidth()/numOfLanes));
        donut1.setVisibility(View.VISIBLE);
        donut2.setVisibility(View.VISIBLE);
    }

    private void checkDonutHits()
    {
        if (checkHit(donut1) ||checkHit(donut2))
        {
            score += 20;
            scoreLabel.setText("" + score);
        }
    }

    public void rotateAnimation(View view)
    {
        view.animate().rotationX(360).rotationY(360).setDuration(2000).setInterpolator(new LinearInterpolator()).start();
    }

    public void makeVisibleAgain()
    {
        ned1.setVisibility(View.VISIBLE);
        ned2.setVisibility(View.VISIBLE);
        ned3.setVisibility(View.VISIBLE);
        ned4.setVisibility(View.VISIBLE);
        ned5.setVisibility(View.VISIBLE);
    }

    public void makeUnvisNed() {
        int maxNed = 5;
        int minNed = 1;
        int nedNum = (int) (Math.random() * ((maxNed - minNed) + 1) + minNed);

        makeVisibleAgain();

        switch (nedNum)
        {
            case 1:
            {
                ned1.setVisibility(View.INVISIBLE);
                break;
            }
            case 2:
            {
                ned2.setVisibility(View.INVISIBLE);
                break;
            }
            case 3:
            {
                ned3.setVisibility(View.INVISIBLE);
                break;
            }
            case 4:
            {
                ned4.setVisibility(View.INVISIBLE);
                break;
            }
            case 5:
            {
                ned5.setVisibility(View.INVISIBLE);
                break;
            }
            default:
                break;
        }
    }

    public boolean checkHit(View view)
    {
        //if view is shown and hit homer -> set it to invisible
        if(view.isShown()) {
            if ((view.getY() + (50 * this.getResources().getDisplayMetrics().density) >= homer.getY()) && (homer.getX() == view.getX())
            && !(view.getY() > homer.getY() + 35 * this.getResources().getDisplayMetrics().density)) {
                view.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return  false;
    }

    public void checkNedsHits()
    {
        ///if ned hits homer change lives number
        if(checkHit(ned1) || (checkHit(ned2)) || (checkHit(ned3)) ||
                (checkHit((ned4)) || checkHit(ned5)))
        {
            counter ++;
            changeLives(counter);
        }
    }

    public void changeLives(int livesNum)
    {
        switch (livesNum)
        {
            case 1:
            {
                heartAnim(heart1);
                 heart1.setVisibility(View.INVISIBLE);
                break;
            }
            case 2:
            {
                heartAnim(heart2);
                heart2.setVisibility(View.INVISIBLE);
                break;
            }
            case 3:
            {
                heartAnim(heart3);
                heart3.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), GameOver.class);
                intent.putExtra(Constants.KEY_SCORE, score);
                startActivity(intent);
                finish();
                timer.cancel();
                scoreThread.cancel();
                moveDonuts.cancel();
                startPause = true;
                break;
            }
            default:
            {
                break;
            }
        }
    }

    public void moveLeftHomer() {

        homerX -= (float)(gameLayout.getWidth() / numOfLanes);

        if (homerX < 0)//if out of border left side
            homerX = 0;

        homer.setX(homerX);
    }

    public void moveRightHomer() {
        homerX += (float)(gameLayout.getWidth() / numOfLanes);

        //if out of border- right side
        if (homerX > (gameLayout.getWidth() - (float)gameLayout.getWidth() / numOfLanes))
            homerX = gameLayout.getWidth() - (float)(gameLayout.getWidth() / numOfLanes);

        homer.setX(homerX);
    }

    private void heartAnim(View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(500);

        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(fadeIn);
        animation.addAnimation(fadeOut);
        view.setAnimation(animation);
    }
}
