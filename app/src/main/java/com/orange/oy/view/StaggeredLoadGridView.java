package com.orange.oy.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.etsy.android.grid.StaggeredGridView;
import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.OverscrollHelper;
import com.handmark.pulltorefresh.library.PullToRefreshAdapterViewBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.EmptyViewMethodAccessor;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.orange.oy.R;

/**
 * Created by Administrator on 2018/7/18.
 */
public class StaggeredLoadGridView extends PullToRefreshAdapterViewBase<StaggeredGridView> {

    public StaggeredLoadGridView(Context context) {
        super(context);
        setScrollingWhileRefreshingEnabled(true);
    }

    public StaggeredLoadGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScrollingWhileRefreshingEnabled(true);
    }

    public StaggeredLoadGridView(Context context, Mode mode) {
        super(context, mode);
        setScrollingWhileRefreshingEnabled(true);
    }

    public StaggeredLoadGridView(Context context, Mode mode, AnimationStyle animStyle) {
        super(context, mode, animStyle);
        setScrollingWhileRefreshingEnabled(true);
    }

    private LoadingLayout mHeaderLoadingView;
    private LoadingLayout mFooterLoadingView;
    private FrameLayout mLvFooterLoadingFrame;
    private boolean mListViewExtrasEnabled;

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

//    @Override
//    protected void onRefreshing(final boolean doScroll) {
//        /**
//         * If we're not showing the Refreshing view, or the list is empty, the
//         * the header/footer views won't show so we use the normal method.
//         */
//        ListAdapter adapter = mRefreshableView.getAdapter();
//        if (!mListViewExtrasEnabled || !getShowViewWhileRefreshing() || null == adapter || adapter.isEmpty()) {
//            super.onRefreshing(doScroll);
//            return;
//        }
//
//        super.onRefreshing(false);
//
//        final LoadingLayout origLoadingView, listViewLoadingView, oppositeListViewLoadingView;
//        final int selection, scrollToY;
//
//        switch (getCurrentMode()) {
//            case MANUAL_REFRESH_ONLY:
//            case PULL_FROM_END:
//                origLoadingView = getFooterLayout();
//                listViewLoadingView = mFooterLoadingView;
//                oppositeListViewLoadingView = mHeaderLoadingView;
//                selection = mRefreshableView.getCount() - 1;
//                scrollToY = getScrollY() - getFooterSize();
//                break;
//            case PULL_FROM_START:
//            default:
//                origLoadingView = getHeaderLayout();
//                listViewLoadingView = mHeaderLoadingView;
//                oppositeListViewLoadingView = mFooterLoadingView;
//                selection = 0;
//                scrollToY = getScrollY() + getHeaderSize();
//                break;
//        }
//
//        // Hide our original Loading View
//        origLoadingView.reset();
//        origLoadingView.hideAllViews();
//
//        // Make sure the opposite end is hidden too
//        oppositeListViewLoadingView.setVisibility(View.GONE);
//
//        // Show the ListView Loading View and set it to refresh.
//        listViewLoadingView.setVisibility(View.VISIBLE);
//        listViewLoadingView.refreshing();
//
//        if (doScroll) {
//            // We need to disable the automatic visibility changes for now
//            disableLoadingLayoutVisibilityChanges();
//
//            // We scroll slightly so that the ListView's header/footer is at the
//            // same Y position as our normal header/footer
//            setHeaderScroll(scrollToY);
//
//            // Make sure the ListView is scrolled to show the loading
//            // header/footer
//            mRefreshableView.setSelection(selection);
//
//            // Smooth scroll as normal
//            smoothScrollTo(0);
//        }
//    }
//
//    @Override
//    protected void onReset() {
//        /**
//         * If the extras are not enabled, just call up to super and return.
//         */
//        if (!mListViewExtrasEnabled) {
//            super.onReset();
//            return;
//        }
//
//        final LoadingLayout originalLoadingLayout, listViewLoadingLayout;
//        final int scrollToHeight, selection;
//        final boolean scrollLvToEdge;
//
//        switch (getCurrentMode()) {
//            case MANUAL_REFRESH_ONLY:
//            case PULL_FROM_END:
//                originalLoadingLayout = getFooterLayout();
//                listViewLoadingLayout = mFooterLoadingView;
//                selection = mRefreshableView.getCount() - 1;
//                scrollToHeight = getFooterSize();
//                scrollLvToEdge = Math.abs(mRefreshableView.getLastVisiblePosition() - selection) <= 1;
//                break;
//            case PULL_FROM_START:
//            default:
//                originalLoadingLayout = getHeaderLayout();
//                listViewLoadingLayout = mHeaderLoadingView;
//                scrollToHeight = -getHeaderSize();
//                selection = 0;
//                scrollLvToEdge = Math.abs(mRefreshableView.getFirstVisiblePosition() - selection) <= 1;
//                break;
//        }
//
//        // If the ListView header loading layout is showing, then we need to
//        // flip so that the original one is showing instead
//        if (listViewLoadingLayout.getVisibility() == View.VISIBLE) {
//
//            // Set our Original View to Visible
//            originalLoadingLayout.showInvisibleViews();
//
//            // Hide the ListView Header/Footer
//            listViewLoadingLayout.setVisibility(View.GONE);
//
//            /**
//             * Scroll so the View is at the same Y as the ListView
//             * header/footer, but only scroll if: we've pulled to refresh, it's
//             * positioned correctly
//             */
//            if (scrollLvToEdge && getState() != State.MANUAL_REFRESHING) {
//                mRefreshableView.setSelection(selection);
//                setHeaderScroll(scrollToHeight);
//            }
//        }
//
//        // Finally, call up to super
//        super.onReset();
//    }
//
//    @Override
//    protected LoadingLayoutProxy createLoadingLayoutProxy(final boolean includeStart, final boolean includeEnd) {
//        LoadingLayoutProxy proxy = super.createLoadingLayoutProxy(includeStart, includeEnd);
//
//        if (mListViewExtrasEnabled) {
//            final Mode mode = getMode();
//
//            if (includeStart && mode.showHeaderLoadingLayout()) {
//                proxy.addLayout(mHeaderLoadingView);
//            }
//            if (includeEnd && mode.showFooterLoadingLayout()) {
//                proxy.addLayout(mFooterLoadingView);
//            }
//        }
//
//        return proxy;
//    }
//
//    @Override
//    protected void handleStyledAttributes(TypedArray a) {
//        super.handleStyledAttributes(a);
//
//        mListViewExtrasEnabled = a.getBoolean(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrListViewExtrasEnabled, true);
//
//        if (mListViewExtrasEnabled) {
//            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
//
//            // Create Loading Views ready for use later
//            FrameLayout frame = new FrameLayout(getContext());
//            mHeaderLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_START, a);
//            mHeaderLoadingView.setVisibility(View.GONE);
//            frame.addView(mHeaderLoadingView, lp);
//            mRefreshableView.addHeaderView(frame, null, false);
//
//            mLvFooterLoadingFrame = new FrameLayout(getContext());
//            mFooterLoadingView = createLoadingLayout(getContext(), Mode.PULL_FROM_END, a);
//            mFooterLoadingView.setVisibility(View.GONE);
//            mLvFooterLoadingFrame.addView(mFooterLoadingView, lp);
//
//            /**
//             * If the value for Scrolling While Refreshing hasn't been
//             * explicitly set via XML, enable Scrolling While Refreshing.
//             */
//            if (!a.hasValue(com.handmark.pulltorefresh.library.R.styleable.PullToRefresh_ptrScrollingWhileRefreshingEnabled)) {
//                setScrollingWhileRefreshingEnabled(true);
//            }
//        }
//    }

    @Override
    protected StaggeredGridView createRefreshableView(Context context, AttributeSet attrs) {
        StaggeredGridView gridView;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            gridView = new InternalStaggeredGridViewSDK9(context, attrs);
        } else {
            gridView = new InternalListView(context, attrs);
        }

        gridView.setId(android.R.id.list);
        return gridView;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        boolean result = false;
        int last = getRefreshableView().getChildCount() - 1;
        View v = getRefreshableView().getChildAt(last);

        int firstVisiblePosition = getRefreshableView()
                .getFirstVisiblePosition();
        int visibleItemCount = getRefreshableView().getChildCount();
        int itemCount = getRefreshableView().getAdapter().getCount() - 1;
        if (firstVisiblePosition + visibleItemCount >= itemCount) {
            if (v != null) {
                boolean isLastFullyVisible = v.getBottom() <= getRefreshableView()
                        .getHeight();

                result = isLastFullyVisible;
            }
        }
        return result;
    }

    @Override
    protected boolean isReadyForPullStart() {
        boolean result = false;
        View v = getRefreshableView().getChildAt(0);
        if (getRefreshableView().getFirstVisiblePosition() == 0) {
            if (v != null) {
                // getTop() and getBottom() are relative to the ListView,
                // so if getTop() is negative, it is not fully visible
                boolean isTopFullyVisible = v.getTop() >= 0;

                result = isTopFullyVisible;
            }
        }
        return result;
    }

    @TargetApi(9)
    final class InternalStaggeredGridViewSDK9 extends StaggeredGridView {
        // WebView doesn't always scroll back to it's edge so we add some
        // fuzziness
        static final int OVERSCROLL_FUZZY_THRESHOLD = 2;

        // WebView seems quite reluctant to overscroll so we use the scale
        // factor to scale it's value
        static final float OVERSCROLL_SCALE_FACTOR = 1.5f;

        public InternalStaggeredGridViewSDK9(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public final static int MAX_SCROLLER_DISTANCE = 30;

        @Override
        protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                       int scrollY, int scrollRangeX, int scrollRangeY,
                                       int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

            //FIXME 在快速滚动时，到达尽头时产生的弹性拉伸量。这里限制一下大小，弹得太远，难看。
            deltaY = Math.max(-MAX_SCROLLER_DISTANCE, Math.min(MAX_SCROLLER_DISTANCE, deltaY));

            final boolean returnValue = super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
                    scrollRangeY, maxOverScrollX, maxOverScrollY, isTouchEvent);

            // Does all of the hard work...
            OverscrollHelper.overScrollBy(StaggeredLoadGridView.this, deltaX, scrollX, deltaY, scrollY, isTouchEvent);

            return returnValue;
        }

        /**
         * Taken from the AOSP ScrollView source
         */
        private int getScrollRange() {
            int scrollRange = 0;
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                scrollRange = Math.max(0, child.getHeight()
                        - (getHeight() - getPaddingBottom() - getPaddingTop()));
            }
            return scrollRange;
        }
    }


    protected class InternalListView extends StaggeredGridView implements EmptyViewMethodAccessor {

        private boolean mAddedLvFooter = false;

        public InternalListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                super.dispatchDraw(canvas);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            /**
             * This is a bit hacky, but Samsung's ListView has got a bug in it
             * when using Header/Footer Views and the list is empty. This masks
             * the issue so that it doesn't cause an FC. See Issue #66.
             */
            try {
                return super.dispatchTouchEvent(ev);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        public void setAdapter(ListAdapter adapter) {
            // Add the Footer View at the last possible moment
            if (null != mLvFooterLoadingFrame && !mAddedLvFooter) {
                addFooterView(mLvFooterLoadingFrame, null, false);
                mAddedLvFooter = true;
            }

            super.setAdapter(adapter);
        }

        @Override
        public void setEmptyView(View emptyView) {
            StaggeredLoadGridView.this.setEmptyView(emptyView);
        }

        @Override
        public void setEmptyViewInternal(View emptyView) {
            super.setEmptyView(emptyView);
        }

    }
}
