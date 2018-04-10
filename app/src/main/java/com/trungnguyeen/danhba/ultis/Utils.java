package com.trungnguyeen.danhba.ultis;

import android.graphics.Color;

import java.util.Random;

/**
 * Created by trungnguyeen on 4/9/18.
 */

public class Utils {
    public static int GeneratorColor(){
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        return color;
    }
}
