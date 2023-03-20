package wrteam.ecart.shop.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import wrteam.ecart.shop.retrofit.model.ProfileUpdate;

public interface RetrofitInterface {
    @Multipart
    @POST("user-registration.php")
    Call<ProfileUpdate> update_profile_pic(
            @Part("accesskey") RequestBody auth_key,
            @Part("user_id") RequestBody user_id,
            @Part("type") RequestBody type,
            @Part MultipartBody.Part profile_pic
    );

}
