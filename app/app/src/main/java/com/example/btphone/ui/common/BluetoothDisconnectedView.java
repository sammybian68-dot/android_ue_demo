package com.example.btphone.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.btphone.databinding.ViewBluetoothDisconnectedBinding;

/**
 * 蓝牙未连接小卡自定义控件。
 * <p>
 * 当蓝牙未连接时显示，引导用户点击"添加蓝牙设备"进入蓝牙配对。
 * 包含左侧添加图标、中间文字、右侧设备图标。
 */
public class BluetoothDisconnectedView extends ConstraintLayout {

    private ViewBluetoothDisconnectedBinding mBinding;

    @Nullable
    private OnAddDeviceClickListener mListener;

    /**
     * 点击"添加蓝牙设备"卡片的回调。
     */
    public interface OnAddDeviceClickListener {

        /**
         * 用户点击卡片时调用。
         */
        void onAddDeviceClick();
    }

    public BluetoothDisconnectedView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BluetoothDisconnectedView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BluetoothDisconnectedView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mBinding = ViewBluetoothDisconnectedBinding.inflate(
                LayoutInflater.from(context), this, true);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAddDeviceClick();
                }
            }
        });
    }

    /**
     * 设置点击监听器。
     *
     * @param listener 监听器，可以为 null 以取消监听
     */
    public void setOnAddDeviceClickListener(@Nullable OnAddDeviceClickListener listener) {
        mListener = listener;
    }

    /**
     * 获取添加图标 ImageView（仅测试用）。
     *
     * @return 添加图标 ImageView
     */
    @VisibleForTesting
    @NonNull
    public ImageView getAddIconView() {
        return mBinding.ivBtAddIcon;
    }

    /**
     * 获取"添加蓝牙设备"文字 TextView（仅测试用）。
     *
     * @return 文字 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getAddDeviceTextView() {
        return mBinding.tvBtAddDevice;
    }

    /**
     * 获取设备图标 ImageView（仅测试用）。
     *
     * @return 设备图标 ImageView
     */
    @VisibleForTesting
    @NonNull
    public ImageView getDeviceIconView() {
        return mBinding.ivBtDeviceIcon;
    }
}
