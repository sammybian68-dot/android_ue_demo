package com.example.btphone.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btphone.R;
import com.example.btphone.databinding.ItemSyncBrandBinding;
import com.example.btphone.model.SyncBrand;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for displaying phone brand items in the sync-operation dialog.
 * Each item can be expanded to reveal detailed sync instructions or collapsed
 * to show only the brand name.
 */
public class SyncBrandAdapter extends RecyclerView.Adapter<SyncBrandAdapter.ViewHolder> {

    @NonNull
    private final List<SyncBrand> mBrands;

    /**
     * Creates a new adapter with the given list of brand items.
     *
     * @param brands the list of SyncBrand items to display
     */
    public SyncBrandAdapter(@NonNull List<SyncBrand> brands) {
        mBrands = new ArrayList<>(brands);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSyncBrandBinding binding = ItemSyncBrandBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SyncBrand brand = mBrands.get(position);
        holder.bind(brand, position);
    }

    @Override
    public int getItemCount() {
        return mBrands.size();
    }

    /**
     * Returns the brand item at the specified position.
     *
     * @param position adapter position
     * @return the SyncBrand at the given position
     * @throws IndexOutOfBoundsException if position is out of range
     */
    @NonNull
    public SyncBrand getItem(int position) {
        return mBrands.get(position);
    }

    /**
     * Replaces the current brand list with a new one.
     *
     * @param brands the new list of brands
     */
    public void setBrands(@NonNull List<SyncBrand> brands) {
        mBrands.clear();
        mBrands.addAll(brands);
        notifyDataSetChanged();
    }

    /**
     * ViewHolder for a single brand item with expand/collapse support.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemSyncBrandBinding mBinding;

        ViewHolder(@NonNull ItemSyncBrandBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        /**
         * Binds a SyncBrand model to this ViewHolder, updating the UI to reflect
         * the brand name, detail text, and expanded state.
         *
         * @param brand    the brand data to bind
         * @param position the adapter position of this item
         */
        void bind(@NonNull SyncBrand brand, int position) {
            mBinding.tvBrandName.setText(brand.getBrandName());
            mBinding.tvDetail.setText(brand.getDetailText());

            updateExpandedState(brand.isExpanded());

            mBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    brand.toggleExpanded();
                    notifyItemChanged(position);
                }
            });
        }

        private void updateExpandedState(boolean expanded) {
            if (expanded) {
                mBinding.layoutDetail.setVisibility(View.VISIBLE);
                mBinding.ivToggle.setImageResource(R.drawable.pop_list_btn_packup_n);
                mBinding.ivToggle.setContentDescription(
                        itemView.getContext().getString(R.string.sync_packup_desc));
            } else {
                mBinding.layoutDetail.setVisibility(View.GONE);
                mBinding.ivToggle.setImageResource(R.drawable.pop_list_btn_unfold_n);
                mBinding.ivToggle.setContentDescription(
                        itemView.getContext().getString(R.string.sync_unfold_desc));
            }
        }
    }
}
