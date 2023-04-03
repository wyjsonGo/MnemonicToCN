package com.wyjson.mnemonic_to_cn;

import android.text.SpannableString;
import android.text.Spanned;
import android.widget.MultiAutoCompleteTextView;

public class SuperTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    private final char tokenChar;
    private final boolean isOnlyToken;

    public SuperTokenizer(char tokenChar, boolean isOnlyToken) {
        this.tokenChar = tokenChar;
        this.isOnlyToken = isOnlyToken;
    }

    @Override
    public int findTokenStart(CharSequence text, int cursor) {
        int i = cursor;
        while (i > 0 && text.charAt(i - 1) != tokenChar) {
            i--;
        }
        while (i < cursor && text.charAt(i) == ' ') {
            i++;
        }
        return i;
    }

    @Override
    public int findTokenEnd(CharSequence text, int cursor) {
        int i = cursor;
        int len = text.length();
        while (i < len) {
            if (text.charAt(i) == tokenChar) {
                return i;
            } else {
                i++;
            }
        }
        return len;
    }

    @Override
    public CharSequence terminateToken(CharSequence text) {
        int i = text.length();
        while (i > 0 && text.charAt(i - 1) == ' ') {
            i--;
        }
        if (i > 0 && text.charAt(i - 1) == tokenChar) {
            return text;
        } else {
            String result = text + String.valueOf(tokenChar);
            if (!isOnlyToken)
                result += " ";
            if (text instanceof Spanned) {
                return new SpannableString(result);
            } else {
                return result;
            }
        }
    }
}