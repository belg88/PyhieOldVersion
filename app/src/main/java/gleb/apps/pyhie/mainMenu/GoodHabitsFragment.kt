package gleb.apps.pyhie.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import gleb.apps.pyhie.R
import gleb.apps.pyhie.customViews.HabitsButtonsLayout
import gleb.apps.pyhie.databinding.FragmentGoodHabitsBinding


class GoodHabitsFragment : Fragment() {
    private lateinit var binding: FragmentGoodHabitsBinding
    var currentLevel = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoodHabitsBinding.inflate(inflater, container, false).apply {
            image.setImageResource(R.drawable.good_habits)
            title.text = getString(R.string.good_habits)
            buttonLayout1.apply {
                firstButtonImage(R.drawable.daily_workout_image)
                secondButtonImage(R.drawable.morning_routine_image)
                firstButtonText("Daily Exercises")
                secondButtonText("Morning Routine")
                firstButton.setOnClickListener {
                    findNavController().navigate(GoodHabitsFragmentDirections.actionSecondaryFragmentToDailyExercises())
                }
                secondButton.setOnClickListener {
                    findNavController().navigate(GoodHabitsFragmentDirections.actionSecondaryFragmentToDailyExercises())
                }

            }
            buttonLayout2.apply {
                firstButtonImage(R.drawable.healthy_eating_image)
                secondButtonImage(R.drawable.ic_money_saving_image)
                firstButtonText("Healthy Eating")
                secondButtonText("Money Saving")
            }
            buttonLayout3.apply {
                firstButtonImage(R.drawable.ic_goals_image)
                secondButtonImage(R.drawable.ic_motivation_image)
                firstButtonText("Set and Achieve Your Goals")
                secondButtonText("Motivate Yourself")
            }
            buttonLayout4.apply {
                firstButtonImage(R.drawable.money_tracking_image)
                secondButtonImage(R.drawable.counting_calories_image)
                firstButtonText("Money Tracking")
                secondButtonText("Counting Calories")
            }
        }
        initializeStartViews()

        return binding.root
    }

    fun navigate(){
        findNavController().navigate(GoodHabitsFragmentDirections.actionSecondaryFragmentToDailyExercises())
    }

    private fun initializeStartViews() {
        binding.buttonLayout1.apply {
            val firstButtonText = if (currentLevel>=1) "" else "Available at Level 1"
            val secondButtonText = if (currentLevel>=2) "" else "Available at Level 2"
            val alpha1 = if (currentLevel>=1) 1f else 0.6f
            val alpha2 = if (currentLevel>=2) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel>=1)
            secondButtonAlpha(alpha2, currentLevel>=2)
        }
        binding.buttonLayout2.apply {
            val firstButtonText = if (currentLevel>=3) "" else "Available at Level 3"
            val secondButtonText = if (currentLevel>=3) "" else "Available at Level 3"
            val alpha1 = if (currentLevel>=3) 1f else 0.6f
            val alpha2 = if (currentLevel>=3) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel>=2)
            secondButtonAlpha(alpha2, currentLevel>=3)
        }
        binding.buttonLayout3.apply {
            val firstButtonText = if (currentLevel>=4) "" else "Available at Level 4"
            val secondButtonText = if (currentLevel>=4) "" else "Available at Level 4"
            val alpha1 = if (currentLevel>=4) 1f else 0.6f
            val alpha2 = if (currentLevel>=4) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel>=4)
            secondButtonAlpha(alpha2, currentLevel>=4)
        }
        binding.buttonLayout4.apply {
            val firstButtonText = if (currentLevel>=5) "" else "Available at Level 5"
            val secondButtonText = if (currentLevel>=6) "" else "Available at Level 6"
            val alpha1 = if (currentLevel>=5) 1f else 0.6f
            val alpha2 = if (currentLevel>=6) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel>=5)
            secondButtonAlpha(alpha2, currentLevel>=6)
        }
    }
}