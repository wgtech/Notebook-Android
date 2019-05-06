package project.pentacore.notebook.model;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface NotebookRESTInterface {

    /**
     * Image Posting
     *
     * @param body
     * @param platform
     * @param id
     * @param serviceType
     * @param publish
     * @return
     */
    @Multipart
    @POST("/upload/")
    Call<MultipartBody.Part> postImage(
            @Part MultipartBody.Part body,
            @Header("User-Agent") String platform,
            @Header("id") String id,
            @Header("service-type") String serviceType,
            @Header("publish") int publish
    );


    /**
     * Login and Database Connectivity
     *
     * @param id
     * @param service_type
     * @return
     */
    @POST("/login/")
    @FormUrlEncoded
    Call<Member> loginAndGetIdx(
            @Header("User-Agent") String platform,
            @Field("id") String id,
            @Field("service_type") String service_type
    );
}

