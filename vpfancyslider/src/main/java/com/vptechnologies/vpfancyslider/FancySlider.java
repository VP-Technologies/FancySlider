package com.vptechnologies.vpfancyslider;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A slider with locking, moving, and reading functionality. Useful for things
 * like weight and height sliders in fitness apps.
 * Created for VP Technologies
 * @author Aaron Vontell
 */
public class FancySlider extends RecyclerView {

    private double smallTickPrecision;
    private double largeTickPrecision;
    private double minimumValue;
    private double maximumValue;
    private double initialValue;
    private int tickDistance;
    private int tickColor;
    private boolean labeled;

    private final float perc = 0.7f;


    private TickAdapter adapter;

    public FancySlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FancySlider,
                0, 0);

        try {
            smallTickPrecision = a.getFloat(R.styleable.FancySlider_smallTickPrecision, 1);
            largeTickPrecision = a.getFloat(R.styleable.FancySlider_largeTickPrecision, 5);
            minimumValue = a.getFloat(R.styleable.FancySlider_maximumValue, 0);
            maximumValue = a.getFloat(R.styleable.FancySlider_minimumValue, 100);
            initialValue = a.getFloat(R.styleable.FancySlider_initialValue, 50);
            tickDistance = a.getInteger(R.styleable.FancySlider_tickDistance, 24);
            tickColor = a.getColor(R.styleable.FancySlider_tickColor, getResources().getColor(android.R.color.black));
            labeled = a.getBoolean(R.styleable.FancySlider_labeled, false);
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

        // Load the adapter
        this.adapter = new TickAdapter(this.smallTickPrecision,
                this.largeTickPrecision,
                this.minimumValue,
                this.maximumValue,
                this.tickDistance,
                this.tickColor);
        this.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
        invalidate();
        requestLayout();

    }

    /**
     * Creates the adapter, using the given tick precision and max/min values
     */
    private void createSliderComponents() {
        throw new RuntimeException("Not yet implemented.");
    }

    /**
     * Returns the value that is currently located at the center of the slider
     * @return the value that is currently located at the center of the slider
     */
    public double getValue() {
        throw new RuntimeException("Not yet implemented.");
    }

    /**
     * Smoothly scrolls to the given value
     * @param value the value to scroll to
     */
    public void scrollTo(double value) {
        throw new RuntimeException("Not yet implemented.");
    }

    /**
     * Jumps to the given value
     * @param value the value to jump to
     */
    public void jumpTo(double value) {
        throw new RuntimeException("Not yet implemented.");
    }

    public void attachValueChangedListener() {

        throw new RuntimeException("Not yet implemented.");
    }

    /**
     * An adapter for providing the ticks and marks within the RecycleView
     */
    private class TickAdapter extends RecyclerView.Adapter<TickAdapter.ViewHolder> {
        private double smallTickPrecision;
        private double largeTickPrecision;
        private double maxValue;
        private double minValue;
        private int tickColor;
        private int tickDistance;

        private final int LARGE_PADDING = 0;
        private final int SMALL_PADDING = 50;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
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


        public TickAdapter(double smallTickPrecision, double largeTickPrecision,
                           double minValue, double maxValue, int tickDistance,
                           @ColorRes int tickColor) {

            // Assert that the desired ticks and ranges are valid
            if (minValue > maxValue) {
                double temp = minValue;
                minValue = maxValue;
                maxValue = temp;
            }
            if ((maxValue - minValue) % smallTickPrecision != 0 ||
                    largeTickPrecision % smallTickPrecision != 0 ||
                    maxValue - minValue == 0) {
                throw new RuntimeException("Range of values for FancySlider are not evenly divisible by the desired tick precisions");
            }

            this.smallTickPrecision = smallTickPrecision;
            this.largeTickPrecision = largeTickPrecision;
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.tickColor = tickColor;
            this.tickDistance = tickDistance;
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

            //TODO: If this is the first or last position, make a gap
            if (position == 0) {

                holder.tickLabel.setText("START");

            } else if (position == getItemCount() - 1) {

                holder.tickLabel.setText("END");

            } else {

                // Decide if this position is a large or small tick
                double value = (this.smallTickPrecision * position) + minValue;
                boolean large = value % this.largeTickPrecision == 0;

                holder.container.setPadding(tickDistance, 0, tickDistance, 0);

                holder.tickLabel.setText(Double.toString(position * smallTickPrecision + minimumValue));
                holder.tickLabel.setSingleLine(true);
                //holder.tickLabel.setText("");
                holder.tickLabel.setTextColor(this.tickColor);
                holder.tickLine.setBackgroundColor(this.tickColor);

                if (large) {
                    // Use smaller top margin
                    holder.spacer.setVisibility(View.GONE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(10, LayoutParams.MATCH_PARENT, 1));
                } else {
                    // Use larger top margin
                    holder.spacer.setVisibility(View.VISIBLE);
                    holder.spacer.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, perc));
                    holder.tickLine.setLayoutParams(new LinearLayout.LayoutParams(10, LayoutParams.MATCH_PARENT, 1-perc));
                }

            }

        }

        @Override
        public int getItemCount() {

            // Return the number of division, plus two for edges
            int count = (int) ((this.maxValue - this.minValue) / smallTickPrecision) + 2;
            //Log.e("COUNT", "" + count);
            return count;
        }
    }

}

