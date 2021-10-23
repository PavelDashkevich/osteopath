package by.dashkevichpavel.osteopath.helpers.operationresult

sealed class OperationResult {
    @Suppress("CanSealedSubClassBeObject")
    class Success : OperationResult()
    class Error(val errorMessage: String) : OperationResult()
}