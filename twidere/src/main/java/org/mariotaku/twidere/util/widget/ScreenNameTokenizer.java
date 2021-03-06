/*
 *                 Twidere - Twitter client for Android
 *
 *  Copyright (C) 2012-2015 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.util.widget;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.widget.MultiAutoCompleteTextView;

/**
 * Created by mariotaku on 15/5/14.
 */
public class ScreenNameTokenizer implements MultiAutoCompleteTextView.Tokenizer {

    @Override
    public int findTokenStart(final CharSequence text, final int cursor) {
        int start = cursor;

        while (start > 0 && text.charAt(start - 1) != ' ') {
            start--;
        }

        while (start < cursor && text.charAt(start) == ' ') {
            start++;
        }

        if (start < cursor && isToken(text.charAt(start))) {
            start++;
        } else {
            start = cursor;
        }

        return start;
    }

    @Override
    public int findTokenEnd(final CharSequence text, final int cursor) {
        int i = cursor;
        final int len = text.length();

        while (i < len) {
            if (text.charAt(i) == ' ')
                return i;
            else {
                i++;
            }
        }

        return len;
    }

    @Override
    public CharSequence terminateToken(final CharSequence text) {
        int i = text.length();

        while (i > 0 && isToken(text.charAt(i - 1))) {
            i--;
        }

        if (i > 0 && text.charAt(i - 1) == ' ' || !(text instanceof Spanned)) return text;
        final SpannableString sp = new SpannableString(text);
        TextUtils.copySpansFrom((Spanned) text, 0, text.length(), Object.class, sp, 0);
        return sp;
    }

    private static boolean isToken(final char character) {
        switch (character) {
            case '\uff20':
            case '@':
            case '\uff03':
            case '#':
                return true;
        }
        return false;
    }
}
