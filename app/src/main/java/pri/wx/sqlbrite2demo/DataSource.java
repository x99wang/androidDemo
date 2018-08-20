package pri.wx.sqlbrite2demo;

import android.content.Context;
import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Single;
import pri.wx.sqlbrite2demo.api.ApiManager;
import pri.wx.sqlbrite2demo.model.User;

import java.util.Date;
import java.util.List;

public class DataSource {

    public static final User EMPTY = new User("0", "Empty", "Database is empty");

    private static final User[] LOCAL = {
            new User("1", "Allen", "Description A"),
            new User("2", "BoJack", "Description B"),
            new User("3", "Charlie", "Description C"),
            new User("4", "Danny", "Description D")
    };

    private static int COUNT = -1;

    private static DataSource INSTANCE = null;

    private ApiManager manager;

    private DataSource(Context context) {
        manager = ApiManager.getInstance(context);
        COUNT = manager.getUsers().blockingGet().size() - 1;
    }

    public static DataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataSource(context);
        }
        return INSTANCE;
    }


    @NonNull
    Single<User> getUser() {
        if(COUNT >=0)
            return manager.getUserById(String.valueOf(COUNT + 1));
        else
            return Single.error(IndexOutOfBoundsException::new);
    }

    Single<List<User>> getUsers() {
        return manager.getUsers();
    }

    Completable saveUser() {
        if(COUNT < LOCAL.length - 1)
            // 每次增加 插入一个User  游标向后移动
            return manager.saveUser(LOCAL[++COUNT]);
        else
            return Completable.error(IndexOutOfBoundsException::new);
    }

    Completable deleteUser() {
        if(COUNT >=0)
            // 每次删除一个User  游标向前移动
            return manager.deleteUser(String.valueOf(COUNT-- + 1));
        else
            return Completable.error(IndexOutOfBoundsException::new);
    }

    void deleteUsers() {
        COUNT = -1;
        manager.deleteAllUsers();
    }

    Completable updateUser() {
        if(COUNT >= 0){
            User update = new User(
                    LOCAL[COUNT].getId(),
                    LOCAL[COUNT].getUsername(),
                    "Update:" + new Date());
            return manager.updateUser(update);
        } else
            return Completable.error(IndexOutOfBoundsException::new);
    }
}
