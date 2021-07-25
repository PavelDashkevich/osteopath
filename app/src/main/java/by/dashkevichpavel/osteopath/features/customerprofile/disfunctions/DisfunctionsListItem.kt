package by.dashkevichpavel.osteopath.features.customerprofile.disfunctions

import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus

abstract class DisfunctionListItem {
    abstract fun isTheSame(other: DisfunctionListItem): Boolean
    abstract fun contentsTheSame(other: DisfunctionListItem): Boolean
}

data class DisfunctionListItemCategory(
    val disfunctionStatus: DisfunctionStatus,
    var isEmpty: Boolean,
    var collapsed: Boolean = disfunctionStatus != DisfunctionStatus.WORK
) : DisfunctionListItem() {
    override fun isTheSame(other: DisfunctionListItem): Boolean {
        if (other !is DisfunctionListItemCategory) return false

        return other.disfunctionStatus == this.disfunctionStatus
    }

    override fun contentsTheSame(other: DisfunctionListItem): Boolean {
        if (other !is DisfunctionListItemCategory) return false

        return other.isEmpty == this.isEmpty
    }
}

data class DisfunctionListItemData(
    var disfunction: Disfunction
) : DisfunctionListItem() {
    override fun isTheSame(other: DisfunctionListItem): Boolean {
        if (other !is DisfunctionListItemData) return false

        return other.disfunction.isTheSame(this.disfunction)
    }

    override fun contentsTheSame(other: DisfunctionListItem): Boolean {
        if (other !is DisfunctionListItemData) return false

        return other.disfunction == this.disfunction
    }
}