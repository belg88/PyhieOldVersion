package gleb.apps.pyhie.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gleb.apps.pyhie.R
import gleb.apps.pyhie.databinding.FragmentHowItWorksBinding


class HowItWorksFragment : Fragment() {
    private lateinit var binding: FragmentHowItWorksBinding
    private val args: HowItWorksFragmentArgs by navArgs()
    var name = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = args.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHowItWorksBinding.inflate(inflater,container,false).apply {
            toolbar.navigationIcon =
                ResourcesCompat.getDrawable(resources, R.drawable.arrow_back_white, resources.newTheme())
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
            when (name) {
                "MainHabits" -> {
                    title.text = getString(R.string.main_activities)
                    description.text = getText(R.string.how_it_works_main_habits)
                    title1.visibility = View.VISIBLE
                    title1.text = getString(R.string.good_and_bad_title)
                    description1.visibility = View.VISIBLE
                    description1.text = getString(R.string.how_it_works_good_bad_habits)
                }
                "SleepingHabit" -> {
                    title.text = getString(R.string.sleeping_habit)
                    description.text = getText(R.string.how_sleeping_routine)
                }
                "EatingHabit" -> {
                    title.text = getString(R.string.eating_habit)
                    description.text = getText(R.string.how_eating_routine)
                }
                "CleaningHabit" -> {
                    title.text = getString(R.string.cleaning_habit)
                    description.text = getText(R.string.how_cleaning_habit)
                }
                "PlanningHabit" -> {
                    title.text = getString(R.string.daily_planing)
                    description.text = getText(R.string.how_planning_habit)
                }

            }
        }

        return binding.root
    }


}