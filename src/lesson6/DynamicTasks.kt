@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File
import java.lang.Math.min


/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 *
 * // N, M - длины строк
 * // N - lengthFirst
 * // M - lengthSecond
 * // Ресурсоемкость - O(N * M)
 * // Трудоемкость - O(N * M)
 */
fun longestCommonSubSequence(first: String, second: String): String {
    when {
        first == second -> return first
        first.isEmpty() || second.isEmpty() -> return ""
    }
    var lengthFirst = first.length
    var lengthSecond = second.length
    val maxLength = Array(lengthFirst + 1) { IntArray(lengthSecond + 1) }
    for (i in 1..lengthFirst) {
        for (j in 1..lengthSecond) {
            if (first[i - 1] == second[j - 1])
                maxLength[i][j] = 1 + maxLength[i - 1][j - 1]
            else
                maxLength[i][j] = Math.max(maxLength[i - 1][j], maxLength[i][j - 1])
        }
    }
    var result = ""
    while (lengthFirst > 0 && lengthSecond > 0) {
        when {
            first[lengthFirst - 1] == second[lengthSecond - 1] -> {
                result = first[lengthFirst - 1] + result
                lengthFirst--
                lengthSecond--
            }
            maxLength[lengthFirst][lengthSecond] == maxLength[lengthFirst - 1][lengthSecond] -> lengthFirst--
            else -> lengthSecond--
        }
    }
    return result
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 *
 * // N - Вместимость листа
 * // N - resultLength
 * // Ресурсоемкость - O(N)
 * // Трудоемкость - O(N*N)
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    when {
        (list.isEmpty()) -> return emptyList()
        (list.size == 1) -> return list
    }
    val resultLength = IntArray(list.size) { 1 }
    var maxLength = 0
    for (i in 1 until list.size) {
        var tempLength = 0
        for (j in 0..i) if (resultLength[j] > tempLength && list[j] < list[i]) tempLength = resultLength[j]
        resultLength[i] = tempLength + 1
        if (resultLength[i] > maxLength) maxLength = resultLength[i]
    }
    val result = mutableListOf<Int>()
    var previous = 0
    while (maxLength > 0) {
        for (i in 0 until resultLength.size)
            if ((previous == 0 || list[i] < previous) && resultLength[i] == maxLength) {
                result.add(0, list[i])
                previous = list[i]
                break
            }
        maxLength--
    }
    return result
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 *
 * // N, M - Высота и ширина поля, соответственно
 * // N - height
 * // M - width
 * // Ресурсоемкость - O(N * M)
 * // Трудоемкость - O(N * M)
 */
fun shortestPathOnField(inputName: String): Int {
    val text = File(inputName).readLines()
    if (text.isEmpty() || text[0].isEmpty()) return 0
    val maxLines = text[0].split(" ").size
    val result = MutableList(text.size + 1) { MutableList(maxLines + 1) { Int.MAX_VALUE } }
    var height = 1
    var width = 1
    result[width - 1][height - 1] = 0
    text.forEach { it ->
        height = 1
        it.split(" ").forEach {
            result[width][height] = it.toInt() +
                    min(
                        result[width - 1][height - 1],
                        min(result[width - 1][height], result[width][height - 1])
                    )
            height++
        }
        width++
    }
    return result[width - 1][height - 1]
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5