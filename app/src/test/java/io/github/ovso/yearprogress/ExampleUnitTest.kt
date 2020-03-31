package io.github.ovso.yearprogress

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    // assertEquals(4, 2 + 2)
    val i = 86400000
    val i1 = i / 1000 / 60 / 60
    println("i1 = $i1")
  }
}
