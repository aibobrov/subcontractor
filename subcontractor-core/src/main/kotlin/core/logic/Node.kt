package core.logic

class Node(private val userId: UserId) {

    private var parents = mutableListOf<Node>()
    private var children  = mutableListOf<Node>()
    private var report : Report? = null
    private var isConfirmReport = false

    fun getUserId() : UserId {
        return userId
    }

    fun addChild(node : Node) {
        children.add(node)
    }

    fun addChildren(node : List<Node>) {
        children.addAll(node)
    }

    fun addParent(node : Node) {
        parents.add(node)
    }

    fun addParents(nodes : List<Node>) {
        parents.addAll(nodes)
    }

    fun setReport(report: Report?) {
        this.report = report
    }

    fun getChildren(): MutableList<Node> {
        return children
    }

    fun getParents(): MutableList<Node> {
        return parents
    }

    fun getReport() : Report? {
        return report
    }

    fun setConfirm(isConfirm : Boolean) {
        isConfirmReport = isConfirm
    }

    fun isConfirmReport(): Boolean {
        return report != null && isConfirmReport
    }

    fun isRoot() = parents.isEmpty()

    override fun equals(other : Any?) : Boolean = (other is Node) && userId == other.userId

    override fun hashCode(): Int {
        var result = userId.hashCode()
        result = 31 * result + parents.hashCode()
        result = 31 * result + children.hashCode()
        return result
    }

}