package core

/// Model to UI interface
interface UIRepresentable<T> {
    fun representation(): T
}

interface BuildableUIRepresentable<Builder, T> : UIRepresentable<T> {
    fun representIn(builder: Builder)
}
