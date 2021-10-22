package gleb.apps.pyhie.challenges

data class Challenge(
    var name: String = "",
    var description: String = "",
    var number: Int = 0,
    var timeAvailable: String = "",
    var reward: Int = 0
) {
}