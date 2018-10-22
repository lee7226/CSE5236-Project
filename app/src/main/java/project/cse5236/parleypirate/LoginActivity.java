package project.cse5236.parleypirate;

import android.support.v4.app.Fragment;

public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() { return new LoginActivityFragment(); }

}
