package com.android.foodorderapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUs extends AppCompatActivity {

    private LinearLayout accordionLayout;
    private TextView accordionHeader;
    private TextView accordionContent;
    private LinearLayout accordionLayoutVision;
    private TextView accordionHeaderVision;
    private TextView accordionContentVision;
    private boolean isExpanded = false;
    private boolean isExpandedVision = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        accordionLayout = findViewById(R.id.accordionLayout);
        accordionHeader = findViewById(R.id.accordionHeader);
        accordionContent = findViewById(R.id.accordionContent);

        accordionLayoutVision = findViewById(R.id.accordionLayoutVision);
        accordionHeaderVision = findViewById(R.id.accordionHeaderVision);
        accordionContentVision = findViewById(R.id.accordionContentVision);

        accordionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    collapseAccordion();
                } else {
                    expandAccordion();
                }
            }
        });

        accordionLayoutVision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpandedVision) {
                    collapseAccordionVision();
                } else {
                    expandAccordionVision();
                }
            }
        });

        // Initially collapse the accordions
        collapseAccordion();
        collapseAccordionVision();
    }

    private void expandAccordion() {
        accordionContent.setVisibility(View.VISIBLE);
        isExpanded = true;
    }

    private void collapseAccordion() {
        accordionContent.setVisibility(View.GONE);
        isExpanded = false;
    }

    private void expandAccordionVision() {
        accordionContentVision.setVisibility(View.VISIBLE);
        isExpandedVision = true;
    }

    private void collapseAccordionVision() {
        accordionContentVision.setVisibility(View.GONE);
        isExpandedVision = false;
    }
}
