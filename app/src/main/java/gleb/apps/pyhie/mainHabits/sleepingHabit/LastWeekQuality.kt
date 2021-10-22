package gleb.apps.pyhie.mainHabits.sleepingHabit

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import gleb.apps.pyhie.util.Keys
import gleb.apps.pyhie.R
import gleb.apps.pyhie.SharedPref
import gleb.apps.pyhie.firebase.FirebaseService
import gleb.apps.pyhie.util.DataPoint
import gleb.apps.pyhie.util.GraphView
import kotlinx.coroutines.launch
import java.util.ArrayList

class LastWeekQuality : Fragment() {
    private val TAG = "TAG"
    private lateinit var graphView: GraphView
    private lateinit var progressBar: ProgressBar
    private lateinit var quality: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.last_week_quality_sleep, container, false)

        graphView = view.findViewById(R.id.graph_view)
        val email = SharedPref(requireActivity()).getString(Keys.EMAIL)
        progressBar = view.findViewById(R.id.efficiency_bar)
        quality = view.findViewById(R.id.quality_text)
        val habitData = SharedPref(requireActivity()).getString(Keys.MAIN_HABITS)

        lifecycleScope.launch {

            val dataPointList: ArrayList<DataPoint> = ArrayList()
            var sum = 0.0
            var averageQuality = 0.0

            when (habitData) {
                Keys.SLEEP_DATA -> {
                    val sleepingList = FirebaseService.getSleepingDataLastWeek(email!!)
                    if (sleepingList != null) {
                        for (x in sleepingList.indices) {
                            dataPointList.add(
                                getPointsOnGraph(
                                    sleepingList[x].weekdayString,
                                    sleepingList[x].getSleepQuality()
                                )
                            )
                            sum += sleepingList.get(x).getSleepQuality()
                            averageQuality = (sum / sleepingList.size) * 100
                        }
                        graphView.setData(dataPointList)
                        setUpProgressView(sleepingList, averageQuality)
                    }
                }

                Keys.EATING_DATA -> {
                    val eatingList = FirebaseService.getEatingDataLastWeek(email!!)
                    if (eatingList != null) {
                        for (x in eatingList.indices) {
                            dataPointList.add(
                                getPointsOnGraph(
                                    eatingList[x].weekdayString,
                                    eatingList[x].getQuality()
                                )
                            )
                            sum += eatingList.get(x).getQuality()
                            averageQuality = (sum / eatingList.size) * 100
                        }
                        graphView.setData(dataPointList)
                        setUpProgressView(eatingList, averageQuality)
                    }
                }
                Keys.CLEANING_DATA -> {
                    val cleaningList = FirebaseService.getCleaningDataLastWeek(email!!)
                    if (cleaningList != null) {
                        for (x in cleaningList.indices) {
                            dataPointList.add(
                                getPointsOnGraph(
                                    cleaningList[x].dayOfTheWeek,
                                    cleaningList[x].quality / 100
                                )
                            )
                            sum += cleaningList[x].quality
                            averageQuality = sum / cleaningList.size
                        }
                        graphView.setData(dataPointList)
                        setUpProgressView(cleaningList, averageQuality)
                    }
                }
            }
        }
        return view
    }

    private fun getPointsOnGraph(weekDayString: String, quality: Double): DataPoint {
        var dataPoint = DataPoint(1, 1.0)

        if (weekDayString == "Monday") dataPoint =
            DataPoint(1, 1 - quality)
        if (weekDayString == "Tuesday") dataPoint =
            DataPoint(2, 1 - quality)
        if (weekDayString == "Wednesday") dataPoint =
            DataPoint(3, 1 - quality)
        if (weekDayString == "Thursday") dataPoint =
            DataPoint(4, 1 - quality)
        if (weekDayString == "Friday") dataPoint =
            DataPoint(5, 1 - quality)
        if (weekDayString == "Saturday") dataPoint =
            DataPoint(6, 1 - quality)
        if (weekDayString == "Sunday") dataPoint =
            DataPoint(7, 1 - quality)

        return dataPoint
    }

    private fun setUpProgressView(list: List<Any>, averageQuality: Double) {
        val anim = ValueAnimator()
        anim.setObjectValues(0, averageQuality.toInt())
        anim.addUpdateListener {
            quality.text = it.animatedValue.toString() + getString(R.string.percent)
        }
        anim.duration = 1500
        anim.start()

        val progressAnim = if (averageQuality == 0.0) ObjectAnimator.ofInt(
            progressBar,
            "progress",
            1
        ) else ObjectAnimator.ofInt(progressBar, "progress", averageQuality.toInt())
        progressAnim.duration = 1500
        progressAnim.start()
    }

}