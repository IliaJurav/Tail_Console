import java.io.File
import java.util.*
import kotlin.system.exitProcess

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
 **/

class Tail {
    enum class TypeElem { NONE, CHARS, LINES }

    private val listInputFiles = mutableListOf<String>()
    private var countElem = 10
    private var outFileName = ""
    private var typeElem = TypeElem.NONE

    // разбор параметров
    fun parse(param: List<String>) {
        var i = 0
        while (i < param.size) {
            when (param[i]) {
                "-o" -> {
                    if (++i == param.size) throw IllegalArgumentException("Error, missing parameter.")
                    if (outFileName != "") throw IllegalArgumentException("Error, there can be no more one output file.")
                    outFileName = param[i]
                }
                "-c" -> {
                    if (++i == param.size) throw IllegalArgumentException("Error, missing parameter.")
                    if (typeElem != TypeElem.NONE) throw IllegalArgumentException("Error in specifying the -n and -c parameters.")
                    countElem = param[i].toInt()
                    typeElem = TypeElem.CHARS
                }
                "-n" -> {
                    if (++i == param.size) throw IllegalArgumentException("Error, missing parameter.")
                    if (typeElem != TypeElem.NONE) throw IllegalArgumentException("Error in specifying the -n and -c parameters.")
                    countElem = param[i].toInt()
                    typeElem = TypeElem.LINES
                }
                else -> {
                    if (!File(param[i]).exists()) throw IllegalArgumentException("Error, input file '${param[i]}' not found.")
                    listInputFiles.add(param[i])
                }
            }
            i++
        }
        if (countElem < 1) throw IllegalArgumentException("Error in specifying the quantity in the -n and -c options.")
        if (typeElem == TypeElem.NONE) typeElem = TypeElem.LINES
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
            if (typeElem == TypeElem.LINES) extractLines(inChars, rez)
            else extractChars(inChars.joinToString("\n"), rez)
        } else {
            for (i in 0 until listInputFiles.size) {
                if (listInputFiles.size > 1) rez.add(listInputFiles[i].padStart(40, '*').padEnd(79, '*'))
                if (typeElem == TypeElem.LINES) extractLines(File(listInputFiles[i]).readLines(), rez)
                else extractChars(File(listInputFiles[i]).readText(), rez)
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
    private fun extractLines(txt: List<String>, out: MutableList<String>): Boolean {
        val st = if (countElem < txt.count()) txt.lastIndex - countElem + 1 else 0
        for (i in st..txt.lastIndex)
            out.add(txt[i])
        return true
    }

    /**
     * Вывести последние count символов
     */
    private fun extractChars(txt: String, out: MutableList<String>): Boolean {
        val st = if (countElem < txt.count()) txt.lastIndex - countElem + 1 else 0
        out.add(txt.substring(st..txt.lastIndex))
        return true
    }

}

/**
 *
 */
fun main(args: Array<String>) {
    val tail = Tail()
    try {
        with(tail) {
            parse(args.toList())
            putResult(worker())
        }
    } catch (e: Exception) {
        println(e.message)
        exitProcess(1)
    }
}
