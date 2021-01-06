package net.sipconsult.pos.util

import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateFormat
import net.sipconsult.pos.util.ReceiptBuilder.TextAlignment
import org.apache.commons.lang3.StringUtils
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * Copyright 2015 LeeryBit
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class Receipt internal constructor() : Parcelable {
    private val data: MutableList<Byte>
    private val fiscalData: HashMap<String, String>
    private val debugStrBuilder: StringBuilder

    @JvmField
    var charsOnLine: Int
    val charsetName: String
    fun putBytes(bytes: ByteArray) {
        for (b in bytes) {
            data.add(b)
        }
    }

    fun setCyrillic() {
        putBytes(PrinterCommands.SELECT_CYRILLIC_CHARACTER_CODE_TABLE)
    }

    private fun decode(text: String): String {
        return try {
            String(text.toByteArray(charset(charsetName)))
        } catch (e: UnsupportedEncodingException) {
            text
        }
    }

    fun text(text: String, alignment: TextAlignment) {
        var text = text
        debugStrBuilder.append(fixText(text, ' ', alignment, 48))
        text = decode(text)
        putBytes(fixText(decode(text), ' ', alignment, charsOnLine).toByteArray())
    }

    fun menuItem(key: String, value: String, space: Char) {
        debugStrBuilder.append(fixMenu(key, value, space, 48))
        putBytes(fixMenu(decode(key), decode(value), space, charsOnLine).toByteArray())
    }

    fun divider(symbol: Char) {
        debugStrBuilder.append(fixHR(symbol, 48))
        putBytes(fixHR(symbol, charsOnLine).toByteArray())
    }

    fun accent(text: String, accent: Char) {
        var accent = accent
        if (text.length - 4 > charsOnLine) {
            accent = ' '
        }
        debugStrBuilder.append(
            fixText(
                " $text ",
                accent,
                TextAlignment.CENTER,
                48
            )
        )
        putBytes(
            fixText(
                ' '.toString() + decode(text) + ' ',
                accent,
                TextAlignment.CENTER,
                charsOnLine
            ).toByteArray()
        )
    }

    fun feed(count: Int) {
        for (i in 0 until count) {
            debugStrBuilder.append(fixHR(' ', 48))
            putBytes("\n".toByteArray())
        }
    }

    fun saveFiscalData() {}
    fun putFiscalData(key: String?, value: String?) {
        require(!(key == null || key.isEmpty())) { "Key is empty" }
        require(!(value == null || value.isEmpty())) { "Value is empty" }
        require(key != "created_at") { "Key \"created_at\" is predefined" }
        fiscalData[key] = value
    }

    fun getData(): List<Byte> {
        return data
    }

    private fun fixHR(symbol: Char, charsOnLine: Int): String {
        return """
            ${StringUtils.repeat(symbol, charsOnLine)}

            """.trimIndent()
    }

    private fun fixMenu(
        key: String,
        value: String,
        space: Char,
        charsOnLine: Int
    ): String {
        return if (key.length + value.length + 2 > charsOnLine) {
            fixText("$key: $value", ' ', TextAlignment.LEFT, charsOnLine)
        } else StringUtils.rightPad(
            key,
            charsOnLine - value.length,
            space
        ) + value + "\n"
    }

    private fun fixText(
        text: String,
        fill: Char,
        alignment: TextAlignment,
        charsOnLine: Int
    ): String {
        if (text.length > charsOnLine) {
            val out = StringBuilder()
            val len = text.length
            for (i in 0..len / charsOnLine) {
                val str =
                    text.substring(i * charsOnLine, Math.min((i + 1) * charsOnLine, len))
                if (!str.trim { it <= ' ' }.isEmpty()) out.append(
                    fixText(
                        str,
                        fill,
                        alignment,
                        charsOnLine
                    )
                )
            }
            return out.toString()
        }
        return when (alignment) {
            TextAlignment.RIGHT -> StringUtils.leftPad(
                text,
                charsOnLine,
                fill
            ) + "\n"
            TextAlignment.CENTER -> StringUtils.center(
                text,
                charsOnLine,
                fill
            ) + "\n"
            else -> StringUtils.rightPad(
                text,
                charsOnLine,
                fill
            ) + "\n"
        }
    }

    val receiptPreview: String
        get() = debugStrBuilder.toString()

    private val timeStamp: String
        private get() = DateFormat.format("yyyy-MM-dd HH:mm:ss", Date())
            .toString()

    val fiscalPreview: String
        get() {
            if (fiscalData.size == 0) {
                return "FISCAL DATA IS EMPTY"
            }
            val builder = StringBuilder()
            val keys: Set<String> = fiscalData.keys
            for (key in keys) {
                builder.append(key).append(": ").append(fiscalData[key]).append("\n")
            }
            builder.append("created_at: ").append(timeStamp)
            return builder.toString()
        }

    constructor(parcel: Parcel) : this() {
        charsOnLine = parcel.readInt()
    }

    override fun toString(): String {
        return """
            Receipt:
            ${receiptPreview}Fiscal data:
            $fiscalPreview
            """.trimIndent()
    }

    init {
        data = ArrayList()
        fiscalData = HashMap()
        charsOnLine = 48
        charsetName = "ASCII"
        debugStrBuilder = StringBuilder()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(charsOnLine)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Receipt> {
        override fun createFromParcel(parcel: Parcel): Receipt {
            return Receipt(parcel)
        }

        override fun newArray(size: Int): Array<Receipt?> {
            return arrayOfNulls(size)
        }
    }
}