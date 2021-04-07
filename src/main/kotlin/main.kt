import java.io.File
import java.util.*

/**
 * Вариант 9 -- tail
 * Выделение из текстового файла его конца некоторого размера:

 * fileN задаёт имя входного файла. Если параметр отсутствует, следует считывать входные данные с консольного ввода.
 * Если параметров несколько, то перед выводом для каждого файла следует вывести его имя в отдельной строке.

 * Флаг -o ofile задаёт имя выходного файла (в данном случае ofile).
 * Если параметр отсутствует, следует выводить результат на консольный вывод.

 * Флаг -с num, где num это целое число, говорит о том, что из файла нужно извлечь последние num символов.

 * Флаг -n num, где num это целое число, говорит о том, что из файла нужно извлечь последние num строк.

 * Command line: tail [-c num|-n num] [-o ofile] [file0 file1 file2 …]

 * В случае, когда какое-нибудь из имён файлов неверно или указаны одновременно флаги -c и -n, следует выдать ошибку.
 * Если ни один из этих флагов не указан, следует вывести последние 10 строк.
 * Кроме самой программы, следует написать автоматические тесты к ней.
 *
 *
 * +1 Не очень хорошо хранить typeElem как целое число, так как непонятно, какое число за какой тип отвечает.
 * В идеале, нужно завести enum, но сгодится и Boolean (но тогда обязательно нужно переименовать typeElem так,
 * чтобы было очевидно, какой тип true, а какой - false).
 * +2 На консоль не следует выводить никаких лишних надписей, например, приглашения ввода.
 * И чтение из консоли должно так же заканчиваться по "концу файла", а не по пустой строке.
 * +3 Классическое (POSIX) определение текстового файла - набор строк, где строка - набор символов,
 * оканчивающийся символом конца строки. То есть даже в конце последней строки принято ставить "перенос строки",
 * поэтому не нужно специально прикладывать усилия, чтобы его не поставить.
 * +4 Не следует выбрасывать ожидаемые исключения из main. Согласитесь, пользователю будет проще понять,
 * что случилось, если вывести ему понятный текст ошибки, а не вываливать на него стек вызовов.
 */

class Tail {
    enum class TypeElem {NONE, CHARS, LINES}
    private val listInputFiles = mutableListOf<String>()
    private var countElem = 10
    private var outFileName = ""
    private var typeElem = TypeElem.NONE

    // разбор параметров
    fun parse(param: List<String>): String {
        var i = 0
        while (i < param.size) {
            when (param[i]) {
                "-o" -> {
                    if (++i == param.size) return "Error, missing parameter."
                    if (outFileName != "") return "Error, there can be no more one output file."
                    outFileName = param[i]
                }
                "-c" -> {
                    if (++i == param.size) return "Error, missing parameter."
                    if (typeElem != TypeElem.NONE) return "Error in specifying the -n and -c parameters."

                    countElem = param[i].toInt()
                    typeElem = TypeElem.CHARS
                }
                "-n" -> {
                    if (++i == param.size) return "Error, missing parameter."
                    if (typeElem != TypeElem.NONE) return "Error in specifying the -n and -c parameters."
                    countElem = param[i].toInt()
                    typeElem = TypeElem.LINES
                }
                else -> {
                    if (!File(param[i]).exists()) return "Error, input file '${param[i]}' not found."
                    listInputFiles.add(param[i])
                }
            }
            i++
        }
        if (typeElem == TypeElem.NONE) typeElem = TypeElem.LINES
        return ""
    }


    // исполнение
    fun worker(): List<String> {
        val rez = mutableListOf<String>()
        if (listInputFiles.size == 0) {
            val inChars = mutableListOf<String>()
            do {
                val s = readLine() ?: break
                inChars.add(s)
            } while (true)
            if (typeElem == TypeElem.LINES) extractLines(inChars, rez, countElem)
            else extractChars(inChars.joinToString("\n"), rez, countElem)
        } else {
            for (i in 0 until listInputFiles.size) {
                if (listInputFiles.size > 1) rez.add(listInputFiles[i].padStart(40, '*').padEnd(79, '*'))
                if (typeElem == TypeElem.LINES) extractLines(File(listInputFiles[i]).readLines(), rez, countElem)
                else extractChars(File(listInputFiles[i]).readText(), rez, countElem)
            }
        }
        return rez
    }

    // вывод результата
    fun putResult(lst: List<String>) {
        if (outFileName != "") {
            File(outFileName).bufferedWriter().use { file ->
                lst.forEachIndexed { ind, s ->
                    file.write(s)
                    file.newLine()
                }
            }
        } else {
            for (s in lst)
                println(s)
        }

    }

    /**
     * Вывести последние count строк
     */
    private fun extractLines(txt: List<String>, out: MutableList<String>, count: Int): Boolean {
        val st = if (count < txt.count()) txt.lastIndex - count + 1 else 0
        for (i in st..txt.lastIndex)
            out.add(txt[i])
        return true
    }

    /**
     * Вывести последние count символов
     */
    private fun extractChars(txt: String, out: MutableList<String>, count: Int): Boolean {
        val st = if (count < txt.count()) txt.lastIndex - count + 1 else 0
        out.add(txt.substring(st..txt.lastIndex))
        return true
    }

}

/**
 *
 */
fun main(args: Array<String>) {
    val tail = Tail()
    with(tail) {
        val s = parse(args.toList())
        if (s != "") println(s)
        else putResult(worker())
    }
}
