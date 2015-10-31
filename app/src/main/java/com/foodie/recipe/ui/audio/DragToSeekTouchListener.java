package com.foodie.recipe.ui.audio;

/**
 * Created by root on 21/7/15.
 */

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DragToSeekTouchListener implements View.OnTouchListener {

    public interface DragToSeekUpdateListener {
        void onSeekStarted();
        void onSeekUpdate(float index);
        void onSeekCompleted(float seekedToPercent);
    }

    private static final String TAG = "DragToSeek";
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = INVALID_POINTER_ID;
    private float mLastTouchX = 0;
    private float mLastTouchY = 0;

    private boolean mSeekStarted = false;
    private boolean mSeeking = false;
    // Robustness parameters for starting seek. We start only when there are at least
    // kNumContinousHorizontalMovesForSeek seeks.
    // This is required, as in some phones, e.g. in Nexus 5 for SW moves we get dx = -1, dy = -1 for
    // the first ACTION_MOVE event.
    private int mContinuousHorizontalMoveCount = 0;
    private int kNumContinousHorizontalMovesForSeek = 2;
    private DragToSeekUpdateListener mListener;
    private long lengthSeeked;

    public DragToSeekTouchListener( DragToSeekUpdateListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean retVal = false;
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                int pointerIndex = MotionEventCompat.getActionIndex(event);

                float x = MotionEventCompat.getX(event, pointerIndex);
                float y = MotionEventCompat.getY(event, pointerIndex);

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                int pointerIndex =
                        MotionEventCompat.findPointerIndex(event, mActivePointerId);

                float x = MotionEventCompat.getX(event, pointerIndex);
                float y = MotionEventCompat.getY(event, pointerIndex);

                float dx = x - mLastTouchX;
                float dy = y - mLastTouchY;

                if (crossesSeekThreshold(dx, dy, mSeeking)) {
                    if (++mContinuousHorizontalMoveCount >= kNumContinousHorizontalMovesForSeek) {
                        Log.v(TAG, "mContinuousHorizontalMoveCount > threshold: "
                                + mContinuousHorizontalMoveCount);
                        boolean startedSeek = seekVideo(v, x, y, dx, dy, mSeeking);
                        if (!mSeeking && startedSeek) {
                            Log.v(TAG, "Starting seek");
                            if (mListener != null) {
                                mListener.onSeekStarted();
                            }
                            mSeeking = true;
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }
                    mLastTouchX = x;
                    mLastTouchY = y;
                } else {
                    mContinuousHorizontalMoveCount = 0;
                }
                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mSeeking) {
                    if (mListener != null) {
                        float viewWidth = (float) v.getWidth();
                        mListener.onSeekCompleted((lengthSeeked / viewWidth) * 100);
                        mSeekStarted = false;
                    }
                }
                mSeeking = false;
                mContinuousHorizontalMoveCount = 0;
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                int pointerIndex = MotionEventCompat.getActionIndex(event);
                int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new active pointer
                    // and adjust accordingly.

                    // Choose the minimum index which is not the current pointerIndex.
                    // This is a random choice as the order of pointer indices is undefined.
                    int newPointerIndex = pointerIndex == 0 ? 1 : 0;

                    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    mActivePointerId =
                            MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }
        }

        return retVal;
    }

    boolean crossesSeekThreshold(float dx, float dy, boolean alreadySeeking) {
        Log.v(TAG, "CrossesThreshold: dx = " + dx + ", dy = " + dy);

        // Don't start seek unless angle of drag is at least 15 degrees,
        // i.e. dy/dx > 0.268, i.e. arctan(dy/dx) > 15 degrees.
        // However, once seek is started, continue with the seek, irrespective of the angle.
        if (!alreadySeeking && (Math.abs(dx) == 0 || Math.abs(dy) / Math.abs(dx) > 0.268)) {
            return  false;
        }
        mSeekStarted = true;
        return true;
    }

    boolean seekVideo(View v, float x, float y, float dx, float dy, boolean alreadySeeking) {
        //Log.v(TAG, "SeekVideo: x = " + x + ", y = " + y + ", dx = " + dx
        //        + ", dy = " + dy + ", viewWidth = " + v.getWidth());

        // Pause the video before starting seek as we want the video to not start playing until the
        // user does an ACTION_UP.
        // TODO(sgoyal): This logic is flaky. Try using currently playing video to guard this.
        if (mSeekStarted || alreadySeeking) {
            float viewWidth = (float) v.getWidth();
            float seekDeltaFraction = dx / viewWidth;
            lengthSeeked += dx;

            if (lengthSeeked <= 0) {
                lengthSeeked = 0;
            }

            if (lengthSeeked >= viewWidth) {
                lengthSeeked = (long)viewWidth;
            }


            if (mListener != null && lengthSeeked > 0 && lengthSeeked < viewWidth) {
                // seek percentage
                mListener.onSeekUpdate((lengthSeeked / viewWidth) * 100);
            }
            return true;
        }
        return false;
    }
}