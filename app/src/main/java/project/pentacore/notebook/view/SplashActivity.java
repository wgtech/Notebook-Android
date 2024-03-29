package project.pentacore.notebook.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private final static String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Splash Start");
        super.onCreate(savedInstanceState);

        //Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);

        finish();

    }
}
