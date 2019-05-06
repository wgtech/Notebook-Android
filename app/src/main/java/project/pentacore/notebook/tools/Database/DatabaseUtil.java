package project.pentacore.notebook.tools.Database;

import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import project.pentacore.notebook.model.Member;
import project.pentacore.notebook.model.NotebookRESTInterface;
import project.pentacore.notebook.tools.RetrofitBuilder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DatabaseUtil {
    private final static String TAG = DatabaseUtil.class.getSimpleName();

    private String url;
    private NotebookRESTInterface api;

    public DatabaseUtil(String url) {
        this.url = url;
    }

    public String login(String id, String serviceType) {
        Retrofit client = new RetrofitBuilder().build(url);
        api = client.create(NotebookRESTInterface.class);
        Call<Member> call = api.loginAndGetIdx("ANDROID", id, serviceType);
        call.enqueue(new Callback<Member>() {
            @Override
            public void onResponse(Call<Member> call, Response<Member> response) {
                Log.d(TAG, "onResponse: " + response.code() + ", " + response.message());
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.body().getId() + ", " + response.body().getServiceType());
                }
            }

            @Override
            public void onFailure(Call<Member> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
            }
        });

        return null;
    }
}
