package ru.android.home.chatexample1

internal class MemberData {
    lateinit var name: String
    lateinit var color: String

    constructor(name: String, color: String) {
        this.name = name
        this.color = color
    }

    // Add an empty constructor so we can later parse JSON into MemberData using Jackson
    constructor() {}
}