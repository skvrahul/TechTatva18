package in.mittt.tt18.network;

import android.content.Context;


import in.mittt.tt18.models.registration.LoginResponse;
import in.mittt.tt18.network.ApplyCookieInterceptor;
import in.mittt.tt18.network.CookieInterceptor;
import okhttp3.Cookie;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by anurag on 13/2/18.
 */
public class RegistrationClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://testing.mitportals.in/includes/";

    public static RegistrationInterface getRegistrationInterface(Context context){
        if (retrofit == null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new CookieInterceptor(context))
                    .addInterceptor(new ApplyCookieInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(RegistrationInterface.class);
    }

    public interface RegistrationInterface {
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("login.php")
        Call<LoginResponse> attemptLogin(@Body RequestBody body);


    }

}