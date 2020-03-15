/**
 * Copyright 2013 - 2016 Xiaoke Zhang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.loadmore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public class EndlessRecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    private static final int VIEW_TYPE_FOOTER = Integer.MAX_VALUE - 1;

    private RecyclerView.Adapter<ViewHolder> mWrapped;
    private EndlessRecyclerView.ViewState mViewState;

    private AdapterDataObserver mAdapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onItemRangeRemoved(final int positionStart, final int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(final int fromPosition, final int toPosition, final int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyItemRangeChanged(fromPosition, itemCount);
        }

        @Override
        public void onItemRangeInserted(final int positionStart, final int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(final int positionStart, final int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }
    };

    public EndlessRecyclerAdapter(final RecyclerView.Adapter<ViewHolder> adapter, final EndlessRecyclerView.ViewState state) {
        mWrapped = adapter;
        mWrapped.registerAdapterDataObserver(mAdapterDataObserver);
        mViewState = state;
    }

    @SuppressWarnings("unused")
    public RecyclerView.Adapter getWrapped() {
        return mWrapped;
    }

    public void updateState() {
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mWrapped.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull final RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mWrapped.onDetachedFromRecyclerView(recyclerView);
        mWrapped.unregisterAdapterDataObserver(mAdapterDataObserver);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull final ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        mWrapped.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull final ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        mWrapped.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewRecycled(@NonNull final ViewHolder holder) {
        super.onViewRecycled(holder);
        mWrapped.onViewRecycled(holder);
    }

    @Override
    public void setHasStableIds(final boolean hasStableIds) {
        mWrapped.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(final int position) {
        if (getItemViewType(position) == VIEW_TYPE_FOOTER) {
            return position;
        } else {
            return mWrapped.getItemId(position);
        }
    }

    @Override
    public int getItemCount() {
        return mWrapped.getItemCount() + getFooterCount();
    }

    @Override
    public int getItemViewType(final int position) {
        if (position == mWrapped.getItemCount()) {
            return VIEW_TYPE_FOOTER;
        }
        return mWrapped.getItemViewType(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return createFooterViewHolder(parent);
        } else {
            return mWrapped.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final int type = getItemViewType(position);
        if (type == VIEW_TYPE_FOOTER) {
            bindFooterViewHolder(holder);
        } else {
            mWrapped.onBindViewHolder(holder, position);
        }
    }


    private int getFooterCount() {
        return (mViewState.getState() == EndlessRecyclerView.STATE_HIDE) ? 0 : 1;
    }

    private ViewHolder createFooterViewHolder(final ViewGroup parent) {
        final Context context = parent.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_recycler_footer, parent, false);
        return new SimpleViewHolder(view);
    }

    private void bindFooterViewHolder(final ViewHolder holder) {
        SimpleViewHolder footer = (SimpleViewHolder) holder;
        switch (mViewState.getState()) {
            case EndlessRecyclerView.STATE_SHOW:
                footer.mRlFooterRecycler.setVisibility(View.VISIBLE);
                break;

            case EndlessRecyclerView.STATE_HIDE:
                footer.mRlFooterRecycler.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    /**
     * holder footer recycler view
     */
    static class SimpleViewHolder extends ViewHolder {
        private RelativeLayout mRlFooterRecycler;

        public SimpleViewHolder(final View itemView) {
            super(itemView);
            mRlFooterRecycler = itemView.findViewById(R.id.rlFooterRecycler);
        }
    }
}
