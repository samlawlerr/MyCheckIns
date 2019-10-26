package android.bignerdbranch.com;

import android.support.v4.app.Fragment;

public class CheckInListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new ListUI();
    }

}
