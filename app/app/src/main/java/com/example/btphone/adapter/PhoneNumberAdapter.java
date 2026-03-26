package com.example.btphone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.databinding.ItemPhoneNumberBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 电话号码列表适配器。
 * <p>
 * 用于联系人详情弹窗中显示一个或多个电话号码，每项可点击触发拨号回调。
 */
public class PhoneNumberAdapter extends RecyclerView.Adapter<PhoneNumberAdapter.ViewHolder> {

    private final List<String> mPhoneNumbers = new ArrayList<>();

    @Nullable
    private OnPhoneNumberClickListener mListener;

    /**
     * 电话号码点击回调接口。
     */
    public interface OnPhoneNumberClickListener {

        /**
         * 当用户点击某条电话号码时调用。
         *
         * @param phoneNumber 被点击的电话号码
         * @param position    列表中的位置
         */
        void onPhoneNumberClick(@NonNull String phoneNumber, int position);
    }

    /**
     * 创建适配器实例。
     *
     * @param phoneNumbers 电话号码列表，不能为 null
     */
    public PhoneNumberAdapter(@NonNull List<String> phoneNumbers) {
        mPhoneNumbers.addAll(phoneNumbers);
    }

    /**
     * 设置电话号码点击回调。
     *
     * @param listener 回调接口，可以为 null 表示不处理点击
     */
    public void setOnPhoneNumberClickListener(@Nullable OnPhoneNumberClickListener listener) {
        mListener = listener;
    }

    /**
     * 更新电话号码列表数据。
     *
     * @param phoneNumbers 新的电话号码列表，不能为 null
     */
    public void setPhoneNumbers(@NonNull List<String> phoneNumbers) {
        mPhoneNumbers.clear();
        mPhoneNumbers.addAll(phoneNumbers);
        notifyDataSetChanged();
    }

    /**
     * 获取指定位置的电话号码。
     *
     * @param position 列表位置
     * @return 电话号码字符串
     * @throws IndexOutOfBoundsException 如果 position 超出范围
     */
    @NonNull
    public String getItem(int position) {
        return mPhoneNumbers.get(position);
    }

    /**
     * 获取号码列表的只读副本（仅用于测试）。
     *
     * @return 号码列表的不可变副本
     */
    @VisibleForTesting
    @NonNull
    List<String> getPhoneNumbers() {
        return Collections.unmodifiableList(mPhoneNumbers);
    }

    /**
     * 获取当前设置的点击回调（仅用于测试）。
     *
     * @return 当前的点击回调，可能为 null
     */
    @VisibleForTesting
    @Nullable
    OnPhoneNumberClickListener getListener() {
        return mListener;
    }

    @Override
    public int getItemCount() {
        return mPhoneNumbers.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPhoneNumberBinding binding = ItemPhoneNumberBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String phoneNumber = mPhoneNumbers.get(position);
        holder.bind(phoneNumber, position);
        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                int adapterPos = holder.getBindingAdapterPosition();
                int effectivePos = adapterPos != RecyclerView.NO_POSITION
                        ? adapterPos : holder.mBoundPosition;
                if (effectivePos >= 0 && effectivePos < mPhoneNumbers.size()) {
                    mListener.onPhoneNumberClick(mPhoneNumbers.get(effectivePos), effectivePos);
                }
            }
        });
    }

    /**
     * 电话号码项 ViewHolder。
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPhoneNumberBinding mBinding;
        int mBoundPosition = RecyclerView.NO_POSITION;

        ViewHolder(@NonNull ItemPhoneNumberBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * 绑定电话号码到视图。
         *
         * @param phoneNumber 电话号码
         * @param position    绑定的列表位置
         */
        void bind(@NonNull String phoneNumber, int position) {
            mBinding.tvPhoneNumber.setText(phoneNumber);
            mBoundPosition = position;
        }
    }
}
