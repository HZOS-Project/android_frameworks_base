/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.display.brightness;

import android.hardware.display.BrightnessInfo;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.TimeUtils;

/**
 * Represents a particular brightness change event.
 */
public final class BrightnessEvent {
    public static final int FLAG_RBC = 0x1;
    public static final int FLAG_INVALID_LUX = 0x2;
    public static final int FLAG_DOZE_SCALE = 0x3;
    public static final int FLAG_USER_SET = 0x4;

    private BrightnessReason mReason = new BrightnessReason();
    private int mDisplayId;
    private float mLux;
    private float mPreThresholdLux;
    private long mTime;
    private float mBrightness;
    private float mRecommendedBrightness;
    private float mPreThresholdBrightness;
    private float mHbmMax;
    private float mThermalMax;
    private int mHbmMode;
    private int mFlags;
    private int mAdjustmentFlags;

    public BrightnessEvent(BrightnessEvent that) {
        copyFrom(that);
    }

    public BrightnessEvent(int displayId) {
        this.mDisplayId = displayId;
        reset();
    }

    /**
     * A utility to clone a brightness event into another event
     *
     * @param that BrightnessEvent which is to be copied
     */
    public void copyFrom(BrightnessEvent that) {
        mDisplayId = that.getDisplayId();
        mTime = that.getTime();
        mLux = that.getLux();
        mPreThresholdLux = that.getPreThresholdLux();
        mBrightness = that.getBrightness();
        mRecommendedBrightness = that.getRecommendedBrightness();
        mPreThresholdBrightness = that.getPreThresholdBrightness();
        mHbmMax = that.getHbmMax();
        mThermalMax = that.getThermalMax();
        mFlags = that.getFlags();
        mHbmMode = that.getHbmMode();
        mReason.set(that.getReason());
        mAdjustmentFlags = that.getAdjustmentFlags();
    }

    /**
     * A utility to reset the BrightnessEvent to default values
     */
    public void reset() {
        mTime = SystemClock.uptimeMillis();
        mBrightness = PowerManager.BRIGHTNESS_INVALID_FLOAT;
        mRecommendedBrightness = PowerManager.BRIGHTNESS_INVALID_FLOAT;
        mLux = 0;
        mPreThresholdLux = 0;
        mPreThresholdBrightness = PowerManager.BRIGHTNESS_INVALID_FLOAT;
        mHbmMax = PowerManager.BRIGHTNESS_MAX;
        mThermalMax = PowerManager.BRIGHTNESS_MAX;
        mFlags = 0;
        mHbmMode = BrightnessInfo.HIGH_BRIGHTNESS_MODE_OFF;
        mReason.set(null);
        mAdjustmentFlags = 0;
    }

    /**
     * A utility to compare two BrightnessEvents. This purposefully ignores comparing time as the
     * two events might have been created at different times, but essentially hold the same
     * underlying values
     *
     * @param that The brightnessEvent with which the current brightnessEvent is to be compared
     * @return A boolean value representing if the two events are same or not.
     */
    public boolean equalsMainData(BrightnessEvent that) {
        // This equals comparison purposefully ignores time since it is regularly changing and
        // we don't want to log a brightness event just because the time changed.
        return mDisplayId == that.mDisplayId
                && Float.floatToRawIntBits(mBrightness)
                == Float.floatToRawIntBits(that.mBrightness)
                && Float.floatToRawIntBits(mRecommendedBrightness)
                == Float.floatToRawIntBits(that.mRecommendedBrightness)
                && Float.floatToRawIntBits(mPreThresholdBrightness)
                == Float.floatToRawIntBits(that.mPreThresholdBrightness)
                && Float.floatToRawIntBits(mLux) == Float.floatToRawIntBits(that.mLux)
                && Float.floatToRawIntBits(mPreThresholdLux)
                == Float.floatToRawIntBits(that.mPreThresholdLux)
                && Float.floatToRawIntBits(mHbmMax) == Float.floatToRawIntBits(that.mHbmMax)
                && mHbmMode == that.mHbmMode
                && Float.floatToRawIntBits(mThermalMax)
                == Float.floatToRawIntBits(that.mThermalMax)
                && mFlags == that.mFlags
                && mAdjustmentFlags == that.mAdjustmentFlags
                && mReason.equals(that.mReason);
    }

    /**
     * A utility to stringify a BrightnessEvent
     * @param includeTime Indicates if the time field is to be added in the stringify version of the
     *                    BrightnessEvent
     * @return A stringified BrightnessEvent
     */
    public String toString(boolean includeTime) {
        return (includeTime ? TimeUtils.formatForLogging(mTime) + " - " : "")
                + "BrightnessEvent: "
                + "disp=" + mDisplayId
                + ", brt=" + mBrightness + ((mFlags & FLAG_USER_SET) != 0 ? "(user_set)" : "")
                + ", rcmdBrt=" + mRecommendedBrightness
                + ", preBrt=" + mPreThresholdBrightness
                + ", lux=" + mLux
                + ", preLux=" + mPreThresholdLux
                + ", hbmMax=" + mHbmMax
                + ", hbmMode=" + BrightnessInfo.hbmToString(mHbmMode)
                + ", thrmMax=" + mThermalMax
                + ", flags=" + flagsToString()
                + ", reason=" + mReason.toString(mAdjustmentFlags);
    }

    @Override
    public String toString() {
        return toString(/* includeTime */ true);
    }

    public void setReason(BrightnessReason reason) {
        this.mReason = reason;
    }

    public BrightnessReason getReason() {
        return mReason;
    }

    public int getDisplayId() {
        return mDisplayId;
    }

    public void setDisplayId(int displayId) {
        this.mDisplayId = displayId;
    }

    public float getLux() {
        return mLux;
    }

    public void setLux(float lux) {
        this.mLux = lux;
    }

    public float getPreThresholdLux() {
        return mPreThresholdLux;
    }

    public void setPreThresholdLux(float preThresholdLux) {
        this.mPreThresholdLux = preThresholdLux;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public float getBrightness() {
        return mBrightness;
    }

    public void setBrightness(float brightness) {
        this.mBrightness = brightness;
    }

    public float getRecommendedBrightness() {
        return mRecommendedBrightness;
    }

    public void setRecommendedBrightness(float recommendedBrightness) {
        this.mRecommendedBrightness = recommendedBrightness;
    }

    public float getPreThresholdBrightness() {
        return mPreThresholdBrightness;
    }

    public void setPreThresholdBrightness(float preThresholdBrightness) {
        this.mPreThresholdBrightness = preThresholdBrightness;
    }

    public float getHbmMax() {
        return mHbmMax;
    }

    public void setHbmMax(float hbmMax) {
        this.mHbmMax = hbmMax;
    }

    public float getThermalMax() {
        return mThermalMax;
    }

    public void setThermalMax(float thermalMax) {
        this.mThermalMax = thermalMax;
    }

    public int getHbmMode() {
        return mHbmMode;
    }

    public void setHbmMode(int hbmMode) {
        this.mHbmMode = hbmMode;
    }

    public int getFlags() {
        return mFlags;
    }

    public void setFlags(int flags) {
        this.mFlags = flags;
    }

    public int getAdjustmentFlags() {
        return mAdjustmentFlags;
    }

    public void setAdjustmentFlags(int adjustmentFlags) {
        this.mAdjustmentFlags = adjustmentFlags;
    }

    private String flagsToString() {
        return ((mFlags & FLAG_USER_SET) != 0 ? "user_set " : "")
                + ((mFlags & FLAG_RBC) != 0 ? "rbc " : "")
                + ((mFlags & FLAG_INVALID_LUX) != 0 ? "invalid_lux " : "")
                + ((mFlags & FLAG_DOZE_SCALE) != 0 ? "doze_scale " : "");
    }
}
