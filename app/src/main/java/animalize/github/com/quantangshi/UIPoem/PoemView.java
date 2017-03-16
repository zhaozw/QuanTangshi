package animalize.github.com.quantangshi.UIPoem;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import animalize.github.com.quantangshi.Data.InfoItem;
import animalize.github.com.quantangshi.Data.PoemWrapper;
import animalize.github.com.quantangshi.Data.RawPoem;
import animalize.github.com.quantangshi.Data.Typeset;
import animalize.github.com.quantangshi.R;


public class PoemView extends LinearLayout {
    private PoemWrapper mPoemWrapper;
    private Typeset mTypeset = Typeset.getInstance();

    private int mChineseMode = 2;

    private TextView mId;
    private TextView mTitle;
    private TextView mAuthor;
    private TextView mText;
    private ScrollView mScroller;

    public PoemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_poem, this);

        mId = (TextView) findViewById(R.id.poem_id);
        mTitle = (TextView) findViewById(R.id.poem_title);
        mAuthor = (TextView) findViewById(R.id.poem_author);
        mText = (TextView) findViewById(R.id.poem_text);
        mScroller = (ScrollView) findViewById(R.id.poem_scroller);
    }

    public void setPoem(RawPoem poem) {
        boolean first = mPoemWrapper == null;
        mPoemWrapper = new PoemWrapper(poem, mTypeset.getLineBreak());

        if (first) {
            updateTypeset();
        }
        refreshPoem(true);
    }

    public void setMode(int mode) {
        mChineseMode = mode;
        if (mPoemWrapper != null) {
            refreshPoem(false);
        }
    }

    public Typeset getTypeset() {
        return mTypeset;
    }

    public InfoItem getInfoItem() {
        InfoItem item = new InfoItem(
                mPoemWrapper.getID(),
                mPoemWrapper.getTitle(mChineseMode),
                mPoemWrapper.getAuthor(mChineseMode));

        return item;
    }

    public void updateTypeset() {
        mPoemWrapper.setLineBreak(mTypeset.getLineBreak());

        mTitle.setMaxLines(mTypeset.getTitleLines());
        mTitle.setTextSize(mTypeset.getTitleSize());

        int temp = (int) (mTypeset.getTitleSize() * 0.618);
        mId.setTextSize(temp);
        mAuthor.setTextSize(temp);

        mText.setTextSize(mTypeset.getTextSize());
        mText.setLineSpacing(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                mTypeset.getLineSpace(),
                getResources().getDisplayMetrics()),
                1.0f);
        mText.setText(mPoemWrapper.getText(mChineseMode));
    }

    private void refreshPoem(boolean toTop) {
        mTitle.setText(mPoemWrapper.getTitle(mChineseMode));

        mId.setText("" + mPoemWrapper.getID());
        mAuthor.setText(mPoemWrapper.getAuthor(mChineseMode));

        if (mChineseMode == 0 || mChineseMode == 1) {
            mText.setText(mPoemWrapper.getText(mChineseMode));
        } else {
            ArrayList<PoemWrapper.CodepointPosition> lst = mPoemWrapper.getCodeList();
            SpannableString ss = new SpannableString(mPoemWrapper.getText(mChineseMode));
            for (final PoemWrapper.CodepointPosition p : lst) {
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
