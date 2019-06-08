import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class AssertkTests {

    data class Person(val name: String, val age: Int)

    @Test
    fun value_access() {
        val error = assertFails {
            val one = 1
            assertThat(one).isEqualTo(2)
        }
        assertEquals("expected [one]:<[2]> but was:<[1]>", error.message)
    }

    @Test
    fun expr_access() {
        val error = assertFails {
            assertThat(2 + 3).isEqualTo(2)
        }
        assertEquals("expected [2 + 3]:<[2]> but was:<[5]>", error.message)
    }

    @Test
    fun property_access() {
        val error = assertFails {
            val person = Person("Sue", 22)
            assertAll {
                assertThat(person.name).isEqualTo("Bob")
                assertThat(person.age).isEqualTo(21)
            }
        }
        assertEquals(
            """The following assertions failed (2 failures)
              |${"\t"}expected [person.name]:<"[Bob]"> but was:<"[Sue]">
              |${"\t"}expected [person.age]:<2[1]> but was:<2[2]>""".trimMargin(), error.message
        )
    }

    @Test
    fun method_call() {
        val error = assertFails {
            assertThat("one".toString()).isEqualTo("two")
        }
        assertEquals("expected [\"one\".toString()]:<\"[two]\"> but was:<\"[one]\">", error.message)
    }

    @Test
    fun explicit_actual() {
        val error = assertFails {
            assertThat(actual = 2 + 3).isEqualTo(2)
        }
        assertEquals("expected [2 + 3]:<[2]> but was:<[5]>", error.message)
    }

    @Test
    fun name() {
        val error = assertFails {
            assertThat(2 + 3, "name").isEqualTo(2)
        }
        assertEquals("expected [name]:<[2]> but was:<[5]>", error.message)
    }

    @Test
    fun name_first() {
        val error = assertFails {
            assertThat(name = "name", actual = 2 + 3).isEqualTo(2)
        }
        assertEquals("expected [name]:<[2]> but was:<[5]>", error.message)
    }
}