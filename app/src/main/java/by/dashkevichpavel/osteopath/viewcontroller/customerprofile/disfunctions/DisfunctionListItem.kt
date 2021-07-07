package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import by.dashkevichpavel.osteopath.model.Disfunction
import by.dashkevichpavel.osteopath.model.DisfunctionStatus

abstract class DisfunctionListItem

data class DisfunctionListItemCategory(
    val nameStringId: Int
) : DisfunctionListItem()

data class DisfunctionListItemData(
    var disfunction: Disfunction
) : DisfunctionListItem()