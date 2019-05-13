package project.pentacore.notebook.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.ResponseBody;
import project.pentacore.notebook.R;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.model.UsersCaptionedData;
import project.pentacore.notebook.tools.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.CardViewHolder> {
    private final static String TAG = CardViewAdapter.class.getSimpleName();

    private Context context;
    private AppCompatActivity activity;
    private ArrayList<UsersCaptionedData> datas;

    private Retrofit client;
    private NotebookRESTInterface api;

    private ProgressDialog dialog;

    public CardViewAdapter(AppCompatActivity activity, ArrayList<UsersCaptionedData> datas) {
        this.activity = activity;
        this.context = this.activity.getBaseContext();
        this.datas = datas;
    }

    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        client = new RetrofitBuilder().build(context.getString(R.string.server_ipv4));
        api = client.create(NotebookRESTInterface.class);
        dialog = new ProgressDialog(activity, R.style.AlertDialogProgress);

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_item_card, parent, false);
        return new CardViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {

        UsersCaptionedData data = datas.get(position);

        Intent i = new Intent(context, DetailActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("init", false);
        i.putExtra("position", position);
        i.putStringArrayListExtra("sentences", data.getTexts());
        i.putExtra("idx", data.getIdx());
        i.putExtra("rename", data.getUrl());
        i.putExtra("date", data.getDate());
        i.putExtra("publish", data.isPublish());


        Glide.with(context)
                .asBitmap()
                .load(context.getString(R.string.server_ipv4) + "/media/" + data.getUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.d(TAG, "onLoadFailed: Glide Exception! " + e.getMessage());
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

                                    holder.llCompat.setBackgroundColor(backgroundColor);
                                    holder.llCompat.setAlpha(0.9f);
                                    holder.tvTitle.setTextColor(titleTextColor);
                                    holder.tvDate.setTextColor(titleTextColor);
                                    holder.btnDelete.setTextColor(bodyTextColor);
                                }
                            });
                        }
                        return false;
                    }
                })
                .thumbnail(0.01f)
                .centerCrop()
                .into(holder.ivImage);


        holder.tvTitle.setText(data.getDate());
        holder.tvDate.setText("");

        holder.btnDelete.setOnClickListener(view -> {

            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == DialogInterface.BUTTON_POSITIVE) {
                        dialog.show();

                        api.deleteCaptionedImage("ANDROID", data.getIdx()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    try {
                                        Log.d(TAG, "onResponse: 삭제" + response.body().string());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    datas.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                    dialog.dismiss();

                                    Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d(TAG, "onFailure: 삭제 실패");
                                t.printStackTrace();
                            }
                        });

                    }

                    if (i == DialogInterface.BUTTON_NEGATIVE) {
                        dialogInterface.cancel();
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyleDark);
            builder.setTitle(R.string.dialog_question_delete)
                    .setPositiveButton(R.string.yes, listener)
                    .setNegativeButton(R.string.no, listener).show();
        });

        holder.cardView.setOnClickListener(view -> {
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity,
                    holder.cardView,
                    holder.cardView.getTransitionName()
            );
            context.startActivity(i, compat.toBundle());
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class CardViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        LinearLayoutCompat llCompat;
        AppCompatImageView ivImage;
        AppCompatTextView tvTitle;
        AppCompatTextView tvDate;
        AppCompatButton btnDelete;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv_item);
            llCompat = itemView.findViewById(R.id.ll_inner_cv);
            ivImage = itemView.findViewById(R.id.iv_inner_cv);
            tvTitle = itemView.findViewById(R.id.tv_cv_inner);
            tvDate = itemView.findViewById(R.id.tv2_cv_inner);
            btnDelete = itemView.findViewById(R.id.btn_cv_inner);
        }
    }
}
