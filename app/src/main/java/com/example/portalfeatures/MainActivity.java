package com.example.portalfeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bitheads.braincloud.client.*;
import com.example.portalfeatures.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements IServerCallback{

    IServerCallback theCallback;
    private FirebaseAnalytics mFirebaseAnalytics;

    private BrainCloudWrapper _bcWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText("ready to push notification!");

        _bcWrapper = new BrainCloudWrapper("bcWrapper");

        _bcWrapper.initialize("12832", "d03c0bf7-e00c-4179-b477-37e90bc54df9", "1.0.0");

        //create a timer that will run the callbacks of our wrapper
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                _bcWrapper.runCallbacks();
            }
            public void onFinish() {
                start(); // just restart the timer
            }
        }.start();

        _bcWrapper.setContext(MainActivity.this);

        _bcWrapper.getItemCatalogService();


        Log.i("pushing onCreate----tttt", "ttt login before" );

        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //attempt to authenticate
                TextView statusTextView = findViewById(R.id.statusTextView);
                //convert the result to a string
                statusTextView.setText("pushing notification");

                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w("inside onCompleteListener", "ttt--getInstanceId failed", task.getException());
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                _bcWrapper.getPushNotificationService().registerPushNotificationToken(Platform.GooglePlayAndroid, token, MainActivity.this);
                                Log.w("inside onCompleteListener", "ttt--token: "+token);
                                _bcWrapper.getPushNotificationService().sendRawPushNotification("561cc37f-58fc-46e8-8702-9d25f456f35d","aaaacontent","iiosContent","facebookdddd",MainActivity.this);
                                Log.w("inside onCompleteListener", "ttt--after token: "+token);
                            }
                        });

            }
        });


        _bcWrapper.authenticateEmailPassword("jasonbitheads@gmail.com", "jason7395", false, MainActivity.this);
//        _bcWrapper.authenticateEmailPassword("jasonl@bitheads.com", "jason7395", false, MainActivity.this);
        Log.i("login pushing----tttt", "ttt login success" );



        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "p1234");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "laptop");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);




//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            Log.w("inside onCompleteListener", "ttt--getInstanceId failed", task.getException());
//                            return;
//                        }
//                        // Get new Instance ID token
//                        String token = task.getResult().getToken();
//                        _bcWrapper.getPushNotificationService().registerPushNotificationToken(Platform.GooglePlayAndroid, token, theCallback);
//                        Log.w("inside onCompleteListener", "ttt--token: "+token);
//                        _bcWrapper.getPushNotificationService().sendRawPushNotification("1005279f-2dfc-4248-9f49-2ab6540cfda7","aaaacontent","iiosContent","facebookdddd",theCallback);
//                        Log.w("inside onCompleteListener", "ttt--after token: "+token);
//                    }
//                });



//        //this is the another way token.
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnSuccessListener( this,  new OnSuccessListener<InstanceIdResult>() {
//            @Override
//            public void onSuccess(InstanceIdResult instanceIdResult) {
//                String token1 = instanceIdResult.getToken();
//                Log.e("NEW_TOKEN", token1 );
//
//                //set the device up to receive pushnotifications
//                _bcWrapper.getPushNotificationService().registerPushNotificationToken(Platform.GooglePlayAndroid, token1, theCallback);
//            }
//        });






    }





    //callback functions
    public void serverCallback(ServiceName serviceName, ServiceOperation serviceOperation, JSONObject jsonData)
    {
        TextView statusTextView = findViewById(R.id.statusTextView);
        //convert the result to a string
        statusTextView.setText("logged in!");


        Log.e("AUTHENTICATED----tttt", "ttt callback success" );
    }

    public void serverError(ServiceName serviceName, ServiceOperation serviceOperation, int statusCode, int reasonCode, String jsonError)
    {
        TextView statusTextView = findViewById(R.id.statusTextView);
        statusTextView.setText("on ServerError");

        Log.e("AUTHENTICATED---servererror---ttt", "fail to callback to braindCloud. ");
    }
}
