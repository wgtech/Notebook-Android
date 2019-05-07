package project.pentacore.notebook.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import project.pentacore.notebook.R;
import project.pentacore.notebook.databinding.ActivityGalleryBinding;
import project.pentacore.notebook.model.Data;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.tools.Constants;
import project.pentacore.notebook.tools.PermissionsActivity;
import project.pentacore.notebook.tools.RetrofitBuilder;
import project.pentacore.notebook.tools.UriFilePathConverter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GalleryActivity extends AppCompatActivity {
    private final static String TAG = GalleryActivity.class.getSimpleName();

    private ActivityGalleryBinding binding;
    private Context mContext;
    private Intent preview;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(GalleryActivity.this, R.layout.activity_gallery);
        binding.setActivity(this);
        mContext = GalleryActivity.this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };

            Intent perm = new Intent(this, PermissionsActivity.class);
            perm.putExtra("permissions", permissions);

            startActivityForResult(perm, Constants.PERMISSIONS_REQUEST);
        } else {
            initGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 권한
        if (requestCode == Constants.PERMISSIONS_REQUEST) {
            if (resultCode == Constants.PERMISSIONS_RESPONSE_OK) {
                Log.d(TAG, "onActivityResult: 권한 획득 성공");
                initGallery();
            }

            else if (resultCode == Constants.GALLERY_RESPONSE_FAIL) {
                Log.d(TAG, "onActivityResult: 권한 획득 실패");
                finish();
            }
        }

        // 카메라
        if (requestCode == Constants.GALLERY_REQUEST) {
            if (data != null) {
                preview = data;
                Log.d(TAG, "onActivityResult: 사진 선택 " + preview.getData().getPath());

                // 프리뷰
                Glide.with(GalleryActivity.this)
                        .asBitmap()
                        .addListener(new RequestListener<Bitmap>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                setResult(Constants.GALLERY_RESPONSE_FAIL);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .load(preview.getData())
                        .into(binding.ivGalleryPreview);


            } else {
                Log.d(TAG, "onActivityResult: 사진 미선택");
                setResult(Constants.GALLERY_RESPONSE_FAIL);
                finish();
            }
        }
    }

    private void initGallery() {
        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_PICK);
        gallery.setType(MediaStore.Images.Media.CONTENT_TYPE);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, Constants.GALLERY_REQUEST);
    }

    public void clickCancel(View view) {
        Toast.makeText(this, "취소했습니다.", Toast.LENGTH_SHORT).show();
        initGallery();
    }

    public void clickOk(View view) {
        dialog = new ProgressDialog(mContext, R.style.AlertDialogProgress);
        dialog.show();

        UriFilePathConverter converter = new UriFilePathConverter(GalleryActivity.this);

        Retrofit client = new RetrofitBuilder().build(getString(R.string.server_ipv4));
        NotebookRESTInterface api = client.create(NotebookRESTInterface.class);

        File file = new File(converter.getPathFromUri(preview.getData()));
        Log.d(TAG, "clickOk: " + file.getAbsolutePath());

        Log.d(TAG, "clickOk: " + getIntent().getStringExtra("id") + ", "
        + getIntent().getStringExtra("serviceType"));
        RequestBody requestImg = RequestBody.create(MediaType.parse("Content-type: multipart/formed-data"), file);

        Call<Data> call = api.postImage(
                MultipartBody.Part.createFormData("image", file.getName(), requestImg),
                "ANDROID",
                getIntent().getStringExtra("idx"),
                getIntent().getStringExtra("id"),
                getIntent().getStringExtra("serviceType"),
                0
        );
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()) {
                    Data data = response.body();
                    Log.d(TAG, "onResponse: " + data);
                    for (String s:
                         data.getSentences()) {
                        Log.d(TAG, "onResponse: " + s);
                    }
                    dialog.dismiss();

                    preview.putExtra("idx", data.getIdx());
                    Log.d(TAG, "onResponse: " + data.getRename());
                    preview.putExtra("rename", data.getRename());
                    preview.putExtra("publish", data.getPublish());
                    preview.putExtra("model", data.getModel());
                    preview.putExtra("sentences", data.getSentences());
                    setResult(Constants.GALLERY_RESPONSE_OK, preview);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Log.d(TAG, "onFailure: 실패 ");
                t.printStackTrace();

                dialog.dismiss();
                setResult(Constants.GALLERY_RESPONSE_FAIL);
                finish();
            }
        });


    }


}
