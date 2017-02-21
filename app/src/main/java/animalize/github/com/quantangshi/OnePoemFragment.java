package animalize.github.com.quantangshi;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;

public class OnePoemFragment extends Fragment {
    private static final String ARG_ID = "id";
    private static final String TAG = "OnePoemFragment";

    private Poem mP;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mText;
    private ScrollView mScroller;

    private Button mTButton;
    private Button mSButton;
    private Button mSpButton;

    public OnePoemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_one_poem, container, false);

        // title
        mTitle = (TextView) v.findViewById(R.id.poem_title);

        // author
        mAuthor = (TextView) v.findViewById(R.id.poem_author);

        // text
        mText = (TextView) v.findViewById(R.id.poem_text);

        // scroller
        mScroller = (ScrollView) v.findViewById(R.id.poem_scroller);

        mTButton = (Button) v.findViewById(R.id.button_t);
        mTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mP.setMode(0);
                refreshPoem(false);
            }
        });
        mSButton = (Button) v.findViewById(R.id.button_s);
        mSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mP.setMode(1);
                refreshPoem(false);
            }
        });
        mSpButton = (Button) v.findViewById(R.id.button_sp);
        mSpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mP.setMode(2);
                refreshPoem(false);
            }
        });

        // 下一首随机诗
        Button b = (Button) v.findViewById(R.id.next_random);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPoem();
            }
        });

        randomPoem();
        // 显示
        refreshPoem(true);

        return v;
    }

    // 随机一首诗
    private void randomPoem() {
        int poemCount = MyDatabaseHelper.getPoemCount();
        int id = new Random().nextInt(poemCount - 1) + 1;
        mP = MyDatabaseHelper.getPoemById(id);
        mP.setMode(2);
        refreshPoem(true);
    }

    private void refreshPoem(boolean toTop) {
        mTitle.setText(mP.getTitle());
        mAuthor.setText(mP.getAuthor());

        int mode = mP.getMode();
        if (mode == 0) {
            mTButton.setTextColor(Color.BLUE);
            mSButton.setTextColor(Color.BLACK);
            mSpButton.setTextColor(Color.BLACK);
            mText.setText(mP.getText());
        } else if (mode == 1) {
            mTButton.setTextColor(Color.BLACK);
            mSButton.setTextColor(Color.BLUE);
            mSpButton.setTextColor(Color.BLACK);
            mText.setText(mP.getText());
        } else {
            mTButton.setTextColor(Color.BLACK);
            mSButton.setTextColor(Color.BLACK);
            mSpButton.setTextColor(Color.BLUE);

            ArrayList<Poem.CodepointPosition> lst = mP.getPosiText();
            SpannableString ss = new SpannableString(mP.getText());
            for (final Poem.CodepointPosition p : lst) {
                ss.setSpan(new ClickableSpan() {
                               @Override
                               public void updateDrawState(TextPaint ds) {
                                   super.updateDrawState(ds);
                                   ds.setColor(Color.rgb(0x33, 0x33, 0x99));
                                   ds.setUnderlineText(false);
                               }

                               @Override
                               public void onClick(View widget) {
                                   String s = String.valueOf(Character.toChars(p.s_codepoint));
                                   Toast t = Toast.makeText(getContext(), s, Toast.LENGTH_SHORT);
                                   // 字体
                                   ViewGroup group = (ViewGroup) t.getView();
                                   TextView messageTextView = (TextView) group.getChildAt(0);
                                   messageTextView.setTextSize(40);
                                   // 居中
                                   t.setGravity(Gravity.CENTER, 0, 0);
                                   // 显示
                                   t.show();
                               }
                           },
                        p.begin, p.end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mText.setMovementMethod(LinkMovementMethod.getInstance());
            mText.setText(ss);
        }

        if (toTop) {
            mScroller.scrollTo(0, 0);
        }

    }
}
