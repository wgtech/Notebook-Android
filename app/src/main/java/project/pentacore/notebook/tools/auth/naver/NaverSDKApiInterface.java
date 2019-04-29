package project.pentacore.notebook.tools.auth.naver;

import project.pentacore.notebook.model.NaverSDKAuthModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface NaverSDKApiInterface {

    @GET("v1/nid/me")
    Call<NaverSDKAuthModel> getUserInfo(@Header("Authorization") String token);

}
