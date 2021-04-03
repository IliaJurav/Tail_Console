import java.io.File

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
 */

class Tail {
    private val listInputFiles = mutableListOf<String>()
    private var countElem = 10
    private var outFileName = ""
    private var typeElem = 0

    // разбор параметров
    fun parse(param: List<String>): Boolean {
        var i = 0
        while (i < param.size) {
            when (param[i]) {
                "-o" -> {
                    i++
                    if (outFileName != "") throw IllegalArgumentException("There can be only one output file")
                    outFileName = param[i]
                }
                "-c" -> {
                    i++
                    if (typeElem != 0)
                        throw IllegalArgumentException("Error in specifying the -n and -c parameters.")
                    countElem = param[i].toInt()
                    typeElem = 1
                }
                "-n" -> {
                    i++
                    if (typeElem != 0) throw IllegalArgumentException("Error in specifying the -n and -c parameters.")
                    countElem = param[i].toInt()
                    typeElem = 2
                }
                else -> {
                    if (!File(param[i]).exists())
                        throw IllegalArgumentException("Input file '${param[i]}' not found.")
                    listInputFiles.add(param[i])
                }
            }
            i++
        }
        if (typeElem == 0) typeElem = 2
        return true
    }

    // исполнение
    fun worker(): List<String> {
        val rez = mutableListOf<String>()
        if (listInputFiles.size == 0) {
            println("Enter text, end of input is an empty line:")
            val inChars = mutableListOf<String>()
            do {
                val s = readLine()
                if (s == null || s == "") break
                inChars.add(s)
            } while (true)
            println("Ввод окончен.")
            if (typeElem == 2) extractLines(inChars, rez, countElem)
            else extractChars(inChars.joinToString("\n"), rez, countElem)
        } else {
            for (i in 0 until listInputFiles.size) {
                if (listInputFiles.size > 1) rez.add(listInputFiles[i].padStart(40,'*').padEnd(79,'*'))
                if (typeElem == 2) extractLines(File(listInputFiles[i]).readLines(), rez, countElem)
                else extractChars(File(listInputFiles[i]).readText(), rez, countElem)
            }
        }
        return rez
    }

    // вывод результата
    fun putResult(lst: List<String>) {
        if (outFileName != "") {
            File(outFileName).bufferedWriter().use { file ->
                lst.forEachIndexed{ ind, s ->
                    file.write(s)
                    if (ind<lst.lastIndex)file.newLine()
                }
            }
        } else {
            for (s in lst)
                System.out.println(s)
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
        parse(args.toList())
        putResult(worker())
    }
}
