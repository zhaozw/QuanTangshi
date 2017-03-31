package animalize.github.com.quantangshi.T2sMap;


import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

import java.util.ArrayList;

import animalize.github.com.quantangshi.Data.PoemWrapper;

public class T2s {
    private static final String TAG = "T2s";

    private static T2s mT2s;
    private static SparseIntArray map;
    private static SparseBooleanArray set;

    private T2s() {
        // 繁->简
        map = new SparseIntArray();
        for (int i = 0; i < T2SData.key.length; i++) {
            map.append(T2SData.key[i], T2SData.value[i]);
        }

        // 多繁对一简
        set = new SparseBooleanArray();
        for (int i = 0; i < T2SData.multi_s.length; i++) {
            set.append(T2SData.multi_s[i], true);
        }
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
            mT2s = new T2s();
        }
    }

}
