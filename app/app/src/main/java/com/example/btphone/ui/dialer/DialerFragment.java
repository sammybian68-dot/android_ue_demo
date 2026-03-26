package com.example.btphone.ui.dialer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btphone.adapter.CallLogAdapter;
import com.example.btphone.databinding.FragmentDialerBinding;
import com.example.btphone.model.CallLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 拨号与通话记录 Fragment。
 * <p>
 * 左侧显示通话记录列表，右侧显示 3×4+2 拨号键盘。
 * 输入号码时对联系人进行模糊搜索，右侧列表切换为匹配结果。
 */
public class DialerFragment extends Fragment {

    private FragmentDialerBinding mBinding;
    private CallLogAdapter mAdapter;
    private final StringBuilder mDialInput = new StringBuilder();

    @NonNull
    private List<CallLog> mCallLogs = new ArrayList<>();

    @NonNull
    private List<CallLog> mContacts = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentDialerBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupKeyboard();
        loadMockData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /**
     * 初始化通话记录列表 RecyclerView。
     */
    private void setupRecyclerView() {
        mAdapter = new CallLogAdapter();
        mBinding.rvCallLog.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.rvCallLog.setAdapter(mAdapter);
    }

    /**
     * 注册拨号键盘按钮点击事件。
     */
    private void setupKeyboard() {
        mBinding.btn1.setOnClickListener(v -> onDialKeyPressed("1"));
        mBinding.btn2.setOnClickListener(v -> onDialKeyPressed("2"));
        mBinding.btn3.setOnClickListener(v -> onDialKeyPressed("3"));
        mBinding.btn4.setOnClickListener(v -> onDialKeyPressed("4"));
        mBinding.btn5.setOnClickListener(v -> onDialKeyPressed("5"));
        mBinding.btn6.setOnClickListener(v -> onDialKeyPressed("6"));
        mBinding.btn7.setOnClickListener(v -> onDialKeyPressed("7"));
        mBinding.btn8.setOnClickListener(v -> onDialKeyPressed("8"));
        mBinding.btn9.setOnClickListener(v -> onDialKeyPressed("9"));
        mBinding.btn0.setOnClickListener(v -> onDialKeyPressed("0"));
        mBinding.btnStar.setOnClickListener(v -> onDialKeyPressed("*"));
        mBinding.btnHash.setOnClickListener(v -> onDialKeyPressed("#"));
        mBinding.btnDel.setOnClickListener(v -> onDeletePressed());
        mBinding.btnDel.setOnLongClickListener(v -> {
            onClearAll();
            return true;
        });
        mBinding.btnCall.setOnClickListener(v -> onCallPressed());
    }

    /**
     * 加载 mock 通话记录和联系人数据。
     */
    @VisibleForTesting
    void loadMockData() {
        mCallLogs = createMockCallLogs();
        mContacts = createMockContacts();
        mAdapter.setItems(mCallLogs);
        updateEmptyState();
    }

    /**
     * 处理拨号键按下事件。
     *
     * @param digit 输入的字符
     */
    @VisibleForTesting
    void onDialKeyPressed(@NonNull String digit) {
        mDialInput.append(digit);
        updateNumberDisplay();
        performSearch();
    }

    /**
     * 处理删除键按下事件，删除最后一个字符。
     */
    @VisibleForTesting
    void onDeletePressed() {
        if (mDialInput.length() > 0) {
            mDialInput.deleteCharAt(mDialInput.length() - 1);
            updateNumberDisplay();
            performSearch();
        }
    }

    /**
     * 清除全部输入并恢复通话记录列表。
     */
    @VisibleForTesting
    void onClearAll() {
        mDialInput.setLength(0);
        updateNumberDisplay();
        mAdapter.setItems(mCallLogs);
        updateEmptyState();
    }

    /**
     * 处理拨打按钮点击事件（当前为 mock 实现，仅清空输入）。
     */
    @VisibleForTesting
    void onCallPressed() {
        if (mDialInput.length() > 0) {
            mDialInput.setLength(0);
            updateNumberDisplay();
            mAdapter.setItems(mCallLogs);
            updateEmptyState();
        }
    }

    /**
     * 更新号码显示区域的文本，每 4 位插入空格方便阅读。
     */
    private void updateNumberDisplay() {
        mBinding.tvNumberDisplay.setText(formatNumber(mDialInput.toString()));
    }

    /**
     * 格式化号码字符串，每 4 位插入一个空格。
     *
     * @param raw 原始号码字符串
     * @return 格式化后的字符串
     */
    @NonNull
    @VisibleForTesting
    static String formatNumber(@NonNull String raw) {
        if (raw.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append(' ');
            }
            sb.append(raw.charAt(i));
        }
        return sb.toString();
    }

    /**
     * 根据当前输入执行模糊搜索。
     * <p>
     * 无输入时显示通话记录，有输入时搜索匹配的联系人号码。
     */
    @VisibleForTesting
    void performSearch() {
        String query = mDialInput.toString();
        if (query.isEmpty()) {
            mAdapter.setItems(mCallLogs);
        } else {
            List<CallLog> results = searchContacts(query, mContacts);
            mAdapter.setItems(results);
        }
        updateEmptyState();
    }

    /**
     * 在联系人列表中搜索号码包含查询字符串的记录。
     *
     * @param query    搜索关键字（数字串）
     * @param contacts 联系人列表
     * @return 匹配的联系人列表
     */
    @NonNull
    @VisibleForTesting
    static List<CallLog> searchContacts(@NonNull String query,
                                        @NonNull List<CallLog> contacts) {
        if (query.isEmpty()) {
            return new ArrayList<>();
        }
        List<CallLog> results = new ArrayList<>();
        for (CallLog contact : contacts) {
            if (contact.getNumber().contains(query)) {
                results.add(contact);
            }
        }
        return results;
    }

    /**
     * 更新空状态视图的可见性。
     */
    private void updateEmptyState() {
        boolean hasInput = mDialInput.length() > 0;
        boolean listEmpty = mAdapter.getItemCount() == 0;

        int emptyVisibility = (hasInput && listEmpty) ? View.VISIBLE : View.GONE;
        mBinding.ivNoMatchIcon.setVisibility(emptyVisibility);
        mBinding.tvNoMatch.setVisibility(emptyVisibility);
    }

    /**
     * 创建 mock 通话记录数据。
     *
     * @return mock 通话记录列表
     */
    @NonNull
    @VisibleForTesting
    static List<CallLog> createMockCallLogs() {
        return Arrays.asList(
                new CallLog("13828299090", null, CallLog.TYPE_MISSED, "11/8"),
                new CallLog("13828299090", null, CallLog.TYPE_INCOMING, "11/8"),
                new CallLog("13828299090", null, CallLog.TYPE_GENERIC, "11/8"),
                new CallLog("13828299090", null, CallLog.TYPE_OUTGOING, "11/8"),
                new CallLog("13828299090", null, CallLog.TYPE_GENERIC, "11/8")
        );
    }

    /**
     * 创建 mock 联系人数据，用于模糊搜索。
     *
     * @return mock 联系人列表
     */
    @NonNull
    @VisibleForTesting
    static List<CallLog> createMockContacts() {
        return Arrays.asList(
                new CallLog("13522229090", "丹妮", CallLog.TYPE_GENERIC, ""),
                new CallLog("135222223234", null, CallLog.TYPE_GENERIC, ""),
                new CallLog("135222256789", null, CallLog.TYPE_GENERIC, ""),
                new CallLog("135222256789", null, CallLog.TYPE_GENERIC, ""),
                new CallLog("13522220000", "A仔", CallLog.TYPE_GENERIC, "")
        );
    }

    /**
     * 获取当前拨号输入内容。
     *
     * @return 当前输入的号码字符串
     */
    @NonNull
    @VisibleForTesting
    String getDialInput() {
        return mDialInput.toString();
    }

    /**
     * 获取 ViewBinding 实例（仅用于测试）。
     *
     * @return FragmentDialerBinding 实例
     */
    @Nullable
    @VisibleForTesting
    FragmentDialerBinding getBinding() {
        return mBinding;
    }

    /**
     * 获取适配器实例（仅用于测试）。
     *
     * @return CallLogAdapter 实例
     */
    @Nullable
    @VisibleForTesting
    CallLogAdapter getAdapter() {
        return mAdapter;
    }
}
