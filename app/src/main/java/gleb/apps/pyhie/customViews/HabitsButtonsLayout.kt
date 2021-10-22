package gleb.apps.pyhie.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import gleb.apps.pyhie.R
import gleb.apps.pyhie.mainMenu.DailyAdviceListDirections
import gleb.apps.pyhie.mainMenu.GoodHabitsFragment

class HabitsButtonsLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttributeSet: Int = 0
): ConstraintLayout(context,attrs, defStyleAttributeSet) {
    init {
        View.inflate(context, R.layout.habits_button_layout,this)
    }

    val firstButton:ImageView = findViewById(R.id.button_one)
    val secondButton: ImageView = findViewById(R.id.button_two)

    fun firstButtonText(text: String) {
        val name = findViewById<TextView>(R.id.text)
        name.text = text
    }

    fun firstButtonImage(imageSource: Int) {
        val image = findViewById<ImageView>(R.id.button_one)
        image.setImageResource(imageSource)
    }

    fun firstButtonAlpha (alpha: Float, isClickable: Boolean) {
        val image = findViewById<ImageView>(R.id.button_one)
        image.alpha = alpha
        image.isClickable = isClickable
    }

    fun secondButtonVisibility(visibility: Int) {
        val image = findViewById<ImageView>(R.id.button_two)
        image.visibility = visibility
    }

    fun firstButtonAvailabilityText (text: String) {
        val textView = findViewById<TextView>(R.id.available_at_text)
        textView.text = text
    }

    fun secondButtonText(text: String) {
        val name = findViewById<TextView>(R.id.text1)
        name.text = text
    }

    fun secondButtonImage(imageSource: Int) {
        val image = findViewById<ImageView>(R.id.button_two)
        image.setImageResource(imageSource)
    }

    fun secondButtonAlpha (alpha: Float, isClickable: Boolean) {
        val image = findViewById<ImageView>(R.id.button_two)
        image.alpha = alpha
        image.isClickable = isClickable
    }

    fun secondButtonAvailabilityText (text: String) {
        val textView = findViewById<TextView>(R.id.available_at_text1)
        textView.text = text
    }

}