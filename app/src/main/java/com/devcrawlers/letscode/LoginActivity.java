package com.devcrawlers.letscode;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.devcrawlers.letscode.fragment.FragmentLogin;
import com.devcrawlers.letscode.fragment.FragmentSignIn;
import com.devcrawlers.letscode.Preferences.UserPreferences;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.register_pager)
    ViewPager registerViewPager;

    @BindView(R.id.dotsIndicator)
    WormDotsIndicator dotsIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserPreferences.init(getApplication());
        if (UserPreferences.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        registerViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        dotsIndicator.setViewPager(registerViewPager);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        for (Fragment fragment : getSupportFragmentManager().getFragments())
            fragment.onActivityResult(requestCode, resultCode, data);

    }

    public void showSignIn() {
        registerViewPager.setCurrentItem(2);
    }

    class MyViewPagerAdapter extends FragmentPagerAdapter {


        public MyViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }


        Fragment[] fragments = new Fragment[]{
                new FragmentLogin(),
                new FragmentSignIn()
        };


        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


}
