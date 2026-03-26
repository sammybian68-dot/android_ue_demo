package com.example.btphone.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.VisibleForTesting;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.btphone.R;
import com.example.btphone.databinding.ViewEmptyStateBinding;

/**
 * 可复用的空状态自定义控件。
 * <p>
 * 显示一个居中的图标、标题、提示文字、"了解更多"链接和"同步"按钮。
 * 图标和标题支持外部配置，适用于"没有通话记录"和"没有联系人"两种场景。
 */
public class EmptyStateView extends ConstraintLayout {

    private ViewEmptyStateBinding mBinding;

    @Nullable
    private OnSyncClickListener mSyncListener;

    @Nullable
    private OnLearnMoreClickListener mLearnMoreListener;

    /**
     * "同步"按钮点击回调。
     */
    public interface OnSyncClickListener {

        /**
         * 用户点击"同步"按钮时调用。
         */
        void onSyncClick();
    }

    /**
     * "了解更多"链接点击回调。
     */
    public interface OnLearnMoreClickListener {

        /**
         * 用户点击"了解更多"链接时调用。
         */
        void onLearnMoreClick();
    }

    public EmptyStateView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmptyStateView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context context) {
        mBinding = ViewEmptyStateBinding.inflate(LayoutInflater.from(context), this, true);

        mBinding.btnEmptySync.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSyncListener != null) {
                    mSyncListener.onSyncClick();
                }
            }
        });

        mBinding.tvEmptyLearnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLearnMoreListener != null) {
                    mLearnMoreListener.onLearnMoreClick();
                }
            }
        });
    }

    /**
     * 设置空状态图标。
     *
     * @param drawableRes 图标 drawable 资源 ID
     */
    public void setIcon(@DrawableRes int drawableRes) {
        mBinding.ivEmptyIcon.setImageResource(drawableRes);
    }

    /**
     * 设置空状态标题文字。
     *
     * @param stringRes 标题字符串资源 ID
     */
    public void setTitle(@StringRes int stringRes) {
        mBinding.tvEmptyTitle.setText(stringRes);
    }

    /**
     * 设置空状态标题文字。
     *
     * @param title 标题字符串
     */
    public void setTitle(@NonNull CharSequence title) {
        mBinding.tvEmptyTitle.setText(title);
    }

    /**
     * 设置"同步"按钮点击监听器。
     *
     * @param listener 监听器，可以为 null 以取消监听
     */
    public void setOnSyncClickListener(@Nullable OnSyncClickListener listener) {
        mSyncListener = listener;
    }

    /**
     * 设置"了解更多"链接点击监听器。
     *
     * @param listener 监听器，可以为 null 以取消监听
     */
    public void setOnLearnMoreClickListener(@Nullable OnLearnMoreClickListener listener) {
        mLearnMoreListener = listener;
    }

    /**
     * 获取空状态图标 ImageView（仅测试用）。
     *
     * @return 图标 ImageView
     */
    @VisibleForTesting
    @NonNull
    public ImageView getIconView() {
        return mBinding.ivEmptyIcon;
    }

    /**
     * 获取标题 TextView（仅测试用）。
     *
     * @return 标题 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getTitleView() {
        return mBinding.tvEmptyTitle;
    }

    /**
     * 获取提示行 1 的 TextView（仅测试用）。
     *
     * @return 提示行 1 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getHint1View() {
        return mBinding.tvEmptyHint1;
    }

    /**
     * 获取提示行 2 的 TextView（仅测试用）。
     *
     * @return 提示行 2 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getHint2View() {
        return mBinding.tvEmptyHint2;
    }

    /**
     * 获取"了解更多"链接 TextView（仅测试用）。
     *
     * @return "了解更多" TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getLearnMoreView() {
        return mBinding.tvEmptyLearnMore;
    }

    /**
     * 获取"同步"按钮 ImageView（仅测试用）。
     *
     * @return "同步"按钮 ImageView
     */
    @VisibleForTesting
    @NonNull
    public ImageView getSyncButton() {
        return mBinding.btnEmptySync;
    }

    /**
     * 获取"同步"按钮文字标签 TextView（仅测试用）。
     *
     * @return "同步"文字 TextView
     */
    @VisibleForTesting
    @NonNull
    public TextView getSyncLabelView() {
        return mBinding.tvEmptySyncLabel;
    }
}
