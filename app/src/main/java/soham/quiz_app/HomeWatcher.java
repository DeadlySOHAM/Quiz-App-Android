package soham.quiz_app;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class HomeWatcher {
    static final String TAG = "hg";
    private Context mContext;
    private IntentFilter mFilter;
    private HomeButtonReceiver mReceiver;

    public HomeWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnHomePressedListener(HomeButtonReceiver.OnHomePressedListener listener) {
        mReceiver = new HomeButtonReceiver(listener);
    }

    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    public void stopWatch() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }
}