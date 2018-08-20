package pri.wx.demo.login;

import com.google.auto.value.AutoValue;
import pri.wx.demo.mvibase.MviAction;

public interface LoginAction extends MviAction {

    @AutoValue
    abstract class CheckUser implements LoginAction {

        abstract String id();

        public static CheckUser create(String id) {
            return new AutoValue_LoginAction_CheckUser(id);
        }

    }

}
