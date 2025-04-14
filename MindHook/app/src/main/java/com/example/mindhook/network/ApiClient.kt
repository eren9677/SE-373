import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.mindhook.network.MindHookApi

object ApiClient {
    private const val BASE_URL = "http://192.168.1.12:5001"

    val instance: MindHookApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MindHookApi::class.java)
    }
}
