package pri.wx.demo.login;

import android.support.annotation.Nullable;
import com.google.auto.value.AutoValue;
import pri.wx.demo.mvibase.MviIntent;

public interface LoginIntent extends MviIntent {
    @AutoValue
    abstract class CheckIntent implements LoginIntent {
        @Nullable
        abstract String id();

        public static CheckIntent create(@Nullable String id) {
            return new AutoValue_LoginIntent_CheckIntent(id);
        }
    }
}
