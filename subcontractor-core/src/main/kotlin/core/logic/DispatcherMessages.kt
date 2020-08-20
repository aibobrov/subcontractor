package core.logic

sealed class DispatcherError(message: String) : Error(message) {
    object OrderAlreadyExists : DispatcherError("Order already exists")
    object OrderNotFound : DispatcherError("Order does not exist")
    object WorkerAlreadyExists : DispatcherError("Worker already exists")
    object WorkerNotFound : DispatcherError("Executor does not exist")
    object CycleFound : DispatcherError("Delegation cycle found")
}