package uwaterloo.ca.patientmobile;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;;
import android.view.MenuItem;

import uwaterloo.ca.patientmobile.dashboard.DashBoardFragment;
import uwaterloo.ca.patientmobile.log.LogFragment;
import uwaterloo.ca.patientmobile.measure.MeasureFragment;
import uwaterloo.ca.patientmobile.setting.SettingFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment logFragment;
    private Fragment dashBoardFragment;
    private Fragment settingFragment;
    private Fragment measureFragment;
    private FragmentManager fragmentManager;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_measure:
                    fragmentManager.beginTransaction().replace(R.id.content_frame, measureFragment).commit();
                    return true;
                case R.id.navigation_dashboard:
                    fragmentManager.beginTransaction().replace(R.id.content_frame, dashBoardFragment).commit();
                    return true;
                case R.id.navigation_setting:
                    fragmentManager.beginTransaction().replace(R.id.content_frame, settingFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        logFragment = new LogFragment();
        dashBoardFragment = new DashBoardFragment();
        measureFragment = new MeasureFragment();
        settingFragment = new SettingFragment();

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, dashBoardFragment).commit();
    }

}
