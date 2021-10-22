package gleb.apps.pyhie.mainMenu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import gleb.apps.pyhie.R
import gleb.apps.pyhie.databinding.FragmentDailyAdviceBinding



class DailyAdvice : Fragment() {
    private lateinit var binding: FragmentDailyAdviceBinding
    private val args: DailyAdviceArgs by navArgs()
    var dayNumber = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDailyAdviceBinding.inflate(inflater, container, false)
        dayNumber = args.dayNumber
        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        when (dayNumber) {
            1 -> getAdviceText(getString(R.string.be_in_the_top_one_percent),getString(R.string.daily_advice1))
            2 -> getAdviceText(getString(R.string.daily_advice2_title),getString(R.string.daily_advice2))
            3 -> getAdviceText(getString(R.string.daily_advice3_title),getString(R.string.daily_advice3))
            else -> findNavController().popBackStack()

        }
        return binding.root
    }


    private fun getAdviceText(title: String, descriptor: String) {
        binding.title.text = title
        binding.adviceDescription.text = descriptor

    }


}