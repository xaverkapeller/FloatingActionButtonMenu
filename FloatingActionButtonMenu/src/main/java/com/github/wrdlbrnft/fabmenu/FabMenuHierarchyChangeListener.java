package com.github.wrdlbrnft.fabmenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kapeller on 18/06/15.
 */
class FabMenuHierarchyChangeListener implements ViewGroup.OnHierarchyChangeListener {

    private final List<FabWrapper> mWrappers = new ArrayList<>();
    private final List<View> mViewsForStateAnimation = new ArrayList<>();

    private final Context mContext;
    private final FloatingActionButtonMenu mParent;

    public FabMenuHierarchyChangeListener(Context context, FloatingActionButtonMenu parent) {
        mContext = context;
        mParent = parent;
    }

    @Override
    public void onChildViewAdded(View parent, final View child) {
        if (parent == mParent && !(child instanceof DescriptionView)) {
            final DescriptionView descriptionView = resolveDescriptionView(child);
            if (descriptionView != null) {
                setupDescriptionView(child, descriptionView);
            }
            final int index = mParent.indexOfChild(child);
            final FabWrapper wrapper = new FabWrapper(child, descriptionView);
            mWrappers.add(index, wrapper);
            updateViewsForStateAnimation();
            mParent.ensureConsistentChildState();
        }
    }

    private void setupDescriptionView(View child, DescriptionView descriptionView) {
        descriptionView.measure(
                View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE, View.MeasureSpec.AT_MOST));

        final FloatingActionButtonMenu.LayoutParams params = (FloatingActionButtonMenu.LayoutParams) child.getLayoutParams();
        final View clickTarget = params.descriptionClickTargetId != FloatingActionButtonMenu.LayoutParams.NO_SPECIAL_CLICK_TARGET_ID
                ? child.findViewById(params.descriptionClickTargetId)
                : child;

        descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTarget.performClick();
            }
        });
    }

    private DescriptionView resolveDescriptionView(View view) {
        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof FloatingActionButtonMenu.LayoutParams) {
            final FloatingActionButtonMenu.LayoutParams fabParams = (FloatingActionButtonMenu.LayoutParams) params;
            if (fabParams.descriptionText != null) {
                final DescriptionView descriptionView = new DescriptionView(mContext);
                descriptionView.setText(fabParams.descriptionText);
                return descriptionView;
            }
        }
        return null;
    }

    private void updateViewsForStateAnimation() {
        mViewsForStateAnimation.clear();
        for (int count = mWrappers.size() - 1, i = count; i >= 0; i--) {
            final FabWrapper wrapper = mWrappers.get(i);
            if (wrapper.descriptionView != null) {
                mViewsForStateAnimation.add(wrapper.descriptionView);
            }

            if (i < count) {
                mViewsForStateAnimation.add(wrapper.fab);
            }
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (parent == mParent && !(child instanceof DescriptionView)) {
            final Iterator<FabWrapper> iterator = mWrappers.iterator();
            boolean searching = true;
            while (iterator.hasNext() && searching) {
                final FabWrapper wrapper = iterator.next();
                if (wrapper.fab == child) {
                    iterator.remove();
                    searching = false;
                    updateViewsForStateAnimation();
                    mParent.ensureConsistentChildState();
                }
            }
        }
    }

    public List<FabWrapper> getWrappersInViewOrder() {
        return mWrappers;
    }

    public List<View> getViewsForStateAnimation() {
        return mViewsForStateAnimation;
    }
}
