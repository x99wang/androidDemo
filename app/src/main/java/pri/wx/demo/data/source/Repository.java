package pri.wx.demo.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import io.reactivex.Completable;
import io.reactivex.Single;
import pri.wx.demo.data.model.User;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Repository implements DataSource {

    @Nullable
    private static Repository INSTANCE = null;

    /*
    数据源
    - 远程数据
    - 本地数据
    - 缓存
     */
    @NonNull
    private final DataSource mUsersRemoteDataSource;

    @NonNull
    private final DataSource mUsersLocalDataSource;

    @VisibleForTesting
    @Nullable
    private
    Map<String, User> mCachedUsers;


    private Repository(DataSource local,DataSource remote) {
        // 初始化数据源
        mUsersLocalDataSource = checkNotNull(local);
        mUsersRemoteDataSource = checkNotNull(remote);
    }

    /**
     * 单例模式
     *
     * @return the {@link Repository} instance
     */
    public static Repository getInstance(DataSource local,DataSource remote) {
        if (INSTANCE == null) {
            INSTANCE = new Repository(local,remote);
        }
        return INSTANCE;
    }

    /**
     * 销毁实例
     */
    public static void destroyInstance() {
        INSTANCE = null;
    }

    @Override
    public Completable saveUser(@NonNull User user) {
        checkNotNull(user);
        mUsersRemoteDataSource.saveUser(user);
        mUsersLocalDataSource.saveUser(user);

        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        mCachedUsers.put(user.getId(), user);
        return Completable.complete();
    }

    @Override
    public Completable updateUser(@NonNull User user) {
        return null;
    }

    @Override
    public Single<User> getUser(@NonNull String id) {
        checkNotNull(id);

        // 首先检查缓存
        final User cachedUser = getUserWithId(id);

        // Respond immediately with cache if available
        if (cachedUser != null) {
            return Single.just(cachedUser);
        }

        // Load from server/persisted if needed.

        // Do in memory cache update to keep the app UI up to date
        if (mCachedUsers == null) {
            mCachedUsers = new LinkedHashMap<>();
        }
        
        Single<User> localUser = getUserWithIdFromLocalRepository(id);

        Single<User> remoteUser = getUserWithFromRemoteRepository(id);

        // 本地数据和远程数据以concat方式发送
        return Single.concat(localUser,remoteUser).firstOrError();
    }

    @Nullable
    private User getUserWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedUsers == null || mCachedUsers.isEmpty()) {
            return null;
        } else {
            return mCachedUsers.get(id);
        }
    }

    @NonNull
    private Single<User> getUserWithIdFromLocalRepository(@NonNull final String id) {
        return mUsersLocalDataSource.getUser(id)
                .doOnSuccess(user -> mCachedUsers.put(id, user));
    }

    private Single<User> getUserWithFromRemoteRepository(@NonNull final String id) {
        return mUsersRemoteDataSource.getUser(id)
                .doOnSuccess(User -> {
                    mUsersLocalDataSource.saveUser(User);
                    mCachedUsers.put(User.getId(), User);
                });
    }

}
