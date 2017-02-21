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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.Database.MyDatabaseHelper;

public class OnePoemFragment extends Fragment {
    private static final String TAG = "OnePoemFragment";

    private Poem mP;
    private int mMode = 2;

    private TextView mTitle;
    private TextView mAuthor;
    private TextView mText;
    private ScrollView mScroller;

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

        return v;
    }

    // 随机一首诗
    public void randomPoem() {
        int poemCount = MyDatabaseHelper.getPoemCount();
        int id = new Random().nextInt(poemCount - 1) + 1;
        mP = MyDatabaseHelper.getPoemById(id);
        mP.setMode(mMode);
        refreshPoem(true);
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        mMode = mode;
        if (mP != null) {
            mP.setMode(mode);
        }

        refreshPoem(false);
    }

    private void refreshPoem(boolean toTop) {
        mTitle.setText(mP.getTitle());
        mAuthor.setText(mP.getAuthor());

        if (mMode == 0) {
            mText.setText(mP.getText());
        } else if (mMode == 1) {
            mText.setText(mP.getText());
        } else {
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
