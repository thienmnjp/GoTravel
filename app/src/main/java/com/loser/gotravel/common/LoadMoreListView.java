package com.loser.gotravel.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.loser.gotravel.R;

/**
 * Created by DucAnhZ on 06/10/2015.
 */
public class LoadMoreListView extends ListView implements OnScrollListener{
    private static final String TAG = "LoadMoreListView";

    /**
     * Listener that will receive notification every time the list scrolls.
     */
    private OnScrollListener onScrollListener;
    private LayoutInflater inflater;
    private OnLoadMoreListener onLoadMoreListener;
    // footer view
    private RelativeLayout footerView;
    // private TextView labLoadMore
    private ProgressBar progressBarLoadMore;
    private boolean isLoadingMore = false;
    private int currentScrollState;

    // Listenr to process load more items when user reaches the end of the list

    public LoadMoreListView(Context context) {
        super(context);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // footer
        footerView = (RelativeLayout) inflater.inflate(
                R.layout.load_more_footer, this, false);
		/*
		 * mLabLoadMore = (TextView) mFooterView
		 * .findViewById(R.id.load_more_lab_view);
		 */
        progressBarLoadMore = (ProgressBar) footerView
                .findViewById(R.id.load_more_progressBar);

        addFooterView(footerView);

        super.setOnScrollListener(this);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     *
     * @param l
     *            The scroll listener.
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        onScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list reaches the end (last
     * item be visible)
     *
     * @param onLoadMoreListener
     *            The callback to run.
     */

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        if (onScrollListener != null) {
            onScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }

        if (onLoadMoreListener != null) {

            if (visibleItemCount == totalItemCount) {
                progressBarLoadMore.setVisibility(View.GONE);
                // mLabLoadMore.setVisibility(View.GONE);
                return;
            }

            boolean loadMore = firstVisibleItem + visibleItemCount >= totalItemCount;

            if (!isLoadingMore && loadMore
                    && currentScrollState != SCROLL_STATE_IDLE) {
                progressBarLoadMore.setVisibility(View.VISIBLE);
                // mLabLoadMore.setVisibility(View.VISIBLE);
                isLoadingMore = true;
                onLoadMore();
            }

        }

    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //bug fix: listview was not clickable after scroll
        if ( scrollState == OnScrollListener.SCROLL_STATE_IDLE )
        {
            view.invalidateViews();
        }

        currentScrollState = scrollState;

        if (onScrollListener != null) {
            onScrollListener.onScrollStateChanged(view, scrollState);
        }

    }

    public void onLoadMore() {
        Log.d(TAG, "onLoadMore");
        if (onLoadMoreListener != null) {
            onLoadMoreListener.onLoadMore();
        }
    }

    /**
     * Notify the loading more operation has finished
     */
    public void onLoadMoreComplete() {
        isLoadingMore = false;
        progressBarLoadMore.setVisibility(View.GONE);
    }

    /**
     * Interface definition for a callback to be invoked when list reaches the
     * last item (the user load more items in the list)
     */
    public interface OnLoadMoreListener {
        /**
         * Called when the list reaches the last item (the last item is visible
         * to the user)
         */
        public void onLoadMore();
    }
}