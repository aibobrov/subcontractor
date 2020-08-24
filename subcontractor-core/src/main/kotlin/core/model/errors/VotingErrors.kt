package core.model.errors

sealed class VotingError(message: String) : Error(message) {
    object CycleFound : VotingError("Delegation cycle is found")
}