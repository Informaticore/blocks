package com.dudes.android.blocks;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * User: tobiasbuchholz @ PressMatrix GmbH
 * Date: 06.04.14 | Time: 23:12
 */
public class SoundEffectHelper {
    private static final int    SOUND_ID_SHOOT = 0;
    private static final int    SOUND_ID_WUSH = 1;
    private static final int    SOUND_ID_YAY = 2;

    private static final int    SOUND_EFFECT_COUNT          = 3;

    private SoundPool           mSoundPool;
    private int[]               mSoundIds;

    public SoundEffectHelper() {
        initSoundPool();
    }

    private void initSoundPool() {
        final Context context = BlocksApplication.getInstance();
        mSoundPool = new SoundPool(SOUND_EFFECT_COUNT, AudioManager.STREAM_MUSIC, 0);

        mSoundIds = new int[SOUND_EFFECT_COUNT];
        mSoundIds[SOUND_ID_SHOOT] = mSoundPool.load(context, R.raw.shoot, 1);
        mSoundIds[SOUND_ID_WUSH] = mSoundPool.load(context, R.raw.wush, 1);
        mSoundIds[SOUND_ID_YAY] = mSoundPool.load(context, R.raw.yay, 1);
    }

    public void playShootSound() {
        playSound(SOUND_ID_SHOOT);
    }

    private void playSound(final int soundId) {
        mSoundPool.play(mSoundIds[soundId], 100, 100, 1, 0, 1.2f);
    }

    public void playWushSound() {
        playSound(SOUND_ID_WUSH);
    }

    public void playYaySound() {
        playSound(SOUND_ID_YAY);
    }
}
