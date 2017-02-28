package animalize.github.com.quantangshi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import java.util.Random;

import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.UIPoem.OnePoemActivity;


public class MainActivity extends AppCompatActivity {
    private Random mRand = new Random();
    private int mPoemCount = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // toolbar
        Toolbar tb = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(tb);


        mPoemCount = MyDatabaseHelper.getPoemCount();

        Button bt = (Button) findViewById(R.id.main_test);
        bt.setText("共" + mPoemCount + "首诗");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnePoemActivity.actionStart(MainActivity.this);
            }
        });

        bt = (Button) findViewById(R.id.main_tag);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagActivity.actionStart(MainActivity.this);
            }
        });
    }

}
