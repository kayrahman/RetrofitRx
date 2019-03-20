package neel.com.retrofitrx.api;

import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import neel.com.retrofitrx.model.Category;
import neel.com.retrofitrx.model.User;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface UserApi {





    interface Auth{

        @GET("/users.json")
        Call<String> getApiResponse(
        );


        @POST("/api/users")
        Call<User> postUserData(@Body User user);



        @Multipart
        @POST("/api/users")
        Call<ResponseBody> uploadFileWithPartMap(
                @PartMap() Map<String, RequestBody> partMap,
                @Part MultipartBody.Part file);



        @Multipart
        @POST("/api/users")
        Call<ResponseBody> uploadDataWithPartMap(
                @PartMap() Map<String, RequestBody> partMap);


        @GET("/api/categories")
        Call<List<Category>> getCategories();


        @Multipart
        @POST("/api/users")
        Observable<ResponseBody> uploadDataWithRx(
                @PartMap() Map<String, RequestBody> partMap);



    }



}
