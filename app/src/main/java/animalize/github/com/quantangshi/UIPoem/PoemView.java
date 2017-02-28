package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import animalize.github.com.quantangshi.Data.Poem;
import animalize.github.com.quantangshi.R;


public class PoemView extends LinearLayout {
    private Poem mPoem;
    private int mChineseMode = 2;

    private TextView mTitle;
    private TextView mAuthor;
    private TextView mText;
    private ScrollView mScroller;

    public PoemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_poem, this);

        mTitle = (TextView) findViewById(R.id.poem_title);
        mAuthor = (TextView) findViewById(R.id.poem_author);
        mText = (TextView) findViewById(R.id.poem_text);
        mScroller = (ScrollView) findViewById(R.id.poem_scroller);
    }

    public void setPoem(Poem poem) {
        mPoem = poem;
        mPoem.setMode(mChineseMode);
        refreshPoem(true);
    }

    public void setMode(int mode) {
        mChineseMode = mode;
        if (mPoem != null) {
            mPoem.setMode(mode);
            refreshPoem(false);
        }
    }

    private void refreshPoem(boolean toTop) {
        mTitle.setText(mPoem.getTitle());
        mAuthor.setText(mPoem.getAuthor());

        if (mChineseMode == 0) {
            mText.setText(mPoem.getText());
        } else if (mChineseMode == 1) {
            mText.setText(mPoem.getText());
        } else {
            ArrayList<Poem.CodepointPosition> lst = mPoem.getPosiText();
            SpannableString ss = new SpannableString(mPoem.getText());
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
