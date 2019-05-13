package project.pentacore.notebook.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.palette.graphics.Palette;

import okhttp3.ResponseBody;
import project.pentacore.notebook.R;
import project.pentacore.notebook.databinding.ActivityDetailBinding;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.tools.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private Context mContext;
    private ActivityDetailBinding binding;

    private String idx;
    private String rename;
    private String model;
    private boolean publish;
    private ArrayList<String> sentences;

    private Retrofit client;
    private NotebookRESTInterface api;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = DetailActivity.this;

        client = new RetrofitBuilder().build(getString(R.string.server_ipv4));
        api = client.create(NotebookRESTInterface.class);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        binding.setActivity(this);

        Intent intent = getIntent();
        if (intent != null) {
            boolean init = intent.getBooleanExtra("init", true);

            idx = intent.getStringExtra("idx"); // CardViewAdapter 에서 넘어온 것은 null 로 체크됨.
            rename = intent.getStringExtra("rename");
            publish = false;
            model = "";
            sentences = intent.getStringArrayListExtra("sentences");
            if (init) {
                publish = intent.getStringExtra("publish").equals("0")? false: true;
                model = intent.getStringExtra("model");
            } else {
                publish = intent.getBooleanExtra("publish", false);
            }

            binding.setPublishMode(publish);
        }


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
                                    binding.btnDetailPublish.setBackgroundTintList(ColorStateList.valueOf(titleTextColor));
                                    binding.btnDetailSound.setBackgroundTintList(ColorStateList.valueOf(titleTextColor));
                                    binding.clDetailAreaBottom.setBackgroundColor(backgroundColor);
                                    binding.clDetailLineTop.setBackgroundColor(bodyTextColor);
                                    binding.clDetailLineBottom.setBackgroundColor(bodyTextColor);
                                    binding.npDetailSentence.setBackgroundColor(backgroundColor);
                                }
                            });
                        }
                        return false;
                    }
                })
                .into(binding.ivDetail);

        if (sentences != null) {
            Log.d(TAG, "onCreate: " + sentences.size());
            playTTS(sentences.get(0));

            String[] array = sentences.toArray(new String[sentences.size()]);
            binding.npDetailSentence.setWrapSelectorWheel(false);
            binding.npDetailSentence.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            binding.npDetailSentence.setMinValue(0);
            binding.npDetailSentence.setMaxValue(array.length-1);
            binding.npDetailSentence.setDisplayedValues(array);
            binding.npDetailSentence.setOnClickListener(v -> {
                stopTTS();
                playTTS(sentences.get(binding.npDetailSentence.getValue()));
            });
            binding.npDetailSentence.setOnScrollListener((view, scrollState) -> {
                if (scrollState == NumberPicker.OnScrollListener.SCROLL_STATE_IDLE) {
                    stopTTS();
                    playTTS(sentences.get(binding.npDetailSentence.getValue()));
                }
            });
        }
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

    public void clickChangePublish(View view) {
        int index = (idx==null? getIntent().getIntExtra("idx",0) : Integer.parseInt(idx));
        api.updatePublishedImage("ANDROID", index).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        Log.d(TAG, "onResponse: " + response.body().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (publish) {
                        Toast.makeText(mContext, getString(R.string.msg_publish_no), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.msg_publish_okay), Toast.LENGTH_SHORT).show();
                    }

                    publish = !publish;
                    binding.setPublishMode(publish);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(mContext, getString(R.string.msg_publish_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (tts != null) tts.shutdown();
        supportFinishAfterTransition();
    }
}

