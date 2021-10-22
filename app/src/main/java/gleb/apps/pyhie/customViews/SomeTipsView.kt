package gleb.apps.pyhie.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import gleb.apps.pyhie.R

class SomeTipsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttributeSet: Int = 0
): LinearLayout(context,attrs, defStyleAttributeSet) {

    init {
        inflate(context, R.layout.tips_layout,this)
    }

    fun setMessage(text:String){
        val messageText: TextView = findViewById(R.id.message)
        messageText.text = text
    }


}