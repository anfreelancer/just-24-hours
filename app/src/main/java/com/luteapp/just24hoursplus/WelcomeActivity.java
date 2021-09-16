package com.luteapp.just24hoursplus;



import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.Activity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class WelcomeActivity extends Activity
{

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private int[] animations;
    private int[] animations2;
    private int[] animClocks;
    private int[] animGestures;
    private Button btnSkip, btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

         // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome pages
        layouts = new int[]
        {
            R.layout.welcome1,
            R.layout.welcome2,
            R.layout.welcome3,
            R.layout.welcome4,
            R.layout.welcome5,
            R.layout.welcome6
        };

        // animations for all welcome pages
        animations = new int[]
        {
            R.anim.welcomeanimation1,
            R.anim.welcomeanimation2,
            R.anim.welcomeanimation3,
            R.anim.welcomeanimation4,
            R.anim.welcomeanimation5,
            0
        };

        // animations for all welcome pages
        animations2 = new int[]
        {
            0,
            R.anim.welcomeanimation22,
            R.anim.welcomeanimation23,
            R.anim.welcomeanimation24,
            R.anim.welcomeanimation25,
            0
        };

        // clockviews on each page to be animated
        animClocks = new int[]
        {
            R.id.textClock1,
            R.id.textClock2,
            R.id.textClock3,
            R.id.textClock4,
            R.id.textClock5,
            0
        };

        // clockviews on each page to be animated
        animGestures = new int[]
        {
            0,
            R.id.imageGesture2,
            R.id.imageGesture3,
            R.id.imageGesture4,
            R.id.imageGesture5,
            0
        };

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        // close the welcome/help screen and go back to the clock dispay
        btnSkip.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                displayClockScreen();
            }
        });

        // display the next page in the welcome/help
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page clock screen will be displayed
                int current = getItem(+1);

                if (current < layouts.length)
                {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                }
                else
                {
                    // close the welcome/help screen and display the clock
                    displayClockScreen();
                }
            }
        });
    }


    // display the dots on the bottom of the screen that show progress through the pages
    private void addBottomDots(int currentPage)
    {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++)
        {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
        {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }


    private int getItem(int i)
    {
        return viewPager.getCurrentItem() + i;
    }


    // end the welcome/help screens and display the clock
    private void displayClockScreen()
    {
        finish();
    }


    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
    {

        @Override
        public void onPageSelected(int pageNumber)
        {
            addBottomDots(pageNumber);

            // changing the next button text 'NEXT' / 'GOT IT'
            if(pageNumber == layouts.length - 1)
            {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);

                // insert the version number
                TextView versionname = (TextView) findViewById(R.id.NameAndVersion);
                versionname.setText(getString(R.string.slide_6_title) + BuildConfig.VERSION_NAME);
            }
            else
            {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }

            // only create and play a clock animation if the page has one and also has a clock image to animate
            if (animations[pageNumber] != 0 && animClocks[pageNumber] != 0)
            {
                // each welcome/help page has an animation - select the textclock element to be animated
                ImageView theClockView = (ImageView) findViewById(animClocks[pageNumber]);
                theClockView.setVisibility(View.VISIBLE);

                // retrieve the animation details from the anim resource file (XML) for this welcome page
                Animation welcomeAnimation = AnimationUtils.loadAnimation(getApplicationContext(), animations[pageNumber]);
                welcomeAnimation.reset();
                theClockView.clearAnimation();
                theClockView.startAnimation(welcomeAnimation);
            }

            // only create and play a gesture animation if the page has one and also has a gesture image to animate
            if (animations2[pageNumber] != 0 && animGestures[pageNumber] != 0)
            {
                // each welcome/help page has an animation - select the textclock element to be animated
                ImageView theGestureView = (ImageView) findViewById(animGestures[pageNumber]);
                theGestureView.setVisibility(View.VISIBLE);

                // retrieve the animation details from the anim resource file (XML) for this welcome page
                Animation welcomeAnimation2 = AnimationUtils.loadAnimation(getApplicationContext(), animations2[pageNumber]);
                welcomeAnimation2.reset();
                theGestureView.clearAnimation();
                theGestureView.startAnimation(welcomeAnimation2);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {

        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {

        }
    };


    // Making notification bar transparent
    private void changeStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }


    // View pager adapter
    public class MyViewPagerAdapter extends PagerAdapter
    {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter()
        {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount()
        {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj)
        {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
