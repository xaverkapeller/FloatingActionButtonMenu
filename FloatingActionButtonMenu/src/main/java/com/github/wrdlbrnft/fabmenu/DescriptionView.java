package com.github.wrdlbrnft.fabmenu;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by kapeller on 18/06/15.
 */
class DescriptionView extends FrameLayout {

    private TextView mTextView;

    public DescriptionView(Context context) {
        super(context);
        setup(context);
    }

    public DescriptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);

    }

    public DescriptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    private void setup(Context context) {
        inflate(context, R.layout.layout_description, this);
        mTextView = (TextView) findViewById(R.id.textView);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setText(int textResId) {
        mTextView.setText(textResId);
    }
}
