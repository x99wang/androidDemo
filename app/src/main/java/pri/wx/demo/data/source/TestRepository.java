package pri.wx.demo.data.source;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import pri.wx.demo.data.model.User;

import java.util.HashMap;
import java.util.Map;

public class TestRepository implements DataSource{

    private static TestRepository INSTANCE = null;

    private User[] users = new User[]{
      new User("1","Ali","Alibaba"),
      new User("2","Bill","Microsoft"),
      new User("3","Candy","Money")
    };

    private Map<String,User> map;

    private TestRepository() {
        map = new HashMap<>();
        for(User u : users)
            map.put(u.getId(), u);
    }

    public static TestRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestRepository();
        }
        return INSTANCE;
    }

    @Override
    public Completable saveUser(@NonNull User user) {
        return null;
    }

    @Override
    public Completable updateUser(@NonNull User user) {
        return null;
    }

    @Override
    public Single<User> getUser(@NonNull String id) {
        if (null == map.get(id)) {
            return Single.error(new Throwable("Not found user"));
        }
        return Observable.just(map.get(id))
                .firstOrError();
    }

}
