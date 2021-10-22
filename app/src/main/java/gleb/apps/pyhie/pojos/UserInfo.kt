package gleb.apps.pyhie.pojos

data class UserInfo (
    var name: String = "",
    val email:String = "",
    var numberOfDays: Int = 0,
    val premiumUser: Boolean = false,
    var currentChallenge: Int = 0,
    var sleepBalance: Int = 0,
    var eatBalance: Int = 0,
    var cleanBalance: Int = 0,
    var planningBalance: Int = 0
) {

}