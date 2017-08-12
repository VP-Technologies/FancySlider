# FancySlider [![](https://jitpack.io/v/VP-Technologies/FancySlider.svg)](https://jitpack.io/#VP-Technologies/FancySlider)

FancySlider is an Android library for easily creating the kind of sliders you would see for weight and height within a fitness app. The library can be integrated into your application using Gradle, and is easily created using a custom view.

<div width="100%" style="text-align: center">
<img src="https://media.giphy.com/media/2nbkVYRVLskz6/giphy.gif" height="250px" style="margin: auto auto; text-align: center"/>
</div>

## Installation Instructions

The library can be downloaded and integrated into your project using Gradle. Simply put the following lines into your project's `build.gradle`:

1. Add the JitPack repository to your app's build file.
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
2. Add the dependency
```groovy
dependencies {
        compile 'com.github.VP-Technologies:FancySlider:V0.1'
}
```

That's it! The FancySlider is now available as `com.vptechnologies.vpfancyslider.FancySlider`.

## API Specification

Once imported into your project, you can add a FancySlider to your layout:

```xml
<com.vptechnologies.vpfancyslider.FancySlider
    android:id="@+id/sliderView"
    android:layout_width="match_parent"
    android:layout_height="64dp"
    slider:tickColor="@android:color/white"
    slider:minimumValue="1"
    slider:maximumValue="100"
    slider:smallTickPrecision="1"
    slider:largeTickPrecision="5"
    slider:smallTickHeight="0.6"
    slider:tickDistance="40"
    slider:tickLineWidth="7"
    />
```

You will want to add the proper namespace to your layout; for example, in the above case, I have added `xmlns:slider="http://schemas.android.com/apk/res-auto"` to my overall layout object (a LinearLayout).

There are quite a few customizable properties for the FancySlider (all of which are optional):
- `tickColor` - A color resource for the color of the ticks and labels. Default is `android.R.color.black`.
- `minimumValue` - A float which will be the first value of the slider. Default is 1.
- `maximumValue` - A float which will be the last value of the slider. Default is 100.
- `smallTickPrecision` - A float which is the amount that indicates how much each tick "is worth". For instance, a value of 1 here indicates that each tick represents an additional value of 1. Default is 1.
- `largeTickPrecision` - A float which indicates the amount increment at which place a taller tick. For example, a value of 5 here (along with a small tick precision of 1) indicates that every 5th tick will be a large tick. Default is 5.
- `smallTickHeight` - A float between 0 and 1 which indicates how tall a short tick is in terms of percentage of height within the FancySlider container. For instance, a value of 0.6 indicates that a small tick will reach 60% of the way to the top of the slider. Note that large ticks fill up the entire container. Default is 0.7.
- `tickDistance` - An integer representing the number of pixels that separate two ticks from each other. The default is 40.
- `tickLineWidth` - An integer representing the width of each tick in pixels. The default is 6.

All of these values also have *getters* for programmatic control. _Setters coming soon_.

### OnValueChangedListener, getValue, and scrollToValue

There are three important methods that you will find useful within the library:

#### `setOnValueChangedListener`

This listener is used to take action whenever the current slider value changes. For instance, you may want to update the value of a TextView as the user slides the FancySlider. The `OnValueChangedListener` handles listening to these changes, which you can take advantage of by providing your own `OnValueChangedListener`. For example:

```Java
FancySlider slider = (FancySlider) findViewById(....);
TextView myTextView = (TextView) findViewById(....);
slider.setOnValueChangedListener(new FancySlider.OnValueChangedListener() {
    @Override
    public void onValueChanged(float value) {
        myTextView.setText(Float.toString(value));
    }
});
```

#### `getValue()`

This method will simply return the currently selected value of the FancySlider, as a float.

#### `scrollToValue(float value, boolean smooth)`

The `scrollToValue()` method takes two arguments; a value to scroll to, and a boolean representing whether or not to scroll smoothly. For instance, the following onClick method would cause the slider to scroll to the value `50` in a smooth fashion.

```Java
button.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        slider.scrollToValue(50, true);
    }
});
```

To see the library in action, check out the example app in the `app` module!