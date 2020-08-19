package core.logic

class Node<Report>(private val userId: UserId) {

    private val parents = mutableListOf<Node<Report>>()
    private val children = mutableListOf<Node<Report>>()
    private var report: Report? = null
    private var isConfirmReport = false

    fun getUserId(): UserId {
        return userId
    }

    fun addChild(node: Node<Report>) {
        children.add(node)
    }

    fun addChildren(node: List<Node<Report>>) {
        children.addAll(node)
    }

    fun addParent(node: Node<Report>) {
        parents.add(node)
    }

    fun addParents(nodes: List<Node<Report>>) {
        parents.addAll(nodes)
    }

    fun setReport(report: Report?) {
        this.report = report
        isConfirmReport = false
    }

    fun getChildren(): MutableList<Node<Report>> {
        return children
    }

    fun getParents(): MutableList<Node<Report>> {
        return parents
    }

    fun getReport(): Report? {
        return report
    }

    fun setConfirm(isConfirm: Boolean) {
        if (isConfirm) {
            report?.let { isConfirmReport = true }
        } else {
            isConfirmReport = false
        }
    }

    fun isConfirmReport(): Boolean {
        return report != null && isConfirmReport
    }

    fun isRoot() = parents.isEmpty()

    override fun equals(other: Any?): Boolean = (other is Node<*>) && userId == other.userId

}