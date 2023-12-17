package soham.quiz_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class HomeButtonReceiver extends BroadcastReceiver {

    private final static String TAG = "test->HomeButtonReceive",
            SYSTEM_DIALOG_REASON_KEY = "reason",
            SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions",
            SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps",
            SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

    private final OnHomePressedListener listener;

    public HomeButtonReceiver(OnHomePressedListener listener){
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null) {
                Log.e(TAG, "action:" + action + ",reason:" + reason);
                if (listener != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        listener.onHomePressed();
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        listener.onHomeLongPressed();
                    }
                }
            }
        }
    }

    // callback interfaces
    public interface OnHomePressedListener {
        void onHomePressed();
        void onHomeLongPressed();
    }
}

