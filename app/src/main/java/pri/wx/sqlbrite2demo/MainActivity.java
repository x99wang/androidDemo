package pri.wx.sqlbrite2demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable mDisposables;

    private DataSource source;

    private TextView textAction;
    private Button btnSave;
    private Button btnDelete;
    private Button btnDeleteAll;
    private Button btnUpdate;
    private Button btnGet;
    private Button btnGetAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textAction = findViewById(R.id.tvAction);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnGet = findViewById(R.id.btnGet);
        btnGetAll = findViewById(R.id.btnGetAll);

        mDisposables = new CompositeDisposable();

        source = DataSource.getInstance(getApplicationContext());

        bind();
    }

    /**
     * 按钮事件与数据请求绑定
     */
    private void bind() {

        mDisposables.add(RxView.clicks(btnGet)
                .subscribe(action -> bindGet()));

        // 变换的形式
        mDisposables.add(RxView.clicks(btnGetAll)
                .flatMap(action -> getAll())
                .subscribe(this::doDbAction));

        mDisposables.add(RxView.clicks(btnSave)
                .subscribe(action -> bindSave()));

        mDisposables.add(RxView.clicks(btnDelete)
                .subscribe(action -> bindDelete()));

        //这里deleteUsers方法并没有返回值所以用doOnNext的形式来写
        mDisposables.add(RxView.clicks(btnDeleteAll)
                .observeOn(Schedulers.io())
                .doOnNext(action -> source.deleteUsers())
                .observeOn(AndroidSchedulers.mainThread())
                .map(str -> "DeleteAll: Success")
                .subscribe(this::doDbAction));

        mDisposables.add(RxView.clicks(btnUpdate)
                .subscribe(action -> bindUpdate()));
    }

    /**
     * 数据请求与UI更新绑定
     */
    private void bindGet() {
        mDisposables.add(source
                .getUser()
                .map(str -> "Select:" + str)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::doDbAction,
                        e -> doToast("Database is empty")
                ));
    }

    /**
     * 或者做一个变换
     */
    private Observable<String> getAll() {
        return source.getUsers()
                .map(str -> "Select All:" + str)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable();
    }

    private void bindSave() {
        mDisposables.add(source
                .saveUser()
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Observable.just("Success"))
                .map(str -> "Save:" + str)
                .subscribe(
                        this::doDbAction,
                        e -> doToast("Enough examples")
                ));
    }

    private void bindDelete() {
        mDisposables.add(source
                .deleteUser()
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Observable.just("Success"))
                .map(str -> "Delete:" + str)
                .subscribe(
                        this::doDbAction,
                        e -> doToast("Database is empty")
                ));
    }

    private void bindUpdate() {
        mDisposables.add(source
                .updateUser()
                .andThen(Observable.just("Success"))
                .map(str -> "Update:" + str)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::doDbAction,
                        e -> doToast("Database is empty")
                ));
    }

    /**
     * 更新UI
     * @param msg
     */
    public void doDbAction(String msg){
        textAction.setText(msg);
    }

    public void doToast(String msg) {

        Toast.makeText(
                getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT).show();

    }

    public void doOnError(Throwable e) {
        Log.e("Error Thread", Thread.currentThread().getName());

        Observable.just(e.getMessage())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::doToast);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消订阅
        mDisposables.dispose();
    }
}
