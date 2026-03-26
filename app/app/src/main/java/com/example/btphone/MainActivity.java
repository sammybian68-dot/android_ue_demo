package com.example.btphone;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.btphone.databinding.ActivityMainBinding;
import com.example.btphone.ui.contacts.ContactsListFragment;
import com.example.btphone.ui.dialer.DialerFragment;

/**
 * 蓝牙电话主 Activity。
 * <p>
 * 承载公共外壳（蓝牙图标 + 设备名 + 通话/联系人 Tab 切换栏）以及
 * 内容区 Fragment 容器。
 */
public class MainActivity extends AppCompatActivity {

    /** 通话 Tab 索引 */
    public static final int TAB_CALL = 0;

    /** 联系人 Tab 索引 */
    public static final int TAB_CONTACTS = 1;

    private ActivityMainBinding mBinding;
    private int mCurrentTab = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setupTabs();
        selectTab(TAB_CONTACTS);
    }

    /**
     * 注册 Tab 点击事件。
     */
    private void setupTabs() {
        mBinding.tvTabCall.setOnClickListener(v -> selectTab(TAB_CALL));
        mBinding.tvTabContacts.setOnClickListener(v -> selectTab(TAB_CONTACTS));
    }

    /**
     * 切换到指定 Tab，更新文字颜色和指示线位置。
     *
     * @param tab 目标 Tab 索引，{@link #TAB_CALL} 或 {@link #TAB_CONTACTS}
     */
    @VisibleForTesting
    void selectTab(int tab) {
        if (tab != TAB_CALL && tab != TAB_CONTACTS) {
            return;
        }
        if (tab == mCurrentTab) {
            return;
        }
        mCurrentTab = tab;
        updateTabStyles(tab);
        updateTabIndicator(tab);
        switchFragment(tab);
    }

    /**
     * 更新 Tab 文字颜色：选中态 {@code @color/tab_active}，未选中 {@code @color/tab_inactive}。
     *
     * @param tab 当前选中的 Tab 索引
     */
    private void updateTabStyles(int tab) {
        int activeColor = ContextCompat.getColor(this, R.color.tab_active);
        int inactiveColor = ContextCompat.getColor(this, R.color.tab_inactive);

        mBinding.tvTabCall.setTextColor(tab == TAB_CALL ? activeColor : inactiveColor);
        mBinding.tvTabContacts.setTextColor(tab == TAB_CONTACTS ? activeColor : inactiveColor);
    }

    /**
     * 移动 Tab 选中指示线到对应位置。
     * <p>
     * 通话 Tab 指示线 marginStart = {@code @dimen/tab_indicator_call_margin_start}，
     * 联系人 Tab 指示线 marginStart = {@code @dimen/tab_indicator_contacts_margin_start}。
     *
     * @param tab 当前选中的 Tab 索引
     */
    private void updateTabIndicator(int tab) {
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) mBinding.ivTabIndicator.getLayoutParams();
        if (tab == TAB_CALL) {
            params.leftMargin = getResources()
                    .getDimensionPixelSize(R.dimen.tab_indicator_call_margin_start);
        } else {
            params.leftMargin = getResources()
                    .getDimensionPixelSize(R.dimen.tab_indicator_contacts_margin_start);
        }
        mBinding.ivTabIndicator.setLayoutParams(params);
    }

    /**
     * 切换 Fragment 容器中显示的 Fragment。
     * <p>
     * 当对应模块的 Fragment 尚未实现时，仅切换 Tab 样式而不替换 Fragment。
     *
     * @param tab 目标 Tab 索引
     */
    private void switchFragment(int tab) {
        Fragment fragment = getFragmentForTab(tab);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    /**
     * 根据 Tab 索引获取对应的 Fragment 实例。
     * <p>
     * 子模块 Fragment 尚未实现时返回 {@code null}。
     *
     * @param tab Tab 索引
     * @return Fragment 实例，可能为 null
     */
    @Nullable
    private Fragment getFragmentForTab(int tab) {
        if (tab == TAB_CALL) {
            return new DialerFragment();
        } else if (tab == TAB_CONTACTS) {
            return new ContactsListFragment();
        }
        return null;
    }

    /**
     * 获取当前选中的 Tab 索引。
     *
     * @return 当前 Tab 索引
     */
    @VisibleForTesting
    int getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * 设置蓝牙设备名称。
     *
     * @param name 设备名称，不能为 null
     */
    public void setDeviceName(@NonNull String name) {
        mBinding.tvDeviceName.setText(name);
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return ActivityMainBinding 实例
     */
    @VisibleForTesting
    ActivityMainBinding getBinding() {
        return mBinding;
    }
}
