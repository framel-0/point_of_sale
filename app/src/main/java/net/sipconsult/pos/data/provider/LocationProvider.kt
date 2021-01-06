package net.sipconsult.pos.data.provider

interface LocationProvider {
    fun getLocation(): String
}