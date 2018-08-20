package pri.wx.sqlbrite2demo.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.squareup.sqlbrite2.BriteDatabase;
import com.squareup.sqlbrite2.SqlBrite;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import pri.wx.sqlbrite2demo.api.PersistenceContract.UserEntry;
import pri.wx.sqlbrite2demo.model.User;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class ApiManager {

    @Nullable
    private static ApiManager INSTANCE;

    @NonNull
    private final BriteDatabase mDatabaseHelper;

    @NonNull
    private Function<Cursor, User> mUserMapperFunction;

    private ApiManager(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, Schedulers.io());
        mUserMapperFunction = this::getUser;
    }

    @NonNull
    private User getUser(@NonNull Cursor c) {
        String id = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_ENTRY_ID));
        String name = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME));
        String description = c.getString(c.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_DESCRIPTION));
        return new User(id, name, description);
    }

    public static ApiManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ApiManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApiManager(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 查
     * @return
     */
    public Single<List<User>> getUsers() {
        //要查询的字段集合
        String[] projection = {
                UserEntry.COLUMN_NAME_ENTRY_ID,
                UserEntry.COLUMN_NAME_USERNAME,
                UserEntry.COLUMN_NAME_DESCRIPTION
        };

        //sql语句
        String sql = String.format("SELECT %s FROM %s",
                TextUtils.join(",", projection),
                UserEntry.TABLE_NAME);

        return mDatabaseHelper.createQuery(UserEntry.TABLE_NAME, sql)
                .mapToList(mUserMapperFunction)
                .firstOrError();
    }


    public Single<User> getUserById(@NonNull String id) {
        String[] projection = {
                UserEntry.COLUMN_NAME_ENTRY_ID,
                UserEntry.COLUMN_NAME_USERNAME,
                UserEntry.COLUMN_NAME_DESCRIPTION
        };

        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?",
                TextUtils.join(",", projection),
                UserEntry.TABLE_NAME,
                UserEntry.COLUMN_NAME_ENTRY_ID);

        return mDatabaseHelper.createQuery(UserEntry.TABLE_NAME, sql, id)
                .mapToOne(mUserMapperFunction)
                .firstOrError();
    }

    /**
     * 增
     *
     * @param User
     * @return
     */
    public Completable saveUser(@NonNull User User) {
        checkNotNull(User);
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_ENTRY_ID, User.getId());
        values.put(UserEntry.COLUMN_NAME_USERNAME, User.getUsername());
        values.put(UserEntry.COLUMN_NAME_DESCRIPTION, User.getDescription());
        mDatabaseHelper.insert(UserEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        return Completable.complete();
    }

    /**
     * 改
     *
     * @param user
     * @return
     */
    public Completable updateUser(@NonNull User user) {
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_DESCRIPTION, user.getDescription());

        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {user.getId()};
        mDatabaseHelper.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
        return Completable.complete();
    }

    public void deleteAllUsers() {
        mDatabaseHelper.delete(UserEntry.TABLE_NAME, null);
    }

    /**
     * 删
     *
     * @param UserId
     * @return
     */
    public Completable deleteUser(@NonNull String UserId) {
        String selection = UserEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {UserId};
        mDatabaseHelper.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
        return Completable.complete();
    }

}
