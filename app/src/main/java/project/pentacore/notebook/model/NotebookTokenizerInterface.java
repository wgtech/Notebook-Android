package project.pentacore.notebook.model;

import okhttp3.Cookie;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface NotebookTokenizerInterface {

    @GET("/upload/")
    Call<ResponseBody> getUploadToken();
}
