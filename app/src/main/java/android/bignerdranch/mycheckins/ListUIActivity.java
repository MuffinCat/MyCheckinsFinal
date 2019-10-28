package android.bignerdranch.mycheckins;

import androidx.fragment.app.Fragment;

public class ListUIActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ListUIFragment();
    }
}
