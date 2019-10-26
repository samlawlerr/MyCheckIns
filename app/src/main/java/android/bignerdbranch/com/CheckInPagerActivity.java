package android.bignerdbranch.com;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;
import java.util.UUID;

public class CheckInPagerActivity extends AppCompatActivity {
    private static final String EXTRA_CHECK_ID = "com.bignerdranch.android.criminalintent.crime_id";
    private static final int REQUEST_ERROR = 0;

    private ViewPager mViewPager;
    private List<CheckIn> mCheckIns;
    public static Intent newIntent(Context packageContext, UUID checkId) {
        Intent intent = new Intent(packageContext, CheckInPagerActivity.class);
        intent.putExtra(EXTRA_CHECK_ID, checkId);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkin_pager);

        UUID checkId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CHECK_ID);


        mViewPager = (ViewPager) findViewById(R.id.checkin_view_pager);
        mCheckIns = CheckInLab.get(this).getCheckIn();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                CheckIn check = mCheckIns.get(position);
                return CheckInFragment.newInstance(check.getId());
            }
            @Override
            public int getCount() {
                return mCheckIns.size();
            }
        });

        for (int i = 0; i < mCheckIns.size(); i++) {
            if (mCheckIns.get(i).getId().equals(checkId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            finish();
                        }
                    });

            errorDialog.show();
        }
    }

}
