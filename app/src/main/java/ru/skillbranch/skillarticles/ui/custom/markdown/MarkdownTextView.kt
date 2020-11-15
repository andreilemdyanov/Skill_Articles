package ru.skillbranch.skillarticles.ui.custom.markdown

import android.content.Context
import android.graphics.Canvas
import android.text.Spannable
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import androidx.core.graphics.withTranslation
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.extensions.attrValue

class MarkdownTextView constructor(
    context: Context,
    fontSize: Float
) : androidx.appcompat.widget.AppCompatTextView(context, null, 0), IMarkdownView {

    val color = context.attrValue(R.attr.colorOnBackground)

    override var fontSize: Float = fontSize
        set(value) {
            textSize = value
            field = value
        }
    override val spannableContent: Spannable
        get() = text as Spannable

    private val searchBgHelper = SearchBgHelper(context) {
        //TODO
    }

    init {
        setTextColor(color)
        textSize = fontSize
        movementMethod = LinkMovementMethod.getInstance()
    }

    override fun onDraw(canvas: Canvas) {
        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingRight.toFloat()) {
                searchBgHelper.draw(canvas, text as Spanned, layout)
            }
        }
        super.onDraw(canvas)
    }
}