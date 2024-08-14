package com.mcstarrysky.land.util

import java.awt.Color
import kotlin.math.min


fun modifiedColorCode(colorCode: String, ratio: Float): String {
    val colorStr = colorCode.removePrefix("#")

    val red = Integer.parseInt(colorStr.substring(0, 2), 16)
    val green = Integer.parseInt(colorStr.substring(2, 4), 16)
    val blue = Integer.parseInt(colorStr.substring(4, 6), 16)

    val newRed = (red * ratio).toInt()
    val newGreen = (green * ratio).toInt()
    val newBlue = (blue * ratio).toInt()

    val finalRed = min(newRed + 30, 255)
    val finalGreen = min(newGreen + 30, 255)
    val finalBlue = min(newBlue + 30, 255)

    return String.format("#%02x%02x%02x", finalRed, finalGreen, finalBlue)
}

fun whiteColorCode(colorCode: String): String {
    return gradientColors(colorCode, "#ffffff", 10)[8]
}

fun gradientColors(cA: String, cB: String, number: Int): List<String> {
    val a = hex2RGB(cA)
    val b = hex2RGB(cB)
    val colors: MutableList<String> = ArrayList()
    for (i in 0 until number) {
        val aR = a[0]
        val aG = a[1]
        val aB = a[2]
        val bR = b[0]
        val bG = b[1]
        val bB = b[2]
        colors.add(
            rgb2Hex(
                calculateColor(aR, bR, number - 1, i),
                calculateColor(aG, bG, number - 1, i),
                calculateColor(aB, bB, number - 1, i)
            )
        )
    }
    return colors
}

fun calculateColor(a: Int, b: Int, step: Int, number: Int): Int {
    return a + (b - a) * number / step
}

fun rgb2Hex(r: Int, g: Int, b: Int): String {
    return String.format("#%02X%02X%02X", r, g, b)
}

fun hex2RGB(hexStr: String): IntArray {
    if (hexStr.length == 7) {
        val rgb = IntArray(3)
        rgb[0] = hexStr.substring(1, 3).toInt(16)
        rgb[1] = hexStr.substring(3, 5).toInt(16)
        rgb[2] = hexStr.substring(5, 7).toInt(16)
        return rgb
    }
    throw Exception()
}

fun hexToColor(hex: String): Color {
    val red = hex.substring(1, 3).toInt(16)
    val green = hex.substring(3, 5).toInt(16)
    val blue = hex.substring(5, 7).toInt(16)

    return Color(red, green, blue)
}