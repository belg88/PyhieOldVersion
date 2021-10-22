package gleb.apps.pyhie.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import gleb.apps.pyhie.R

class DailyListItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttributeSet: Int = 0
): ConstraintLayout(context,attrs, defStyleAttributeSet)  {
    init {
        View.inflate(context, R.layout.daily_advice_list_item,this)
    }

    fun dayName(text:String) {
        val dayName = findViewById<TextView>(R.id.textView1)
        dayName.text = text
        dayName.setTextColor(Color.WHITE)
    }
}