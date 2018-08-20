package pri.wx.demo.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import pri.wx.demo.R;
import pri.wx.demo.data.source.TestRepository;
import pri.wx.demo.mvibase.MviView;
import pri.wx.demo.util.schedulers.SchedulerProvider;

public class LoginActivity extends AppCompatActivity implements MviView<LoginIntent,LoginViewState> {

    // Used to manage the data flow lifecycle and avoid memory leak.
    private CompositeDisposable mDisposables = new CompositeDisposable();

    private LoginViewModel mViewModel;

    private EditText etId;

    private Button btnLogin;

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mViewModel = new LoginViewModel(new LoginActionProcessorHolder(
                TestRepository.getInstance(), SchedulerProvider.getInstance()));

        etId = findViewById(R.id.et_id);
        btnLogin = findViewById(R.id.btn_login);
        tvResult = findViewById(R.id.tv_result);

        bind();
    }

    private void bind() {

        // 业务逻辑结果 -> UI适配
        mDisposables.add(mViewModel.states().subscribe(this::render));

        // 交互动作 -> 业务逻辑
        mViewModel.processIntents(intents());
    }


    @Override
    public Observable<LoginIntent> intents() {
        return loginIntent();
    }

    private Observable<LoginIntent> loginIntent() {

        return RxView.clicks(btnLogin).map(
                obj -> etId.getText().toString())
                .filter(str -> !str.isEmpty())
                .map(LoginIntent.CheckIntent::create);

    }

    @Override
    public void render(LoginViewState state) {
        Log.i("Main","UI适配");

        if (state.isLoading()) {
            Toast.makeText(this,
                    getString(R.string.loading),
                    Toast.LENGTH_SHORT)
                    .show();
        }
        if (state.error() != null) {
            tvResult.setText(state.error().getMessage());
        }
        if (state.error() == null && !state.isLoading()) {
            tvResult.setText(state.user().getUsername());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposables.dispose();
    }
}
