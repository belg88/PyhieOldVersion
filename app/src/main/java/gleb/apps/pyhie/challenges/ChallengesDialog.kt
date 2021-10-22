package gleb.apps.pyhie.challenges

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import gleb.apps.pyhie.*
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.PenaltyFragment
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.Keys
import kotlinx.coroutines.launch
import java.security.Key

class ChallengesDialog: DialogFragment() {
    private lateinit var title: TextView
    private lateinit var name: TextView
    private lateinit var description: TextView
    private lateinit var reward: TextView
    private lateinit var timeRemaining: TextView
    private lateinit var backButton: Button
    private var currentChallenge = 0

    private var status = ""

    companion object {
        const val TAG = "ChallengesDialog"
        const val CHALLENGE_STATUS = "Challenge status"
        @JvmStatic
        fun newInstance(status: String) =
            ChallengesDialog().apply {
                arguments = Bundle().apply {
                    putString(CHALLENGE_STATUS, status)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.challenge_dialog,container,false)
        title = view.findViewById(R.id.title_challenge)
        name = view.findViewById(R.id.challenge_name)
        description = view.findViewById(R.id.challenge_description)
        backButton = view.findViewById(R.id.back_button)
        reward = view.findViewById(R.id.challenge_reward)
        timeRemaining = view.findViewById(R.id.challenge_time_remaining)
        currentChallenge = SharedPref(requireContext()).getInt(Keys.CURRENT_CHALLENGE)!!
        status = arguments?.getString(CHALLENGE_STATUS,"") ?: ""
        backButton.setOnClickListener { dismiss() }
        lifecycleScope.launch {
            val listOfChallenges = FirebaseService.getMainChallenges()
            when (status) {
                Keys.ACTIVATE_CHALLENGE -> {
                    if (currentChallenge != 0 && listOfChallenges != null && currentChallenge <= listOfChallenges.size) {
                        title.text = getString(R.string.you_have_a_new_challenge)
                        name.text = listOfChallenges[currentChallenge - 1].name
                        description.text = listOfChallenges[currentChallenge - 1].description
                        reward.text = getString(R.string.reward_challenge,listOfChallenges[currentChallenge - 1].reward)
                        timeRemaining.text = getString(R.string.time_remaining_challenge,listOfChallenges[currentChallenge - 1].timeAvailable)
                    }
                }
                Keys.CHALLENGE_COMPLETED -> {
                    if (listOfChallenges != null && currentChallenge <= listOfChallenges.size){
                        title.text = getString(R.string.challenge_complete, listOfChallenges[currentChallenge - 1].name)
                        name.visibility = View.GONE
                        description.text = if (currentChallenge == 1) getString(R.string.challenge1_complete) else ""
                        reward.text = getString(R.string.reward_complete, listOfChallenges[currentChallenge - 1].reward)
                        timeRemaining.visibility = View.INVISIBLE
                        SharedPref(requireContext()).saveInt(Keys.CURRENT_CHALLENGE,currentChallenge + 1)
                        val userPoints = FirebaseService.getUserPoints(SharedPref(requireContext()).getString(Keys.EMAIL)!!)
                        val newPoints = userPoints!!.totalPoints + listOfChallenges[currentChallenge - 1].reward.toDouble()
                        var newUserPoints = UserPoints(newPoints,userPoints.level)
                        if (newUserPoints.newLevelReached) {
                            newUserPoints = UserPoints(newPoints, newUserPoints.currentLevel)
                            SharedPref(requireContext()).saveBoolean(Keys.NEW_LEVEL_NOT_REACHED, false)
                        }
                        FirebaseService.insertUserPoints(newUserPoints,SharedPref(requireContext()).getString(Keys.EMAIL)!!)
                    }
                }
                Keys.CHALLENGE_FAILED -> {
                    if (listOfChallenges != null && currentChallenge <= listOfChallenges.size) {
                        title.text = getString(R.string.challenge_failed, listOfChallenges[currentChallenge - 1].name)
                        name.visibility = View.GONE
                        description.visibility = View.GONE
                        reward.visibility = View.GONE
                        timeRemaining.visibility = View.GONE
                        if (currentChallenge != 3) SharedPref(requireContext()).saveInt(Keys.CURRENT_CHALLENGE,currentChallenge + 1)
                    }
                }
            }
        }

        return view
    }
}