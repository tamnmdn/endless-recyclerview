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
package com.example.endlessrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class EndlessRecyclerView extends RecyclerView {
    public static final String TAG = EndlessRecyclerView.class.getSimpleName();
    public static final int DEFAULT_THRESHOLD = 3;
    public static final int STATE_HIDE = 0;
    public static final int STATE_SHOW = 1;
    public static final int MODE_AUTO = 0;
    public static final int MODE_NONE = 1;
    private EndlessRecyclerView mRecyclerView = this;
    private ViewState mViewState = new ViewState();
    private EndlessRecyclerAdapter mAdapter;
    private OnLoadMoreListener mLoadMoreListener;
    private volatile ScrollType mScrollLoadType = ScrollType.FIRST_LOAD;

    private OnScrollListener mEndlessScrollListener = new OnScrollListener() {
        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (mViewState.getMode() != MODE_AUTO) {
                return;
            }
            if (mScrollLoadType == ScrollType.FIRST_LOAD || mScrollLoadType == ScrollType.PENDING
                    || mScrollLoadType == ScrollType.DONE) {
                return;
            }
            final RecyclerViewHelper recyclerViewHelper = new RecyclerViewHelper(recyclerView);
            final int threshold = mViewState.getThreshold();
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerViewHelper.getItemCount();
            int firstVisibleItem = recyclerViewHelper.findFirstVisibleItemPosition();
            if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + threshold)) {
                mViewState.incIndex();
                setLoading(ScrollType.PENDING);
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore(mRecyclerView);
                }
            }
        }
    };

    public EndlessRecyclerView(final Context context) {
        this(context, null);
    }

    public EndlessRecyclerView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EndlessRecyclerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(final Context context) {
        super.setLayoutManager(new LinearLayoutManager(context));
        addOnScrollListener(mEndlessScrollListener);
    }

    private void updateState() {
        if (mAdapter != null) {
            mAdapter.updateState();
        }
    }

    public void showProgress() {
        if (mViewState.getMode() != MODE_NONE) {
            mViewState.setState(STATE_SHOW);
            updateState();
        } else {
            Log.w(TAG, "showProgress() ignore, endless mode is disabled");
        }
    }

    public void hideProgress() {
        mViewState.setState(STATE_HIDE);
        updateState();
    }

    public void setLoading(ScrollType scrollType) {
        mScrollLoadType = scrollType;
        switch (scrollType) {
            case IN_PROGRESS:
                showProgress();
                break;
            case PENDING:
                break;
            case FIRST_LOAD:
            case DONE:
            default:
                hideProgress();
                break;
        }
    }

    public EndlessRecyclerView setOnLoadMoreListener(final OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
        return this;
    }

    public boolean isLoadingMore() {
        switch (mScrollLoadType) {
            case IN_PROGRESS:
            case PENDING:
                return true;
            case FIRST_LOAD:
            case DONE:
            default:
                return false;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnScrollListener(mEndlessScrollListener);
    }

    /**
     * Check scroll Listener
     *
     * @param isRemoved true/false
     */
    public void changeOnScrollListener(boolean isRemoved) {
        if (isRemoved) {
            removeOnScrollListener(mEndlessScrollListener);
        } else {
            addOnScrollListener(mEndlessScrollListener);
        }
    }

    public EndlessRecyclerAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setAdapter(final Adapter adapter) {
        if (adapter == null) {
            mAdapter = null;
            super.setAdapter(null);
        } else {
            mAdapter = new EndlessRecyclerAdapter(adapter, mViewState);
            super.setAdapter(mAdapter);
        }
    }

    /**
     * Scroll Type enum
     */
    public enum ScrollType {
        FIRST_LOAD,
        IN_PROGRESS,
        PENDING,
        DONE
    }

    /**
     * Interface callback listener load more
     */
    public interface OnLoadMoreListener {
        void onLoadMore(final EndlessRecyclerView view);
    }

    /**
     * View state
     */
    public static class ViewState {
        private int mode;
        private int state;
        private int threshold;
        private int index;
        private CharSequence text;

        public ViewState() {
            reset();
        }

        public ViewState(final ViewState s) {
            this.mode = s.mode;
            this.state = s.state;
            this.threshold = s.threshold;
            this.index = s.index;
        }

        public ViewState copy() {
            return new ViewState(this);
        }

        private void reset() {
            mode = MODE_AUTO;
            state = STATE_HIDE;
            threshold = DEFAULT_THRESHOLD;
            index = 0;
        }

        public int getState() {
            return state;
        }

        public ViewState setState(final int s) {
            this.state = s;
            return this;
        }

        public int getMode() {
            return mode;
        }

        public int getThreshold() {
            return threshold;
        }

        public ViewState incIndex() {
            this.index++;
            return this;
        }

        public CharSequence getText() {
            return text;
        }

        public ViewState setText(final CharSequence t) {
            this.text = t;
            return this;
        }

        @Override
        public String toString() {
            return "ViewState{" +
                    "display=" + state +
                    ", mode=" + mode +
                    ", threshold=" + threshold +
                    '}';
        }
    }
}
