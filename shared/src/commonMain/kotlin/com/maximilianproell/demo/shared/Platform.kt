package com.maximilianproell.demo.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform