package com.github.wrdlbrnft.fabmenu.app;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.wrdlbrnft.fabmenu.FloatingActionButtonMenu;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        final FloatingActionButtonMenu menu = (FloatingActionButtonMenu) findViewById(R.id.menu);

        final View dimLayer = findViewById(R.id.dimLayer);
        dimLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.collapse();
            }
        });
        menu.setDimLayer(dimLayer);

        final FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.expand();
            }
        });

        final FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordinatorLayout, R.string.snackbar_click_edit, Snackbar.LENGTH_SHORT).show();
                menu.collapse();
            }
        });

        final FloatingActionButton fabExampleOne = (FloatingActionButton) findViewById(R.id.fab_example_one);
        fabExampleOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordinatorLayout, R.string.snackbar_click_example_one, Snackbar.LENGTH_SHORT).show();
                menu.collapse();
            }
        });

        final FloatingActionButton fabExampleTwo = (FloatingActionButton) findViewById(R.id.fab_example_two);
        fabExampleTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(coordinatorLayout, R.string.snackbar_click_example_two, Snackbar.LENGTH_SHORT).show();
                menu.collapse();
            }
        });
    }
}
