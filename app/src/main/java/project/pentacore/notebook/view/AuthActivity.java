package project.pentacore.notebook.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kakao.auth.KakaoSDK;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import project.pentacore.notebook.R;
import project.pentacore.notebook.databinding.ActivityAuthBinding;
import project.pentacore.notebook.model.ServiceType;
import project.pentacore.notebook.tools.KeyHashTools;
import project.pentacore.notebook.tools.auth.kakao.KakaoSDKApplication;
import project.pentacore.notebook.tools.auth.kakao.KakaoSDKAdapter;
import project.pentacore.notebook.tools.auth.naver.NaverSDKApplication;

public class AuthActivity extends AppCompatActivity {
    private final static String TAG = AuthActivity.class.getSimpleName();

    private ActivityAuthBinding binding;
    private KakaoSDKAdapter kakaoAdapter;
    private KakaoSDKApplication kakaoApp;
    private NaverSDKApplication naverApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth);
        binding.setActivity(this);

        // 디버그 앱 키 해쉬
        Log.d(TAG, "onCreate: " + KeyHashTools.getKeyHashForKakao(getBaseContext()));

    }

    @Override
    protected void onStart() {
        super.onStart();
        startSDK();
    }

    private void startSDK() {
        // 카카오
        if (KakaoSDK.getAdapter() == null) {
            Log.d(TAG, "startSDK: KakaoSDK 초기 실행");
            kakaoAdapter = new KakaoSDKAdapter();
            KakaoSDK.init(kakaoAdapter);
        } else {
            Log.d(TAG, "startSDK: KakaoSDK 실행중");
        }

        // 네이버
        if (naverApp == null) {
            naverApp = new NaverSDKApplication(AuthActivity.this,
                getString(R.string.naver_client_id),
                getString(R.string.naver_client_secret),
                getString(R.string.naver_client_name)
            );
        }

    }


    public void naverAuthClick(View view) {
        ObservableMap<String, String> map = new ObservableArrayMap<>();
        map.addOnMapChangedCallback(new ObservableMap.OnMapChangedCallback<ObservableMap<String, String>, String, String>() {
            @Override
            public void onMapChanged(ObservableMap<String, String> sender, String key) {
                if (sender.size() >= 2) { // 필수 : id, email
                    Log.d(TAG, "onMapChanged: " + sender.get("id") + ", " + sender.get("email"));
                    Intent intent = new Intent(AuthActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("serviceType", ServiceType.NAVER.getValue());
                    intent.putExtra("id", sender.get("id"));
                    intent.putExtra("email", sender.get("email"));
                    intent.putExtra("profileURL", sender.get("profile_image"));
                    intent.putExtra("serviceTypeIconPath", ServiceType.NAVER.getIconPath());
                    startActivity(intent);
                    finish();
                }
            }
        });
        naverApp.login(map);
    }

    public void kakaoAuthClick(View view) {
        kakaoApp = KakaoSDKApplication.getInstance();
        kakaoApp.setActivity(this);
        kakaoApp.getKakaoAuth(KakaoSDK.getAdapter());
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
