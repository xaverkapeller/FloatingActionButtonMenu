package com.github.wrdlbrnft.fabmenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kapeller on 18/06/15.
 */
@CoordinatorLayout.DefaultBehavior(FloatingActionButtonMenu.Behavior.class)
public class FloatingActionButtonMenu extends ViewGroup {

    public interface Callback {
        public void onStartingTransition(State from, State to);
        public void onTransitioning(float progress);
        public void onSettled(State state);
    }

    public enum State {
        TRANSITIONING,
        COLLAPSED,
        EXPANDED
    }

    private Callback mCallback = null;
    private State mState = State.COLLAPSED;
    private int mFabOffset = 0;
    private final List<FloatingActionButtonSwitcher> mExchangers = new ArrayList<>();
    private FabMenuHierarchyChangeListener mFabMenuHierarchyChangeListener;

    private int mDimLayerCollapsedColor;
    private int mDimLayerExpandedColor;

    private int mStateAnimationDuration;

    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private View mDimLayer;

    public FloatingActionButtonMenu(Context context) {
        super(context);
        setup(context);
        loadDefaults(context);
    }

    private void loadDefaults(Context context) {
        final Resources resources = context.getResources();
        mDimLayerExpandedColor = resources.getColor(R.color.default_dim_layer_expanded);
        mDimLayerCollapsedColor = resources.getColor(R.color.default_dim_layer_collapsed);
        mStateAnimationDuration = resources.getInteger(R.integer.default_fab_menu_animation_duration);
    }

    public FloatingActionButtonMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
        readAttributes(context, attrs);
    }

    public FloatingActionButtonMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
        readAttributes(context, attrs);
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        final Resources resources = context.getResources();
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingActionButtonMenu);
        try {
            final int defaultDimLayerCollapsedColor = resources.getColor(R.color.default_dim_layer_collapsed);
            mDimLayerCollapsedColor = typedArray.getColor(R.styleable.FloatingActionButtonMenu_dimLayerCollapsedColor, defaultDimLayerCollapsedColor);

            final int defaultDimLayerExpandedColor = resources.getColor(R.color.default_dim_layer_expanded);
            mDimLayerExpandedColor = typedArray.getColor(R.styleable.FloatingActionButtonMenu_dimLayerExpandedColor, defaultDimLayerExpandedColor);

            final int defaultStateAnimationDuration = resources.getInteger(R.integer.default_fab_menu_animation_duration);
            mStateAnimationDuration = typedArray.getInt(R.styleable.FloatingActionButtonMenu_stateAnimationDuration, defaultStateAnimationDuration);
        } finally {
            typedArray.recycle();
        }
    }

    private void setup(Context context) {
        mFabMenuHierarchyChangeListener = new FabMenuHierarchyChangeListener(context, this);
        setOnHierarchyChangeListener(mFabMenuHierarchyChangeListener);
    }

    protected void ensureConsistentChildState() {
        final List<FabWrapper> wrappers = mFabMenuHierarchyChangeListener.getWrappersInViewOrder();

        for (int i = 0, count = wrappers.size(); i < count; i++) {
            FabWrapper wrapper = wrappers.get(i);
            if (mState == State.COLLAPSED) {
                if (i < count - 1) {
                    wrapper.fab.setVisibility(GONE);
                } else {
                    wrapper.fab.setVisibility(VISIBLE);
                }
            } else {
                wrapper.fab.setVisibility(VISIBLE);
                if (wrapper.descriptionView != null) {
                    addView(wrapper.descriptionView);
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        final int width = getMeasuredWidth();
        final int fabAreaWidth = width - mFabOffset;
        final int fabLeft = getPaddingLeft() + mFabOffset;
        final int descriptionLeft = getPaddingLeft();
        int fabTop = getPaddingTop();

        final List<FabWrapper> wrappers = mFabMenuHierarchyChangeListener.getWrappersInViewOrder();
        for (FabWrapper wrapper : wrappers) {
            final View fab = wrapper.fab;

            if (fab.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams params = (LayoutParams) fab.getLayoutParams();

            final int fabWidth = fab.getMeasuredWidth();
            final int fabHeight = fab.getMeasuredHeight();
            final int fabOffsetX = (fabAreaWidth - fabWidth) / 2;
            fab.layout(fabLeft + fabOffsetX, fabTop + params.topMargin, fabLeft + fabOffsetX + fabWidth, fabTop + fabHeight + params.topMargin);

            if (isDescriptionVisible(wrapper)) {
                final View descriptionView = wrapper.descriptionView;
                final int descriptionWidth = descriptionView.getMeasuredWidth();
                final int descriptionHeight = descriptionView.getMeasuredHeight();
                final int descriptionOffsetY = (fabHeight - descriptionHeight) / 2 + params.topMargin;
                final int descriptionOffsetX = mFabOffset - descriptionWidth;
                descriptionView.layout(descriptionLeft + descriptionOffsetX, fabTop + descriptionOffsetY, descriptionLeft + descriptionOffsetX + descriptionWidth, fabTop + descriptionOffsetY + descriptionHeight);
            }

            fabTop += fabHeight + params.topMargin + params.bottomMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightUsed = 0;
        int maxFabWidth = 0;
        int maxDescriptionWidth = 0;
        mExchangers.clear();

        final List<FabWrapper> wrappers = mFabMenuHierarchyChangeListener.getWrappersInViewOrder();
        for (FabWrapper wrapper : wrappers) {
            final View fab = wrapper.fab;

            if (fab.getVisibility() == GONE) {
                continue;
            }

            final LayoutParams params = (LayoutParams) fab.getLayoutParams();
            if (fab instanceof FloatingActionButtonSwitcher) {
                mExchangers.add((FloatingActionButtonSwitcher) fab);
            }

            measureChildWithMargins(fab, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            heightUsed += fab.getMeasuredHeight() + params.topMargin + params.bottomMargin;
            maxFabWidth = Math.max(maxFabWidth, fab.getMeasuredWidth() + params.leftMargin + params.rightMargin);

            if (isDescriptionVisible(wrapper)) {
                maxDescriptionWidth = Math.max(maxDescriptionWidth, wrapper.descriptionView.getMeasuredWidth());
            }
        }

        mFabOffset = maxDescriptionWidth;

        setMeasuredDimension(maxFabWidth + maxDescriptionWidth + getPaddingLeft() + getPaddingRight(), heightUsed + getPaddingTop() + getPaddingBottom());
    }

    private boolean isDescriptionVisible(FabWrapper wrapper) {
        return mState != State.COLLAPSED && wrapper.descriptionView != null;
    }

    public void expand() {
        if (mState != State.COLLAPSED) {
            return;
        }
        notifyStartingTransition(State.COLLAPSED, State.EXPANDED);
        mState = State.TRANSITIONING;

        switchToExpandedLayout();

        final List<View> views = mFabMenuHierarchyChangeListener.getViewsForStateAnimation();

        final AnimatorSet set = new AnimatorSet();
        set.playTogether(
                createExchangerAnimator(FloatingActionButtonSwitcher.State.BOTTOM_VISIBLE),
                createStagedAlphaAnimator(views, 0.0f, 1.0f),
                createProgressAnimator(0.0f, 1.0f)
        );

        set.setDuration(mStateAnimationDuration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = State.EXPANDED;
                notifySettled(mState);
            }
        });
        set.start();
    }

    public void collapse() {
        if (mState != State.EXPANDED) {
            return;
        }
        notifyStartingTransition(State.EXPANDED, State.COLLAPSED);
        mState = State.TRANSITIONING;

        final List<View> views = mFabMenuHierarchyChangeListener.getViewsForStateAnimation();

        final AnimatorSet set = new AnimatorSet();
        set.playTogether(
                createExchangerAnimator(FloatingActionButtonSwitcher.State.TOP_VISIBLE),
                createStagedAlphaAnimator(views, 1.0f, 0.0f),
                createProgressAnimator(1.0f, 0.0f)
        );

        set.setDuration(mStateAnimationDuration);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mState = State.COLLAPSED;

                switchToCollapsedLayout();

                notifySettled(mState);
            }
        });
        set.start();
    }

    private void switchToExpandedLayout() {
        final List<FabWrapper> wrappers = mFabMenuHierarchyChangeListener.getWrappersInViewOrder();
        for (FabWrapper wrapper : wrappers) {

            wrapper.fab.setVisibility(VISIBLE);

            if (wrapper.descriptionView != null) {
                addView(wrapper.descriptionView);
            }
        }
    }

    private void switchToCollapsedLayout() {
        final List<FabWrapper> wrappers = mFabMenuHierarchyChangeListener.getWrappersInViewOrder();
        for (int i = 0, count = wrappers.size(); i < count; i++) {
            final FabWrapper wrapper = wrappers.get(i);

            if (i < count - 1) {
                wrapper.fab.setVisibility(GONE);
            }

            if (wrapper.descriptionView != null) {
                removeView(wrapper.descriptionView);
            }
        }
    }

    private ValueAnimator createProgressAnimator(float from, float to) {
        final ValueAnimator progressAnimator = ValueAnimator.ofFloat(from, to);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                notifyTransitioning(progress);
            }
        });
        return progressAnimator;
    }

    private Animator createExchangerAnimator(FloatingActionButtonSwitcher.State exchangerState) {
        final List<Animator> animators = new ArrayList<>();
        for (FloatingActionButtonSwitcher exchanger : mExchangers) {
            animators.add(exchanger.createStateAnimator(exchangerState));
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        return animatorSet;
    }

    private Animator createStagedAlphaAnimator(final List<View> views, float fromAlpha, float toAlpha) {
        final ValueAnimator animator = ValueAnimator.ofFloat(fromAlpha, toAlpha);
        final int viewCount = views.size();
        final float viewOffset = 1.0f / (2.0f * (viewCount - 1.0f));
        final float period = 0.5f;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                for (int i = 0; i < viewCount; i++) {
                    final View view = views.get(i);

                    final float offset = i * viewOffset;
                    final float alpha = range(0.0f, (progress - offset) / period, 1.0f);
                    view.setAlpha(alpha);
                }
            }
        });
        return animator;
    }

    private float range(float min, float value, float max) {
        if (value < min) {
            return min;
        }

        if (value > max) {
            return max;
        }

        return value;
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams params) {
        if (params instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) params);
        }

        if (params instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) params);
        }

        return new LayoutParams(params);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    public void setDimLayer(View dimLayer) {
        mDimLayer = dimLayer;
    }

    public boolean isExpanded() {
        return mState == State.EXPANDED;
    }

    public boolean isCollapsed() {
        return mState == State.COLLAPSED;
    }

    protected void notifyStartingTransition(State from, State to) {
        if (mDimLayer != null && from == State.COLLAPSED) {
            mDimLayer.setVisibility(VISIBLE);
        }

        if (mCallback != null) {
            mCallback.onStartingTransition(from, to);
        }
    }

    protected void notifyTransitioning(float progress) {
        if (mDimLayer != null) {
            final int color = (int) mArgbEvaluator.evaluate(progress, mDimLayerCollapsedColor, mDimLayerExpandedColor);
            mDimLayer.setBackgroundColor(color);
        }

        if (mCallback != null) {
            mCallback.onTransitioning(progress);
        }
    }

    protected void notifySettled(State state) {
        if (mDimLayer != null && state == State.COLLAPSED) {
            mDimLayer.setVisibility(GONE);
        }

        if (mCallback != null) {
            mCallback.onSettled(state);
        }
    }

    public static class LayoutParams extends MarginLayoutParams {

        public static final int NO_SPECIAL_CLICK_TARGET_ID = -1;

        public final CharSequence descriptionText;
        public final int descriptionClickTargetId;

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);

            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FabMenuLayout_LayoutParams);
            try {
                descriptionText = typedArray.getText(R.styleable.FabMenuLayout_LayoutParams_descriptionText);
                descriptionClickTargetId = typedArray.getResourceId(R.styleable.FabMenuLayout_LayoutParams_descriptionClickTargetId, NO_SPECIAL_CLICK_TARGET_ID);
            } finally {
                typedArray.recycle();
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            descriptionText = null;
            descriptionClickTargetId = NO_SPECIAL_CLICK_TARGET_ID;
        }

        public LayoutParams(int width, int height, CharSequence descriptionText, int descriptionClickTargetId) {
            super(width, height);
            this.descriptionText = descriptionText;
            this.descriptionClickTargetId = descriptionClickTargetId;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            descriptionText = source.descriptionText;
            descriptionClickTargetId = source.descriptionClickTargetId;
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
            descriptionText = null;
            descriptionClickTargetId = NO_SPECIAL_CLICK_TARGET_ID;
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
            descriptionText = null;
            descriptionClickTargetId = NO_SPECIAL_CLICK_TARGET_ID;
        }
    }

    public static class Behavior extends CoordinatorLayout.Behavior<View> {

        private static final boolean SNACK_BAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;
        private static final FastOutSlowInInterpolator INTERPOLATOR = new FastOutSlowInInterpolator();
        private float mTranslationY;

        public Behavior() {

        }

        public Behavior(Context context, AttributeSet attributeSet) {

        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
            return SNACK_BAR_BEHAVIOR_ENABLED && dependency instanceof Snackbar.SnackbarLayout;
        }

        @Override
        public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
            updateFabTranslationForSnackbar(parent, child, dependency);
            return false;
        }

        private void updateFabTranslationForSnackbar(CoordinatorLayout parent, View fab, View snackbar) {
            final float translationY = getFabTranslationYForSnackbar(parent, fab);
            if (translationY != mTranslationY) {
                ViewCompat.animate(fab).cancel();
                if (Math.abs(translationY - mTranslationY) == (float) snackbar.getHeight()) {
                    ViewCompat.animate(fab).translationY(translationY).setInterpolator(INTERPOLATOR).setListener(null);
                } else {
                    ViewCompat.setTranslationY(fab, translationY);
                }

                mTranslationY = translationY;
            }
        }

        private float getFabTranslationYForSnackbar(CoordinatorLayout parent, View fab) {
            final List<View> dependencies = parent.getDependencies(fab);

            float minOffset = 0.0F;
            for (View view : dependencies) {
                if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                    minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - (float) view.getHeight());
                }
            }

            return minOffset;
        }
    }
}

