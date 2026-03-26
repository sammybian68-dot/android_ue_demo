package com.example.btphone.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.btphone.databinding.ViewLoadingStateBinding;

/**
 * 可复用的加载态自定义控件。
 * <p>
 * 显示一个居中的旋转加载图标和"正在同步…"文字。
 * 在 {@link #onAttachedToWindow()} 时自动启动旋转动画，
 * {@link #onDetachedFromWindow()} 时自动停止。
 */
public class LoadingStateView extends ConstraintLayout {

    private static final long ROTATION_DURATION_MS = 1000L;

    private ViewLoadingStateBinding mBinding;

    @Nullable
    private RotateAnimation mRotateAnimation;

    public LoadingStateView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public LoadingStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mBinding = ViewLoadingStateBinding.inflate(LayoutInflater.from(context), this, true);
        mRotateAnimation = createRotateAnimation();
    }

    /**
     * 创建无限循环的旋转动画。
     *
     * @return 旋转动画实例
     */
    @NonNull
    @VisibleForTesting
    RotateAnimation createRotateAnimation() {
        RotateAnimation rotate = new RotateAnimation(
                0f, 360f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(ROTATION_DURATION_MS);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        return rotate;
    }

    /**
     * 启动加载动画。
     */
    public void startLoading() {
        if (mRotateAnimation != null) {
            mBinding.ivLoadingIcon.startAnimation(mRotateAnimation);
        }
    }

    /**
     * 停止加载动画。
     */
    public void stopLoading() {
        mBinding.ivLoadingIcon.clearAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startLoading();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopLoading();
        super.onDetachedFromWindow();
    }

    /**
     * 获取加载图标 ImageView（仅测试用）。
     *
     * @return 加载图标 ImageView
     */
    @VisibleForTesting
    @NonNull
    public ImageView getLoadingIconView() {
        return mBinding.ivLoadingIcon;
    }

    /**
     * 获取加载文字 TextView（仅测试用）。
     *
     * @return 加载文字 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getLoadingTextView() {
        return mBinding.tvLoadingText;
    }
}
