package test

import Tail
import main
import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.*

class Tests {
    @Test
    @Tag("Main")
    fun checkMain() {

        main(arrayOf("-c", "50", "-o", "output/rez.txt", "input/file2.txt"))
        assertEquals(File("output/rez.txt").readLines(), File("output/rezOk2.txt").readLines())
        File("output/rez.txt").delete()

        main(
            arrayOf(
                "-o",
                "output/rez.txt",
                "input/file1.txt",
                "input/file2.txt",
                "input/file3.txt",
                "input/file4.txt"
            )
        )
        assertEquals(File("output/rez.txt").readLines(), File("output/rezOk3.txt").readLines())
        File("output/rez.txt").delete()

        main(arrayOf("-n", "12", "-o", "output/rez.txt", "input/file2.txt", "input/file1.txt"))
        assertEquals(File("output/rez.txt").readLines(), File("output/rezOk1.txt").readLines())
        File("output/rez.txt").delete()
    }

    // проверка ошибок
    @Test
    @Tag("Error")
    fun checkError() {
        // использование -c и -n одновременно
        val t = Tail()
        //assertThrows(IllegalArgumentException::class.java) { plusMinus("+2") }
        assertThrows(IllegalArgumentException::class.java) {
            t.parse(
                listOf(
                    "-c",
                    "50",
                    "-n",
                    "20",
                    "input/file2.txt"
                )
            )
        }

        // несуществующий входной файл
        assertThrows(IllegalArgumentException::class.java) { t.parse(listOf("input/file22.txt")) }

        // использование двух выходных файлов

        assertThrows(IllegalArgumentException::class.java) {
            t.parse(
                listOf(
                    "-o",
                    "output/rez22.txt",
                    "-o",
                    "output/rez23.txt"
                )
            )
        }
        // неполный параметр
        assertThrows(IllegalArgumentException::class.java) { t.parse(listOf("-o", "output/rez22.txt", "-c")) }

        // неправильное количество
        assertThrows(IllegalArgumentException::class.java) { t.parse(listOf("-n", "-1")) }
    }
}


