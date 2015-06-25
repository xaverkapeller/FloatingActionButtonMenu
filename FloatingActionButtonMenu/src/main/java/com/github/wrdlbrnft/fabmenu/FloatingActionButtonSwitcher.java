package com.github.wrdlbrnft.fabmenu;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by kapeller on 18/06/15.
 */
public class FloatingActionButtonSwitcher extends FrameLayout {

    public static final int TOP_CHILD_INDEX = 1;
    public static final int BOTTOM_CHILD_INDEX = 0;

    private Animator mTopHideTemplate;
    private Animator mTopRevealTemplate;
    private Animator mBottomHideTemplate;
    private Animator mBottomRevealTemplate;

    public enum State {
        TOP_VISIBLE,
        BOTTOM_VISIBLE
    }

    private State mState = State.TOP_VISIBLE;

    private View mBottomView;
    private View mTopView;

    public FloatingActionButtonSwitcher(Context context) {
        super(context);
        loadDefaultAnimations(context);
    }

    private void loadDefaultAnimations(Context context) {
        mTopHideTemplate = AnimatorInflater.loadAnimator(context, R.animator.default_top_hide);
        mTopRevealTemplate = AnimatorInflater.loadAnimator(context, R.animator.default_top_reveal);
        mBottomHideTemplate = AnimatorInflater.loadAnimator(context, R.animator.default_bottom_hide);
        mBottomRevealTemplate = AnimatorInflater.loadAnimator(context, R.animator.default_bottom_reveal);
    }

    public FloatingActionButtonSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
    }

    public FloatingActionButtonSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readAttributes(context, attrs);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButtonSwitcher);
        try {
            final int topHideResourceId = typedArray.getResourceId(R.styleable.FloatingActionButtonSwitcher_topHideAnimation, R.animator.default_top_hide);
            mTopHideTemplate = AnimatorInflater.loadAnimator(context, topHideResourceId);

            final int topRevealResourceId = typedArray.getResourceId(R.styleable.FloatingActionButtonSwitcher_topRevealAnimation, R.animator.default_top_reveal);
            mTopRevealTemplate = AnimatorInflater.loadAnimator(context, topRevealResourceId);

            final int bottomHideResourceId = typedArray.getResourceId(R.styleable.FloatingActionButtonSwitcher_bottomHideAnimation, R.animator.default_bottom_hide);
            mBottomHideTemplate = AnimatorInflater.loadAnimator(context, bottomHideResourceId);

            final int bottomRevealResourceId = typedArray.getResourceId(R.styleable.FloatingActionButtonSwitcher_bottomRevealAnimation, R.animator.default_bottom_reveal);
            mBottomRevealTemplate = AnimatorInflater.loadAnimator(context, bottomRevealResourceId);
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (getChildCount() != 2) {
            throw new IllegalStateException(FloatingActionButtonSwitcher.class.getSimpleName() + " needs exactly two children!");
        }

        mBottomView = getChildAt(BOTTOM_CHILD_INDEX);
        mTopView = getChildAt(TOP_CHILD_INDEX);
    }

    public Animator createStateAnimator(State state) {
        switch (state) {

            case TOP_VISIBLE:
                return createShowTopAnimator();

            case BOTTOM_VISIBLE:
                return createShowBottomAnimator();

            default:
                throw new UnsupportedOperationException("Cannot create Animator for State " + state);
        }
    }

    private Animator createShowTopAnimator() {
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(
                createAnimatorFromTemplate(mTopRevealTemplate, mTopView),
                createAnimatorFromTemplate(mBottomHideTemplate, mBottomView)
        );
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationEnd(animation);
                mTopView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = State.TOP_VISIBLE;
            }
        });
        return set;
    }

    private Animator createShowBottomAnimator() {
        final AnimatorSet set = new AnimatorSet();
        set.playTogether(
                createAnimatorFromTemplate(mTopHideTemplate, mTopView),
                createAnimatorFromTemplate(mBottomRevealTemplate, mBottomView)
        );
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mTopView.setVisibility(GONE);
                mState = State.BOTTOM_VISIBLE;
            }
        });
        return set;
    }

    public void showTop() {
        if (mState == State.TOP_VISIBLE) {
            return;
        }

        final Animator animator = createShowTopAnimator();
        animator.start();
    }

    public void showBottom() {
        if (mState == State.BOTTOM_VISIBLE) {
            return;
        }

        final Animator animator = createShowBottomAnimator();
        animator.start();
    }

    public State getState() {
        return mState;
    }

    private Animator createAnimatorFromTemplate(Animator template, View target) {
        final Animator clone = template.clone();
        clone.setTarget(target);
        return clone;
    }
}
