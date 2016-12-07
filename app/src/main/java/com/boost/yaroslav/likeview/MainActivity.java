package com.boost.yaroslav.likeview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    LikesSurfaceView likesSurfaceView;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        likesSurfaceView = (LikesSurfaceView) findViewById(R.id.sv_likes);
        likesSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: ");
                if (++counter % 2 == 0){
                    likesSurfaceView.onLikeAdded(R.drawable.ic_launcher);
                } else if (counter % 3 == 0){
                    likesSurfaceView.onLikeAdded(R.drawable.home);
                } else {
                    likesSurfaceView.onLikeAdded(R.drawable.phone);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
