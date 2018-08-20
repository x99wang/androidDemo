package pri.wx.retrofitdemo.api;

import okhttp3.ResponseBody;
import retrofit2.Converter;

import java.io.IOException;

public class StringConverter implements Converter<ResponseBody,String> {
    public static final StringConverter INSTANCE = new StringConverter();

    @Override
    public String convert(ResponseBody responseBody) throws IOException {
        return responseBody.string();
    }
}
