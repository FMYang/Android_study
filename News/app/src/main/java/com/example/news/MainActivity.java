package com.example.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.function.Consumer;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    ProgressDialog progressDialog;
    ProgressBar progressBar;
    View overlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressBar = findViewById(R.id.loading);
        overlay = findViewById(R.id.overlay);
    }

    protected void requestData() {
//        progressDialog.show();
        progressBar.setVisibility(View.VISIBLE);
        overlay.setVisibility(View.VISIBLE);
        loadData(joke -> {
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            Log.d("fmlog", "success");
            if(joke!= null && joke.data != null && joke.data.message != null) {
                Log.d("fmlog", joke.data.message);
                textView.setText(joke.data.message);
            } else {
                textView.setText("null");
            }
        }, error-> {
//            progressDialog.dismiss();
            progressBar.setVisibility(View.GONE);
            overlay.setVisibility(View.GONE);
            textView.setText("failed");
            Log.d("fmlog", "failed");
        });
    }

    Joke parse(String json) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<Joke> jokeAdapter = moshi.adapter(Joke.class);
        try {
            Joke joke = jokeAdapter.fromJson(json);
            return joke;
        } catch (IOException e) {
            // 处理异常
            e.printStackTrace();
            // 或者显示错误消息，重新加载数据等
        }
        return null;
    }

    // 笑话： https://collect.xmwxxc.com/collect/joke/
    // 心灵鸡汤  https://collect.xmwxxc.com/collect/djt/?type=0
    protected void loadData(Consumer<Joke> onSuccess, Consumer<String> onFailure) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://collect.xmwxxc.com/collect/djt/?type=4")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onFailure.accept(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String res = response.body().string();
                final Joke joke = parse(res);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onSuccess.accept(joke);
                    }
                });
            }
        });
    }
}