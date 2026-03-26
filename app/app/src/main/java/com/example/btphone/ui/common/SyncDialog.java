package com.example.btphone.ui.common;

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
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.btphone.R;
import com.example.btphone.adapter.SyncBrandAdapter;
import com.example.btphone.databinding.DialogSyncBinding;
import com.example.btphone.model.SyncBrand;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog that guides the user through enabling Bluetooth contact & call-log sync.
 * Displays step-by-step instructions and an expandable list of phone brands
 * with brand-specific authorization paths.
 */
public class SyncDialog extends DialogFragment {

    /** Fragment tag for use with FragmentManager transactions. */
    public static final String TAG = "SyncDialog";

    private DialogSyncBinding mBinding;
    private SyncBrandAdapter mAdapter;

    /**
     * Creates a new instance of SyncDialog.
     *
     * @return a new SyncDialog fragment
     */
    @NonNull
    public static SyncDialog newInstance() {
        return new SyncDialog();
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
        mBinding = DialogSyncBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupDisclaimer();
        setupBrandList();
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
     * Returns the adapter used by the brand RecyclerView.
     * Useful for testing.
     *
     * @return the SyncBrandAdapter, or null if the view has not been created
     */
    @Nullable
    public SyncBrandAdapter getAdapter() {
        return mAdapter;
    }

    private void setupDisclaimer() {
        String line1 = getString(R.string.sync_disclaimer_line1);
        String line2 = getString(R.string.sync_disclaimer_line2);
        mBinding.tvDisclaimer.setText(line1 + "\n" + line2);
    }

    private void setupBrandList() {
        List<SyncBrand> brands = buildDefaultBrands();
        mAdapter = new SyncBrandAdapter(brands);

        mBinding.rvBrands.setLayoutManager(new LinearLayoutManager(requireContext()));
        mBinding.rvBrands.setAdapter(mAdapter);
    }

    @NonNull
    List<SyncBrand> buildDefaultBrands() {
        List<SyncBrand> brands = new ArrayList<>();
        brands.add(new SyncBrand(
                getString(R.string.sync_brand_huawei),
                getString(R.string.sync_huawei_detail)));
        brands.add(new SyncBrand(
                getString(R.string.sync_brand_apple),
                getString(R.string.sync_apple_detail)));
        brands.add(new SyncBrand(
                getString(R.string.sync_brand_other),
                getString(R.string.sync_other_detail)));
        return brands;
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
        params.width = getResources().getDimensionPixelSize(R.dimen.sync_dialog_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.sync_dialog_height);
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = getResources().getDimensionPixelSize(R.dimen.sync_dialog_margin_start);
        params.y = getResources().getDimensionPixelSize(R.dimen.sync_dialog_margin_top);
        window.setAttributes(params);
    }
}
