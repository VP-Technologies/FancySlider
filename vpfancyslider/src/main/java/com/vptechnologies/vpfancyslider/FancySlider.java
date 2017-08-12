package com.vptechnologies.vpfancyslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    /**
     * Variables for the properties of the FancySlider
     */
    private float smallTickPrecision;
    private float largeTickPrecision;
    private float minimumValue;
    private float maximumValue;
    private float initialValue;
    private float smallTickHeight;
    private int tickDistance;
    private int tickLineWidth;
    private int tickColor;
    private boolean labeled;

    /**
     * Default values for the FancySlider
     */
    private final float DEFAULT_SMALL_TICK_PRECISION = 1;
    private final float DEFAULT_LARGE_TICK_PRECISION = 5;
    private final float DEFAULT_MINIMUM_VALUE = 1;
    private final float DEFAULT_MAXIMUM_VALUE = 100;
    private final float DEFAULT_INITIAL_VALUE = 50;
    private final float DEFAULT_SMALL_TICK_HEIGHT = 0.7f;
    private final int DEFAULT_TICK_LINE_WIDTH = 6;
    private final int DEFAULT_TICK_DISTANCE = 40;
    private final int DEFAULT_TICK_COLOR = android.R.color.black;
    private final boolean DEFAULT_LABELED = false;

    /**
     * Variables for the functioning of the slider
     */
    private int progress;
    private TickAdapter adapter;
    private float lastValue = initialValue;
    private OnValueChangedListener onValueChangedListener;

    /**
     * Creates a FancySlider from the given context and attributes
     * @param context The context asking for this FancySlider
     * @param attrs The attributes used to customize the view
     */
    public FancySlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FancySlider,
                0, 0);

        try {
            smallTickPrecision = a.getFloat(R.styleable.FancySlider_smallTickPrecision, DEFAULT_SMALL_TICK_PRECISION);
            largeTickPrecision = a.getFloat(R.styleable.FancySlider_largeTickPrecision, DEFAULT_LARGE_TICK_PRECISION);
            minimumValue = a.getFloat(R.styleable.FancySlider_minimumValue, DEFAULT_MINIMUM_VALUE);
            maximumValue = a.getFloat(R.styleable.FancySlider_maximumValue, DEFAULT_MAXIMUM_VALUE);
            initialValue = a.getFloat(R.styleable.FancySlider_initialValue, DEFAULT_INITIAL_VALUE);
            tickDistance = a.getInteger(R.styleable.FancySlider_tickDistance, DEFAULT_TICK_DISTANCE);
            tickLineWidth = a.getInteger(R.styleable.FancySlider_tickLineWidth, DEFAULT_TICK_LINE_WIDTH);
            smallTickHeight = a.getFloat(R.styleable.FancySlider_smallTickHeight, DEFAULT_SMALL_TICK_HEIGHT);
            tickColor = a.getColor(R.styleable.FancySlider_tickColor, getResources().getColor(DEFAULT_TICK_COLOR));
            labeled = a.getBoolean(R.styleable.FancySlider_labeled, DEFAULT_LABELED);
        } finally {
            a.recycle();
        }

        setupSlider();
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

        // Update the progress of the slider, and call any listeners that are attached
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                progress += dx;
                float newValue = getValue();
                if (newValue != lastValue && onValueChangedListener != null) {
                    lastValue = newValue;
                    onValueChangedListener.onValueChanged(newValue);
                }
            }
        });

        // Load the adapter
        this.adapter = new TickAdapter();
        this.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        invalidate();
        requestLayout();

        // TODO: Scroll to the initial value
        this.scrollToValue(initialValue, false);

    }

    /**
     * Returns the number of pixels from the left edge of the slider to the center
     * of the slider
     * @return the center offset from the left
     */
    private int getCenterOffset() {
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

    /**
     * Attaches a listener to the value currently selected within the slider (from getValue())
     * @param onValueChangedListener The custom value changed listener
     */
    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }

    /**
     * Returns the initial position of this slider
     * @return the initial position of this slider
     */
    public float getInitialValue() {
        return initialValue;
    }

    /**
     * Gets the precision of each large tick
     * @return the precision of each large tick
     */
    public float getLargeTickPrecision() {
        return largeTickPrecision;
    }

    /**
     * Returns the max value of this slider
     * @return the max value of this slider
     */
    public float getMaximumValue() {
        return maximumValue;
    }

    /**
     * Returns the minimum value of this slider
     * @return the minimum value of this slider
     */
    public float getMinimumValue() {
        return minimumValue;
    }

    /**
     * Returns the height of the small tick, as a percentage of the container
     * @return the height of the small tick (from 0 to 1)
     */
    public float getSmallTickHeight() {
        return smallTickHeight;
    }

    /**
     * Returns the precision of each small tick
     * @return the small tick precision of this slider
     */
    public float getSmallTickPrecision() {
        return smallTickPrecision;
    }

    /**
     * Returns the color resource used to color the components of this slider
     * @return the color resource used to color the components of this slider
     */
    public int getTickColor() {
        return tickColor;
    }

    /**
     * Returns the distance, in pixels, between each tick
     * @return the distance between each tick
     */
    public int getTickDistance() {
        return tickDistance;
    }

    /**
     * Returns the width of each tick, in pixels
     * @return the width of each tick
     */
    public int getTickLineWidth() {
        return tickLineWidth;
    }

    /**
     * A listener interface for a changing value
     */
    public interface OnValueChangedListener {
        void onValueChanged(float value);
    }

    /**
     * An adapter for providing the ticks and marks within the RecycleView
     */
    private class TickAdapter extends RecyclerView.Adapter<TickAdapter.ViewHolder> {

        /**
         * Holds each tick or spacer within this FancySlider
         */
        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tickLabel;
            FrameLayout tickLine;
            LinearLayout container;
            FrameLayout spacer;

            /**
             * Creates a ViewHolder representing a tick or spacer
             * @param tickLabel A TextView for the tick as a label
             * @param tickLine A FrameLayout to hold the tick line
             * @param container A container for the tick and its label
             */
            ViewHolder(TextView tickLabel, FrameLayout tickLine, LinearLayout container) {
                super(container);
                this.tickLabel= tickLabel;
                this.tickLine = tickLine;
                this.container = container;
                this.spacer = new FrameLayout(container.getContext());
                this.container.addView(spacer, 0);
            }
        }

        /**
         * An adapter which manages the ticks within the FancySlider
         */
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

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            // If at the beginning or end, attach the spacer
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
                float value = (smallTickPrecision * (position - 1)) + minimumValue;
                boolean large = value % largeTickPrecision == 0;

                //holder.tickLabel.setText(Double.toString((position - 1) * smallTickPrecision + minimumValue));
                holder.tickLabel.setSingleLine(true);
                holder.tickLabel.setTextColor(tickColor);
                holder.tickLine.setBackgroundColor(tickColor);

                if (large) {
                    // Use smaller top margin
                    holder.spacer.setVisibility(View.GONE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(tickLineWidth, LayoutParams.MATCH_PARENT, 1));
                } else {
                    // Use larger top margin
                    holder.spacer.setVisibility(View.VISIBLE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, smallTickHeight));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(tickLineWidth, LayoutParams.MATCH_PARENT, 1 - smallTickHeight));
                }

            }

        }

        @Override
        public int getItemCount() {
            // Return the number of divisions, plus two for edges
            return (int) ((maximumValue - minimumValue) / smallTickPrecision) + 2;
        }
    }

}

