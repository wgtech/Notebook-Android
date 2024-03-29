package project.pentacore.notebook.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionManager;

import okhttp3.ResponseBody;
import project.pentacore.notebook.R;
import project.pentacore.notebook.databinding.ActivityMainBinding;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.model.UserInfo;
import project.pentacore.notebook.tools.Constants;
import project.pentacore.notebook.tools.RetrofitBuilder;
import project.pentacore.notebook.viewmodel.CaptionedCardViewModel;
import retrofit2.Call;
import retrofit2.Retrofit;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding binding;

    private ActionBarDrawerToggle toggle;

    private String addr;
    private NotebookRESTInterface api;
    private ViewModelProvider.AndroidViewModelFactory viewModelFactory;

    private UserInfo info;
    private String mIdx;
    private String mId;
    private String mEmail;
    private String mProfileUrl;
    private String mServiceType;
    private String mServiceTypeIconPath;

    private ArrayList<String> dates;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: MainActivity");
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setActivity(this);

        getDrawerLayout();
        getRawData();
    }

    private void getDrawerLayout() {
        binding.dlMain.closeDrawer(GravityCompat.START);
        toggle = new ActionBarDrawerToggle(this, binding.dlMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setToolbarNavigationClickListener(v -> {
            switch (v.getId()) {
                default:
                    Toast.makeText(this, "아무거나 눌렀네", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        toggle.syncState();
        binding.nvMain.setVerticalFadingEdgeEnabled(false);
    }

    ///////////////////////////////////////

    private void getRawData() {
        viewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        addr = getString(R.string.server_ipv4);
        Intent i = getIntent();

        Retrofit client = new RetrofitBuilder().build(addr);
        api = client.create(NotebookRESTInterface.class);

        mId = i.getStringExtra("id");
        mEmail = i.getStringExtra("email");
        mProfileUrl = i.getStringExtra("profileURL");
        mServiceType = i.getStringExtra("serviceType");
        mServiceTypeIconPath = i.getStringExtra("serviceTypeIconPath");

        // idx 가져오기
        Call<ResponseBody> call = api.loginAndGetIdx("ANDROID", mId, mServiceType);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    mIdx = call.execute().body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return mIdx;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                mIdx = s;
                Log.d(TAG, "onPostExecute: " + s);
                info = new UserInfo.Builder()
                        .id(mId)
                        .idx(mIdx)
                        .email(mEmail)
                        .serviceType(mServiceType)
                        .serviceTypeIcon(mServiceTypeIconPath)
                        .profileURL(mProfileUrl)
                        .build();

                Log.d(TAG, "onPostExecute: " + info);


                // 이후 리스트 가져오기
                getMainLayout();
                setDatasIntoRecyclerView();
            }
        }.execute();
    }

    private void getMainLayout() {
        setSupportActionBar(binding.viewIncludeMain.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_menu_black_36);
        getSupportActionBar().setTitle("");

        binding.viewIncludeMain.setFabClickMode(false);
        binding.viewIncludeMain.fab.setOnClickListener(view -> {
            clickQuickFab(view);
        });
        binding.viewIncludeMain.fabMainCamera.setOnClickListener(view -> {
            clickCameraFab(view);
        });
        binding.viewIncludeMain.fabMainGallery.setOnClickListener(view -> {
            clickGalleryFab(view);
        });

        View nav = binding.nvMain.getHeaderView(0);

        AppCompatButton btn = nav.findViewById(R.id.btn_drawer_logout);
        btn.setOnClickListener(new OnNavigationHeaderItemClickListener(MainActivity.this));

        AppCompatTextView tv = nav.findViewById(R.id.tv_drawer_name);
        if (info.getEmail() == null || info.getEmail().equals("")) {
            tv.setText(info.getId());
        } else {
            tv.setText(info.getEmail());
        }

        AppCompatImageView profile = nav.findViewById(R.id.iv_drawer_head);
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(info.getProfileURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .thumbnail(1.0f)
                .error(R.drawable.baseline_account_circle_black_48)
                .into(profile);

        AppCompatImageView icon = nav.findViewById(R.id.iv_drawer_service_type);
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(info.getServiceTypeIcon())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.baseline_account_circle_black_48)
                .into(icon);

        binding.nvMain.setNavigationItemSelectedListener(this);
    }

    protected void setDatasIntoRecyclerView() {
        CaptionedCardViewModel model = viewModelFactory.create(CaptionedCardViewModel.class);
        model.getDatas(getString(R.string.server_ipv4), mIdx).observe(this, usersCaptionedDatas -> {

            // 데이터 있으면
            if (usersCaptionedDatas == null || usersCaptionedDatas.size() == 0) {
                LottieViewAdapter adapter = new LottieViewAdapter();
                binding.viewIncludeMain.rvMain.setAdapter(adapter);
                binding.viewIncludeMain.rvMain.setLayoutManager(new GridLayoutManager(
                        MainActivity.this, 1));
            }

            // 없으면
            else {
                binding.viewIncludeMain.rvMain.setHasFixedSize(true);
                CardViewAdapter adapter = new CardViewAdapter(MainActivity.this, usersCaptionedDatas);
                binding.viewIncludeMain.rvMain.setAdapter(adapter);
                binding.viewIncludeMain.rvMain.setLayoutManager(new LinearLayoutManager(
                        MainActivity.this, RecyclerView.VERTICAL, false));
            }
        });
    }

    ///////////////////////////////////////

    public void clickQuickFab(View view) {
        boolean isFabClick = binding.viewIncludeMain.getFabClickMode();

        if (isFabClick) {
            Transition t = TransitionInflater.from(mContext).inflateTransition(R.transition.trans_fab_rotate_off);
            TransitionManager.beginDelayedTransition(binding.viewIncludeMain.mainLayout, t);
        } else {
            Transition t = TransitionInflater.from(mContext).inflateTransition(R.transition.trans_fab_rotate_on);
            TransitionManager.beginDelayedTransition(binding.viewIncludeMain.mainLayout, t);
        }

        binding.viewIncludeMain.setFabClickMode(!isFabClick);
    }

    public void clickCameraFab(View view) {
        Intent camera = new Intent(MainActivity.this, Camera2Activity.class);
        camera.putExtra("idx", info.getIdx());
        camera.putExtra("id", info.getId());
        camera.putExtra("serviceType", info.getServiceType());
        startActivityForResult(camera, Constants.CAMERA_REQUEST);
    }

    public void clickGalleryFab(View view) {
        Intent gallery = new Intent(MainActivity.this, GalleryActivity.class);
        gallery.putExtra("idx", info.getIdx());
        gallery.putExtra("id", info.getId());
        gallery.putExtra("serviceType", info.getServiceType());
        startActivityForResult(gallery, Constants.GALLERY_REQUEST);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // Account Icon
            default: // Navigation Icon
                if (binding.dlMain.isDrawerOpen(GravityCompat.START)) {
                    binding.dlMain.closeDrawer(GravityCompat.START);
                } else {
                    binding.dlMain.openDrawer(GravityCompat.START);
                }
                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_nav_camera:
                Intent camera = new Intent(MainActivity.this, Camera2Activity.class);
                camera.putExtra("idx", info.getIdx());
                camera.putExtra("id", info.getId());
                camera.putExtra("serviceType", info.getServiceType());
                startActivityForResult(camera, Constants.CAMERA_REQUEST);
                break;

            case R.id.item_nav_gallery:
                Intent gallery = new Intent(MainActivity.this, GalleryActivity.class);
                gallery.putExtra("idx", info.getIdx());
                gallery.putExtra("id", info.getId());
                gallery.putExtra("serviceType", info.getServiceType());
                startActivityForResult(gallery, Constants.GALLERY_REQUEST);
                break;

            case R.id.item_nav_credits:
                break;

            default:
                break;
        }
        binding.dlMain.closeDrawers();

        return false;
    }


    @Override
    public void onBackPressed() {
        if (binding.dlMain.isDrawerOpen(GravityCompat.START)) {
            binding.dlMain.closeDrawers();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.PERMISSIONS_RESPONSE_FAIL) {
            Snackbar.make(binding.dlMain, "권한 확인바랍니다.", Snackbar.LENGTH_LONG).show();
        }

        if (resultCode == Constants.CAMERA_PIC_OK) {
            // 데이터 새로고침
            Log.d(TAG, "onActivityResult: 카메라 저장 성공");

            // ArrayList 추가
            setDatasIntoRecyclerView();

            // 미리보기 화면 전환
            Intent preview = new Intent(mContext, DetailActivity.class);
            preview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            preview.putExtras(data);
            startActivity(preview);
        }

        if (resultCode == Constants.CAMERA_PIC_FAIL) {

        }

        if (resultCode == Constants.GALLERY_RESPONSE_OK) {
            // 호출
            Log.d(TAG, "onActivityResult: 사진 불러오기 성공 " + data.getData().getPath());

            // ArrayList 추가
            setDatasIntoRecyclerView();

            // 미리보기 화면 전환
            Intent preview = new Intent(mContext, DetailActivity.class);
            preview.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            preview.putExtras(data);
            startActivity(preview);
        }

        if (resultCode == Constants.GALLERY_RESPONSE_FAIL) {
            Log.d(TAG, "onActivityResult: 사진 불러오기 실패/취소");
        }
    }
}

