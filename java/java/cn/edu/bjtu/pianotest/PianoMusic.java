package cn.edu.bjtu.pianotest;

import java.util.HashMap;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;



public class PianoMusic {
    //由于资源包含了钢琴的全部音效，特此备注：资源里与琴键的对应为：
    //c2 - C; c3 - c; c4 - c1(中央C); c5 - c2
    int Music[] = {R.raw.c2, R.raw.d2, R.raw.e2, R.raw.f2, R.raw.g2, R.raw.a2, R.raw.b2,
                    R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
                    R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4,
                    R.raw.c5, R.raw.d5, R.raw.e5, R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5,
                    R.raw.c2m, R.raw.d2m, R.raw.f2m, R.raw.g2m, R.raw.a2m,
                    R.raw.c3m, R.raw.d3m, R.raw.f3m, R.raw.g3m, R.raw.a3m,
                    R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m,
                    R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m, R.raw.a5m };

    //使用soundpool播放音效
    SoundPool soundPool;
    HashMap<Integer, Integer> soundPoolMap;

    //加载ID
    public PianoMusic(Context context) {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 100);
        soundPoolMap = new HashMap<Integer, Integer>();
        //加载音效ID到SoundPoolMap
        for (int i = 0; i < Music.length; i++) {
            soundPoolMap.put(i, soundPool.load(context, Music[i], 1));
        }
    }

    //播放
    public int soundPlay(int no) {
        return soundPool.play(soundPoolMap.get(no), 100, 100, 1, 0, 1.0f);
    }

    //结束
    public int soundOver() {
        return soundPool.play(soundPoolMap.get(1), 100, 100, 1, 0, 1.0f);
    }

    @Override
    protected void finalize() throws Throwable {
        soundPool.release();
        super.finalize();
    }
}