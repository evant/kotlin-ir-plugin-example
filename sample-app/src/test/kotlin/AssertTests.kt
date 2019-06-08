import org.junit.Test

class AssertTests {

    @Test
    fun equals_expr() {
        val one = 1
        assert(one == 2, { "${one} == 2"})
    }
}