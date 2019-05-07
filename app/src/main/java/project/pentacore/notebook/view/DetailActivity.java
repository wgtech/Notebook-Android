package project.pentacore.notebook.view;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.util.Log;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;
import project.pentacore.notebook.R;
import project.pentacore.notebook.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private ActivityDetailBinding binding;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
        getWindow().setSharedElementEnterTransition(new AutoTransition());
        getWindow().setSharedElementReturnTransition(new AutoTransition());
        getWindow().setEnterTransition(new Explode());
        getWindow().setExitTransition(new Explode());

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setActivity(this);

        Intent i = getIntent();
        Log.d(TAG, "onCreate: " + i.getIntExtra("position", 99999) + ", " + i.getStringExtra("url"));

        String transitionTag = i.getStringExtra("transition");
        if (transitionTag != null) binding.ivDetail.setTransitionName(transitionTag);

        String idx = i.getStringExtra("idx");
        String rename = i.getStringExtra("rename");
        String publish = i.getStringExtra("publish");
        String model = i.getStringExtra("model");
        ArrayList<String> sentences = i.getStringArrayListExtra("sentences");


        Glide.with(getBaseContext())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .load(getString(R.string.server_ipv4) + "/media/" + rename)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        if (resource != null){
                            Palette.from(resource).generate(palette -> {
                                if (palette == null) return;

                                Palette.Swatch vibrantSwatch = palette.getDarkVibrantSwatch();

                                if (vibrantSwatch != null) {
                                    int backgroundColor = vibrantSwatch.getRgb();
                                    int titleTextColor = vibrantSwatch.getTitleTextColor();
                                    int bodyTextColor = vibrantSwatch.getBodyTextColor();

                                    binding.clDetailButtons.setBackgroundColor(backgroundColor);
                                    binding.btnDetailCloud.setBackgroundTintList(ColorStateList.valueOf(titleTextColor));
                                    binding.btnDetailShare.setBackgroundTintList(ColorStateList.valueOf(titleTextColor));
                                    binding.btnDetailSound.setBackgroundTintList(ColorStateList.valueOf(titleTextColor));
                                    binding.clDetailAreaBottom.setBackgroundColor(backgroundColor);
                                }
                            });
                        }
                        return false;
                    }
                })
                .into(binding.ivDetail);

        if (sentences != null) playTTS(sentences.get(0));
    }

    private void playTTS(String text) {
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.ENGLISH);
                tts.setSpeechRate(1.0f);
                tts.setPitch(1.0f);
                tts.speak(text,
                        tts.QUEUE_FLUSH,
                        null, null);

            }
        });
    }

    private void stopTTS() {
        if (tts.isSpeaking()) {
            tts.stop();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: 눌렀다.");
        super.onBackPressed();
        tts.shutdown();
        finishAfterTransition();
    }
}

