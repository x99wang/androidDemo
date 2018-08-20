package pri.wx.demo.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import pri.wx.demo.data.model.User;
import pri.wx.demo.mvibase.MviResult;
import pri.wx.demo.util.LceStatus;

interface LoginResult extends MviResult {
    @AutoValue
    abstract class LoadLogin implements LoginResult {
        @NonNull
        abstract LceStatus status();

        @Nullable
        abstract User user();

        @Nullable
        abstract Throwable error();

        @NonNull
        static LoadLogin success(User user) {
            return new AutoValue_LoginResult_LoadLogin(LceStatus.SUCCESS,user, null);
        }

        @NonNull
        static LoadLogin failure(Throwable error) {
            return new AutoValue_LoginResult_LoadLogin(LceStatus.FAILURE,null, error);
        }

        @NonNull
        static LoadLogin inFlight() {
            return new AutoValue_LoginResult_LoadLogin(LceStatus.IN_FLIGHT, null,null);
        }
    }
}
