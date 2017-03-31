package animalize.github.com.quantangshi.T2sMap;


import android.content.Context;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import animalize.github.com.quantangshi.Data.PoemWrapper;
import animalize.github.com.quantangshi.MyApplication;

public class T2s {
    private static final String TAG = "T2s";

    private static T2s mT2s;
    private static SparseIntArray map;
    private static SparseBooleanArray set;

    private T2s(Context context) {
        String s = getFromAssets(context, "map.json");
        if ("".equals(s))
            return;

        try {
            JSONArray three = new JSONArray(s);
            JSONArray jk = three.getJSONArray(0);
            JSONArray jv = three.getJSONArray(1);
            JSONArray jset = three.getJSONArray(2);

            // 繁->简
            map = new SparseIntArray();
            for (int i = 0; i < jk.length(); i++) {
                map.append(jk.getInt(i), jv.getInt(i));
            }

            // 多繁对一简
            set = new SparseBooleanArray();
            for (int i = 0; i < jset.length(); i++) {
                Integer codepoint = jset.getInt(i);
                set.append(codepoint, true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int[] getList(JSONArray ja) {
        int[] ret = new int[ja.length()];

        for (int i = 0; i < ja.length(); i++) {
            try {
                ret[i] = ja.getInt(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /*
     s: 字符串
     lst: null表示繁->简，否则是繁->简+
    * */
    public static String t2s(String s,
                             ArrayList<PoemWrapper.CodepointPosition> lst) {
        getT2s();

        StringBuilder sb = new StringBuilder();
        int codepoint;

        for (int i = 0; i < s.length(); i++) {
            final int temp_i = i;

            // 得到codepoint
            final char c = s.charAt(temp_i);

            if (Character.isHighSurrogate(c)) {
                // 是surrogates
                if (temp_i + 1 < s.length() &&
                        Character.isLowSurrogate(s.charAt(temp_i + 1))) {
                    codepoint = Character.codePointAt(s, temp_i);
                    i += 1;
                } else {
                    // 缺失LowSurrogate，转换为一个方块
                    codepoint = 0x25A1;
                }
            } else if (Character.isLowSurrogate(c)) {
                // 残缺Surrogate，转换为一个方块
                codepoint = 0x25A1;
            } else {
                // 非surrogates
                codepoint = c;
            }

            // 转换
            final Integer temp_codepoint = map.get(codepoint, -1);
            if (temp_codepoint != -1) {
                // 可转换
                if (lst != null) {
                    // 简体+ 模式
                    if (!set.get(temp_codepoint, false)) {
                        // 不在集合，直接采用结果
                        codepoint = temp_codepoint;
                    } else if (codepoint != temp_codepoint) {
                        // 在集合，并且不是简->简，而是繁->简
                        PoemWrapper.CodepointPosition p = new PoemWrapper.CodepointPosition(temp_i,
                                temp_i + (codepoint > 0xffff ? 2 : 1),
                                temp_codepoint);
                        lst.add(p);
                    }
                    // else 在集合，是简->简，则codepoint = codepoint
                } else {
                    // 简体 模式
                    codepoint = temp_codepoint;
                }
            }

            sb.appendCodePoint(codepoint);
        }

        return sb.toString();
    }

    private static void getT2s() {
        if (mT2s == null) {
            mT2s = new T2s(MyApplication.getContext());
        }
    }

    private static String getFromAssets(Context context, String fileName) {
        try {
            InputStream is = context.getResources().getAssets().open(fileName);
            InputStreamReader inputReader = new InputStreamReader(is);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line;
            String Result = "";

            while ((line = bufReader.readLine()) != null)
                Result += line;

            bufReader.close();

            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
