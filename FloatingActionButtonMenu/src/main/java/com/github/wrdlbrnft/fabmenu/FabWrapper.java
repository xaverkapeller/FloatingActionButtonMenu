package com.github.wrdlbrnft.fabmenu;

import android.view.View;

/**
 * Created by kapeller on 18/06/15.
 */
class FabWrapper {
    public final View fab;
    public final View descriptionView;

    public FabWrapper(View fab, View descriptionView) {
        this.fab = fab;
        this.descriptionView = descriptionView;
    }
}
