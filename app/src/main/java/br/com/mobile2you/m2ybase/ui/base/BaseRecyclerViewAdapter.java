package br.com.mobile2you.m2ybase.ui.base;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.mobile2you.m2ybase.R;

/**
 * Created by mobile2you on 26/07/16.
 */
public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    public static final int TYPE_LOADING = 2;
    public static final int TYPE_EMPTY = 3;
    public static final int TYPE_ERROR = 4;
    public static final int TYPE_FOOTER = 5;
    public static final int TYPE_PAGE_LOADING = 6;
    public static final int TYPE_PAGE_ERROR = 7;

    private final int HEADER_SIZE = 1;
    private final int FOOTER_SIZE = 1;
    private final int PLACE_HOLDER_SIZE = 1;

    private View.OnClickListener mTryAgainClickListener;
    private OnEndReached mOnEndReached;

    //FLAGS
    private boolean mIsLoading = false;
    private boolean mIsError = false;
    private boolean mIsPageError = false;
    private boolean mIsListEndReached = false;
    private boolean mAlwaysShowFooter = false;
    private boolean mAlwaysShowHeader = false;

    //PRE-SETTINGS
    private boolean mIsPaginated = false;

    public BaseRecyclerViewAdapter(View.OnClickListener tryAgainClickListener) {
        mTryAgainClickListener = tryAgainClickListener;
    }

    public BaseRecyclerViewAdapter(View.OnClickListener tryAgainClickListener, OnEndReached onEndReached) {
        mTryAgainClickListener = tryAgainClickListener;
        mOnEndReached = onEndReached;
    }

    //DISPLAY METHODS
    public final void startLoading() {
        mIsLoading = true;
        mIsError = false;
        mIsPageError = false;
        super.notifyDataSetChanged();
    }

    public final void startLoading(int page) {
        if (page == 0) {
            startLoading();
        } else {
            mIsPageError = false;
            super.notifyDataSetChanged();
        }
    }

    public final void showError() {
        mIsError = true;
        mIsLoading = false;
        mIsPageError = false;
        super.notifyDataSetChanged();
    }

    public final void showError(int page) {
        if (page == 0) {
            showError();
        } else {
            mIsPageError = true;
            mIsLoading = false;
            mIsError = false;
            super.notifyDataSetChanged();
        }
    }

    public final void notifyDataChanged() {
        mIsError = false;
        mIsLoading = false;
        mIsPageError = false;
        super.notifyDataSetChanged();
    }

    public final void stopLoading() {
        mIsLoading = false;
        super.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_EMPTY:
                return getEmptyViewHolder(parent);
            case TYPE_ERROR:
                return getErrorViewHolder(parent);
            case TYPE_LOADING:
                return getLoadingViewHolder(parent);
            case TYPE_PAGE_LOADING:
                return getPageLoadingViewHolder(parent);
            case TYPE_PAGE_ERROR:
                return getPageErrorViewHolder(parent);
            case TYPE_HEADER:
                return getHeaderViewHolder(parent);
            case TYPE_FOOTER:
                return getFooterViewHolder(parent);
            default:
                return getItemViewHolder(parent);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoading() && isPlaceHolderPosition(position)) {
            return TYPE_LOADING;
        } else if (isError() && isPlaceHolderPosition(position)) {
            return TYPE_ERROR;
        } else if (getDisplayableItemsCount() == 0 && isPlaceHolderPosition(position)) {
            return TYPE_EMPTY;
        } else if (hasHeader() && isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPaginated() && isPositionFooter(position) && !isListEndReached() && isPageError()) {
            return TYPE_PAGE_ERROR;
        } else if (isPaginated() && isPositionFooter(position) && !isListEndReached()) {
            mOnEndReached.endReached();
            return TYPE_PAGE_LOADING;
        } else if (hasFooter() && isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    public boolean isPlaceHolderPosition(int position) {
        return (hasHeader() && mAlwaysShowHeader && position == 1) ||
                (!hasHeader() && position == 0) ||
                (!mAlwaysShowHeader && position == 0);
    }

    @Override
    public int getItemCount() {
        if (getDisplayableItemsCount() == 0 || mIsLoading || mIsError) {
            return PLACE_HOLDER_SIZE + (mAlwaysShowHeader ? HEADER_SIZE : 0) + (mAlwaysShowFooter ? FOOTER_SIZE : 0);
        } else {
            return getDisplayableItemsCount() + (hasFooter() ? FOOTER_SIZE : 0) + (hasHeader() ? HEADER_SIZE : 0) + (isPaginated() && !isListEndReached() ? PLACE_HOLDER_SIZE : 0);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindRecyclerViewHolder(holder, getRealPosition(position));
        if (holder instanceof PlaceHolderVH) {
            if (holder.getItemViewType() == TYPE_EMPTY) {
                ((PlaceHolderVH) holder).bind(getEmptyLabel(), getEmptyIcon(), null, mAlwaysShowFooter || mAlwaysShowHeader);
            } else if (holder.getItemViewType() == TYPE_ERROR) {
                ((PlaceHolderVH) holder).bind(getErrorLabel(), getErrorIcon(), mTryAgainClickListener, mAlwaysShowFooter || mAlwaysShowHeader);
            }
        } else if (holder instanceof LoadingItemVH) {
            if (holder.getItemViewType() == TYPE_PAGE_LOADING) {
                ((LoadingItemVH) holder).bind();
            } else if (mAlwaysShowFooter || mAlwaysShowHeader) {
                ((LoadingItemVH) holder).bind();
            }
        } else if (holder instanceof PageLoadingErrorVH) {
            ((PageLoadingErrorVH) holder).bind(mTryAgainClickListener);
        }
    }

    public abstract int getDisplayableItemsCount();

    public abstract void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position);

    //ITEM METHODS
    protected abstract RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent);

    //FOOTER METHODS
    protected RecyclerView.ViewHolder getFooterViewHolder(ViewGroup parent) {
        return null;
    }

    public final void alwaysShowFooter(boolean isAlways) {
        mAlwaysShowFooter = isAlways;
    }

    //HEADER METHODS
    protected RecyclerView.ViewHolder getHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    public final void alwaysShowHeader(boolean isAlways) {
        mAlwaysShowHeader = isAlways;
    }

    //EMPTY METHODS
    protected int getEmptyLabel() {
        return R.string.placeholder_empty_label;
    }

    protected int getEmptyIcon() {
        return R.drawable.ic_default_empty;
    }

    protected RecyclerView.ViewHolder getEmptyViewHolder(ViewGroup parent) {
        View sectionView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeholder_default_general, parent, false);
        return new PlaceHolderVH(sectionView);
    }

    //ERROR METHODS
    protected int getErrorLabel() {
        return R.string.placeholder_error_label;
    }

    protected int getErrorIcon() {
        return R.drawable.ic_default_request_error;
    }

    protected RecyclerView.ViewHolder getErrorViewHolder(ViewGroup parent) {
        View errorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeholder_default_general, parent, false);
        return new PlaceHolderVH(errorView);
    }

    //LOADING METHODS
    protected RecyclerView.ViewHolder getLoadingViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeholder_default_loading, parent, false);
        return new LoadingItemVH(itemView);
    }

    //LOADING PAGE METHODS
    protected RecyclerView.ViewHolder getPageLoadingViewHolder(ViewGroup parent) {
        View pageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeholder_default_loading, parent, false);
        return new LoadingItemVH(pageView);
    }

    //LOADING PAGE ERROR METHODS
    protected RecyclerView.ViewHolder getPageErrorViewHolder(ViewGroup parent) {
        View pageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.placeholder_default_page_loading_error, parent, false);
        return new PageLoadingErrorVH(pageView);
    }

    public final void setPaginated(boolean paginated) {
        mIsPaginated = paginated;
    }

    public final void setListEndReached(boolean isEndReached) {
        mIsListEndReached = isEndReached;
        notifyDataChanged();
    }

    //GETTERS
    private int getRealPosition(int position) {
        return position - (hasHeader() ? HEADER_SIZE : 0);
    }

    public final boolean isLoading() {
        return mIsLoading;
    }

    public final boolean isError() {
        return mIsError;
    }

    public final boolean isPageError() {
        return mIsPageError;
    }

    public final boolean isListEndReached() {
        return mIsListEndReached;
    }

    public final boolean hasHeader() {
        try {
            return getHeaderViewHolder(null) != null;
        } catch (NullPointerException e) {
            return true;
        }
    }

    public final boolean hasFooter() {
        try {
            return getFooterViewHolder(null) != null;
        } catch (NullPointerException e) {
            return true;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public final boolean isPaginated() {
        return mIsPaginated;
    }

    private boolean isPositionFooter(int position) {
        return position == getItemCount() - 1;
    }

    //VIEW HOLDERS
    public class PlaceHolderVH extends RecyclerView.ViewHolder {
        private TextView vLabel;
        private ImageView vIcon;
        private Button vRefreshButton;

        public PlaceHolderVH(View v) {
            super(v);
            vLabel = (TextView) itemView.findViewById(R.id.item_default_label);
            vIcon = (ImageView) itemView.findViewById(R.id.item_default_icon);
            vRefreshButton = (Button) itemView.findViewById(R.id.item_default_button);
        }

        public void bind(int emptyMsg, int drawable, View.OnClickListener refreshClickListener, boolean wrapView) {
            vLabel.setText(itemView.getContext().getString(emptyMsg));
            Drawable tintedDrawable = ContextCompat.getDrawable(itemView.getContext(), drawable).mutate();
            tintedDrawable.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
            vIcon.setImageDrawable(tintedDrawable);
            if (refreshClickListener != null) {
                vRefreshButton.setVisibility(View.VISIBLE);
                vRefreshButton.setOnClickListener(refreshClickListener);
            }
            if (wrapView) {
                itemView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                itemView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }

    public class LoadingItemVH extends RecyclerView.ViewHolder {
        private ProgressBar vProgressBar;

        public LoadingItemVH(View itemView) {
            super(itemView);
            vProgressBar = (ProgressBar) itemView.findViewById(R.id.default_progressbar);
            vProgressBar.getIndeterminateDrawable().mutate().setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimaryDark), PorterDuff.Mode.SRC_ATOP);
        }

        public void bind() {
            itemView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    public class PageLoadingErrorVH extends RecyclerView.ViewHolder {
        private Button vRefreshButton;

        public PageLoadingErrorVH(View itemView) {
            super(itemView);
            vRefreshButton = (Button) itemView.findViewById(R.id.page_loading_error_button);
        }

        public void bind(View.OnClickListener onClickListener) {
            vRefreshButton.setOnClickListener(onClickListener);
        }
    }

    public interface OnEndReached {
        void endReached();
    }
}