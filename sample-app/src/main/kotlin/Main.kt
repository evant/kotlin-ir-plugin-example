@file:JvmName("Main")

package me.tatarka.kotlinir.sample

class Main {
    companion object {
        fun magic(): String = TODO()

        @JvmStatic
        fun main(args: Array<String>) {
            println(magic())
        }
    }
}