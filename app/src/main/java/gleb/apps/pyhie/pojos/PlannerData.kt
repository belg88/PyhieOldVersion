
package gleb.apps.pyhie.pojos

import android.view.View
import java.util.*

data class PlannerData(
    val startTimeHour: Int = 0,
    val startTimeMinute: Int = 0,
    val identifier: String = "",
    val title: String = "",
    val description: String = "",
    val firstActivity: Boolean = false,
    val lastActivity: Boolean = false,
    val bedTimePastMidnight: Boolean = false,
    val marked: Boolean = false
) {
    val timeStamp = Date().time

    val firstConnectorVisibility = if (firstActivity) View.VISIBLE else View.GONE
    val lastConnectorVisibility = if (lastActivity) View.VISIBLE else View.GONE
    val connectorVisibility = if (firstActivity || lastActivity) View.GONE else View.VISIBLE
    val startTimeString =
        "${String.format("%02d", startTimeHour)}:${String.format("%02d", startTimeMinute)}"
    val startTimeDouble =
        if (bedTimePastMidnight) startTimeHour.toDouble() + startTimeMinute.toDouble() / 60 + 24 else startTimeHour.toDouble() + startTimeMinute.toDouble() / 60

    val checkMarkVisibility = if (marked) View.VISIBLE else View.GONE


}