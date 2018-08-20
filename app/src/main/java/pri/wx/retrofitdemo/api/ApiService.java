package pri.wx.retrofitdemo.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    @GET("/ajax/demo_get.asp")
    Observable<String> testGet();

    @POST("/ajax/demo_post.asp")
    Observable<String> testPost();
}


