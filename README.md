# FloatingActionButtonMenu

Easy and simple way to create an expanding and collapsing menu with `FloatingActionButtons`!

For a detailed example of how to use this library please take a look at the sample app which is included in this repository!

# Basic Usage

Most of the configuration of the menu can be done in xml! The `FloatingActionButton` at the bottom will be visible in both the expanded and collapsed state:

```xml
<com.github.wrdlbrnft.fabmenu.FloatingActionButtonMenu
    android:id="@+id/menu"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_gravity="bottom|end"
    android:layout_margin="16dp">

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_example_two"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/icon_example_two"
        app:descriptionText="@string/description_example_two"
        app:fabSize="mini"/>

    <android.support.design.widget.FloatingActionButton
        android:id="@+id/fab_example_one"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:src="@drawable/icon_example_one"
        app:descriptionText="@string/description_example_one"
        app:fabSize="mini"/>

    <com.github.wrdlbrnft.fabmenu.FloatingActionButtonSwitcher
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        app:descriptionClickTargetId="@+id/fab_edit"
        app:descriptionText="@string/description_edit">

        <android.support.design.widget.FloatingActionButton
            android:id="@id/fab_edit"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/icon_edit"/>

        <android.support.design.widget.FloatingActionButton
            android:id="@+id/fab_add"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/icon_add"/>

    </com.github.wrdlbrnft.fabmenu.FloatingActionButtonSwitcher>

</com.github.wrdlbrnft.fabmenu.FloatingActionButtonMenu>
```

The XML attribute `descriptionText` defines the text which will be shown as description of each item in the expanded state!

The `FloatingActionButtonSwitcher` at the bottom will automatically show one `FloatingActionButton` in the collapsed state and another one in the expanded state! Again the bottom `FloatingActionButton` inside the `FloatingActionButtonSwitcher` is the one who will be visible in the collapsed state! 
