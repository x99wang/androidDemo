package pri.wx.demo.login;

import android.support.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import pri.wx.demo.data.source.DataSource;
import pri.wx.demo.mvibase.MviAction;
import pri.wx.demo.mvibase.MviResult;
import pri.wx.demo.util.schedulers.BaseSchedulerProvider;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoginActionProcessorHolder {
    public static final String TAG = "LoginActionProcessor";

    @NonNull
    private DataSource mRepository;

    @NonNull
    private BaseSchedulerProvider mSchedulerProvider;

    public LoginActionProcessorHolder(@NonNull DataSource repository,
                                      @NonNull BaseSchedulerProvider schedulerProvider) {
        this.mRepository = checkNotNull(repository, "Repository cannot be null");
        this.mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

    }

    /**
     * 业务逻辑 -> 数据请求
     * 这里进行数据请求
     * 返回请求结果
     * result
     */
    private ObservableTransformer<LoginAction.CheckUser, LoginResult.LoadLogin>
            checkUserProcessor = actions ->
            actions.flatMap(action ->
                    mRepository.getUser(action.id())
                            // Transform the Single to an Observable to allow emission of multiple
                            // events down the stream (e.g. the InFlight event)
                            .toObservable()
                            .doOnNext(log -> Log.i(TAG,"生成结果"+log.getId()))
                            .map(LoginResult.LoadLogin::success)
                            // Wrap any error into an immutable object and pass it down the stream
                            // without crashing.
                            // Because errors are data and hence, should just be part of the stream.
                            .onErrorReturn(LoginResult.LoadLogin::failure)
                            .subscribeOn(mSchedulerProvider.io())
                            .observeOn(mSchedulerProvider.ui())
                            // Emit an InFlight event to notify the subscribers (e.g. the UI) we are
                            // doing work and waiting on a response.
                            // We emit it after observing on the UI thread to allow the event to be emitted
                            // on the current frame and avoid jank.
                            .startWith(LoginResult.LoadLogin.inFlight()));

    /**
     * Splits the {@link Observable<MviAction>} to match each type of {@link MviAction} to
     * its corresponding business logic processor. Each processor takes a defined {@link MviAction},
     * returns a defined {@link MviResult}
     * The global actionProcessor then merges all {@link Observable<MviResult>} back to
     * one unique {@link Observable<MviResult>}.
     * <p>
     * The splitting is done using {@link Observable#publish(Function)} which allows almost anything
     * on the passed {@link Observable} as long as one and only one {@link Observable} is returned.
     * <p>
     * An security layer is also added for unhandled {@link MviAction} to allow early crash
     * at runtime to easy the maintenance.
     *
     * 上层交互 -> 业务逻辑
     * 将 Action 转为 Result
     */
    ObservableTransformer<LoginAction, LoginResult> actionProcessor =
            actions -> actions.publish(shared ->
                    // Match LoadLogin to loadLoginProcessor
                    shared.ofType(LoginAction.CheckUser.class)
                            .doOnNext(action -> Log.i(TAG,"动作过程"))
                            .compose(checkUserProcessor)
                            .cast(LoginResult.class)
                            .doOnNext(action -> Log.i(TAG,"过滤结果类型"))
                            .mergeWith(
                                    // Error for not implemented actions
                                    shared.filter(v -> !(v instanceof LoginAction.CheckUser))
                                            .flatMap(w -> Observable.error(
                                            new IllegalArgumentException("Unknown Action type: " + w)))));

}
