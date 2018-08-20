package pri.wx.retrofitdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.TextView;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private CompositeDisposable mDisposables;

    private TextView textGet;
    private TextView textPost;
    private Button btnGet;
    private Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textGet = findViewById(R.id.tvGet);
        textPost = findViewById(R.id.tvPost);
        btnGet = findViewById(R.id.btnGet);
        btnPost = findViewById(R.id.btnPost);

        mDisposables = new CompositeDisposable();

        bind();
    }

    /**
     * 订阅事件
     */
    private void bind() {

        // 将用户事件与数据请求关联

        mDisposables.add(
                RxView.clicks(btnGet)
                        .subscribe(action -> DataSource.getInstance()
                                .testGet()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::doGet)));

        // 两种绑定方式
        mDisposables.add(
                RxView.clicks(btnPost)
                .flatMap(action -> DataSource.getInstance()
                        .testPost()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
                .subscribe(this::doPost));
    }

    /**
     * 更新UI
     * @param data 数据源
     */
    public void doGet(String data){
        textGet.setText(Html.fromHtml(data));
    }

    public void doPost(String data) {
        textPost.setText(Html.fromHtml(data));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 取消订阅
        mDisposables.dispose();
    }
}
