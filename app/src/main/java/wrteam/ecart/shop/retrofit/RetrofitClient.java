package wrteam.ecart.shop.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL = "https://vegurban.com/api-firebase/";
    //    public static final String BASE_URL = "https://bodyrecomp.app/app/project/bodyrecomp_clone/api/";
    public static final String IMAGE_URL = "https://vegurban.com/api-firebase/";

    private static RetrofitClient client;
    private Retrofit retrofit;

    private RetrofitClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(/*gson*/))
                .client(client)             //interceptor
                .build();

    }

    public static synchronized RetrofitClient getInstance() {
        if (client == null) {
            client = new RetrofitClient();
        }
        return client;
    }

    public RetrofitInterface getApi() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().setLenient().create()))
                .client(client)             //interceptor
                .build();

        return retrofit.create(RetrofitInterface.class);

    }

    // This method  converts String to RequestBody
    public static RequestBody to_request_body(String value) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), value);
    }

    public static MultipartBody.Part image_to_part(String name, File file) {
//        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        return MultipartBody.Part.createFormData(name, file.getName(), requestFile);
    }

}