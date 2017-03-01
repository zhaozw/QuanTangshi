package animalize.github.com.quantangshi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import animalize.github.com.quantangshi.Database.MyDatabaseHelper;
import animalize.github.com.quantangshi.UIPoem.OnePoemActivity;


public class MainActivity extends AppCompatActivity {
    private int mPoemCount = -1;

    private EditText idEdit;

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

        idEdit = (EditText) findViewById(R.id.jump_edit);
        bt = (Button) findViewById(R.id.jump_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int id = Integer.parseInt(idEdit.getText().toString());
                    if (1 <= id && id <= mPoemCount && mPoemCount != -1) {
                        OnePoemActivity.actionStart(MainActivity.this, id);
                    }
                } catch (NumberFormatException e) {
                }
            }
        });

        bt = (Button) findViewById(R.id.jump_clear);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idEdit.setText("");
            }
        });
    }

}
