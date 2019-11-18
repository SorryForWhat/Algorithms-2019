package lesson3

import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) return false
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     *
     * // N - количество узлов (высота) дерева
     * // Ресурсоемкость - O(N)
     * // Трудоемкость - O(N)
     */
    override fun remove(element: T): Boolean {
        val compareElement: Boolean
        val closest = find(element)
        if (closest != null) {
            element.compareTo(closest.value)
            compareElement = true
        } else return false
        if (!compareElement)
            return false
        return remove(closest)
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun remove(closest: Node<T>): Boolean {
        val parent = findParent(closest)
        val closestLeft = closest.left
        val closestRight = closest.right
        val parentLeft = parent?.left
        val parentRight = parent?.right
        if ((closestLeft == null) and (closestRight == null)) {
            when {
                parent == null -> root = null
                parentLeft == closest -> parent.left = null
                parentRight == closest -> parent.right = null
            }
            size--
        } else if ((closestLeft != null) and (closestRight == null)) {
            when {
                parent == null -> root = closest.left
                parentLeft == closest -> parent.left = closest.left
                parentRight == closest -> parent.right = closest.left
            }
            size--
        } else if ((closestLeft == null) and (closestRight != null)) {
            when {
                parent == null -> root = closest.right
                parentLeft == closest -> parent.left = closest.right
                parentRight == closest -> parent.right = closest.right
            }
            size--
        } else if ((closestLeft != null) and (closestRight != null)) {
            var lastAllowed = closest.right
            var lastAllowedToMove = false
            while (lastAllowed?.left != null) {
                lastAllowed = lastAllowed.left
                if (lastAllowed?.right != null)
                    lastAllowedToMove = true
            }
            val replacementNodeParent = findParent(lastAllowed!!)
            when {
                parent == null -> root = lastAllowed
                parentLeft == closest -> parent.left = lastAllowed
                parentRight == closest -> parent.right = lastAllowed
            }
            lastAllowed.left = closest.left
            if (lastAllowed != closest.right) {
                if (lastAllowedToMove)
                    replacementNodeParent?.left = lastAllowed.right
                else
                    replacementNodeParent?.left = null
                lastAllowed.right = closest.right
            }
            size--
        }
        return true
    }

    private var firstElement: T? = null
    private var lastElement: T? = null

    private fun find(value: T): Node<T>? {
        return when {
            lastElement != null && (firstElement!! > value || lastElement!! <= value) && firstElement != null -> null
            lastElement != null && lastElement!! <= value || firstElement != null && lastElement!! > value -> null
            else -> root?.run { find(this, value) }
        }
    }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.run { find(this, value) } ?: start
            else -> start.right?.run { find(this, value) } ?: start
        }
    }

    private fun findParent(node: Node<T>): Node<T>? =
        root?.run { findParent(this, null, node.value) }

    private fun findParent(start: Node<T>, parent: Node<T>?, value: T): Node<T>? {
        val comparison = value.compareTo(start.value)
        return when {
            comparison < 0 -> start.left?.run { findParent(this, start, value) } ?: parent
            comparison == 0 -> parent
            comparison > 0 -> start.right?.run { findParent(this, start, value) } ?: parent
            else -> throw NoSuchElementException()
        }
    }


    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {

        private var current = root
        private val visited = mutableSetOf<Node<T>>()

        /**
         * Проверка наличия следующего элемента
         * Средняя
         * // N - количество узлов дерева
         * // Ресурсоемкость - O(1)
         * // Трудоемкость - O(N)
         */
        override fun hasNext(): Boolean {
            var currentRoot = root
            if (currentRoot?.right != null)
                currentRoot = currentRoot.right
            return currentRoot != current
        }

        /**
         * Поиск следующего элемента
         * Средняя
         * // N - количество узлов дерева
         * // Ресурсоемкость - O(N)
         * // Трудоемкость - O(N)
         */
        private fun nextAllowedNote(
            currentNode: Node<T>?,
            visited: MutableSet<Node<T>>
        ): Pair<Node<T>?, MutableSet<Node<T>>> {
            var lastAllowed: Node<T>?
            val currentNodeLeft = currentNode?.left
            val currentNodeRight = currentNode?.right
            when {
                (currentNodeLeft != null).and(currentNodeLeft !in visited) -> {
                    lastAllowed = currentNode
                    while (lastAllowed?.left != null) lastAllowed = lastAllowed.left
                }
                currentNode !in visited -> lastAllowed = currentNode
                (currentNodeRight != null).and(currentNodeRight !in visited) -> {
                    lastAllowed = currentNodeRight
                    while (lastAllowed?.left != null) lastAllowed = lastAllowed.left
                }
                else -> {
                    if (currentNode == last()) return Pair(null, visited)
                    lastAllowed = currentNode
                    while (lastAllowed in visited) lastAllowed = findParent(lastAllowed!!)
                }
            }
            lastAllowed.run {
                visited.add(this!!)
            }
            return lastAllowed to visited
        }

        override fun next(): T {
            val nextNodeData = nextAllowedNote(current, visited)
            current = nextNodeData.first
            visited.addAll(nextNodeData.second)
            return nextNodeData.first!!.value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         * // Ресурсоемкость - O(1)
         * // Трудоемкость - O(1)
         */
        override fun remove() {
            remove(find(current!!.value)!!)
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null

    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> {
        TODO()
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> {
        TODO()
    }

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
