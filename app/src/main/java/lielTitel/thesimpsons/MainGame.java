package lielTitel.thesimpsons;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainGame extends AppCompatActivity
{
    int numOfLanes = 3;
    int donutSpeed = 20;
    int counter = 0;
    int score = 0;

    private FrameLayout frame;
    private LinearLayout gameLayout;
    private ImageView homer;
    private ImageButton left;
    private ImageButton right;
    private ImageView pauseBtn;
    private ImageView donut1;
    private ImageView donut2;
    private ImageView donut3;
    private ImageView heart1;
    private ImageView heart2;
    private ImageView heart3;

    //Score
    private TextView scoreLabel;

    //positions
    private float homerX;
    private float donut1X;
    private float donut1Y;
    private float donut2X;
    private float donut2Y;
    private float donut3X;
    private float donut3Y;

    //checking actions
    boolean stopPause = false;
    private boolean pauseLeft = false;
    private boolean pauseRight = false;

    private Handler handler = new Handler();
    private Timer timer;
    private Timer scoreThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        gameLayout = (LinearLayout) findViewById(R.id.startLayout);
        frame = (FrameLayout) findViewById(R.id.frameGame);
        scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        homer = (ImageView) findViewById(R.id.homer);
        donut1 = (ImageView) findViewById(R.id.donut1);
        donut2 = (ImageView) findViewById(R.id.donut2);
        donut3 = (ImageView) findViewById(R.id.donut3);
        pauseBtn = (ImageView) findViewById(R.id.pause);
        heart1 = (ImageView) findViewById(R.id.heart1);
        heart2 = (ImageView) findViewById(R.id.heart2);
        heart3 = (ImageView) findViewById(R.id.heart3);
        left = (ImageButton) findViewById(R.id.leftBtn);
        right = (ImageButton) findViewById(R.id.rightBtn);

        //set homer x to the left side of the screen
        homer.setX(gameLayout.getWidth() / numOfLanes);
        homerX = homer.getX();

        //score label
        scoreLabel.setText("Score : " + score);

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

        pauseBtn.setOnClickListener (new View.OnClickListener()  {
            @Override
            public void onClick(View v) {
                if (stopPause) { //if player press play again
//                   pauseBtn.setVisibility(View.VISIBLE);
                    countScore();
                    startNeds();
                    stopPause = false;
                    pauseRight = false;
                    pauseLeft = false;
                } else { //if player pauses the game
//                    pauseBtn.setVisibility(View.INVISIBLE);
                    timer.cancel();
                    scoreThread.cancel();
                    pauseLeft = true;
                    pauseRight = true;
                    stopPause = true;
                }
            }
        });

        makeUnvisDonut();
        startNeds();
        countScore();
    }

    public void startNeds(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
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

    public void countScore() {
        scoreThread = new Timer();
        scoreThread.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        score += 15;
                        scoreLabel.setText("Score: " + score);
                    }
                });
            }
        }, 20, 700);
    }

    public void moveDonuts()
    {
        int frameHeight = frame.getHeight();

        //set x positions
        donut1X = 0;
        donut2X = frame.getWidth()/numOfLanes;
        donut3X = frame.getWidth() - (frame.getWidth()/numOfLanes);
        donut1.setX(donut1X);
        donut2.setX(donut2X);
        donut3.setX(donut3X);

        donut1Y += donutSpeed;
        donut2Y += donutSpeed;
        donut3Y += donutSpeed;

        if (donut2Y > frameHeight || donut1Y > frameHeight || donut3Y > frameHeight) {
            donut1Y = -300;
            donut2Y = -300;
            donut3Y = -300;
            makeUnvisDonut();
        }

        donut1.setY(donut1Y);
        donut2.setY(donut2Y);
        donut3.setY(donut3Y);

        checkHits();
    }

    public void makeVisibleAgain()
    {
        donut1.setVisibility(View.VISIBLE);
        donut2.setVisibility(View.VISIBLE);
        donut3.setVisibility(View.VISIBLE);
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

    public void makeUnvisDonut() {
        int maxDon = 3;
        int minDon = 1;
        int donutNum = (int) (Math.random() * ((maxDon - minDon) + 1) + minDon);

        makeVisibleAgain();

        if (donutNum == 1) {
            donut1.setVisibility(View.INVISIBLE);
        } else if (donutNum == 2) {
            donut2.setVisibility(View.INVISIBLE);
        } else if(donutNum == 3){
            donut3.setVisibility(View.INVISIBLE);
        }
    }

    public boolean checkDonutHit(View donut)
    {
       if(donut.isShown()) {
           if ((donut.getY() >= homer.getY()) && (homerX == donut.getX()) && donut.getVisibility() == View.VISIBLE) {
               donut.setVisibility(View.INVISIBLE);
               return true;
           }
       }
        return  false;
    }

    public void checkHits()
    {
        ///if donut hits play this
        if(checkDonutHit(donut1) || (checkDonutHit(donut2)) || (checkDonutHit(donut3)))
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
                heart1.setVisibility(View.INVISIBLE);
                break;
            }
            case 2:
            {
                heart2.setVisibility(View.INVISIBLE);
                break;
            }
            case 3:
            {
                heart3.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getApplicationContext(), GameOver.class);
                intent.putExtra("key", String.valueOf(score));
                startActivity(intent);
                finish();
                timer.cancel();
                scoreThread.cancel();
                stopPause = true;
                break;
            }
            default:
            {
                break;
            }
        }
    }

}
