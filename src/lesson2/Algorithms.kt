@file:Suppress("UNUSED_PARAMETER")

package lesson2

import java.io.File
import java.lang.Math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
// N - кол-во чисел
// Ресурсоемкость - O(N)
// Трудоемкость - O(N)
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    var bestIncome = Pair(0, 0)
    val income = File(inputName).readLines().map {
        if (!Regex("""\d+""").matches(it))
            throw Exception("Inc. Format")
        it.toInt()
    }
    if (income.isEmpty())
        throw Exception("Incorrect input")
    var tempIndex = 0
    for (i in 0 until income.size) {
        if (income[i] < income[tempIndex]) tempIndex = i
        val tempIncome = Pair(tempIndex, i)
        val resTempIncome = income[tempIncome.second] - income[tempIncome.first]
        val resBestIncome = income[bestIncome.second] - income[bestIncome.first]
        if (resBestIncome < resTempIncome) bestIncome = tempIncome
    }
    bestIncome = Pair(bestIncome.first + 1, bestIncome.second + 1)
    return bestIncome
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
// N - menNumber
// Ресурсоемкость - O(N)
// Трудоемкость - O(1)
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    var temp = 0
    for (it in 0 until menNumber) {
        temp = (temp + choiceInterval) % (it + 1)
    }
    return temp + 1
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */
// N - длина первой строки
// M - длина второй строки
// Ресурсоемкость - O(MN)
// Трудоемкость - O(MN)
fun longestCommonSubstring(first: String, second: String): String {
    if (first.isEmpty() || second.isEmpty()) return ""
    val substrings = mutableListOf("")
    val firstLength = first.lastIndex
    val secondLength = second.lastIndex
    for (i in 0..firstLength) {
        for (j in 0..secondLength) {
            var tempStr = ""
            var nextIndex = 0
            while ((i + nextIndex) <= firstLength
                && (j + nextIndex) <= secondLength
                && first[i + nextIndex] == second[j + nextIndex]
            ) {
                tempStr += first[i + nextIndex]
                nextIndex++
            }
            if (tempStr.length > substrings[0].length)
                substrings[0] = tempStr
        } // Θ(mn)
    }
    return substrings.first()
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */
// N - Число
// Ресурсоемкость - O(N*sqrt(N))
// Трудоемкость - O(1)
fun isPrime(n: Int): Int {
    if (n % 2 == 0) return 0
    val x = sqrt(n.toDouble()).toInt()
    for (m in 3..x step 2) { //O(N*sqrt(N))
        if (n % m == 0) return 0
    }
    return 1
}
fun calcPrimesNumber(limit: Int): Int {
    if (limit < 2) return 0
    var res = 1
    for (i in 2..limit) res += isPrime(i)
    return res
}
/**
 * Балда
 * Сложная
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}