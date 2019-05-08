package project.pentacore.notebook.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.model.UsersCaptionedData;
import project.pentacore.notebook.tools.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CaptionedCardViewModel extends ViewModel {
    private final static String TAG = CaptionedCardViewModel.class.getSimpleName();

    private MutableLiveData<ArrayList<UsersCaptionedData>> list;

    public MutableLiveData<ArrayList<UsersCaptionedData>> getDatas(String url, String mIdx) {
        if (list == null) {
            list = new MutableLiveData<>();

            loadDatas(url, mIdx);
        }

        return list;
    }

    private void loadDatas(String url, String mIdx) {
        Retrofit client = new RetrofitBuilder().build(url);
        NotebookRESTInterface api = client.create(NotebookRESTInterface.class);

        Call<ResponseBody> callImages = api.getCaptionedImages("ANDROID", Integer.parseInt(mIdx));
        callImages.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String body = response.body().string();
                        Log.d(TAG, "onResponse: " + body);

                        Gson gson = new Gson();
                        ArrayList<UsersCaptionedData> datas = gson.fromJson(body, new TypeToken<ArrayList<UsersCaptionedData>>(){}.getType());
                        list.setValue(datas);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: ");
                t.printStackTrace();
            }
        });


    }
}
