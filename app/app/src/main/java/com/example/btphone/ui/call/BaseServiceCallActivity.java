package com.example.btphone.ui.call;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

/**
 * I-Call / E-Call 共用的服务呼叫基类。
 * <p>
 * 封装了全屏与小窗模式的切换逻辑，以及通话计时器。
 * 子类需要实现具体的 UI 绑定和状态更新回调。
 */
public abstract class BaseServiceCallActivity extends AppCompatActivity {

    /** 呼叫状态：拨号中。 */
    public static final int STATE_DIALING = 0;

    /** 呼叫状态：通话中。 */
    public static final int STATE_IN_CALL = 1;

    /** 显示模式：全屏。 */
    public static final int MODE_FULLSCREEN = 0;

    /** 显示模式：小窗。 */
    public static final int MODE_MINI = 1;

    private static final long TIMER_INTERVAL_MS = 1000L;

    private int mCallState = STATE_DIALING;
    private int mDisplayMode = MODE_FULLSCREEN;
    private int mElapsedSeconds = 0;

    private Handler mTimerHandler;
    private Runnable mTimerRunnable;
    private boolean mTimerRunning = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTimerHandler = new Handler(Looper.getMainLooper());
        mTimerRunnable = () -> {
            mElapsedSeconds++;
            onTimerTick(formatTime(mElapsedSeconds));
            mTimerHandler.postDelayed(mTimerRunnable, TIMER_INTERVAL_MS);
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    /**
     * 切换到全屏模式。
     * <p>
     * 显示全屏视图组，隐藏小窗。子类需提供各自视图的引用。
     */
    @VisibleForTesting
    void showFullScreen() {
        mDisplayMode = MODE_FULLSCREEN;
        getFullscreenGroup().setVisibility(View.VISIBLE);
        getMiniRoot().setVisibility(View.GONE);
        onDisplayModeChanged(MODE_FULLSCREEN);
    }

    /**
     * 切换到小窗模式。
     * <p>
     * 隐藏全屏视图组，显示小窗。
     */
    @VisibleForTesting
    void showMiniWindow() {
        mDisplayMode = MODE_MINI;
        getFullscreenGroup().setVisibility(View.GONE);
        getMiniRoot().setVisibility(View.VISIBLE);
        onDisplayModeChanged(MODE_MINI);
    }

    /**
     * 将呼叫状态切换为通话中并启动计时器。
     */
    @VisibleForTesting
    void switchToInCall() {
        mCallState = STATE_IN_CALL;
        mElapsedSeconds = 0;
        onCallStateChanged(STATE_IN_CALL);
        startTimer();
    }

    /**
     * 执行挂断操作，停止计时器并结束 Activity。
     */
    @VisibleForTesting
    void hangUp() {
        stopTimer();
        finish();
    }

    /**
     * 启动通话计时器。
     */
    @VisibleForTesting
    void startTimer() {
        if (!mTimerRunning) {
            mTimerRunning = true;
            mTimerHandler.postDelayed(mTimerRunnable, TIMER_INTERVAL_MS);
        }
    }

    /**
     * 停止通话计时器。
     */
    @VisibleForTesting
    void stopTimer() {
        mTimerRunning = false;
        mTimerHandler.removeCallbacks(mTimerRunnable);
    }

    /**
     * 格式化通话时长为 mm:ss 形式。
     *
     * @param totalSeconds 总秒数
     * @return 格式化后的时间字符串
     */
    @VisibleForTesting
    @NonNull
    static String formatTime(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    /**
     * 获取当前呼叫状态。
     *
     * @return {@link #STATE_DIALING} 或 {@link #STATE_IN_CALL}
     */
    @VisibleForTesting
    int getCallState() {
        return mCallState;
    }

    /**
     * 获取当前显示模式。
     *
     * @return {@link #MODE_FULLSCREEN} 或 {@link #MODE_MINI}
     */
    @VisibleForTesting
    int getDisplayMode() {
        return mDisplayMode;
    }

    /**
     * 获取已流逝的通话秒数。
     *
     * @return 秒数
     */
    @VisibleForTesting
    int getElapsedSeconds() {
        return mElapsedSeconds;
    }

    /**
     * 计时器是否正在运行。
     *
     * @return {@code true} 表示运行中
     */
    @VisibleForTesting
    boolean isTimerRunning() {
        return mTimerRunning;
    }

    /**
     * 返回全屏视图 Group，由子类提供。
     *
     * @return 全屏模式下需要控制可见性的 Group 或容器 View
     */
    @NonNull
    protected abstract View getFullscreenGroup();

    /**
     * 返回小窗根视图，由子类提供。
     *
     * @return 小窗根 View
     */
    @NonNull
    protected abstract View getMiniRoot();

    /**
     * 当显示模式切换时回调，子类可在此更新 UI 细节。
     *
     * @param mode 新的显示模式
     */
    protected abstract void onDisplayModeChanged(int mode);

    /**
     * 当呼叫状态变化时回调。
     *
     * @param state 新的呼叫状态
     */
    protected abstract void onCallStateChanged(int state);

    /**
     * 计时器每秒回调。
     *
     * @param formattedTime 格式化后的时间字符串（mm:ss）
     */
    protected abstract void onTimerTick(@NonNull String formattedTime);
}
