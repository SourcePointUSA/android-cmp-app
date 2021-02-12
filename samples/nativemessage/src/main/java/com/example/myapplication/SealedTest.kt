package com.example.myapplication

sealed class Base(
    val name : String
)

class Sealed1(
    name : String,
    val id : Int
) : Base(name)
class Sealed2(
    name : String,
    val text : String
) : Base(name)

fun getSealed1() : Base = Sealed1("test", 1)
fun getSealed2() : Base = Sealed2("test", "")