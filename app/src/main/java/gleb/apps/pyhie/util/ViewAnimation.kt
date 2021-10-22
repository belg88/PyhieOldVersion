package gleb.apps.pyhie.util

import android.animation.Animator
import android.util.Log
import android.view.View
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Delay

class ViewAnimation {
    fun rotateFab(view: View, rotate: Boolean): Boolean {
        val anim = view.animate()
        anim.duration = 200
        anim.setListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {

            }

        }).rotation(if (rotate) 135f else 0f)
        return rotate
    }

    fun showIn(view: ExtendedFloatingActionButton) {
        val anim = view.animate()
        anim.duration = 200
        anim.translationY(0f)
        anim.alpha(1f)
        anim.setListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {

            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationStart(animation: Animator?) {
                view.visibility = View.VISIBLE
                view.translationY = view.height.toFloat()
            }

        })
        anim.start()

    }

    fun showOut(view: ExtendedFloatingActionButton) {
        val anim = view.animate()
        anim.duration = 200
        anim.translationY(view.height.toFloat())
        anim.setListener(object : Animator.AnimatorListener{
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


    }

     fun init (view: ExtendedFloatingActionButton) {
         view.translationY = 100f
         view.visibility = View.GONE
     }
}