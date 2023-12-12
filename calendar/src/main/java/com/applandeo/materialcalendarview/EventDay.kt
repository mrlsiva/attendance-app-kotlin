package com.applandeo.materialcalendarview

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RestrictTo
import com.applandeo.materialcalendarview.utils.EventImage
import com.applandeo.materialcalendarview.utils.setMidnight
import java.util.*

data class EventDay(val calendar: Calendar) {
    //An object which contains image to display in the day row
    internal var imageDrawable: EventImage = EventImage.EmptyEventImage

    internal var labelColor: Int = 0

    @set:RestrictTo(RestrictTo.Scope.LIBRARY)
    var isEnabled: Boolean = false

    init {
        calendar.setMidnight()
    }

    constructor(day: Calendar, drawable: Drawable) : this(day) {
        imageDrawable = EventImage.EventImageDrawable(drawable)
    }

    constructor(day: Calendar, @DrawableRes drawableRes: Int) : this(day) {
        imageDrawable = EventImage.EventImageResource(drawableRes)
    }

    constructor(day: Calendar, @DrawableRes drawableRes: Int, @ColorInt labelColor: Int) : this(day) {
        imageDrawable = EventImage.EventImageResource(drawableRes)
        this.labelColor = labelColor
    }
}