package net.sipconsult.pos.data.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

const val POSNumber = "pos_number"

class PosNumberProviderImpl(context: Context) : PosNumberProvider {
    private val appContext = context.applicationContext

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    override fun getPOSNumber(): String {
        val selectedLocation = preferences.getString(POSNumber, "P1")
        return selectedLocation!!

    }
}