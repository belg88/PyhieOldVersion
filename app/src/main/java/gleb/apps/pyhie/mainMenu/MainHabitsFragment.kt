package gleb.apps.pyhie.mainMenu

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import gleb.apps.pyhie.*
import gleb.apps.pyhie.challenges.ChallengeStatus
import gleb.apps.pyhie.challenges.ChallengesDialog
import gleb.apps.pyhie.databinding.FragmentHomeBinding
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.mainHabits.sleepingHabit.SleepingSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.cleaningHabit.CleaningSavedState
import gleb.apps.pyhie.mainHabits.sleepingHabit.eatingHabit.EatingSavedState
import gleb.apps.pyhie.pojos.EatingInfo
import gleb.apps.pyhie.pojos.SleepingInfo
import gleb.apps.pyhie.pojos.UserInfo
import gleb.apps.pyhie.pojos.UserPoints
import gleb.apps.pyhie.util.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule


class MainHabitsFragment : Fragment() {
    private lateinit var viewModel_: MainMenuViewModel
    private lateinit var viewModelFactory: MainViewModelFactory
    private lateinit var binding: FragmentHomeBinding
    lateinit var toolbar: Toolbar
    lateinit var menu: Menu
    private val TAG = "TAG"
    private lateinit var userPoints: UserPoints
    private var userInfo = UserInfo()
    var email = ""
    private lateinit var sharedPref: SharedPref
    private var currentChallenge = 0
    private var sleepSubmitted = false
    private var eatSubmitted = false
    private var cleanSubmitted = false
    private var plannerSubmitted = false
    private var balance = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPref = SharedPref(requireActivity())
        currentChallenge = sharedPref.getInt(Keys.CURRENT_CHALLENGE)!!
        sleepSubmitted = sharedPref.getBoolean(Keys.SLEEP1_NOT_SUBMITTED)
        eatSubmitted = sharedPref.getBoolean(Keys.EAT1_NOT_SUBMITTED)
        cleanSubmitted = sharedPref.getBoolean(Keys.CLEAN_NOT_SUBMITTED)
        plannerSubmitted = sharedPref.getBoolean(Keys.PLANNER_NOT_SUBMITTED)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        email = SharedPref(this.requireActivity()).getString(Keys.EMAIL)!!
        viewModelFactory = MainViewModelFactory(email, requireContext())
        viewModel_ = ViewModelProvider(this, viewModelFactory)[MainMenuViewModel::class.java]
        binding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = viewModel_
            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.dailyAdviceMenu -> findNavController().navigate(
                        MainHabitsFragmentDirections.actionHomeFragmentToDailyAdviceList(userInfo.numberOfDays)
                    )
                    R.id.howItWorks -> findNavController().navigate(
                        MainHabitsFragmentDirections.actionHomeFragmentToHowItWorksFragment(
                            "MainHabits"
                        )
                    )
                }
                true
            }
        }



        activateButtons()
        displayChallenges()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            userInfo = FirebaseService.getUserInfo(email)!!
            userPoints = FirebaseService.getUserPoints(email)!!

        }.invokeOnCompletion {
            todaysMessage()
            animateProgressBar(userPoints)
            setBalance()
            Timer().schedule(2500) { dailyAdvice(userInfo) }
        }
    }

    override fun onResume() {
        super.onResume()
        changeOfDay()
        activateChallenges()
        checkChallengesStatus()
        checkMarks()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun changeOfDay() {
        binding.apply {
            when (GetTimes().hour) {
                in 6..11 -> {
                    mainLayout.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.morning,
                        resources.newTheme()
                    )
                    name.text = getString(R.string.good_morning, sharedPref.getString(Keys.NAME))
                    name.setTextColor(
                        resources.getColor(
                            R.color.primary_light,
                            resources.newTheme()
                        )
                    )
                }
                in 12..17 -> {
                    binding.mainLayout.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.afternoon,
                        resources.newTheme()
                    )
                    name.text = getString(R.string.good_afternoon, sharedPref.getString(Keys.NAME))
                    name.setTextColor(
                        resources.getColor(
                            R.color.primary_light,
                            resources.newTheme()
                        )
                    )
                }
                in 18..24 -> {
                    binding.mainLayout.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.evening,
                        resources.newTheme()
                    )
                    name.text = getString(R.string.good_evening, sharedPref.getString(Keys.NAME))
                }
                in 0..5 -> {
                    binding.mainLayout.background = ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.night,
                        resources.newTheme()
                    )
                    name.text = getString(R.string.good_noght, sharedPref.getString(Keys.NAME))
                }
            }
        }
    }

    private fun animateProgressBar(userPoints: UserPoints) {
        val duration: Long = 1500
        val anim = ValueAnimator()
        anim.setObjectValues(0, userPoints.currentLevelPoints.toInt())
        anim.addUpdateListener {
            binding.progressSoFar.text = it.animatedValue.toString()
        }
        anim.duration = duration
        anim.start()

        binding.progressBar.max = userPoints.nextLevelUpgrade.toInt()
        binding.efficiencyBar.max = 100
        val animator = ObjectAnimator.ofInt(
            binding.progressBar,
            "progress",
            userPoints.currentLevelPoints.toInt()
        )
        animator.duration = duration
        animator.start()
        levelUp(userPoints.currentLevel)
}

private fun setBalance() {
    val sleepBalance = sharedPref.getInt(Keys.SLEEP_BALANCE)
    val eatBalance = sharedPref.getInt(Keys.EAT_BALANCE)
    val cleanBalance = sharedPref.getInt(Keys.CLEAN_BALANCE)
    val plannerBalance = sharedPref.getInt(Keys.PLANNER_BALANCE)

    balance =
        if (sleepBalance == 0 || eatBalance == 0 || cleanBalance == 0 || plannerBalance == 0) 0 else (sleepBalance!! + eatBalance!! + cleanBalance!! + plannerBalance!!) / 4
    val animEffic = if (balance == 0) ObjectAnimator.ofInt(
        binding.efficiencyBar,
        "progress",
        1
    ) else ObjectAnimator.ofInt(binding.efficiencyBar, "progress", balance)
    animEffic.duration = 1500L
    animEffic.start()
    val animEfficValue = ValueAnimator()
    animEfficValue.setObjectValues(0, balance)
    animEfficValue.addUpdateListener {
        binding.efficiency.text = it.animatedValue.toString() + getString(R.string.percent)
    }
    animEfficValue.duration = 1500L
    animEfficValue.start()
}

private fun activateChallenges() {
    if (sharedPref.getBoolean(Keys.CHALLENGE_NOT_SHOWN) && currentChallenge != 0) {
        val challengesDialog = ChallengesDialog.newInstance(Keys.ACTIVATE_CHALLENGE)
        sharedPref.saveBoolean(Keys.CHALLENGE_NOT_SHOWN, false)
        when (currentChallenge) {
            1 -> {
                challengesDialog.show(childFragmentManager, ChallengesDialog.TAG)
                sharedPref.saveInt(Keys.CHALLENGE_1_START_DAY, GetDates().today)
            }
            2 -> challengesDialog.show(childFragmentManager, ChallengesDialog.TAG)
            3 -> {
                challengesDialog.show(childFragmentManager, ChallengesDialog.TAG)
                sharedPref.saveLong(Keys.CHALLENGE_3_START_DAY, Date().time)
            }
        }
    }
}

private fun checkChallengesStatus() {
    lifecycleScope.launchWhenResumed {
        val currentLevel = FirebaseService.getUserPoints(email)!!.level
        Timer().schedule(6000) {
            when (currentChallenge) {
                1 -> {
                    if (ChallengeStatus.challenge1Completed(
                            plannerSubmitted,
                            eatSubmitted,
                            cleanSubmitted,
                            sleepSubmitted
                        )
                    ) challengeCompleted()
                    if (ChallengeStatus.challenge1Failed(sharedPref.getInt(Keys.CHALLENGE_1_START_DAY)!!)) challengeFailed()
                }
                2 -> {
                    if (ChallengeStatus.challenge2Completed(
                            currentLevel,
                            balance.toDouble()
                        )
                    ) challengeCompleted()
                    if (ChallengeStatus.challenge2Failed(
                            currentLevel,
                            balance.toDouble()
                        )
                    ) challengeFailed()
                }
                3 -> {
                    val savedDay = sharedPref.getLong(Keys.CHALLENGE_3_START_DAY)
                    if (ChallengeStatus.challenge3Completed(
                            balance.toDouble(),
                            savedDay
                        )
                    ) challengeCompleted()
                    if (ChallengeStatus.challenge3Failed(
                            balance.toDouble(),
                            savedDay
                        )
                    ) challengeFailed()
                }
            }
        }
    }
}

private fun challengeCompleted() {
    sharedPref.saveBoolean(Keys.CHALLENGE_NOT_SHOWN, true)
    ChallengesDialog.newInstance(Keys.CHALLENGE_COMPLETED)
        .show(parentFragmentManager, ChallengesDialog.TAG)
}

private fun challengeFailed() {
    ChallengesDialog.newInstance(
        Keys.CHALLENGE_FAILED
    ).show(parentFragmentManager, ChallengesDialog.TAG)
    sharedPref.saveBoolean(Keys.CHALLENGE_NOT_SHOWN, true)
}


private fun displayChallenges() {
    binding.apply {
        viewModel_.getChallenges.observe(viewLifecycleOwner, Observer {
            if (currentChallenge == 0) {
                challengeTitle.text = getString(R.string.no_challenges)
            } else {
                val currentChallenge = it[currentChallenge - 1]
                challengeTitle.text = currentChallenge.name
                challengeDescription.text = currentChallenge.description
                challengeTimeLimit.text =
                    getString(R.string.time_remaining_challenge, currentChallenge.timeAvailable)
            }

        })
    }
}

private fun activateButtons() {
    binding.sleepingButton.setOnClickListener {
        lifecycleScope.launch {
            findNavController().navigate(
                MainHabitsFragmentDirections.actionHomeFragmentToSleepingMain(
                    FirebaseService.getSleepingPref(email)!!
                )
            )

            SharedPref(requireActivity()).saveString(Keys.MAIN_HABITS, Keys.SLEEP_DATA)
        }
    }
    binding.eatingButton.setOnClickListener {
        lifecycleScope.launch {
            findNavController().navigate(
                MainHabitsFragmentDirections.actionHomeFragmentToEatingMain(
                    FirebaseService.getEatingInfo(email)!!
                )
            )

            SharedPref(requireActivity()).saveString(Keys.MAIN_HABITS, Keys.EATING_DATA)
        }
    }
    binding.planningButton.setOnClickListener {
        findNavController().navigate(
            MainHabitsFragmentDirections.actionHomeFragmentToPlanningHabitToday()
        )
    }
    binding.cleaningButton.setOnClickListener {
        lifecycleScope.launch {
            val cleaningInfo = FirebaseService.getCleaningInfo(email)!!
            if (ResetHabit(cleaningInfo.currentDay, cleaningInfo.currentYear).isNextDay()) {
                val gson = Gson()
                val gsonString = gson.toJson(
                    CleaningSavedState()
                )
                SharedPref(requireActivity()).saveString(Keys.CLEANING_SAVED_STATE, gsonString)
            }

            findNavController().navigate(
                MainHabitsFragmentDirections.actionHomeFragmentToCleaningMain(
                    cleaningInfo
                )
            )
            SharedPref(requireActivity()).saveString(Keys.MAIN_HABITS, Keys.CLEANING_DATA)
        }
    }
}

private fun dailyAdvice(userInfo: UserInfo) {
    if (sharedPref.getInt(Keys.ADVICE_DAY_NUMBER) != GetDates().today) {
        userInfo.numberOfDays = userInfo.numberOfDays + 1
        FirebaseService.insertUserInfo(userInfo, email)
        sharedPref.saveInt(Keys.ADVICE_DAY_NUMBER, GetDates().today)
        // if (userInfo.numberOfDays => maxAdviceDays) { do nothing } else { -> }
        findNavController().navigate(
            MainHabitsFragmentDirections.actionHomeFragmentToDailyAdvice(
                userInfo.numberOfDays
            )
        )
    }
}

private fun checkMarks() {
    binding.apply {
        sleepCheckmark.isVisible = !sharedPref.getBoolean(Keys.SLEEP1_NOT_SUBMITTED)
        eatCheckmark.isVisible = !sharedPref.getBoolean(Keys.EAT1_NOT_SUBMITTED)
        cleanCheckmark.isVisible = !sharedPref.getBoolean(Keys.CLEAN_NOT_SUBMITTED)
        plannerCheckmark.isVisible = !sharedPref.getBoolean(Keys.PLANNER_NOT_SUBMITTED)
    }
}

private fun todaysMessage() {
    when (userInfo.numberOfDays) {
        0, 1 -> binding.todaysMessage.setMessage(getString(R.string.message1))
        2 -> binding.todaysMessage.setMessage(getString(R.string.message2))
        else -> binding.todaysMessage.visibility = View.GONE
    }
}

private fun levelUp(level: Int) {
    if (!sharedPref.getBoolean(Keys.NEW_LEVEL_NOT_REACHED)) findNavController().navigate(
        MainHabitsFragmentDirections.actionHomeFragmentToLevelIsCompleted(level)
    )
}
}
