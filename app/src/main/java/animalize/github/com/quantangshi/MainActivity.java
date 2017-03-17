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

        // 打开诗 ------------------------------
        mPoemCount = MyDatabaseHelper.getPoemCount();
        Button bt = (Button) findViewById(R.id.main_test);
        bt.setText("共" + mPoemCount + "首诗");
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnePoemActivity.actionStart(MainActivity.this);
            }
        });

        // 跳转指定ID ------------------------------
        idEdit = (EditText) findViewById(R.id.jump_edit);
        bt = (Button) findViewById(R.id.jump_button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = Integer.parseInt(idEdit.getText().toString());
                if (1 <= id && id <= mPoemCount && mPoemCount != -1) {
                    OnePoemActivity.actionStart(MainActivity.this, id);
                }
            }
        });

        // 清空ID
        bt = (Button) findViewById(R.id.jump_clear);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idEdit.setText("");
            }
        });

        // 标签管理 -------------------------
        bt = (Button) findViewById(R.id.main_opentag);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TagActivity.actionStart(MainActivity.this);
            }
        });

        // 设置 ------------------------------
        bt = (Button) findViewById(R.id.main_option);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionActivity.actionStart(MainActivity.this);
            }
        });
    }

}
