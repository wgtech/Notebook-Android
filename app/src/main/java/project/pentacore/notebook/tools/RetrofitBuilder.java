package project.pentacore.notebook.tools;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBuilder {

    public Retrofit build(String baseUrl) {
        return new Retrofit.Builder()
                .client(new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.MINUTES)
                        .callTimeout(10, TimeUnit.MINUTES)
                        .build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
