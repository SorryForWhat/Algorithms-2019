@file:Suppress("UNUSED_PARAMETER")

package lesson1

import java.io.File
import java.util.*
import kotlin.math.*

/**
 * Сортировка времён
 *
 * Простая
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле с именем inputName содержатся моменты времени в формате ЧЧ:ММ:СС AM/PM,
 * каждый на отдельной строке. См. статью википедии "12-часовой формат времени".
 *
 * Пример:
 *
 * 01:15:19 PM
 * 07:26:57 AM
 * 10:00:03 AM
 * 07:56:14 PM
 * 01:15:19 PM
 * 12:40:31 AM
 *
 * Отсортировать моменты времени по возрастанию и вывести их в выходной файл с именем outputName,
 * сохраняя формат ЧЧ:ММ:СС AM/PM. Одинаковые моменты времени выводить друг за другом. Пример:
 *
 * 12:40:31 AM
 * 07:26:57 AM
 * 10:00:03 AM
 * 01:15:19 PM
 * 01:15:19 PM
 * 07:56:14 PM
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// N - количество строк во входном файле
// Ресурсоемкость - O(N)
// Трудоемкость - O(N log2N)
fun sortTimes(inputName: String, outputName: String) {
    File(outputName).bufferedWriter().use {
        val file = File(inputName).readLines().map {
            if (!Regex("""((1[0-2])|(0\d)):(\d\d):(\d\d) ((AM)|(PM))""").matches(it))
                throw Exception("Inc. Format")
            val parts = it.split(" ")
            val timeParts = parts[0].split(":")
            val hours = timeParts[0].toInt()
            val minutes = timeParts[1].toInt()
            val seconds = timeParts[2].toInt()
            val period = parts[1]
            if (hours !in 0..12 || minutes !in 0..59 || seconds !in 0..59) throw Exception("Inc. Format")
            Time(hours, minutes, seconds, period) to it
        }
        val result = file.sortedWith(
            compareBy(
                { it.first.period },
                { it.first.hours % 12 },
                { it.first.minutes },
                { it.first.seconds })
        )
        it.write(result.joinToString("\n") { it.second })
    }
}
data class Time(
    var hours: Int,
    var minutes: Int,
    var seconds: Int,
    var period: String
)


/**
 * Сортировка адресов
 *
 * Средняя
 *
 * Во входном файле с именем inputName содержатся фамилии и имена жителей города с указанием улицы и номера дома,
 * где они прописаны. Пример:
 *
 * Петров Иван - Железнодорожная 3
 * Сидоров Петр - Садовая 5
 * Иванов Алексей - Железнодорожная 7
 * Сидорова Мария - Садовая 5
 * Иванов Михаил - Железнодорожная 7
 *
 * Людей в городе может быть до миллиона.
 *
 * Вывести записи в выходной файл outputName,
 * упорядоченными по названию улицы (по алфавиту) и номеру дома (по возрастанию).
 * Людей, живущих в одном доме, выводить через запятую по алфавиту (вначале по фамилии, потом по имени). Пример:
 *
 * Железнодорожная 3 - Петров Иван
 * Железнодорожная 7 - Иванов Алексей, Иванов Михаил
 * Садовая 5 - Сидоров Петр, Сидорова Мария
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// N - количество строк во входном файле
// Ресурсоемкость - O(N)  Трудоемкость - O(2N * log(N))
fun sortAddresses(inputName: String, outputName: String) {
    File(outputName).bufferedWriter().use {
        val address = sortedMapOf<String, SortedMap<Int, MutableList<String>>>()// O(s*m*k)
        File(inputName).readLines().forEach {
            if (!Regex("""[A-zА-яёЁ]+ [A-zА-яёЁ]+ - [A-zА-яёЁ-]+ \d+""").matches(it))
                throw Exception("Inc. Format")
            val lineParts = it.split(" ")
            address.getOrPut(lineParts[3], { sortedMapOf() })
                .getOrPut(lineParts[4].toInt(), { mutableListOf() })
                .add(lineParts[0] + " " + lineParts[1])
        }//O(N * log(s * m)) - вероятно это можно также определить как O(2N * log(N))
        //Т.к. соотносительное затрачиваемое логорифмическое время примерно одинаково
        for (street in address)
            for (number in street.value) {
                it.write(street.key + " " + number.key + " - " + number.value.sorted().joinToString(", "))
                it.newLine()
            }
    }
}

/**
 * Сортировка температур
 *
 * Средняя
 * (Модифицированная задача с сайта acmp.ru)
 *
 * Во входном файле заданы температуры различных участков абстрактной планеты с точностью до десятых градуса.
 * Температуры могут изменяться в диапазоне от -273.0 до +500.0.
 * Например:
 *
 * 24.7
 * -12.6
 * 121.3
 * -98.4
 * 99.5
 * -12.6
 * 11.0
 *
 * Количество строк в файле может достигать ста миллионов.
 * Вывести строки в выходной файл, отсортировав их по возрастанию температуры.
 * Повторяющиеся строки сохранить. Например:
 *
 * -98.4
 * -12.6
 * -12.6
 * 11.0
 * 24.7
 * 99.5
 * 121.3
 */
// N - количество строк во входном файле
// Трудоёмкость - O(N)
// Ресурсоёмкость - O(N)
const val tempLimit = 7731
const val tempAbsMinLimit = 2730
fun sortTemperatures(inputName: String, outputName: String) {
    val temp = IntArray(tempLimit)
    File(outputName).bufferedWriter().use {
        File(inputName).readLines().forEach {
            if (!Regex("""-?\d+.\d""").matches(it))
                throw Exception("Inc. Format")
            val tempValue = it.replace(".", "").toInt() + tempAbsMinLimit
            temp[tempValue]++
        }
        for (i in 0 until tempLimit)
            while (temp[i] > 0) {
                val result = i - tempAbsMinLimit
                it.write("-".takeIf { (result < 0) }.orEmpty() + "${abs(result) / 10}.${abs(result) % 10}")
                it.newLine()
                temp[i]--
            }
    }
}

/**
 * Сортировка последовательности
 *
 * Средняя
 * (Задача взята с сайта acmp.ru)
 *
 * В файле задана последовательность из n целых положительных чисел, каждое в своей строке, например:
 *
 * 1
 * 2
 * 3
 * 2
 * 3
 * 1
 * 2
 *
 * Необходимо найти число, которое встречается в этой последовательности наибольшее количество раз,
 * а если таких чисел несколько, то найти минимальное из них,
 * и после этого переместить все такие числа в конец заданной последовательности.
 * Порядок расположения остальных чисел должен остаться без изменения.
 *
 * 1
 * 3
 * 3
 * 1
 * 2
 * 2
 * 2
 */
fun sortSequence(inputName: String, outputName: String) {
    TODO()
}

/**
 * Соединить два отсортированных массива в один
 *
 * Простая
 *
 * Задан отсортированный массив first и второй массив second,
 * первые first.size ячеек которого содержат null, а остальные ячейки также отсортированы.
 * Соединить оба массива в массиве second так, чтобы он оказался отсортирован. Пример:
 *
 * first = [4 9 15 20 28]
 * second = [null null null null null 1 3 9 13 18 23]
 *
 * Результат: second = [1 3 4 9 9 13 15 20 23 28]
 */
// N - количество строк во входном файле
// Трудоёмкость - O(N)
// Ресурсоёмкость - O(1) или O(N)
fun <T : Comparable<T>> mergeArrays(first: Array<T>, second: Array<T?>) {
    var firstIndicate = 0
    var secondIndicate = first.size
    for (num in second.indices) {
        if (!first.isNullOrEmpty() && !second.isNullOrEmpty()) {
            if (secondIndicate >= second.size ||
                first.size > firstIndicate &&
                first[firstIndicate] <= second[secondIndicate]!!) {
                second[num] = first[firstIndicate]
                firstIndicate++
            } else {
                second[num] = second[secondIndicate]
                secondIndicate++
            }
        } else throw Exception("Empty Array")
    }
}

