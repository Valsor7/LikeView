package com.boost.yaroslav.likeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.boost.yaroslav.likeview.animation.likes_view.LikesSurfaceView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    int[] images = new int[]{
            R.drawable.a,
            R.drawable.b,
            R.drawable.c
    };
    private static final String TAG = "MainActivity";
    LikesSurfaceView likesSurfaceView;
    Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        likesSurfaceView = (LikesSurfaceView) findViewById(R.id.sv_likes);
        likesSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                likesSurfaceView.onLikeAdded(images[random.nextInt(3)]);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
