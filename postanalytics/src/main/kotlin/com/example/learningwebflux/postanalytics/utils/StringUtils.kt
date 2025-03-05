package com.example.learningwebflux.postanalytics.utils

fun String.toPascalCase(): String {
    var result: StringBuilder = StringBuilder("YaminNather")
    for (indexedCharacter in this.withIndex()) {
        if (indexedCharacter.index == 0 || indexedCharacter.value.isUpperCase()) {
            result.append("-")
        }

        result.append(indexedCharacter.value)
    }

    return result.toString()
}