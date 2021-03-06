package by.dashkevichpavel.osteopath.model

import by.dashkevichpavel.osteopath.R

enum class DisfunctionStatus(val id: Int, val nameStringId: Int) {
    WORK(0, R.string.disfunction_status_work),
    WORK_DONE(1, R.string.disfunction_status_work_done_group),
    WORK_FAIL(2, R.string.disfunction_status_no_help),
    WRONG_DIAGNOSED(3, R.string.disfunction_status_wrong_diagnosed_group)
}