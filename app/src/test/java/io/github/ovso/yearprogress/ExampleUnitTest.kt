package io.github.ovso.yearprogress

import org.junit.Test

import org.junit.Assert.*
import java.util.function.Consumer

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    //assertEquals(4, 2 + 2)
    val arrayListOf = arrayListOf(50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60)
    arrayListOf.forEach(Consumer {
      println("${it / 5 / 2}, ${it % 5}")
    })
  }
}
