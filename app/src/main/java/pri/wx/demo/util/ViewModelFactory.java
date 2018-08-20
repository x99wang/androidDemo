package pri.wx.demo.util;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import pri.wx.demo.data.source.TestRepository;
import pri.wx.demo.login.LoginActionProcessorHolder;
import pri.wx.demo.login.LoginViewModel;
import pri.wx.demo.util.schedulers.SchedulerProvider;

public class ViewModelFactory implements ViewModelProvider.Factory {
    @SuppressLint("StaticFieldLeak")
    private static ViewModelFactory INSTANCE;

    private final Context applicationContext;

    private ViewModelFactory(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static ViewModelFactory getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new ViewModelFactory(context.getApplicationContext());
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass == LoginViewModel.class) {
            return (T) new LoginViewModel(
                    new LoginActionProcessorHolder(
                            TestRepository.getInstance(),
                            SchedulerProvider.getInstance()));
        }
        throw new IllegalArgumentException("unknown model class " + modelClass);
    }
}
