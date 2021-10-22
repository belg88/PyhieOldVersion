package gleb.apps.pyhie.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import gleb.apps.pyhie.R
import gleb.apps.pyhie.databinding.FragmentGoodHabitsBinding


class BadHabitsFragment : Fragment() {


    private lateinit var binding: FragmentGoodHabitsBinding
    var currentLevel = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGoodHabitsBinding.inflate(inflater, container, false).apply {
            image.setImageResource(R.drawable.bad_habits)
            title.text = getString(R.string.bad_habits)
            buttonLayout1.apply {
                firstButtonImage(R.drawable.ic_smoking_image)
                secondButtonImage(R.drawable.ic_alcohol_image)
                firstButtonText("Smoking")
                secondButtonText("Alcohol Drinking")
                firstButton.setOnClickListener {
                    findNavController().navigate(BadHabitsFragmentDirections.actionChallengeFragmentToAcoholDrinking2())
                }
                secondButton.setOnClickListener {

                }

            }
            buttonLayout2.apply {
                firstButtonImage(R.drawable.ic_unproductive_image)
                secondButtonImage(R.drawable.ic_unhealthy_eating_image)
                firstButtonText("Being Unproductive")
                secondButtonText("Unhealthy Eating")
            }
        }
        initializeStartViews()

        return binding.root
    }

    private fun initializeStartViews() {
        binding.buttonLayout1.apply {
            val firstButtonText = if (currentLevel >= 1) "" else "Available at Level 1"
            val secondButtonText = if (currentLevel >= 2) "" else "Available at Level 2"
            val alpha1 = if (currentLevel >= 1) 1f else 0.6f
            val alpha2 = if (currentLevel >= 2) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel >= 1)
            secondButtonAlpha(alpha2, currentLevel >= 2)
        }
        binding.buttonLayout2.apply {
            val firstButtonText = if (currentLevel >= 3) "" else "Available at Level 3"
            val secondButtonText = if (currentLevel >= 3) "" else "Available at Level 3"
            val alpha1 = if (currentLevel >= 3) 1f else 0.6f
            val alpha2 = if (currentLevel >= 3) 1f else 0.6f
            firstButtonAvailabilityText(firstButtonText)
            secondButtonAvailabilityText(secondButtonText)
            firstButtonAlpha(alpha1, currentLevel >= 2)
            secondButtonAlpha(alpha2, currentLevel >= 3)
        }
    }

}