package com.luteapp.just24hoursplus;

import com.luteapp.just24hoursplus.util.SystemUiHider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextClock;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class theClockActivity extends Activity
{
    private static final int SWIPE_MIN_DISTANCE = 400;  // minimum distance to consider gesture a swipe
    private static final int SCROLL_MIN_DISTANCE = 1;  // minimum distance to consider gesture a scroll
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;  // minimum velocity to consider gesture a swipe

    boolean invert;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent welcomeIntent = new Intent(this, WelcomeActivity.class);

        setContentView(R.layout.activity_the_clock);

        int clockBrightness;


        PackageInfo pInfo;      // info stored in the app package - used to retrieve the app version number

        // retrieve save user settings
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        // retrieve the user set clock brightness
        clockBrightness = settings.getInt("clockBrightness", 255);
        // retrieve the user set invert (bright text or bright background)
        invert = settings.getBoolean("invert", false);

        // set the brightness of the clock to the save user set brightness
        TextClock theClockView = findViewById(R.id.textClock);

        // set if the text or the background is bright (rather than pure black)
        if (invert)
        {
            // background is set to default or user set brightness
            theClockView.setBackgroundColor(Color.rgb(clockBrightness, clockBrightness, clockBrightness));
            theClockView.setTextColor(Color.rgb(0, 0, 0));
        }
        else
        {
            // text is set to default or user set brightness
            theClockView.setBackgroundColor(Color.rgb(0, 0, 0));
            theClockView.setTextColor(Color.rgb(clockBrightness, clockBrightness, clockBrightness));
        }

        // set up the clock view to detect gestures (swipes and flings).
        final GestureDetector gestureDetector = new GestureDetector(this, new SwipeGestureDetector());

        View.OnTouchListener gestureListener = new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
              return gestureDetector.onTouchEvent(event);
            }
        };
        theClockView.setOnTouchListener(gestureListener);

        // determine if this is the first time running this version of the application
        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = settings.getInt("AppVersion", -1);
            int currentVersionCode = pInfo.versionCode;

            if (lastVersionCode < 10)
            {

                startActivity(welcomeIntent);
            }
            // Update version in preferences - will be used to detect first run in future versions
            boolean appVersion = settings.edit().putInt("AppVersion", currentVersionCode).commit();
        }
        catch (PackageManager.NameNotFoundException e)
        {
            // nothing
        }


    }


    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);

        // resize the clock for orientation changes
        setClockSize();
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            startActivity(new Intent(theClockActivity.this, NotPremiumActivity.class));
            finish();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);

        // set the clock to display as large as possible without extending beyond the screen extents
        TextClock theClockView = findViewById(R.id.textClock);
        // retrieve the animation details from the anim resource file (XML)
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.startanimation);
        startAnimation.reset();
        theClockView.clearAnimation();
        theClockView.startAnimation(startAnimation);
        handler.postDelayed(runnable, 5000);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);

        // resize the clock (to handle rotation of the screen)
        setClockSize();
    }


    // make sure that the clock fills as much of the screen as possible (when starting the application and when
    //  rotating the screen
    private void setClockSize()
    {
        // set the clock to display as large as possible without extending beyond the screen extents
        TextClock theClockView = findViewById(R.id.textClock);
        theClockView.refreshDrawableState();
        int clockTextSize = 10;

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int maxClockWidth = size.x;

        // start with a small text size
        int textWidth = 10;
        while (textWidth < maxClockWidth - 10 && theClockView.getText() != "")
        {
            clockTextSize = clockTextSize + 1;
            theClockView.setTextSize(clockTextSize);
            theClockView.measure(0, 0);       // check the size of the clock!
            textWidth = theClockView.getMeasuredWidth();  //get the clock's width//
        }
    }


    @Override
    public void onBackPressed()
    {
        finish();
    }


    // react to user swipes on the clock screen
    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        // flinging from left to right quits the application
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            try
            {
                // retrieve the start and end point of the fling
                float deltaX = e2.getX() - e1.getX();
                // float deltaY = e2.getY() - e1.getY(); *** not used yet

                // right to left fling to show first start tutorial
                if (deltaX < SWIPE_MIN_DISTANCE * -1 && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                    // show the first start tutorial

                    Intent welcomeIntent = new Intent(theClockActivity.this, WelcomeActivity.class);
                    startActivity(welcomeIntent);
                }

                // left to right fling to quit the application
                if (deltaX > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                    TextClock theClockView = findViewById(R.id.textClock);

                    // retrieve the animation details from the anim resource file (XML)
                    Animation quitAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.quitanimation);
                    quitAnimation.reset();
                    theClockView.clearAnimation();
                    theClockView.startAnimation(quitAnimation);

                    // set the delay to quit application to the animiation time
                    int theDelay = (int) quitAnimation.getDuration();

                    // delay quitting the clock application so that the animation is viewable
                    theClockView.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // quit the clock application
                            finish();
                            System.exit(0);
                        }
                    }, theDelay);

                }
            }
            catch (Exception e)
            {
                // nothing
            }
            return false;
        }


        @Override
        // allow user to swipe up and down to set the "brightness" (the range of black to grey to white) of the clock text or clock background (whichever is not black)
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float deltaX, float deltaY)
        {

            TextClock theClockView = findViewById(R.id.textClock);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            // determine the percentage of the screen that the user has swiped (used to determine the amount of brightness to change)
            // float xIncrease =  Math.abs(deltaX)/size.x;  ***not used yet
            float yIncrease =  Math.abs(deltaY)/size.y;

            int color = theClockView.getCurrentTextColor();
            int newColor = color;


            if (invert)
            {
                // extract the clock's background colour
                Drawable background = theClockView.getBackground();
                if (background instanceof ColorDrawable)
                {
                    color = ((ColorDrawable)background).getColor();
                }
            }


            // make sure that the swipe is mostly a vertical one
            if (Math.abs(deltaY) > 10 && deltaX < 30)
            {
                // swipe down to decrease clock brightness
                if (-deltaY > SCROLL_MIN_DISTANCE)
                {
                    newColor = Math.max(Color.red(color) - (int) (yIncrease * 255), 0);
                }
                // swipe up to increase clock brightness
                else if (deltaY > SCROLL_MIN_DISTANCE)
                {
                    newColor = Math.min(Color.red(color) + (int)(yIncrease * 255), 255);
                }

                // set the new brightness of the clock
                if (invert)
                {
                    theClockView.setBackgroundColor(Color.rgb(newColor, newColor, newColor));
                }
                else
                {
                    theClockView.setTextColor(Color.rgb(newColor, newColor, newColor));
                }

                // save the colour settings so that they appear the next runtime
                SharedPreferences settings = getSharedPreferences("UserInfo", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("clockBrightness", newColor);

                editor.putBoolean("invert", invert);

                editor.apply();
            }

            return true;
        }


        @Override
        public boolean onDown(MotionEvent e)
        {
            return true;
        }


        @Override
        // invert clock and background brightness
        public boolean onDoubleTap(MotionEvent e)
        {
            // get the clock object
            TextClock theClockView = findViewById(R.id.textClock);
            // retrieve the colours of the clock text and clock background
            int color = theClockView.getCurrentTextColor();
            int bgColor = Color.rgb(0,0,0);  // default background colour to black

            // extract the clock's background colour
            Drawable background = theClockView.getBackground();
            if (background instanceof ColorDrawable)
            {
                bgColor = ((ColorDrawable)background).getColor();
            }

            // swap the clock's text and background colours
            theClockView.setTextColor(Color.rgb(bgColor, bgColor, bgColor));
            theClockView.setBackgroundColor(Color.rgb(color, color, color));

            // save the colour settings so that they appear the next runtime
            SharedPreferences settings = getSharedPreferences("UserInfo", 0);
            SharedPreferences.Editor editor = settings.edit();

            // change the invert flag (true becomes false, false becomes true
            invert = !invert;
            editor.putBoolean("invert", invert);

            editor.apply();
            return true;
        }
    }

}