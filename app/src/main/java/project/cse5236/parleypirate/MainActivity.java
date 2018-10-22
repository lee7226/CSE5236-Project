package project.cse5236.parleypirate;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toast onCreateToast = Toast.makeText(MainActivity.this,R.string.onCreateToast,
                Toast.LENGTH_LONG);
        onCreateToast.show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onStart() {
        Toast onStartToast = Toast.makeText(MainActivity.this,R.string.onStartToast,
                Toast.LENGTH_LONG);
        onStartToast.show();
        super.onStart();
    }
    @Override
    public void onResume() {
        Toast onResumeToast = Toast.makeText(MainActivity.this,R.string.onResumeToast,
                Toast.LENGTH_LONG);
        onResumeToast.show();
        super.onResume();
    }
    @Override
    public void onPause() {
        Toast onPauseToast = Toast.makeText(MainActivity.this,R.string.onPauseToast,
                Toast.LENGTH_LONG);
        onPauseToast.show();
        super.onPause();
    }
    @Override
    public void onStop() {
        Toast onStopToast = Toast.makeText(MainActivity.this,R.string.onStopToast,
                Toast.LENGTH_LONG);
        onStopToast.show();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Toast onDestroyToast = Toast.makeText(MainActivity.this,R.string.onDestroyToast,
                Toast.LENGTH_LONG);
        onDestroyToast.show();
        super.onDestroy();
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
