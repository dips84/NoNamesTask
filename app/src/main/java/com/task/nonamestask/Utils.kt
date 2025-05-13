package com.task.nonamestask

import android.content.Context
import android.util.DisplayMetrics

object Utils {

    var noOfItems = 0

    public fun DpToPixel(c: Context, dp:Float):Float {

        val scaleFactor = c.resources.displayMetrics.densityDpi/DisplayMetrics.DENSITY_DEFAULT

        return dp*scaleFactor

    }

    public fun getNoOfItems(c:Context,width:Int,height:Int) {

        noOfItems = (c.resources.displayMetrics.heightPixels/height)*2
    }
}