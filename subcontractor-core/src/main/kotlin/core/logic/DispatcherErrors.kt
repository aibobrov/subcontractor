package core.logic

sealed class DispatcherError(message: String) : Exception(message) {
    object OrderAlreadyExists : DispatcherError("Order already exists")
    object OrderNotFound : DispatcherError("Order does not exist")
    object WorkAlreadyExists : DispatcherError("Work already exists")
    object WorkNotFound : DispatcherError("Work does not exist")
    object WorkerAlreadyExists : DispatcherError("Worker already exists")
    object WorkerNotFound : DispatcherError("Worker does not exist")
    object CycleFound : DispatcherError("Delegation cycle is found")
    object DelegationIsNotRelevant : DispatcherError("Delegation is not relevant")
}