package com.crab.es.study;

import android.content.Intent;
import android.graphics.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.crab.es.game.Sameple51Actvity;
import com.crab.es.game.Sample81Activity;
import com.crab.es.game.Sample87Activity;
import com.crab.es.game.Sample94Activity;
import com.crab.es.game.view.Sample87SurfaceView;
import com.crab.es.study.obj.ObjLoadActivity;
import com.crab.es.study.obj.ObjLoadActivity2;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView mGLView;
    private GLSurfaceView mGLView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this,MyLightActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mGLView = findViewById(R.id.gl_view);
        mGLView1 = findViewById(R.id.gl_view1);
        EsApplication.setGlobalRecource(getResources());
    }
    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        mGLView1.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        mGLView1.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
