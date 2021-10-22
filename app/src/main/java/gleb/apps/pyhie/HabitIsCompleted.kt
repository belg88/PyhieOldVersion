package gleb.apps.pyhie

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.auth.User
import gleb.apps.pyhie.databinding.FragmentHabitIsCompletedBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch


class HabitIsCompleted : Fragment() {

    private lateinit var binding: FragmentHabitIsCompletedBinding
    private val args: HabitIsCompletedArgs by navArgs()
    var points = 0f
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        points = args.points
        email = SharedPref(requireContext()).getString(Keys.EMAIL)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHabitIsCompletedBinding.inflate(inflater,container, false)
        val anim = AnimationUtils.loadAnimation(requireContext(),R.anim.checkmark)
        binding.checkmark.startAnimation(anim)
        binding.circle.startAnimation(anim)
        binding.habitIsComplete.text = getString(R.string.habit_is_now_complete, args.habitName)
        binding.youEarnedText.text = getString(R.string.you_ve_earned_points, args.points)
        binding.backButton.setOnClickListener {
            findNavController().navigate(HabitIsCompletedDirections.actionHabitIsCompletedToHomeFragment())
        }

        lifecycleScope.launch {
            val userPoints = FirebaseService.getUserPoints(email)
            val newPoints = userPoints!!.totalPoints + points
            var newUserPoints = UserPoints(newPoints,userPoints.level)
            if (newUserPoints.newLevelReached) {
                newUserPoints = UserPoints(newPoints, newUserPoints.currentLevel)
                SharedPref(requireContext()).saveBoolean(Keys.NEW_LEVEL_NOT_REACHED, false)
            }
            FirebaseService.insertUserPoints(newUserPoints,email)
        }

        return binding.root
    }

}