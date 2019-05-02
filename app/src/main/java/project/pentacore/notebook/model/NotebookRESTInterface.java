package project.pentacore.notebook.model;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NotebookRESTInterface {

    @Multipart
    @POST("/upload/")
    Call<MultipartBody.Part> postImage(
            @Part MultipartBody.Part body,
            @Header("User-Agent") String platform,
            @Header("id") String id,
            @Header("service-type") String serviceType,
            @Header("publish") int publish

    );
}

