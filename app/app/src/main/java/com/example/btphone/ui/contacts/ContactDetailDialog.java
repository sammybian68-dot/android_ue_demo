package com.example.btphone.ui.contacts;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btphone.R;
import com.example.btphone.adapter.PhoneNumberAdapter;
import com.example.btphone.databinding.DialogContactDetailBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人详情弹窗。
 * <p>
 * 点击联系人后弹出，居中显示大头像、姓名和电话号码列表。
 * 号码项可点击触发拨号操作，支持 1 条、2 条及多条号码，多条时列表可滚动。
 */
public class ContactDetailDialog extends DialogFragment {

    /** Fragment tag for FragmentManager transactions. */
    public static final String TAG = "ContactDetailDialog";

    private static final String ARG_NAME = "arg_name";
    private static final String ARG_AVATAR_TEXT = "arg_avatar_text";
    private static final String ARG_AVATAR_COLOR_INDEX = "arg_avatar_color_index";
    private static final String ARG_PHONE_NUMBERS = "arg_phone_numbers";

    private static final int[] BIG_AVATAR_DRAWABLES = {
            R.drawable.icon_linkman_bg_big_1,
            R.drawable.icon_linkman_bg_big_2,
            R.drawable.icon_linkman_bg_big_3_1,
            R.drawable.icon_linkman_bg_big_4
    };

    private DialogContactDetailBinding mBinding;
    private PhoneNumberAdapter mAdapter;

    @Nullable
    private PhoneNumberAdapter.OnPhoneNumberClickListener mPhoneNumberClickListener;

    /**
     * 创建联系人详情弹窗实例。
     *
     * @param name             联系人姓名
     * @param avatarText       头像显示文字（通常为姓名末字）
     * @param avatarColorIndex 头像背景颜色索引（0-3）
     * @param phoneNumbers     电话号码列表
     * @return 新的 ContactDetailDialog 实例
     */
    @NonNull
    public static ContactDetailDialog newInstance(@NonNull String name,
                                                  @NonNull String avatarText,
                                                  int avatarColorIndex,
                                                  @NonNull List<String> phoneNumbers) {
        ContactDetailDialog dialog = new ContactDetailDialog();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_AVATAR_TEXT, avatarText);
        args.putInt(ARG_AVATAR_COLOR_INDEX, avatarColorIndex);
        args.putStringArrayList(ARG_PHONE_NUMBERS, new ArrayList<>(phoneNumbers));
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * 设置电话号码点击回调。
     *
     * @param listener 点击回调，传入 null 清除
     */
    public void setOnPhoneNumberClickListener(
            @Nullable PhoneNumberAdapter.OnPhoneNumberClickListener listener) {
        mPhoneNumberClickListener = listener;
        if (mAdapter != null) {
            mAdapter.setOnPhoneNumberClickListener(listener);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_BtPhone_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DialogContactDetailBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindContactInfo();
        setupPhoneList();
    }

    @Override
    public void onStart() {
        super.onStart();
        configureWindow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /**
     * 获取当前适配器（用于测试）。
     *
     * @return PhoneNumberAdapter 实例，视图未创建时为 null
     */
    @Nullable
    public PhoneNumberAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 从 arguments 读取联系人姓名。
     *
     * @return 联系人姓名
     */
    @NonNull
    @VisibleForTesting
    String getContactName() {
        Bundle args = getArguments();
        return args != null ? args.getString(ARG_NAME, "") : "";
    }

    /**
     * 从 arguments 读取头像文字。
     *
     * @return 头像文字
     */
    @NonNull
    @VisibleForTesting
    String getAvatarText() {
        Bundle args = getArguments();
        return args != null ? args.getString(ARG_AVATAR_TEXT, "") : "";
    }

    /**
     * 从 arguments 读取头像颜色索引。
     *
     * @return 头像颜色索引 0-3
     */
    @VisibleForTesting
    int getAvatarColorIndex() {
        Bundle args = getArguments();
        return args != null ? args.getInt(ARG_AVATAR_COLOR_INDEX, 0) : 0;
    }

    /**
     * 从 arguments 读取电话号码列表。
     *
     * @return 电话号码列表
     */
    @NonNull
    @VisibleForTesting
    List<String> getPhoneNumbers() {
        Bundle args = getArguments();
        if (args != null) {
            ArrayList<String> list = args.getStringArrayList(ARG_PHONE_NUMBERS);
            if (list != null) {
                return list;
            }
        }
        return new ArrayList<>();
    }

    private void bindContactInfo() {
        String name = getContactName();
        String avatarText = getAvatarText();
        int colorIndex = getAvatarColorIndex();

        mBinding.tvAvatar.setText(avatarText);
        int safeIndex = Math.abs(colorIndex) % BIG_AVATAR_DRAWABLES.length;
        mBinding.tvAvatar.setBackgroundResource(BIG_AVATAR_DRAWABLES[safeIndex]);

        mBinding.tvName.setText(name);
    }

    private void setupPhoneList() {
        List<String> phoneNumbers = getPhoneNumbers();
        mAdapter = new PhoneNumberAdapter(phoneNumbers);
        if (mPhoneNumberClickListener != null) {
            mAdapter.setOnPhoneNumberClickListener(mPhoneNumberClickListener);
        }
        mBinding.rvPhoneNumbers.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.rvPhoneNumbers.setAdapter(mAdapter);
    }

    private void configureWindow() {
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.contact_detail_dialog_width);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }
}
