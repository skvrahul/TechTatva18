package in.mittt.tt18.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.mittt.tt18.models.registration.LoginResponse;
import in.mittt.tt18.models.registration.ProfileResponse;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by anurag on 13/2/18.
 */
public class RegistrationClient {
    private static Retrofit retrofit = null;
    private static final String BASE_URL = "https://testing.mitportals.in/includes/";
    private static final String TAG = "RegistrationClient";
    public static RegistrationInterface getRegistrationInterface(final Context context){
        if (retrofit == null){
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            //Storing the Cookie when receive in Response
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                            Log.d(TAG, "saveFromResponse: cookie list size"+cookies.size());
                            for(Cookie cookie:cookies){
                                cookieStore.put(url, cookies);
                                if(cookie.name().equals("my_session")) {
                                    editor.putString("SESSION_COOKIE", cookie.value());
                                    Log.d(TAG, "saveFromResponse: Putting Session cookie "+cookie.value());
                                }
                                else if (cookie.name().equals("__cfduid")){
                                    editor.putString("cloudflare_COOKIE", cookie.value());
                                    Log.d(TAG, "saveFromResponse: Putting cloudfarecookie "+cookie.value());
                                }
                            }
                            editor.apply();
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            //Adding a cookie to the Request
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                            final ArrayList<Cookie> oneCookie = new ArrayList<>(1);
                            oneCookie.add(createCookie(sp.getString("COOKIE", "")));
                            return oneCookie;
                        }
                    })
                    .build();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client)
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit.create(RegistrationInterface.class);
    }
    public static Cookie createCookie(String cookieVal) {
        return new Cookie.Builder()
                .domain("mitportals.in")
                .path("/")
                .name("my_session")
                .value(cookieVal)
                .httpOnly()
                .secure()
                .build();
    }
    public interface RegistrationInterface {
        @Headers({"Content-Type: application/x-www-form-urlencoded"})
        @POST("login.php")
        Call<LoginResponse> attemptLogin(@Body RequestBody body);

        @GET("get_details.php")
        Call<ProfileResponse> getProfileDetails(@Header("Cookie")String cookie);

    }

}