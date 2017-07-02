package br.com.mobile2you.m2ybase.utils.helpers;

/**
 * Created by mobile2you on 17/11/16.
 */


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.ui.widget.TouchImageView;

/**
 * Expand a image with zoom from a thumbnail all over the activity
 * The result image has pinch to zoom properties
 *
 * Created by filippecarvalho on 31/03/16.
 */
public class ExpandPhotoHelper {
    // Hold a reference to the current animator,
    // so that it can be canceled mid-way.
    private Animator mCurrentAnimator;
    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int mShortAnimationDuration;
    private Activity mActivity;
    private TouchImageView mExpandedImageView;
    private RelativeLayout mExpandedBackGround;
    private ProgressBar mProgressBar;
    private View mThumbView;
    private ImageView mCloseButton;
    private Rect mStartBounds;
    private Rect mFinalBounds;
    private float mStartScaleFinal;

    private ViewGroup mActivityContainer;
    private View mImageExpandView;
    private boolean mIsVisible = false;

    public ExpandPhotoHelper(Activity activity) {
        mActivity = activity;
        mShortAnimationDuration = mActivity.getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }

    //http://developer.android.com/intl/pt-br/training/animation/zoom.html

    /**
     * Zoom image from mThumbView over the whole activity
     *
     * @param thumbView thumbnail
     * @param imgUrl    url from image
     */
    public void zoomImageFromThumb(final View thumbView, String imgUrl) {
        mActivityContainer = (ViewGroup) mActivity.findViewById(android.R.id.content);
        mImageExpandView = mActivity.getLayoutInflater().inflate(R.layout.partial_expanded_image, mActivityContainer, false);
        mActivityContainer.addView(mImageExpandView);

        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        this.mThumbView = thumbView;
        // Load the high-resolution "zoomed-in" image.
        mExpandedImageView = (TouchImageView) mImageExpandView.findViewById(
                R.id.expanded_image_tiv);
        mExpandedBackGround = (RelativeLayout) mImageExpandView.findViewById(R.id.expanded_image_background);
        mCloseButton = (ImageView) mImageExpandView.findViewById(R.id.expanded_image_iv);
        mProgressBar = (ProgressBar) mImageExpandView.findViewById(R.id.expanded_image_pb);
        mExpandedBackGround.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mIsVisible = true;
        Glide.with(mActivity).load(imgUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                if (mExpandedBackGround.getVisibility() == View.VISIBLE) {
                    mExpandedImageView.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    mExpandedImageView.setImageBitmap(resource);
                }
            }
        });
        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        mStartBounds = new Rect();
        mFinalBounds = new Rect();
        Point globalOffset = new Point();
        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(mStartBounds);

        mActivityContainer.getGlobalVisibleRect(mFinalBounds, globalOffset);
        mStartBounds.offset(-globalOffset.x, -globalOffset.y);
        mFinalBounds.offset(-globalOffset.x, -globalOffset.y);
        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) mFinalBounds.width() / mFinalBounds.height()
                > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) mStartBounds.height() / mFinalBounds.height();
            float startWidth = startScale * mFinalBounds.width();
            float deltaWidth = (startWidth - mStartBounds.width()) / 2;
            mStartBounds.left -= deltaWidth;
            mStartBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) mStartBounds.width() / mFinalBounds.width();
            float startHeight = startScale * mFinalBounds.height();
            float deltaHeight = (startHeight - mStartBounds.height()) / 2;
            mStartBounds.top -= deltaHeight;
            mStartBounds.bottom += deltaHeight;
        }
        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        mCloseButton.setVisibility(View.VISIBLE);
        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mExpandedImageView.setPivotX(0f);
        mExpandedImageView.setPivotY(0f);
        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mExpandedImageView, View.X,
                        mStartBounds.left, mFinalBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.Y,
                        mStartBounds.top, mFinalBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(mExpandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        mStartScaleFinal = startScale;
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissPhoto();
            }
        });
    }

    /**
     * Dismisses the expanded image
     */
    public void dismissPhoto() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }
        mIsVisible = false;
        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator
                .ofFloat(mExpandedImageView, View.X, mStartBounds.left))
                .with(ObjectAnimator
                        .ofFloat(mExpandedImageView,
                                View.Y, mStartBounds.top))
                .with(ObjectAnimator
                        .ofFloat(mExpandedImageView,
                                View.SCALE_X, mStartScaleFinal))
                .with(ObjectAnimator
                        .ofFloat(mExpandedImageView,
                                View.SCALE_Y, mStartScaleFinal));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mThumbView.setAlpha(1f);
                mExpandedImageView.resetZoom();
                mExpandedImageView.setVisibility(View.GONE);
                mExpandedBackGround.setVisibility(View.GONE);
                Glide.with(mActivity).load("sfs").asBitmap().into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mExpandedImageView.setImageBitmap(resource);
                    }
                });
                mCloseButton.setVisibility(View.GONE);
                mCurrentAnimator = null;
                mActivityContainer.removeView(mImageExpandView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mThumbView.setAlpha(1f);
                mExpandedImageView.resetZoom();
                mExpandedImageView.setVisibility(View.GONE);
                mExpandedBackGround.setVisibility(View.GONE);
                mCloseButton.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    public boolean isVisible() {
        return mIsVisible;
    }
}
