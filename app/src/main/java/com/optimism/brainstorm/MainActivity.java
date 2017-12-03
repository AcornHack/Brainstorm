package com.optimism.brainstorm;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager());

        ViewPager pager = findViewById(R.id.viewpager);
        pager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            //noinspection ConstantConditions
            tabLayout.getTabAt(i).setIcon(pagerAdapter.getIcon(i));
        }
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private Fragment[] fragments;
        private int[] icons;

        PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            fragments = new Fragment[] {
                    HomeFragment.newInstance(),
                    UploadFragment.newInstance(),
                    ProfileFragment.newInstance()
            };

            icons = new int[] {
                    R.drawable.ic_home,
                    R.drawable.ic_upload,
                    R.drawable.ic_profile
            };
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        int getIcon(int position) {
            return icons[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }
    }
}
