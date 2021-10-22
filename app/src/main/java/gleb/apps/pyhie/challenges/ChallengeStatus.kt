package gleb.apps.pyhie.challenges

import android.text.BoringLayout
import gleb.apps.pyhie.util.GetDates
import java.util.*
import java.util.concurrent.TimeUnit

object ChallengeStatus {
    private val currentDay = GetDates().today
    private val currentTime = Date().time

    fun challenge1Completed(
        sleepNotSubmitted: Boolean,
        eatNotSubmitted: Boolean,
        cleanNotSubmitted: Boolean,
        plannerNotSubmitted: Boolean
    ) = !sleepNotSubmitted && !eatNotSubmitted && !cleanNotSubmitted && !plannerNotSubmitted

    fun challenge1Failed(savedDay: Int): Boolean = savedDay != currentDay

    fun challenge2Completed(level: Int, balance: Double): Boolean = level == 2 && balance >= 60.0

    fun challenge2Failed(level: Int, balance: Double): Boolean = level == 2 && balance <= 60.0

    fun challenge3Completed(balance: Double, savedTime: Long): Boolean = TimeUnit.MILLISECONDS.toDays(currentTime - savedTime) >= 3 && balance >= 65

    fun challenge3Failed(balance: Double, savedTime: Long): Boolean = TimeUnit.MILLISECONDS.toDays(currentTime - savedTime) >= 3 && balance < 65


}