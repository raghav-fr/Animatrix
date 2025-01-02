package com.example.animatrix.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.animatrix.R;
import com.example.animatrix.adapters.FragmentAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int NOTIFICATION_PERMISSION_CODE = 3153;
    private static final String TAG = "MADARA";
    private ViewPager2 viewPager2;
    private RelativeLayout mainn;
    private TabLayout tabLayout;
    private boolean keepSplashScreen = true;
    private boolean doubleBackPressed = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//    ----------------------------------------------------------------------------------------------
        //Splash screen initilize
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);
        new Handler().postDelayed(() -> keepSplashScreen = false, 3000);

//    ----------------------------------------------------------------------------------------------
        initVars();

        //--------------------------------------------------------------------------------------------

        ImageButton btn = findViewById(R.id.searchButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });

        ImageButton settingbtn = findViewById(R.id.settingbtn);
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
        //------------------------------------------------------------------------------------------
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentAdapter fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle(), tabLayout.getTabCount());
        viewPager2.setAdapter(fragmentAdapter);
        viewPager2.setUserInputEnabled(true);

        //for changing the first tab icon color
        Objects.requireNonNull(Objects.requireNonNull(tabLayout.getTabAt(0)).getIcon()).setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());

                Objects.requireNonNull(tab.getIcon()).setColorFilter(getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Objects.requireNonNull(tab.getIcon()).setColorFilter(getColor(R.color.grey), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }


    //    ----------------------------------------------------------------------------------------------
    // Define variables
    private void initVars() {
        viewPager2 = findViewById(R.id.fragmentContainerViewPager2Main);
        tabLayout = findViewById(R.id.tabLayout);
        mainn = findViewById(R.id.main);
    }

    //    ----------------------------------------------------------------------------------------------
    // Double press to exit
    @Override
    public void onBackPressed() {
        if (doubleBackPressed) {
            super.onBackPressed();
        } else {
            this.doubleBackPressed = true;
            Snackbar.make(mainn, "Double press to exit!", Snackbar.LENGTH_LONG).show();

            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackPressed = false, 2000);
        }
    }



}