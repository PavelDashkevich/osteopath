package by.dashkevichpavel.osteopath.viewcontroller.customerprofile.disfunctions

import by.dashkevichpavel.osteopath.model.Disfunction

abstract class DisfunctionListItem

data class DisfunctionListItemCategory(
    var name: String
) : DisfunctionListItem()

data class DisfunctionListItemData(
    var disfunction: Disfunction
) : DisfunctionListItem()