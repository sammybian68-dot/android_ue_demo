package com.example.btphone.ui.contacts;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.adapter.ContactsAdapter;
import com.example.btphone.databinding.FragmentContactsListBinding;
import com.example.btphone.model.Contact;

import java.util.List;

/**
 * 联系人列表 Fragment。
 * <p>
 * 以两列网格形式展示按拼音首字母分组的联系人列表，右侧配合
 * {@link LetterIndexView} 实现字母索引快速定位。
 */
public class ContactsListFragment extends Fragment {

    private static final int SPAN_COUNT = 2;

    private FragmentContactsListBinding mBinding;
    private ContactsAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    /**
     * 创建 ContactsListFragment 的新实例。
     *
     * @return Fragment 实例
     */
    @NonNull
    public static ContactsListFragment newInstance() {
        return new ContactsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = FragmentContactsListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupLetterIndex();
        loadMockData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    /**
     * 初始化 RecyclerView，配置 GridLayoutManager 和 ItemDecoration。
     */
    private void setupRecyclerView() {
        mAdapter = new ContactsAdapter();
        mLayoutManager = new GridLayoutManager(requireContext(), SPAN_COUNT);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItemViewType(position) == ContactsAdapter.TYPE_HEADER) {
                    return SPAN_COUNT;
                }
                return 1;
            }
        });

        mBinding.rvContacts.setLayoutManager(mLayoutManager);
        mBinding.rvContacts.setAdapter(mAdapter);
        mBinding.rvContacts.addItemDecoration(new ContactsItemDecoration());
    }

    /**
     * 初始化字母索引控件的触摸回调。
     */
    private void setupLetterIndex() {
        mBinding.letterIndexView.setOnLetterSelectedListener(
                new LetterIndexView.OnLetterSelectedListener() {
                    @Override
                    public void onLetterSelected(@NonNull String letter) {
                        scrollToLetter(letter);
                    }

                    @Override
                    public void onLetterSelectionFinished() {
                        // 选择结束，气泡已隐藏，无需额外处理
                    }
                });
    }

    /**
     * 加载 Mock 联系人数据。
     */
    private void loadMockData() {
        List<Contact> contacts = Contact.createMockContacts();
        mAdapter.setContacts(contacts);
    }

    /**
     * 滚动 RecyclerView 到指定字母的分组位置。
     *
     * @param letter 目标字母
     */
    @VisibleForTesting
    void scrollToLetter(@NonNull String letter) {
        int position = mAdapter.getPositionForLetter(letter);
        if (position >= 0) {
            RecyclerView.SmoothScroller smoothScroller =
                    new LinearSmoothScroller(requireContext()) {
                        @Override
                        protected int getVerticalSnapPreference() {
                            return SNAP_TO_START;
                        }
                    };
            smoothScroller.setTargetPosition(position);
            mLayoutManager.startSmoothScroll(smoothScroller);
        }
    }

    /**
     * 获取适配器实例（仅测试用）。
     *
     * @return ContactsAdapter 实例
     */
    @VisibleForTesting
    @Nullable
    ContactsAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * 获取 ViewBinding 实例（仅测试用）。
     *
     * @return FragmentContactsListBinding 实例
     */
    @VisibleForTesting
    @Nullable
    FragmentContactsListBinding getViewBinding() {
        return mBinding;
    }

    /**
     * 联系人列表 ItemDecoration，处理列间距和行间距。
     * <p>
     * 分组标题占满整行，联系人条目两列布局，列间距 32px，行间距 24px。
     */
    @VisibleForTesting
    class ContactsItemDecoration extends RecyclerView.ItemDecoration {

        private final int mColumnGap;
        private final int mRowGap;

        ContactsItemDecoration() {
            mColumnGap = getResources().getDimensionPixelSize(R.dimen.contact_column_gap);
            mRowGap = getResources().getDimensionPixelSize(R.dimen.contact_row_gap);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position < 0) {
                return;
            }

            int viewType = mAdapter.getItemViewType(position);
            if (viewType == ContactsAdapter.TYPE_HEADER) {
                outRect.set(0, 0, 0, 0);
            } else {
                GridLayoutManager.LayoutParams lp =
                        (GridLayoutManager.LayoutParams) view.getLayoutParams();
                int spanIndex = lp.getSpanIndex();
                int halfGap = mColumnGap / 2;
                if (spanIndex == 0) {
                    outRect.right = halfGap;
                } else {
                    outRect.left = halfGap;
                }
                outRect.bottom = mRowGap;
            }
        }
    }
}
