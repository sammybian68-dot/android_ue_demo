package com.example.btphone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.databinding.ItemCallLogBinding;
import com.example.btphone.model.CallLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 通话记录列表适配器。
 * <p>
 * 根据通话类型设置不同的图标和文字颜色：
 * <ul>
 *     <li>未接来电：红色图标 + 红色文字</li>
 *     <li>已接来电：绿色图标 + 白色文字</li>
 *     <li>呼出：蓝色图标 + 白色文字</li>
 *     <li>通用：灰色图标 + 白色文字</li>
 * </ul>
 */
public class CallLogAdapter extends RecyclerView.Adapter<CallLogAdapter.ViewHolder> {

    @NonNull
    private List<CallLog> mItems = new ArrayList<>();

    /**
     * 设置通话记录数据并刷新列表。
     *
     * @param items 通话记录列表，不能为 null
     */
    public void setItems(@NonNull List<CallLog> items) {
        mItems = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    /**
     * 获取当前数据列表。
     *
     * @return 当前通话记录列表
     */
    @NonNull
    public List<CallLog> getItems() {
        return mItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCallLogBinding binding = ItemCallLogBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * 通话记录列表项 ViewHolder。
     */
    static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemCallLogBinding mBinding;

        ViewHolder(@NonNull ItemCallLogBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * 绑定通话记录数据到视图。
         *
         * @param callLog 通话记录数据
         */
        void bind(@NonNull CallLog callLog) {
            mBinding.tvNumber.setText(callLog.getDisplayText());
            mBinding.tvDate.setText(callLog.getDate());

            int iconRes = getCallTypeIcon(callLog.getCallType());
            mBinding.ivCallType.setImageResource(iconRes);

            int textColorRes = callLog.isMissed()
                    ? R.color.call_log_missed_text
                    : R.color.call_log_normal_text;
            mBinding.tvNumber.setTextColor(
                    ContextCompat.getColor(itemView.getContext(), textColorRes));

            mBinding.ivCallType.setContentDescription(
                    getCallTypeDescription(callLog.getCallType()));
        }

        /**
         * 根据通话类型获取对应的图标资源 ID。
         *
         * @param callType 通话类型
         * @return 图标资源 ID
         */
        private int getCallTypeIcon(int callType) {
            switch (callType) {
                case CallLog.TYPE_MISSED:
                    return R.drawable.icon_list_phone_miss;
                case CallLog.TYPE_INCOMING:
                    return R.drawable.icon_list_phone_in;
                case CallLog.TYPE_OUTGOING:
                    return R.drawable.icon_list_phone_out;
                default:
                    return R.drawable.icon_list_phone_1;
            }
        }

        /**
         * 根据通话类型获取无障碍描述文本。
         *
         * @param callType 通话类型
         * @return 描述文本
         */
        @NonNull
        private String getCallTypeDescription(int callType) {
            switch (callType) {
                case CallLog.TYPE_MISSED:
                    return itemView.getContext().getString(R.string.call_type_missed_desc);
                case CallLog.TYPE_INCOMING:
                    return itemView.getContext().getString(R.string.call_type_incoming_desc);
                case CallLog.TYPE_OUTGOING:
                    return itemView.getContext().getString(R.string.call_type_outgoing_desc);
                default:
                    return itemView.getContext().getString(R.string.call_type_generic_desc);
            }
        }
    }
}
