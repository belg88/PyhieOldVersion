package gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.databinding.FragmentCleaningMainBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.*
import gleb.apps.pyhie.util.GetDates
import gleb.apps.pyhie.util.ResetHabit
import gleb.apps.pyhie.util.ViewAnimation
import gleb.apps.pyhie.util.ZoomOutPageTransformer
import kotlinx.coroutines.launch


class CleaningMain : Fragment() {
    private lateinit var binding: FragmentCleaningMainBinding
    private var cleaningSavedState = CleaningSavedState()
    private var rotate = false
    private var email = ""
    private lateinit var viewPager: ViewPager2
    private val args: CleaningMainArgs by navArgs()
    private var cleaningInfo = CleaningInfo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = SharedPref(requireActivity()).getString(Keys.EMAIL)!!
        cleaningInfo = args.cleaningInfo
        getCurrentState()

    }

    override fun onResume() {
        super.onResume()
        getCurrentState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        saveCurrentState()
    }

    override fun onPause() {
        super.onPause()
        saveCurrentState()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCleaningMainBinding.inflate(layoutInflater, container, false).apply {
            savedState = cleaningSavedState
            cleanButton.setOnClickListener { submitRatings(getString(R.string.clean), "") }
            partCleanButton.setOnClickListener {
                submitRatings(
                    getString(R.string.partially_clean),
                    ""
                )
            }
            partUncleanButton.setOnClickListener {
                submitRatings(
                    getString(R.string.partially_unclean),
                    ""
                )
            }
            uncleanButton.setOnClickListener { submitRatings(getString(R.string.unclean), "") }
            yesButton.setOnClickListener { submitRatings("", "Yes") }
            noButton.setOnClickListener { submitRatings("", "No") }
            submitButton.setOnClickListener { submitHabit() }
            undoButton.setOnClickListener { undo() }
            submittedText.text = getString(R.string.main_habit_is_completed, "Cleaning habit")
            howItWorks.setOnClickListener { findNavController().navigate(CleaningMainDirections.actionCleaningMainToHowItWorksFragment("CleaningHabit")) }
        }

        floatingButtons()
        setViewPager()
        resetHabit()
        return binding.root
    }

    private fun floatingButtons() {
        binding.apply {
            ViewAnimation().init(addMore)
            ViewAnimation().init(howItWorks)

            binding.fab.setOnClickListener {
                rotate = ViewAnimation().rotateFab(it, !rotate)
                if (rotate) {
                    ViewAnimation().showIn(addMore)
                    ViewAnimation().showIn(howItWorks)
                } else {
                    ViewAnimation().showOut(addMore)
                    ViewAnimation().showOut(howItWorks)
                }
            }
        }
    }

    private fun submitRatings(rating: String, yesNo: String) {
        binding.apply {
            scrollView.post {
                scrollView.fullScroll(View.FOCUS_DOWN)
            }
            when (cleaningSavedState.currentRating) {
                "Bedroom" -> {
                    cleanText1.text = getString(R.string.bedroom_living_text_done, rating)
                    cleanText1.setTextColor(Color.BLACK)
                    cleaningSavedState.bedroomRating = rating
                    cleanLayout2.isVisible = true
                    cleaningSavedState.currentRating = "Kitchen"
                    undoButton.visibility = View.VISIBLE
                }
                "Kitchen" -> {
                    cleanText2.text = getString(R.string.kitchen_text_done, rating)
                    cleanText2.setTextColor(Color.BLACK)
                    cleaningSavedState.kitchenRating = rating
                    cleanLayout3.isVisible = true
                    cleaningSavedState.currentRating = "Dishes"
                    rateLayout.visibility = View.GONE
                    yesNoLayout.visibility = View.VISIBLE
                }
                "Dishes" -> {
                    when (yesNo) {
                        "Yes" -> {
                            cleanText3.text = getString(R.string.do_you_have_any_dishes_yes)
                            cleanText3.setTextColor(Color.BLACK)
                            cleanLayout4.isVisible = true
                            cleaningSavedState.dishesDone = false
                            cleaningSavedState.currentRating = "Clothes"
                            yesNoLayout.visibility = View.VISIBLE
                        }
                        "No" -> {
                            cleanText3.text = getString(R.string.do_you_have_any_dishes_no)
                            cleanText3.setTextColor(Color.BLACK)
                            cleanLayout4.isVisible = true
                            cleaningSavedState.dishesDone = true
                            cleaningSavedState.currentRating = "Clothes"
                            yesNoLayout.visibility = View.VISIBLE
                        }
                    }
                }
                "Clothes" -> {
                    when (yesNo) {
                        "Yes" -> {
                            cleanText4.text = getString(R.string.do_you_have_any_clothes_yes)
                            cleanText4.setTextColor(Color.BLACK)
                            cleaningSavedState.clothesDone = true
                            yesNoLayout.visibility = View.GONE
                            cleaningSavedState.currentRating = "Submit"
                            ViewAnimation().init(submitButton)
                            ViewAnimation().showIn(submitButton)
                        }
                        "No" -> {
                            cleanText4.text = getString(R.string.do_you_have_any_clothes_no)
                            cleanText4.setTextColor(Color.BLACK)
                            cleaningSavedState.clothesDone = false
                            cleaningSavedState.currentRating = "Submit"
                            yesNoLayout.visibility = View.GONE
                            ViewAnimation().init(submitButton)
                            ViewAnimation().showIn(submitButton)
                        }
                    }
                }
            }
        }
    }

    private fun submitHabit() {
        activateFirstChallenge()
        binding.submitButton.visibility = View.GONE
        binding.submittedText.visibility = View.VISIBLE
        binding.undoButton.visibility = View.GONE
        lifecycleScope.launch {
            val cleaningData = CleaningData(
                cleaningSavedState.bedroomRating,
                cleaningSavedState.kitchenRating,
                cleaningSavedState.dishesDone,
                cleaningSavedState.clothesDone
            )

            FirebaseService.insertCleaningData(cleaningData, email)
            findNavController().navigate(
                CleaningMainDirections.actionCleaningMainToHabitIsCompleted(
                    getString(R.string.cleaning_habit_title),
                    cleaningData.points().toFloat()
                )
            )
            cleaningInfo.currentDay = GetDates().today
            cleaningInfo.currentYear = GetDates().year
            cleaningInfo.submitted = true
            cleaningInfo.numberOfSubmits = cleaningInfo.numberOfSubmits + 1
            cleaningInfo.totalPoints = cleaningInfo.totalPoints + cleaningData.points()
            val cleanBalance = (cleaningInfo.totalPoints/cleaningInfo.numberOfSubmits)*10
            SharedPref(requireActivity()).saveInt(Keys.CLEAN_BALANCE,cleanBalance.toInt())
            FirebaseService.insertCleaningInfo(cleaningInfo,email)
            SharedPref(requireContext()).saveBoolean(Keys.CLEAN_NOT_SUBMITTED, false)
        }
    }

    private fun resetHabit() {
        val reset = ResetHabit(cleaningInfo.currentDay,cleaningInfo.currentYear)
        lifecycleScope.launch {
        if (reset.isNextDay()) {
            cleaningInfo.currentDay = GetDates().today
            cleaningInfo.currentYear = GetDates().year
            if (!cleaningInfo.submitted) {
                val penaltyDialog  = PenaltyFragment()
                penaltyDialog.show(parentFragmentManager, PenaltyFragment.TAG)

            } else {
                cleaningInfo.submitted = false
            }
             FirebaseService.insertCleaningInfo(cleaningInfo,email) }
        }
    }

    private fun undo() {
        binding.apply {
            when (cleaningSavedState.currentRating) {
                "Kitchen" -> {
                    cleanText1.text = getString(R.string.bedroom_living_text)
                    cleanText1.setTextColor(Color.GRAY)
                    cleanLayout2.isVisible = false
                    cleaningSavedState.currentRating = "Bedroom"
                    undoButton.visibility = View.GONE
                }
                "Dishes" -> {
                    cleanText2.text = getString(R.string.kitchen_text)
                    cleanText2.setTextColor(Color.GRAY)
                    cleanLayout3.visibility = View.GONE
                    cleaningSavedState.currentRating = "Kitchen"
                    rateLayout.visibility = View.VISIBLE
                    yesNoLayout.visibility = View.GONE
                }
                "Clothes" -> {
                    cleanText3.text = getString(R.string.do_you_have_any_dishes)
                    cleanText3.setTextColor(Color.GRAY)
                    cleanLayout4.isVisible = false
                    cleaningSavedState.currentRating = "Dishes"
                }
                "Submit" ->{
                    cleanText4.text = getString(R.string.do_you_have_any_clothes)
                    cleanText4.setTextColor(Color.GRAY)
                    yesNoLayout.visibility = View.VISIBLE
                    cleaningSavedState.currentRating = "Clothes"
                    submitButton.visibility = View.GONE
                }
            }
        }
    }

    private fun setViewPager() {
        viewPager = binding.viewPager
        viewPager.setPageTransformer(ZoomOutPageTransformer())
        val pagerAdapter = ScreenSlidePagerAdapterCleaning(this)
        viewPager.adapter = pagerAdapter
    }

    private fun saveCurrentState() {
        binding.apply {
            cleaningSavedState.kitchenText = cleanText2.text.toString()
            cleaningSavedState.bedroomText = cleanText1.text.toString()
            cleaningSavedState.dishesText = cleanText3.text.toString()
            cleaningSavedState.clothesText = cleanText4.text.toString()
            cleaningSavedState.kitchenVisibility = cleanLayout2.visibility
            cleaningSavedState.clothesVisibility = cleanLayout3.visibility
            cleaningSavedState.dishesVisibility = cleanLayout4.visibility
            cleaningSavedState.submitTextVisibility = submittedText.visibility
            cleaningSavedState.submitVisibility = submitButton.visibility
            cleaningSavedState.bedroomColor = cleanText1.currentTextColor
            cleaningSavedState.kitchenColor = cleanText2.currentTextColor
            cleaningSavedState.dishesColor = cleanText3.currentTextColor
            cleaningSavedState.clothesColor = cleanText4.currentTextColor
            cleaningSavedState.ratingVisibility = rateLayout.visibility
            cleaningSavedState.undoVisibility = undoButton.visibility
            cleaningSavedState.yesNoVisibility = yesNoLayout.visibility
        }
        val gson = Gson()
        val gsonString = gson.toJson(
            cleaningSavedState
        )
        SharedPref(requireActivity()).saveString(Keys.CLEANING_SAVED_STATE, gsonString)
    }

    private fun getCurrentState() {
        val gson = Gson()
        val json = SharedPref(requireActivity()).getString(Keys.CLEANING_SAVED_STATE)
        val type = object : TypeToken<CleaningSavedState?>() {}.type
        cleaningSavedState = gson.fromJson<CleaningSavedState>(json, type)
    }

    private fun activateFirstChallenge () {
        if (SharedPref(requireContext()).getInt(Keys.CURRENT_CHALLENGE) == 0) SharedPref(requireContext()).saveInt(Keys.CURRENT_CHALLENGE,1)
    }

    class ScreenSlidePagerAdapterCleaning(frag: Fragment) : FragmentStateAdapter(frag) {
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> ThisWeekQuality()
                1 -> LastWeekQuality()
                2 -> OverallEfficiency()
                else -> ThisWeekQuality()
            }
        }
    }
}

