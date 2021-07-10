package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus

abstract class DisfunctionListItem

data class DisfunctionListItemCategory(
    val disfunctionStatus: DisfunctionStatus,
    var isEmpty: Boolean,
    var collapsed: Boolean = disfunctionStatus != DisfunctionStatus.WORK
) : DisfunctionListItem()

data class DisfunctionListItemData(
    var disfunction: Disfunction
) : DisfunctionListItem()