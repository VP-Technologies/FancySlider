package com.vptechnologies.vpfancyslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A slider with locking, moving, and reading functionality. Useful for things
 * like weight and height sliders in fitness apps.
 * Created for VP Technologies
 * @author Aaron Vontell
 */
public class FancySlider extends RecyclerView {

    private float smallTickPrecision;
    private float largeTickPrecision;
    private float minimumValue;
    private float maximumValue;
    private float initialValue;
    private float smallTickHeight;
    private int tickDistance;
    private int tickColor;
    private int progress;
    private boolean labeled;

    private TickAdapter adapter;
    private final FancySlider slider;

    public FancySlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FancySlider,
                0, 0);

        try {
            smallTickPrecision = a.getFloat(R.styleable.FancySlider_smallTickPrecision, 1);
            largeTickPrecision = a.getFloat(R.styleable.FancySlider_largeTickPrecision, 5);
            minimumValue = a.getFloat(R.styleable.FancySlider_minimumValue, 1);
            maximumValue = a.getFloat(R.styleable.FancySlider_maximumValue, 100);
            initialValue = a.getFloat(R.styleable.FancySlider_initialValue, 50);
            tickDistance = a.getInteger(R.styleable.FancySlider_tickDistance, 40);
            smallTickHeight = a.getFloat(R.styleable.FancySlider_smallTickHeight, 0.7f);
            tickColor = a.getColor(R.styleable.FancySlider_tickColor, getResources().getColor(android.R.color.black));
            labeled = a.getBoolean(R.styleable.FancySlider_labeled, false);
        } finally {
            a.recycle();
        }

        this.slider = this;
        setupSlider();
    }

    public void watchText(final TextView tv) {

        this.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                tv.setText("" + getValue());
            }
        });

    }

    /**
     * Sets up the customizable components of the FancySlider
     */
    private void setupSlider() {

        // Put this into horizontal mode
        this.setLayoutManager(
                new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Disable the scroll bar
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);

        // Update the progress of the slider
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                progress += dx;
            }
        });

        // Load the adapter
        this.adapter = new TickAdapter();
        this.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        invalidate();
        requestLayout();

        // Scroll to the initial value
        this.scrollToValue(initialValue, false);

    }

    /**
     * Returns the number of pixels from the left edge of the slider to the center
     * of the slider
     * @return
     */
    public int getCenterOffset() {
        return this.getMeasuredWidth() / 2;
    }

    /**
     * Returns the value that is currently located at the center of the slider
     * @return the value that is currently located at the center of the slider
     */
    public float getValue() {

        // Now need to figure out which view the center of our screen is closest
        return ((((progress + this.tickDistance / 2) / this.tickDistance)) * this.smallTickPrecision) + this.minimumValue;
    }

    /**
     * Scrolls to the given value, by either jumping or scrolling smoothly
     * @param value the value to scroll to
     * @param smooth if true, then a smooth scroll to that value will occur
     */
    public void scrollToValue(float value, boolean smooth) {
        float valDiff = value - getValue();
        float scrollByAbs = ((int) (valDiff / smallTickPrecision) * tickDistance);
        float centerOffset = -(scrollByAbs + progress + tickDistance / 2) % this.smallTickPrecision;
        int scrollAmount = (int) (scrollByAbs + centerOffset);
        if (smooth) {
            this.smoothScrollBy(scrollAmount, 0);
        } else {
            this.scrollBy(scrollAmount, 0);
        }
    }

    public void attachValueChangedListener() {

        throw new RuntimeException("Not yet implemented.");
    }

    private int getOffsetFromValue(float value) {

        // This is essentially the inverse of getValue()

        return (int) ((value - minimumValue) * tickDistance / smallTickPrecision - (tickDistance / 2));

    }

    /**
     * An adapter for providing the ticks and marks within the RecycleView
     */
    private class TickAdapter extends RecyclerView.Adapter<TickAdapter.ViewHolder> {

        /**
         * Holds each tick or spacer within this FancySlider
         */
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tickLabel;
            public FrameLayout tickLine;
            public LinearLayout container;
            public FrameLayout spacer;
            public ViewHolder(TextView tickFrame, FrameLayout tickLine, LinearLayout container) {
                super(container);
                this.tickLabel= tickFrame;
                this.tickLine = tickLine;
                this.container = container;
                this.spacer = new FrameLayout(container.getContext());
                this.container.addView(spacer, 0);
            }
        }


        private TickAdapter() {

            // Assert that the desired ticks and ranges are valid
            if (minimumValue > maximumValue) {
                float temp = minimumValue;
                minimumValue = maximumValue;
                maximumValue = temp;
            }
            if ((maximumValue - minimumValue) % smallTickPrecision != 0 ||
                    largeTickPrecision % smallTickPrecision != 0 ||
                    maximumValue - minimumValue == 0) {
                throw new RuntimeException("Range of values for FancySlider are not evenly divisible by the desired tick precisions");
            }

        }

        @Override
        public TickAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // Create a tick with the correct padding
            FrameLayout tick = new FrameLayout(parent.getContext());
            TextView label = new TextView(parent.getContext());
            LinearLayout container = new LinearLayout(parent.getContext());
            container.setOrientation(LinearLayout.VERTICAL);
            container.setLayoutParams(new RecyclerView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            label.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            container.setGravity(Gravity.CENTER);
            label.setGravity(Gravity.CENTER);

            container.addView(tick);
            container.addView(label);

            return new ViewHolder(label, tick, container);

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            if (position == 0 || position == getItemCount() - 1) {

                // Hide the label and tick, and make an appropriate gap
                holder.container.getLayoutParams().width = getCenterOffset() - (tickDistance / 2);
                holder.tickLine.setVisibility(View.GONE);
                holder.tickLabel.setVisibility(View.GONE);

            } else {

                // Otherwise, reset the width of the container, and show items
                holder.tickLine.setVisibility(View.VISIBLE);
                holder.tickLabel.setVisibility(View.VISIBLE);
                holder.container.getLayoutParams().width = tickDistance;

                // Decide if this position is a large or small tick
                float value = (smallTickPrecision * position) + minimumValue;
                boolean large = value % largeTickPrecision == 0;

                //holder.container.setPadding(tickDistance, 0, tickDistance, 0);

                //holder.tickLabel.setText(Double.toString((position - 1) * smallTickPrecision + minimumValue));
                holder.tickLabel.setSingleLine(true);
                //holder.tickLabel.setText("");
                holder.tickLabel.setTextColor(tickColor);
                holder.tickLine.setBackgroundColor(tickColor);

                if (large) {
                    // Use smaller top margin
                    holder.spacer.setVisibility(View.GONE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(10, LayoutParams.MATCH_PARENT, 1));
                } else {
                    // Use larger top margin
                    holder.spacer.setVisibility(View.VISIBLE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, smallTickHeight));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(10, LayoutParams.MATCH_PARENT, 1 - smallTickHeight));
                }

            }

        }

        @Override
        public int getItemCount() {
            // Return the number of divisions, plus two for edges
            int count = (int) ((maximumValue - minimumValue) / smallTickPrecision) + 2;
            return count;
        }
    }

}

