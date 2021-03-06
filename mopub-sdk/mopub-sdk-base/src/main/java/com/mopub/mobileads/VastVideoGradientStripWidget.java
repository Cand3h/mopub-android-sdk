// Copyright 2018-2020 Twitter, Inc.
// Licensed under the MoPub SDK License Agreement
// http://www.mopub.com/legal/sdk-license-agreement/

package com.mopub.mobileads;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mopub.common.logging.MoPubLog;
import com.mopub.common.util.Dips;
import com.mopub.mobileads.resource.DrawableConstants;

import static com.mopub.common.logging.MoPubLog.SdkLogEvent.CUSTOM;

public class VastVideoGradientStripWidget extends ImageView {
    private int mVisibilityForCompanionAd;
    private boolean mHasCompanionAd;
    private boolean mIsVideoComplete;
    private boolean mAlwaysVisibleDuringVideo;

    public VastVideoGradientStripWidget(@NonNull final Context context,
            @NonNull final GradientDrawable.Orientation gradientOrientation,
            final boolean hasCompanionAd, final int visibilityForCompanionAd, final int layoutVerb,
            final int layoutAnchor, final boolean alwaysVisibleDuringVideo) {
        super(context);

        mVisibilityForCompanionAd = visibilityForCompanionAd;
        mHasCompanionAd = hasCompanionAd;
        mAlwaysVisibleDuringVideo = alwaysVisibleDuringVideo;

        final GradientDrawable gradientDrawable = new GradientDrawable(gradientOrientation,
                new int[] {DrawableConstants.GradientStrip.START_COLOR,
                        DrawableConstants.GradientStrip.END_COLOR});
        setImageDrawable(gradientDrawable);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                Dips.dipsToIntPixels(DrawableConstants.GradientStrip.GRADIENT_STRIP_HEIGHT_DIPS,
                        context));
        layoutParams.addRule(layoutVerb, layoutAnchor);
        setLayoutParams(layoutParams);

        updateVisibility();
    }

    void notifyVideoComplete() {
        mIsVideoComplete = true;
        updateVisibility();
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        updateVisibility();
    }

    private void updateVisibility() {
        if (mIsVideoComplete) {
            if (mHasCompanionAd) {
                setVisibility(mVisibilityForCompanionAd);
            } else {
                setVisibility(View.GONE);
            }

            return;
        }

        if (mAlwaysVisibleDuringVideo) {
            setVisibility(View.VISIBLE);
            return;
        }

        final int currentOrientation = getResources().getConfiguration().orientation;

        switch (currentOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                setVisibility(View.VISIBLE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                setVisibility(View.INVISIBLE);
                break;
            case Configuration.ORIENTATION_UNDEFINED:
                MoPubLog.log(CUSTOM, "Screen orientation undefined: do not show gradient strip widget");
                setVisibility(View.INVISIBLE);
                break;
            case Configuration.ORIENTATION_SQUARE:
                MoPubLog.log(CUSTOM, "Screen orientation is deprecated ORIENTATION_SQUARE: do not show gradient strip widget");
                setVisibility(View.INVISIBLE);
                break;
            default:
                MoPubLog.log(CUSTOM, "Unrecognized screen orientation: do not show gradient strip widget");
                setVisibility(View.INVISIBLE);
                break;
        }
    }
}
