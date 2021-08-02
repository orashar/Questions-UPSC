package com.orashar.android.questions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class IntroActivity extends AppCompatActivity {

    private ViewPager introPager;
    private IntroPagerAdapter introPagerAdapter;
    private LinearLayout dotsll;
    private TextView[] dotstvArr;
    private int[] layouts;
    private Button skipbtn, nextbtn;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefManager = new PrefManager(this);
        if(!prefManager.isFirstTimeLaunch()){
            launchHomeScreen(0);
            finish();
        }

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_intro);

        introPager = findViewById(R.id.intro_pager);
        dotsll = findViewById(R.id.layoutDots);
        skipbtn = findViewById(R.id.skip_btn);
        nextbtn = findViewById(R.id.next_btn);

        layouts = new int[]{
                R.layout.intro_slide_1,
                R.layout.intro_slide_2,
                R.layout.intro_slide_3,
                R.layout.intro_slide_4
        };

        addBottomDots(0);

        changeStatusBarColor();

        introPagerAdapter = new IntroPagerAdapter();
        introPager.setAdapter(introPagerAdapter);
        introPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                addBottomDots(position);
                if(position == layouts.length-1){
                    nextbtn.setText("START");
                    skipbtn.setVisibility(View.GONE);
                }
                else{
                    nextbtn.setText("NEXT");
                    skipbtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        skipbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen(1);
            }
        });

        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int current = getItem(+1);
                if(current < layouts.length){
                    introPager.setCurrentItem(current);
                } else{
                    launchHomeScreen(1);
                }
            }
        });

    }

    private void addBottomDots(int currentPage){
        dotstvArr = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsll.removeAllViews();
        for(int i = 0; i < dotstvArr.length; i++){
            dotstvArr[i] = new TextView(this);
            dotstvArr[i].setText(Html.fromHtml("&#8226;"));
            dotstvArr[i].setTextSize(35);
            dotstvArr[i].setTextColor(colorsInactive[currentPage]);
            dotsll.addView(dotstvArr[i]);
        }

        if(dotstvArr.length > 0){
            dotstvArr[currentPage].setTextColor(colorsActive[currentPage]);
        }

    }

    private int getItem(int i){
        return introPager.getCurrentItem() + i;
    }

    private void launchHomeScreen(int i){
        if(skipbtn != null) skipbtn.setEnabled(false);
        if(nextbtn != null) nextbtn.setEnabled(false);
        prefManager.setFirstTimeLaunch(false);
        if(i == 0) {
            startActivity(new Intent(this, SplashActivity.class));
        } else{
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private void changeStatusBarColor(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    class IntroPagerAdapter extends PagerAdapter{
        private LayoutInflater layoutInflater;

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
