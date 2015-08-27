package com.ibm.project_traffic.Packages.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

/**
 * Created by larryasante on 8/12/15.
 */
public class TrafficaEditText extends EditText {

    public TrafficaEditText(Context context) {
        super(context);
    }

    public TrafficaEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrafficaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;    }
}
