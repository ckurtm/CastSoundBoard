package mbanje.kurt.soundboard;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONObject;

/**
 * @author Kurt
 * @since 2017/02/01.
 *
 * This is a demo application meant to demonstrate how to create a custom chromecast receiver application making use of the web audio apis,
 * it is in no way production ready, but merely serves as a demo
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SessionManager sessionManager;

    //we use this to listen for connections to a cast device so we enable or disable the toggle widgets
    CastSessionListener castSessionListener = new CastSessionListener() {
        @Override
        public void onSessionStarted(final Session session, final String s) {
            super.onSessionStarted(session, s);
            enableSwitches(true);
        }

        @Override
        public void onSessionResumed(final Session session, final boolean b) {
            super.onSessionResumed(session, b);
            enableSwitches(true);
        }

        @Override
        public void onSessionEnded(final Session session, final int i) {
            super.onSessionEnded(session, i);
            enableSwitches(false);
        }
    };

    //the onscreen switches to handle turning on/off and audio clip on the receiver application
    private Switch switchRain, switchThunder, switchBirds, switchWolves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switchRain = (Switch) findViewById(R.id.switch_rain);
        switchThunder = (Switch) findViewById(R.id.switch_thunder);
        switchBirds = (Switch) findViewById(R.id.switch_birds);
        switchWolves = (Switch) findViewById(R.id.switch_wolves);
        setUpCastIcon();
        setUpToggleListeners();
    }

    private void setUpToggleListeners() {
        //setup listeners for the toggling of the switch widgets
        switchRain.setOnCheckedChangeListener(new ToggleListener("rain"));
        switchThunder.setOnCheckedChangeListener(new ToggleListener("thunder"));
        switchBirds.setOnCheckedChangeListener(new ToggleListener("birds"));
        switchWolves.setOnCheckedChangeListener(new ToggleListener("wolves"));
    }

    private void setUpCastIcon() {
        MediaRouteButton castButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), castButton);
        CastContext castContext = CastContext.getSharedInstance(this);
        sessionManager = castContext.getSessionManager();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);
        return true;
    }

    @Override
    protected void onResume() {
        sessionManager.addSessionManagerListener(castSessionListener);
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        sessionManager.removeSessionManagerListener(castSessionListener);
    }

    private void enableSwitches(boolean enable) {
        switchRain.setEnabled(enable);
        switchBirds.setEnabled(enable);
        switchThunder.setEnabled(enable);
        switchWolves.setEnabled(enable);
        if(!enable){//toggle all switches to off when we disconnect
            switchRain.setChecked(false);
            switchBirds.setChecked(false);
            switchThunder.setChecked(false);
            switchWolves.setChecked(false);
        }
    }

    private class ToggleListener implements CompoundButton.OnCheckedChangeListener {

        private final String sound;
        private final String ACTION_PLAY = "play";
        private final String ACTION_STOP = "stop";

        private ToggleListener(final String sound) {
            this.sound = sound;
        }

        @Override
        public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
            try {

                //create the message payload we will send to the cast receiver application
                final JSONObject obj = new JSONObject();
                obj.put("action",isChecked?ACTION_PLAY:ACTION_STOP);
                obj.put("sound",sound);

                final String json = obj.toString(1);
                //we only send the message if we have an active session
                if(buttonView.isEnabled() && sessionManager.getCurrentCastSession() != null) {
                    sessionManager.getCurrentCastSession().sendMessage(CastOptionsProvider.NAMESPACE, json).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull final Status status) {
                            if (status.isSuccess()) {
                                Log.d(TAG, "message sent successfully: " + json);
                            } else {
                                Log.e(TAG, "failed to send message: " + json);
                            }
                        }
                    });
                }
            }catch (Exception e){
                Log.e(TAG, "sending message for sound["+sound+"] failed",e);
            }
        }
    }
}
