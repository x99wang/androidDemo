package pri.wx.demo.login;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import pri.wx.demo.data.model.User;
import pri.wx.demo.mvibase.MviViewState;

@AutoValue
abstract class LoginViewState implements MviViewState {

    abstract boolean isLoading();

    @Nullable
    abstract User user();

    @Nullable
    abstract Throwable error();

    public abstract LoginViewState.Builder buildWith();

    static LoginViewState idle() {
        return new AutoValue_LoginViewState.Builder()
                .isLoading(false)
                .user(new User())
                .error(null)
                .build();
    }

    @AutoValue.Builder
    static abstract class Builder {
        abstract Builder isLoading(boolean isLoading);

        abstract Builder user(User user);

        abstract Builder error(@Nullable Throwable error);

        abstract LoginViewState build();

    }
}

