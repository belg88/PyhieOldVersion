package gleb.apps.pyhie.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gleb.apps.pyhie.customViews.DailyListItemView
import gleb.apps.pyhie.databinding.FragmentDailyAdviceListBinding

class DailyAdviceList : Fragment() {
    private lateinit var binding: FragmentDailyAdviceListBinding
    private val args:DailyAdviceListArgs by navArgs()
    var numberOfDays = 0
    private lateinit var view: DailyListItemView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        numberOfDays = args.numberOfDays

        view = DailyListItemView(requireContext())

        binding = FragmentDailyAdviceListBinding.inflate(inflater, container, false).apply {
            showView("Day one",1,dayOne)
            if (numberOfDays>1) showView("Day two",2, day2)
            if (numberOfDays>2) showView("Day three",3, day3)
            if (numberOfDays>3) showView("Day four",4, day4)
            if (numberOfDays>4) showView("Day five",5, day5)
            if (numberOfDays>5) showView("Day six",6, day6)
            if (numberOfDays>6) showView("Day seven",7, day7)
            if (numberOfDays>7) showView("Day eight",8, day8)
        }

        return binding.root
    }


    private fun showView(dayText:String, dayNumber:Int, view: DailyListItemView) {
        view.visibility = View.VISIBLE
        view.dayName(dayText)
        view.setOnClickListener {
            findNavController().navigate(
                DailyAdviceListDirections.actionDailyAdviceListToDailyAdvice(
                    dayNumber
                )
            )
        }

    }



}