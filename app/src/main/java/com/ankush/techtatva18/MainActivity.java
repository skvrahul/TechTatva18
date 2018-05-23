package com.ankush.techtatva18;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ankush.techtatva18.fragments.CategoriesFragment;
import com.ankush.techtatva18.fragments.HomeFragment;
import com.ankush.techtatva18.fragments.ResultsFragment;
import com.ankush.techtatva18.fragments.ScheduleFragment;
import com.ankush.techtatva18.fragments.WorkshopsFragment;

public class MainActivity extends AppCompatActivity {
    // Listener for the selected bottom navigation items
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Load the respective fragments when each item is selected
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return loadFragment(new HomeFragment());
                case R.id.navigation_schedule:
                    return loadFragment(new ScheduleFragment());
                case R.id.navigation_categories:
                    return loadFragment(new CategoriesFragment());
                case R.id.navigation_workshops:
                    return loadFragment(new WorkshopsFragment());
                case R.id.navigation_results:
                    return loadFragment(new ResultsFragment());
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Load the home fragment first when the app launches
        loadFragment(new HomeFragment());
    }

    /**
     * Loads fragment for each selected navigation item
     *
     * @param fragment to be loaded
     * @return true if successful else false
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

}
