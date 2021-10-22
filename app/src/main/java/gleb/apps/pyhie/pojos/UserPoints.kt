package gleb.apps.pyhie.pojos

import android.content.res.Resources
import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.gson.annotations.SerializedName
import com.google.rpc.context.AttributeContext
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.io.SerializablePermission
import java.lang.reflect.Array.getFloat

@Parcelize
data class UserPoints(
    var totalPoints: Double = 0.0,
    var level: Int = 1
) : Parcelable {

    @IgnoredOnParcel
    var nextLevelUpgrade = level * 100.0

    @IgnoredOnParcel
    val previousLevelUpgrade = nextLevelUpgrade - 100.0

    @IgnoredOnParcel
    var currentLevelPoints = when (level) {
        1 -> totalPoints
        2 -> totalPoints - 100
        3 -> totalPoints - 300
        4 -> totalPoints - 600
        5 -> totalPoints - 1000
        6 -> totalPoints - 1500
        7 -> totalPoints - 2100
        8 -> totalPoints - 2800
        9 -> totalPoints - 3600
        10 -> totalPoints - 4500
        11 -> totalPoints - 5500
        else -> 0.0
    }

    @IgnoredOnParcel
    val newLevelReached = currentLevelPoints >= nextLevelUpgrade

    @IgnoredOnParcel
    var currentLevel = if (newLevelReached) level + 1 else level

    @IgnoredOnParcel
    val nextLevelUpgradeString = "/${nextLevelUpgrade.toInt()}"

    @IgnoredOnParcel
    val levelString = "Level $level"

}


