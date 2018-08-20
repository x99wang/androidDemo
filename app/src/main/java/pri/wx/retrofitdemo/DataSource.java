package pri.wx.retrofitdemo;

import io.reactivex.Observable;
import pri.wx.retrofitdemo.api.ApiManager;

public class DataSource {

    private static DataSource INSTANCE = null;

    private ApiManager manager;

    private DataSource() {
        manager = ApiManager.getInstance();
    }

    public static DataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataSource();
        }
        return INSTANCE;
    }

    public Observable<String> testGet() {
        return manager.getService().testGet();
    }

    public Observable<String> testPost() {
        return manager.getService().testPost();
    }

}
