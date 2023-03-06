package com.androidcollider.easyfin.common.managers.ui.letter_tile

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import com.androidcollider.easyfin.R
import kotlin.math.abs

/**
 * Used to create a [Bitmap] that contains a letter used in the English
 * alphabet or digit, if there is no letter or digit available, a default image
 * is shown instead
 *
 * @param context The [Context] to use
 */
class LetterTileManager(context: Context) {
    /**
     * The [TextPaint] used to draw the letter onto the tile
     */
    private val mPaint = TextPaint()

    /**
     * The bounds that enclose the letter
     */
    private val mBounds = Rect()

    /**
     * The [Canvas] to draw on
     */
    private val mCanvas = Canvas()

    /**
     * The first char of the name being displayed
     */
    private val mFirstChar = CharArray(1)

    /**
     * The background colors of the tile
     */
    private val mColors: TypedArray

    /**
     * The font size used to display the letter
     */
    private val mTileLetterFontSize: Int

    /**
     * The default size used to display the letter tile
     */
    private val mTileLetterSize: Int

    init {
        val res = context.resources
        mPaint.typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
        mPaint.color = Color.WHITE
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.isAntiAlias = true
        mColors = res.obtainTypedArray(R.array.material_palette_array)
        mTileLetterFontSize = res.getDimensionPixelSize(R.dimen.tile_letter_font_size)
        mTileLetterSize = res.getDimensionPixelSize(R.dimen.letter_tile_size)
    }

    /**
     * @param displayName The name used to create the letter for the tile
     * @param key         The key used to generate the background color for the tile
     * @param width       The desired width of the tile
     * @param height      The desired height of the tile
     * @return A [Bitmap] that contains a letter used in the English
     * alphabet or digit, if there is no letter or digit available, a
     * default image is shown instead
     */
    private fun getLetterTile(displayName: String, key: String, width: Int, height: Int): Bitmap {
        var displayName1 = displayName
        var key1 = key
        if (displayName1.isEmpty()) displayName1 = "A"
        if (key1.isEmpty()) key1 = "A"
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val firstChar = displayName1[0]
        val c = mCanvas
        c.setBitmap(bitmap)
        c.drawColor(pickColor(key1))
        mFirstChar[0] = firstChar.uppercaseChar()
        mPaint.textSize = mTileLetterFontSize.toFloat()
        mPaint.getTextBounds(mFirstChar, 0, 1, mBounds)
        c.drawText(
            mFirstChar,
            0,
            1,
            width / 2f,
            height / 2f + (mBounds.bottom - mBounds.top) / 2f,
            mPaint
        )
        return getCircleBitmap(bitmap)
    }

    fun getLetterTile(displayName: String): Bitmap {
        return getLetterTile(displayName, displayName, mTileLetterSize, mTileLetterSize)
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.RED
        canvas.drawOval(rectF, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        bitmap.recycle()
        return output
    }

    /**
     * @param key The key used to generate the tile color
     * @return A new or previously chosen color for `key` used as the
     * tile background color
     */
    private fun pickColor(key: String): Int {
        // String.hashCode() is not supposed to change across java versions, so
        // this should guarantee the same key always maps to the same color
        val color = abs(key.hashCode()) % NUM_OF_TILE_COLORS
        return try {
            mColors.getColor(color, Color.BLACK)
        } finally {
            //mColors.recycle()
        }
    }

    companion object {
        /**
         * The number of available tile colors (see R.array.letter_tile_colors)
         */
        private const val NUM_OF_TILE_COLORS = 19
    }
}