package pri.wx.demo.login;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;

import pri.wx.demo.mvibase.MviAction;
import pri.wx.demo.mvibase.MviIntent;
import pri.wx.demo.mvibase.MviResult;
import pri.wx.demo.mvibase.MviView;
import pri.wx.demo.mvibase.MviViewModel;
import pri.wx.demo.mvibase.MviViewState;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoginViewModel extends ViewModel implements MviViewModel<LoginIntent,LoginViewState> {

    public static final String TAG = "LoginViewModel";

    @NonNull
    private PublishSubject<LoginIntent> mIntentsSubject;
    @NonNull
    private Observable<LoginViewState> mStatesObservable;
    @NonNull
    private LoginActionProcessorHolder mActionProcessorHolder;

    public LoginViewModel(@NonNull LoginActionProcessorHolder actionProcessorHolder) {
        this.mActionProcessorHolder = checkNotNull(actionProcessorHolder, "actionProcessorHolder cannot be null");

        mIntentsSubject = PublishSubject.create();
        mStatesObservable = compose();
    }

    @Override
    public void processIntents(Observable<LoginIntent> intents) {
        intents.doOnNext(action -> Log.i(TAG,"用户交互 绑定"))
                .subscribe(mIntentsSubject);
    }

    @Override
    public Observable<LoginViewState> states() {
        return mStatesObservable;
    }

    /**
     * Compose all components to create the stream logic
     */
    private Observable<LoginViewState> compose() {
        return mIntentsSubject
                .doOnNext(action -> Log.i(TAG,"过滤交互"))
                .compose(intentFilter)
                .doOnNext(action -> Log.i(TAG,"交互 -> 业务逻辑"))
                .map(this::actionFromIntent)
                .doOnNext(action -> Log.i(TAG,"开始动作"))
                .compose(mActionProcessorHolder.actionProcessor)
                // Cache each state and pass it to the reducer to create a new state from
                // the previous cached one and the latest Result emitted from the action processor.
                // The Scan operator is used here for the caching.
                // 缓存每个状态并将其传递给还原器，以便从上一个缓存状态和动作处理器发出的最新结果中创建一个新状态。
                // 这里使用Scan操作符进行缓存。
                .scan(LoginViewState.idle(), reducer)
                // When a reducer just emits previousState, there's no reason to call render. In fact,
                // redrawing the UI in cases like this can cause jank (e.g. messing up snackbar animations
                // by showing the same snackbar twice in rapid succession).
                // 判断这个数据项跟前一个数据项是否相同
                //.distinctUntilChanged()
                // Emit the last one event of the stream on subscription.
                // Useful when a View rebinds to the ViewModel after rotation.
                .replay(1)
                // Create the stream on creation without waiting for anyone to subscribe
                // This allows the stream to stay alive even when the UI disconnects and
                // match the stream's lifecycle to the ViewModel's one.
                .autoConnect(0);
    }

    /**
     * take only the first ever InitialIntent and all intents of other types
     * to avoid reloading data on config changes
     *
     * intent 过滤
     * 只接受第一个InitialIntent和其他类型的所有intents
     * 以避免在配置上重新加载数据
     */
    private ObservableTransformer<LoginIntent, LoginIntent> intentFilter =
            intents -> intents.publish(shared ->
                    Observable.merge(
                            shared.ofType(LoginIntent.CheckIntent.class),//.take(1),
                            shared.filter(intent -> !(intent instanceof LoginIntent.CheckIntent))
                    )
            );

    /**
     * Translate an {@link MviIntent} to an {@link MviAction}.
     * Used to decouple the UI and the business logic to allow easy testings and reusability.
     *
     * 将 Intent 转化为 Action
     * 将UI和业务逻辑解耦以便于测试和复用
     */
    private LoginAction actionFromIntent(MviIntent intent) {
        if (intent instanceof LoginIntent.CheckIntent) {
            String id = ((LoginIntent.CheckIntent) intent).id();
            if (id == null) {

            }
            return LoginAction.CheckUser.create(((LoginIntent.CheckIntent) intent).id());
        }
        throw new IllegalArgumentException("do not know how to treat this intent " + intent);
    }

    /**
     * The Reducer is where {@link MviViewState}, that the {@link MviView} will use to
     * render itself, are created.
     * It takes the last cached {@link MviViewState}, the latest {@link MviResult} and
     * creates a new {@link MviViewState} by only updating the related fields.
     * This is basically like a big switch statement of all possible types for the {@link MviResult}
     *
     * 将 Result 转为 ViewState
     */
    private static BiFunction<LoginViewState, LoginResult, LoginViewState> reducer =
            (previousState, result) -> {
                // 新建于上一个(缓存)状态
                LoginViewState.Builder stateBuilder = previousState.buildWith();
                // Builder 初始化
                stateBuilder.error(null).user(null);
                if (result instanceof LoginResult.LoadLogin) {
                    LoginResult.LoadLogin loadResult = (LoginResult.LoadLogin) result;
                    switch (loadResult.status()) {
                        case SUCCESS:
                            return stateBuilder.isLoading(false).user(loadResult.user()).build();
                        case FAILURE:
                            return stateBuilder.isLoading(false).error(loadResult.error()).build();
                        case IN_FLIGHT:
                            return stateBuilder.isLoading(true).build();
                    }
                } else {
                    throw new IllegalArgumentException("Don't know this result " + result);
                }
                // Fail for unhandled results
                throw new IllegalStateException("Mishandled result? Should not happen (as always)");
            };
}
