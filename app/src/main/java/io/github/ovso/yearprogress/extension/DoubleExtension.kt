package io.github.ovso.yearprogress.extension

fun Double.round0(): Double = "%.0f".format(this).toDouble()

fun Double.round1(): Double = "%.1f".format(this).toDouble()

fun Double.round2(): Double = "%.2f".format(this).toDouble()