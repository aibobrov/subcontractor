package core.logic

sealed class DispatcherError(message: String) : Error(message) {
    object OrderAlreadyExists : DispatcherError("Order already exists")
    object NodeAlreadyExists : DispatcherError("Node already exists")
    object OrderNotFound : DispatcherError("Order does not exist")
    object NodeNotFound : DispatcherError("Node does not exist")
    object CycleFound : DispatcherError("Delegation cycle found")
    object OrderAlreadyHasExecutors : DispatcherError("Order already has executors")
}