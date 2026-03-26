package com.example.btphone.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.databinding.ItemContactBinding;
import com.example.btphone.databinding.ItemContactHeaderBinding;
import com.example.btphone.model.Contact;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人列表适配器。
 * <p>
 * 支持按拼音首字母分组显示，包含两种 ViewType：分组标题和联系人条目。
 * 配合 GridLayoutManager 使用时，分组标题跨满整行。
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** 分组标题类型 */
    public static final int TYPE_HEADER = 0;
    /** 联系人条目类型 */
    public static final int TYPE_CONTACT = 1;

    private static final int[] AVATAR_DRAWABLES = {
            R.drawable.icon_linkman_bg_1,
            R.drawable.icon_linkman_bg_2,
            R.drawable.icon_linkman_bg_3,
            R.drawable.icon_linkman_bg_4
    };

    private final List<Object> mItems = new ArrayList<>();
    private final Map<String, Integer> mLetterPositionMap = new LinkedHashMap<>();

    /**
     * 设置联系人数据，自动按拼音首字母插入分组标题。
     *
     * @param contacts 已按拼音排序的联系人列表，不能为 null
     */
    public void setContacts(@NonNull List<Contact> contacts) {
        mItems.clear();
        mLetterPositionMap.clear();

        String lastLetter = "";
        for (Contact contact : contacts) {
            String letter = contact.getSortLetter();
            if (!letter.equals(lastLetter)) {
                mLetterPositionMap.put(letter, mItems.size());
                mItems.add(letter);
                lastLetter = letter;
            }
            mItems.add(contact);
        }
        notifyDataSetChanged();
    }

    /**
     * 获取指定字母对应的分组标题在列表中的位置。
     *
     * @param letter 大写字母 A-Z 或 "#"
     * @return 列表位置，如果不存在返回 -1
     */
    public int getPositionForLetter(@NonNull String letter) {
        Integer position = mLetterPositionMap.get(letter);
        return position != null ? position : -1;
    }

    /**
     * 获取所有有数据的分组字母集合。
     *
     * @return 字母到位置的映射
     */
    @NonNull
    public Map<String, Integer> getLetterPositionMap() {
        return mLetterPositionMap;
    }

    /**
     * 获取内部数据列表（仅用于测试）。
     *
     * @return 包含标题字符串和 Contact 对象的列表
     */
    @VisibleForTesting
    @NonNull
    List<Object> getItems() {
        return mItems;
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position) instanceof String ? TYPE_HEADER : TYPE_CONTACT;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            ItemContactHeaderBinding binding =
                    ItemContactHeaderBinding.inflate(inflater, parent, false);
            return new HeaderViewHolder(binding);
        } else {
            ItemContactBinding binding =
                    ItemContactBinding.inflate(inflater, parent, false);
            return new ContactViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            String letter = (String) mItems.get(position);
            ((HeaderViewHolder) holder).bind(letter, position == 0);
        } else if (holder instanceof ContactViewHolder) {
            Contact contact = (Contact) mItems.get(position);
            ((ContactViewHolder) holder).bind(contact);
        }
    }

    /**
     * 分组标题 ViewHolder。
     */
    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final ItemContactHeaderBinding mBinding;

        HeaderViewHolder(@NonNull ItemContactHeaderBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * 绑定分组标题数据。
         *
         * @param letter  分组字母
         * @param isFirst 是否为第一个分组（无顶部间距）
         */
        void bind(@NonNull String letter, boolean isFirst) {
            mBinding.tvSectionLetter.setText(letter);
            int topPadding = isFirst ? 0 : mBinding.getRoot().getResources()
                    .getDimensionPixelSize(R.dimen.section_header_margin_top);
            mBinding.tvSectionLetter.setPadding(
                    mBinding.tvSectionLetter.getPaddingLeft(),
                    topPadding,
                    mBinding.tvSectionLetter.getPaddingRight(),
                    mBinding.tvSectionLetter.getPaddingBottom());
        }
    }

    /**
     * 联系人条目 ViewHolder。
     */
    static class ContactViewHolder extends RecyclerView.ViewHolder {

        private final ItemContactBinding mBinding;

        ContactViewHolder(@NonNull ItemContactBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * 绑定联系人数据到视图。
         *
         * @param contact 联系人对象
         */
        void bind(@NonNull Contact contact) {
            mBinding.tvAvatar.setText(contact.getAvatarText());
            int colorIndex = Math.abs(contact.getAvatarColorIndex()) % AVATAR_DRAWABLES.length;
            mBinding.tvAvatar.setBackgroundResource(AVATAR_DRAWABLES[colorIndex]);
            mBinding.tvName.setText(contact.getName());
        }
    }
}
