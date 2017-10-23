package com.wind.simonlikeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhangcong on 2017/10/20.
 * @author Simon
 */

public class SimonLikeView extends LinearLayout implements Animator.AnimatorListener{
    private LinearLayout likeAll,likeBack,disLikeAll,disLikeBack;
    private ImageView likeImageView,disLikeImageView;
    private AnimationDrawable animationLike,animationDislike;
    private TextView likeNumText,likeText,disLikeNumText,disLikeText;
    private float likeNum=80;
    private float disLikeNum=20;
    private ValueAnimator animationBack;
    private boolean isClose=false;//判断收起动画
    private int type=0;//选择执行帧动画的笑脸 //0 笑脸 1 哭脸
    public SimonLikeView(Context context) {
        super(context);
        init();
        bindListener();
    }

    public SimonLikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        bindListener();
        setVisibilities(GONE);
    }

    private void setVisibilities(int v) {
        likeText.setVisibility(v);
        likeNumText.setVisibility(v);
        disLikeText.setVisibility(v);
        disLikeNumText.setVisibility(v);
    }

    public SimonLikeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        bindListener();
    }

    public void setNum(float likeNum,float disLikeNum)
    {
        this.likeNum=likeNum;
        this.disLikeNum=disLikeNum;
    }

    /**
     * 绑定监听
     */
    private void bindListener() {
        likeBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type=0;
                scaleAnim();
                setVisibilities(VISIBLE);
                likeBack.setBackgroundResource(R.drawable.yellow_background);
                disLikeBack.setBackgroundResource(R.drawable.white_background);
                likeImageView.setImageResource(R.drawable.animation_like);
                animationLike= (AnimationDrawable) likeImageView.getDrawable();
                animationLike.start();
            }
        });
        disLikeBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                type=1;
                scaleAnim();
                setVisibilities(VISIBLE);
                disLikeBack.setBackgroundResource(R.drawable.yellow_background);
                likeBack.setBackgroundResource(R.drawable.white_background);
                disLikeImageView.setImageResource(R.drawable.animation_dislike);
                animationDislike= (AnimationDrawable) disLikeImageView.getDrawable();
                animationDislike.start();

            }
        });
    }



    /**
    *背景拉伸
     */
    private void scaleAnim() {
        float disLikePercent=disLikeNum/(disLikeNum+likeNum);
        float likePercent=(likeNum/(likeNum+disLikeNum));
        //计算点赞百分比*300
        final int likeMax=Math.round(likePercent*300);
        //计算差评百分比*300
        final int disLikeMax=Math.round(disLikePercent*300);

        int max=Math.max(likeMax,disLikeMax);
        /**
         * 本来想创建两个ValueAnimator对象，不过这种方法创建一个对象一样可以达到目的
         */
        animationBack=ValueAnimator.ofInt(0,max);
        animationBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int margin= (int) animation.getAnimatedValue();
                LayoutParams layoutParams= (LayoutParams) likeImageView.getLayoutParams();
                layoutParams.bottomMargin=margin;
                if (margin<=likeMax)
                {
                    likeImageView.setLayoutParams(layoutParams);
                }
                if (margin<=disLikeMax)
                {
                    disLikeImageView.setLayoutParams(layoutParams);
                }
                invalidate();
            }
        });
        likeBack.setClickable(false);
        disLikeBack.setClickable(false);
        isClose=false;
        animationBack.setDuration(1000);
        animationBack.start();
        animationBack.addListener(this);
    }

    private void init() {
        this.removeAllViews();

        setOrientation(HORIZONTAL);
        setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);

        //初始化文字--点赞
        likeText=new TextView(getContext());
        likeText.setText("喜欢");
        likeText.setTextSize(25);
        likeText.setTextColor(Color.WHITE);
        likeNumText=new TextView(getContext());

        likeNumText.setTextSize(23);
        likeNumText.setText(likeNum+"%");
        likeNumText.setTextColor(Color.WHITE);

        //初始化文字--无感
        disLikeText=new TextView(getContext());
        disLikeText.setText("无感");
        disLikeText.setTextSize(25);
        disLikeText.setTextColor(Color.WHITE);
        disLikeNumText=new TextView(getContext());
        disLikeNumText.setTextSize(23);
        disLikeNumText.setText(disLikeNum+"%");
        disLikeNumText.setTextColor(Color.WHITE);


        //差评线性布局
        disLikeImageView=new ImageView(getContext());
        disLikeImageView.setImageResource(R.drawable.dislike_1);
        disLikeBack=new LinearLayout(getContext());
        LayoutParams disLikeBackLP=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        disLikeBackLP.gravity=Gravity.BOTTOM;
        disLikeBack.addView(disLikeImageView,disLikeBackLP);
        disLikeBack.setBackgroundResource(R.drawable.white_background);
        disLikeBack.setOrientation(VERTICAL);


        //点赞线性布局
        likeImageView=new ImageView(getContext());
        likeImageView.setImageResource(R.drawable.like_1);
        LayoutParams likeBackLP=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        likeBackLP.gravity=Gravity.BOTTOM;
        likeBack=new LinearLayout(getContext());
        likeBack.addView(likeImageView,likeBackLP);
        likeBack.setBackgroundResource(R.drawable.white_background);
        likeBack.setOrientation(VERTICAL);


        //点赞总布局
        LayoutParams textLP=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLP.gravity=Gravity.CENTER_HORIZONTAL;
        likeAll =new LinearLayout(getContext());
        likeAll.setOrientation(VERTICAL);
        likeAll.addView(likeNumText,textLP);
        likeAll.addView(likeText,textLP);
        likeAll.addView(likeBack);


        //差评总布局
        disLikeAll=new LinearLayout(getContext());
        disLikeAll.setOrientation(VERTICAL);
        disLikeAll.addView(disLikeNumText,textLP);
        disLikeAll.addView(disLikeText,textLP);
        disLikeAll.addView(disLikeBack);



        //中间竖线
        ImageView centerLineImageView=new ImageView(getContext());
        centerLineImageView.setBackground(new ColorDrawable(Color.GRAY));
        LayoutParams centerLineLP=new LayoutParams(3,160);
        centerLineLP.setMargins(20, 10, 20, 50);
        centerLineLP.gravity= Gravity.BOTTOM;

        //初始化总布局
        LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin=50;
        addView(disLikeAll,lp);
        addView(centerLineImageView,centerLineLP);
        addView(likeAll,lp);


    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd();

            if (isClose)
            {

                setVisibilities(GONE);
                return;
            }
            isClose=true;
            if (type==0)
            {
                animationLike.stop();
                translationY(likeImageView);
            }
            if (type==1)
            {
                animationDislike.stop();
                translationX(disLikeImageView);
            }
        likeBack.setClickable(true);
        disLikeBack.setClickable(true);
    }

    /**
     * 差评补充属性动画
     * @param disLikeImageView
     */
    private void translationX(ImageView disLikeImageView) {
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(disLikeImageView,"translationX",-10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(1500);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setBackUpAnimation();
            }
        });
    }

    /**
     * 背景收回动画
     */
    private void setBackUpAnimation() {
        float disLikePercent=disLikeNum/(disLikeNum+likeNum);
        float likePercent=(likeNum/(likeNum+disLikeNum));
        //计算点赞百分比*300
        final int likeMax=Math.round(likePercent*300);
        //计算差评百分比*300
        final int disLikeMax=Math.round(disLikePercent*300);

        int max=Math.max(likeMax,disLikeMax);

        animationBack=ValueAnimator.ofInt(max,0);
        animationBack.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int margin = (int) animation.getAnimatedValue();
                LayoutParams lp= (LayoutParams) disLikeImageView.getLayoutParams();
                lp.height= ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.width= ViewGroup.LayoutParams.WRAP_CONTENT;
                lp.bottomMargin=margin;
                if (margin<likeMax)
                {
                    likeImageView.setLayoutParams(lp);
                }
                if (margin<disLikeMax)
                {

                    disLikeImageView.setLayoutParams(lp);
                }
                Log.i(">>>>>>>","margin"+margin);

                invalidate();
            }
        });
        animationBack.setDuration(500);
        animationBack.start();

    }

    /**
     * 点赞补充属性动画
     * @param likeImageView
     */
    private void translationY(ImageView likeImageView) {
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(likeImageView,"translationY",-10.0f, 0.0f, 10.0f, 0.0f, -10.0f, 0.0f, 10.0f, 0);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(1500);
        objectAnimator.start();
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                setBackUpAnimation();
            }
        });
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
