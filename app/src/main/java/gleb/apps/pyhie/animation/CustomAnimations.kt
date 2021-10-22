package gleb.apps.pyhie.animation

import android.animation.Animator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import gleb.apps.pyhie.util.ViewAnimation
import java.time.Duration
import java.util.*
import kotlin.concurrent.schedule

object CustomAnimations {
    fun slideUpOffTheScreen(view: View, translationY: Float, duration: Long, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            val anim = view.animate()
            anim.translationY(translationY)
            anim.duration = duration
            anim.start()
            anim.setListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    view.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })
        }, delay)
    }


    fun slideUpToTheScreen(view: View, duration: Long, fromTranslationY: Float, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.translationY = fromTranslationY
            view.visibility = View.VISIBLE
            val anim = view.animate()
            anim.translationY(0f)
            anim.duration = duration
            anim.start()

        }, delay)
    }

    fun slideInFromTheLeft(view: View, duration: Long, delay: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = View.VISIBLE
            view.translationX = -1000f
            val anim = view.animate()
            anim.translationX(0f)
            anim.duration = duration
            anim.start()
        }, delay)
    }

    fun appearFromZeroAlpha(view: View, delay: Long, duration: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.visibility = View.VISIBLE
            view.alpha = 0f
            val anim = view.animate()
            anim.duration = duration
            anim.alpha(1f)
            anim.start()
        }, delay)
    }
}
