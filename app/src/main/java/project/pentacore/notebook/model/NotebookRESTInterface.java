package project.pentacore.notebook.model;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
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
    Call<UserAfterCaptionedData> postImage(
            @Part MultipartBody.Part body,
            @Header("User-Agent") String platform,
            @Header("idx") String idx,
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
    Call<ResponseBody> loginAndGetIdx(
            @Header("User-Agent") String platform,
            @Field("id") String id,
            @Field("service_type") String service_type
    );


    /**
     * Get Captioned Images
     *
     * @param platform
     * @param idx
     * @return
     */
    @GET("/")
    Call<ResponseBody> getCaptionedImages(
           @Header("User-Agent") String platform,
           @Header("idx") int idx
    );


    /**
     * Delete the user's delete captioned image.
     * @param platform
     * @param idx
     * @return
     */
    @POST("/api/delete/")
    @FormUrlEncoded
    Call<ResponseBody> deleteCaptionedImage(
            @Header("User-Agent") String platform,
            @Field("idx") int idx
    );


    /**
     * Update Published Image
     * @param platform
     * @param idx
     * @return
     */
    @POST("/api/update/")
    @FormUrlEncoded
    Call<ResponseBody> updatePublishedImage(
            @Header("User-Agent") String platform,
            @Field("idx") int idx
    );

}

