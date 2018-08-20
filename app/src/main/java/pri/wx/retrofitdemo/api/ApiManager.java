package pri.wx.retrofitdemo.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private static final String API_URL = "http://www.w3school.com.cn";

    private static ApiManager INSTANCE;

    private ApiService apiService;

    public static ApiManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ApiManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiManager();
                }
            }
        }
        return INSTANCE;
    }

    public ApiService getService(){

        /* 这里可以添加OkHttpClient拦截设置 */

        if (apiService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    //.client()//添加拦截器
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    //添加自定义格式适配(String)
                    .addConverterFactory(StringConverterFactory.create())
                    //如果返回类型为json格式 则需要添加Gson适配
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

}
