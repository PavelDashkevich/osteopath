package by.dashkevichpavel.osteopath.model

data class FilterValues(
    var byAgeChildren: Boolean = false,
    var byAgeAdults: Boolean = false,
    var byCategoryWork: Boolean = false,
    var byCategoryWorkDone: Boolean = false,
    var byCategoryNoHelp: Boolean = false
) {
    fun isFilterOff() =
        !(byAgeChildren || byAgeAdults || byCategoryWork || byCategoryWorkDone || byCategoryNoHelp)
}
