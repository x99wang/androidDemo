package pri.wx.demo.data.source;

import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import pri.wx.demo.data.model.User;

public interface DataSource {

    Completable saveUser(@NonNull User user);

    Completable updateUser(@NonNull User user);

    Single<User> getUser(@NonNull String id);




}
