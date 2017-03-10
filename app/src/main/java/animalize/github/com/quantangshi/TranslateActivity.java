package animalize.github.com.quantangshi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;


public class TranslateActivity extends AppCompatActivity {

    private TextView title;
    private TextView author;
    private TextView text;

    public static void actionStart(Context context, int id) {
        Intent i = new Intent(context, TranslateActivity.class);
        i.putExtra("id", id);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 1);
        RawPoem poem = MyDatabaseHelper.getPoemById(id);

        title = (TextView) findViewById(R.id.poem_title);
        title.setText(poem.getTitle());

        author = (TextView) findViewById(R.id.poem_author);
        author.setText(poem.getAuthor());

        text = (TextView) findViewById(R.id.poem_text);
        text.setText(poem.getText());

    }
}
