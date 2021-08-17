package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import by.dashkevichpavel.osteopath.helpers.recyclerviewutils.DiffUtilComparable
import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus

abstract class DisfunctionListItem : DiffUtilComparable

data class DisfunctionListItemCategory(
    val disfunctionStatus: DisfunctionStatus,
    var isEmpty: Boolean,
    var collapsed: Boolean = disfunctionStatus != DisfunctionStatus.WORK
) : DisfunctionListItem() {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionListItemCategory) return false

        return item.disfunctionStatus == this.disfunctionStatus
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionListItemCategory) return false

        return item.isEmpty == this.isEmpty
    }
}

data class DisfunctionListItemData(
    var disfunction: Disfunction
) : DisfunctionListItem() {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionListItemData) return false

        return item.disfunction.isTheSameItemAs(this.disfunction)
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionListItemData) return false

        return item.disfunction == this.disfunction
    }
}

data class DisfunctionsListSelectableItemData(
    var disfunction: Disfunction,
    var isSelected: Boolean
) : DisfunctionListItem() {
    override fun isTheSameItemAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionsListSelectableItemData) return false

        return item.disfunction.isTheSameItemAs(this.disfunction)
    }

    override fun contentsTheSameAs(item: DiffUtilComparable?): Boolean {
        if (item == null) return false
        if (item !is DisfunctionsListSelectableItemData) return false

        return item.disfunction == this.disfunction
    }
}