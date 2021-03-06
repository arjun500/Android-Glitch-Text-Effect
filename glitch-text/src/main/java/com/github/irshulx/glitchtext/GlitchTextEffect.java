package com.github.irshulx.glitchtext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IRSHU on 22/12/2017.
 */

public class GlitchTextEffect extends FrameLayout{
    private List<Integer> colors = new ArrayList<>();
    private int noise=10;
    private String text;
    List<TextView> textViews = new ArrayList<>();
    private AnimationSet animationSet;
    private int speed =70;
    private boolean reverse =false;
    private int textSize = 50;
    private String fontFile="fonts/Poppins-Black.ttf";
    private int gravity = Gravity.CENTER;

    public GlitchTextEffect(Context context,List<Integer> colors, String text) {
        super(context);
        this.text = text;
        this.colors = colors;
        setLayerType(View.LAYER_TYPE_HARDWARE,null);
    }
    public void start(){
        init();
    }
    public GlitchTextEffect(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadStateFromAttrs(attrs);
    }

    private void loadStateFromAttrs(AttributeSet attributeSet) {
        if (attributeSet == null) {
            throw new IllegalArgumentException(
                    "Configurations not found");
        }

        TypedArray a = null;
        try {
            a = getContext().obtainStyledAttributes(attributeSet, R.styleable.GlitchTextEffect);
            this.noise = a.getInteger(R.styleable.GlitchTextEffect_noise,this.noise);
            this.textSize = a.getInteger(R.styleable.GlitchTextEffect_textSize,this.textSize);
            this.speed = a.getInteger(R.styleable.GlitchTextEffect_speed,this.speed);
            String fontFile = a.getString(R.styleable.GlitchTextEffect_fontLocation);
            this.text = a.getString(R.styleable.GlitchTextEffect_text);
            if(TextUtils.isEmpty(this.text)){
                throw new IllegalArgumentException(
                        "Text not specified, please include app:text attribute");
            }
            int colorsResId = a.getResourceId(R.styleable.GlitchTextEffect_textColors, 0);
            if (colorsResId == 0) {
                throw new IllegalArgumentException(
                        "IntegerListPreference: error - colors not specified, please include app:textColors");
            }
            TypedArray ta = getContext().getResources().obtainTypedArray(R.array.glitch_colors);
            for (int i = 0; i < ta.length(); i++) {
                int color = Color.parseColor(ta.getString(i));
                colors.add(color);
            }

            this.fontFile = TextUtils.isEmpty(fontFile)?this.fontFile:fontFile;

            start();

        } finally {
            if (a != null) {
                a.recycle(); // ensure this is always called
            }
        }
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setFontFile(String fontFile) {
        this.fontFile = fontFile;
    }

    public void setGravity(int gravity){
        this.gravity = gravity;
    }

    public void setNoise(int noise){
        this.noise = noise;
    }

    public void setSpeed(int speed){
        this.speed = speed;
    }


    private void init() {
        for(int i = 0 ; i <colors.size() ; i++){
            TextView textView = getTextView(colors.get(i));
            addView(textView);
            textViews.add(textView);
            if(i+1!=colors.size()) {
                animate(textView, noise + (i/2*2));
            }
        }
    }

    private TextView getTextView(int color){
        TextView textView = new TextView(getContext());
        textView.setGravity(gravity);
        textView.setTypeface(FontCache.get(fontFile,getContext()));
        textView.setTextColor(color);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        return textView;
    }

    public void animate(final TextView textView, final int noise) {
        animationSet = new AnimationSet(false);
        TranslateAnimation trans = new TranslateAnimation(0, 0,
                TranslateAnimation.ABSOLUTE, reverse?1*noise: -1*noise , 0, 0,
                TranslateAnimation.ABSOLUTE, reverse?-1*noise : noise);
        trans.setDuration(this.speed);

        TranslateAnimation trans2 = new TranslateAnimation(0, 0,
                TranslateAnimation.ABSOLUTE,reverse?-1*noise: noise, 0, 0,
                TranslateAnimation.ABSOLUTE, reverse?noise*2:-1*noise*2);
        trans2.setStartOffset(this.speed);
        trans2.setDuration(this.speed);



        TranslateAnimation trans3 = new TranslateAnimation(0, 0,
                TranslateAnimation.ABSOLUTE,reverse?-1*noise: noise, 0, 0,
                TranslateAnimation.ABSOLUTE,reverse?-2*noise: noise*2);
        trans3.setStartOffset(2* this.speed);
        trans3.setDuration(this.speed);


        TranslateAnimation trans4 = new TranslateAnimation(0, 0,
                TranslateAnimation.ABSOLUTE,reverse?noise: -1*noise, 0, 0,
                TranslateAnimation.ABSOLUTE,reverse?noise*1: -1*noise*1);
        trans4.setStartOffset(3* this.speed);
        trans4.setDuration(this.speed);


        // add new animations to the set
        animationSet.addAnimation(trans);
        animationSet.addAnimation(trans2);
        animationSet.addAnimation(trans3);
        animationSet.addAnimation(trans4);

        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animate(textView,noise);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        textView.startAnimation(animationSet);
        reverse = !reverse;
    }

}
