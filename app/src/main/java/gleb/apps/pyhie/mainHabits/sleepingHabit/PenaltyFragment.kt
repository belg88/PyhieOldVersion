package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.pojos.UserPoints
import kotlinx.coroutines.launch

class PenaltyFragment : DialogFragment() {
    var userPoints = UserPoints()

    companion object {
        const val TAG = "PenaltyDialog"
        private const val KEY_TITLE = "email"

        fun newInstance(title: String): PenaltyFragment {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            val fragment = PenaltyFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val email = SharedPref(requireActivity()).getString(Keys.EMAIL)!!
        val view = inflater.inflate(R.layout.penalty_dialog_fragment, container, false)
        lifecycleScope.launch {
            userPoints = FirebaseService.getUserPoints(email)!!
            val newUserPoints = UserPoints(userPoints.totalPoints - 10, userPoints.level)
            if (newUserPoints.totalPoints < 0) newUserPoints.totalPoints = 0.0
            if (newUserPoints.currentLevelPoints < 0) {
                if (newUserPoints.level != 1) {
                    newUserPoints.currentLevel = userPoints.level - 1
                    newUserPoints.level = newUserPoints.currentLevel
                } else {
                    newUserPoints.currentLevelPoints = 0.0
                }
            }
            FirebaseService.insertUserPoints(newUserPoints, email)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val returnButton = view.findViewById<Button>(R.id.go_back_button)
        returnButton.setOnClickListener { dismiss() }
    }
}