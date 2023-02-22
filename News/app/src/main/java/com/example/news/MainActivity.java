package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        loadData();
        parse();
    }

    void parse() {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Joke> jokeAdapter = moshi.adapter(Joke.class);
        String json = "{\"name\":\"John\",\"age\":30}";
        try {
            Joke joke = jokeAdapter.fromJson(json);
            Log.d("fmlog", joke.name + " " + joke.age);
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
            // 或者显示错误消息，重新加载数据等
        }
    }

    protected void loadData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://collect.xmwxxc.com/collect/joke/")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("fmlog", "请求成功");
                        Log.d("fmlog", res);
                        Toast.makeText(MainActivity.this, "success", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}