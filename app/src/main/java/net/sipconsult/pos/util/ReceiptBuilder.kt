package net.sipconsult.pos.util

import android.content.Context
import net.sipconsult.pos.data.models.CartItem
import net.sipconsult.pos.data.models.PaymentMethodItem
import net.sipconsult.pos.util.PrinterFonts.FontGroup
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.DecimalFormat
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
class ReceiptBuilder {
    enum class TextAlignment {
        LEFT, RIGHT, CENTER
    }

    private val receipt: Receipt = Receipt()
    private val charsOnLine: Int
    fun isCyrillic(cyrillic: Boolean): ReceiptBuilder {
        if (cyrillic) receipt.setCyrillic()
        return this
    }

    @Throws(IOException::class)
    fun raw(
        context: Context,
        resId: Int,
        vararg values: String?
    ): ReceiptBuilder {
        val inputStream = context.resources.openRawResource(resId)
        val inputReader = InputStreamReader(inputStream, "UTF-8")
        val bufferedReader = BufferedReader(inputReader)
        var line: String
        var lineNumber = -3
        var arg = 0
        while (bufferedReader.readLine().also { line = it } != null) {
            if (line.startsWith("^")) {
                if (lineNumber == -3) {
                    // TODO check version!
                } else if (lineNumber == -2) {
                    var group: FontGroup
                    group = try {
                        FontGroup.valueOf(line.substring(1))
                    } catch (e: IllegalArgumentException) {
                        FontGroup.BASIC
                    } catch (e: NullPointerException) {
                        FontGroup.BASIC
                    }
                    PrinterFonts.setFontGroup(group)
                } else if (lineNumber == -1) {
                    // TODO set character set
                } else {
                    while (line.contains("~")) {
                        line = line.replace("~", values[arg]!!)
                        arg++
                    }
                    val items =
                        line.substring(1).split(":".toRegex(), 2).toTypedArray()
                    if (items.size > 2) continue
                    var small = false
                    var bold = false
                    var huge = false
                    var wide = false
                    var underline = false
                    val keys =
                        items[0].toUpperCase(Locale.getDefault()).split("&".toRegex())
                            .toTypedArray()
                    for (key in keys) {
                        if ("S" == key || "SMALL" == key) {
                            small = true
                        } else if ("B" == key || "BOLD" == key) {
                            bold = true
                        } else if ("H" == key || "HUGE" == key) {
                            huge = true
                        } else if ("W" == key || "WIDE" == key) {
                            wide = true
                        } else if ("U" == key || "UNDERLINE" == key) {
                            underline = true
                        }
                    }
                    var charsOnLine = charsOnLine
                    if (small && wide) {
                        charsOnLine = PrinterFonts.smallWideCharsOnLine(charsOnLine)
                    } else if (wide) {
                        charsOnLine = PrinterFonts.wideCharsOnLine(charsOnLine)
                    } else if (small) {
                        charsOnLine = PrinterFonts.smallCharsOnLine(charsOnLine)
                    }
                    receipt.charsOnLine = charsOnLine
                    receipt.putBytes(PrinterFonts.font(small, bold, huge, wide, underline))
                    var find = false
                    for (key in keys) {
                        if ("HD" == key || "HEADER" == key) {
                            header(items[1])
                            find = true
                            break
                        }
                        if ("SHD" == key || "SUB_HEADER" == key) {
                            subHeader(items[1])
                            find = true
                            break
                        }
                        if ("C" == key || "CENTER" == key) {
                            text(items[1], TextAlignment.CENTER)
                            find = true
                            break
                        }
                        if ("R" == key || "RIGHT" == key) {
                            text(items[1], TextAlignment.RIGHT)
                            find = true
                            break
                        }
                        if ("A" == key || "ACCENT" == key) {
                            find = true
                            val ss =
                                items[1].split("|".toRegex()).toTypedArray()
                            if (ss.size != 2) break
                            accent(ss[0], ss[1][0])
                            break
                        }
                        if ("HR" == key) {
                            if (items.size == 1) {
                                divider()
                            } else {
                                divider(items[1][0])
                            }
                            find = true
                            break
                        }
                        if ("HRD" == key) {
                            dividerDouble()
                            find = true
                            break
                        }
                        if ("BR" == key) {
                            var count = 1
                            if (items.size == 2) {
                                count = Integer.valueOf(items[1])
                            }
                            feedLine(count)
                            find = true
                            break
                        }
                        if ("*" == key || "STARED" == key) {
                            stared(items[1])
                            find = true
                            break
                        }
                        if ("M" == key || "MENU" == key) {
                            val ss =
                                items[1].split("\\|".toRegex(), 3).toTypedArray()
                            if (ss.size == 2) {
                                menuLine(ss[0], ss[1])
                            } else if (ss.size == 3) {
                                menuLine(ss[0], ss[1], ss[2][0])
                            }
                            find = true
                            break
                        }
                    }
                    if (!find && items.size == 2) text(items[1])
                    // ticket.putBytes("\n".getBytes());
                    receipt.putBytes(PrinterFonts.resetFont())
                }
                lineNumber++
            }
        }
        receipt.charsOnLine = charsOnLine
        return this
    }

    /**
     * Will print text aligned by right like this:
     * ┌────────────────────┐
     * │          Right text│
     * │                    │
     * │Very long right stri│
     * │       ng text value│
     * └────────────────────┘
     *
     * @param text text value for print
     * @return TicketBuilder
     */
    fun right(text: String?): ReceiptBuilder {
        return text(text, TextAlignment.RIGHT)
    }

    /**
     * Will print text aligned by center like this:
     * ┌────────────────────┐
     * │    Center text     │
     * │                    │
     * │Very long center str│
     * │   ing text value   │
     * └────────────────────┘
     *
     * @param text text value for print
     * @return TicketBuilder
     */
    fun center(text: String?): ReceiptBuilder {
        return text(text, TextAlignment.CENTER)
    }
    /**
     * Will print line with @space across the width of the paper like this:
     * ┌────────────────────┐
     * │Key............Value│
     * └────────────────────┘
     *
     * @param key   key
     * @param value value
     * @param space char for fill empty space
     * @return TicketBuilder
     */
    /**
     * Will print line with spaces across the width of the paper like this:
     * ┌────────────────────┐
     * │Key            Value│
     * └────────────────────┘
     *
     * @param key   key
     * @param value value
     * @return TicketBuilder
     */
    @JvmOverloads
    fun menuLine(
        key: String?,
        value: String?,
        space: Char = ' '
    ): ReceiptBuilder {
        receipt.menuItem(key!!, value!!, space)
        return this
    }

    private fun truncate(str: String, len: Int): String? {
        return if (str.length > len) {
            str.substring(0, len) + "..."
        } else {
            str
        }
    }

    @JvmOverloads
    fun menuItems(
        items: List<CartItem>
    ): ReceiptBuilder {
        val decimalFormater = DecimalFormat("0.00")

        for (item in items) {

            val itemName = item.product.description
            val itemPrice = item.product.price.salePrice

            val itemQuantity = item.quantity


            val itemTotalPrice = itemPrice * itemQuantity
            val itemTotalPriceString = decimalFormater.format(itemTotalPrice)

            menuLine("${truncate(itemName, 21)} $itemQuantity x $itemPrice", itemTotalPriceString)
        }

        return this
    }

    @JvmOverloads
    fun menuPaymentMethod(
        items: ArrayList<PaymentMethodItem>
    ): ReceiptBuilder {
        val decimalFormater = DecimalFormat("0.00")

        for (item in items) {
            menuLine(item.displayName, "GHC ${item.amountPaid}")
        }
        return this
    }

    /**
     * Will print divider like this:
     * ┌────────────────────┐
     * │Text                │
     * ├────────────────────┤
     * │Another text        │
     * └────────────────────┘
     *
     * @return TicketBuilder
     */
    fun divider(): ReceiptBuilder {
        receipt.divider('-')
        return this
    }

    /**
     * Will print divider like this:
     * ┌────────────────────┐
     * │Text                │
     * ╞════════════════════╡
     * │Another text        │
     * └────────────────────┘
     *
     * @return TicketBuilder
     */
    fun dividerDouble(): ReceiptBuilder {
        receipt.divider('=')
        return this
    }

    /**
     * Will print divider with custom divider char like this:
     * ┌────────────────────┐
     * │Text                │
     * │••••••••••••••••••••│
     * │Another text        │
     * └────────────────────┘
     *
     * @return TicketBuilder
     */
    fun divider(symbol: Char): ReceiptBuilder {
        receipt.divider(symbol)
        return this
    }

    /**
     * Will print starred text like this:
     * ┌────────────────────┐
     * │    *** Star ***    │
     * └────────────────────┘
     *
     * @param text header text value
     * @return TicketBuilder
     */
    fun stared(text: String): ReceiptBuilder {
        receipt.text("*** $text ***", TextAlignment.CENTER)
        return this
    }

    /**
     * Will print header like text with custom accent char like this:
     * ┌────────────────────┐
     * │•••••• Accent ••••••│
     * └────────────────────┘
     *
     * @param text header text value
     * @return TicketBuilder
     */
    fun accent(text: String?, accent: Char): ReceiptBuilder {
        receipt.accent(text!!, accent)
        return this
    }

    /**
     * Will print header like this:
     * ┌────────────────────┐
     * │###### HEADER ######│
     * └────────────────────┘
     *
     * @param text header text value
     * @return TicketBuilder
     */
    fun header(text: String): ReceiptBuilder {
        return accent(text.toUpperCase(Locale.getDefault()), '#')
    }

    /**
     * Will print sub header like this:
     * ┌────────────────────┐
     * │   - Sub header -   │
     * └────────────────────┘
     *
     * @param text sub header text value
     * @return TicketBuilder
     */
    fun subHeader(text: String): ReceiptBuilder {
        receipt.text("- $text -", TextAlignment.CENTER)
        return this
    }
    /**
     * Will print simple text value like this:
     * ┌────────────────────┐
     * │Text                │
     * └────────────────────┘
     *
     * @param text      text value
     * @param alignment text alignment
     * @return TicketBuilder
     */
    /**
     * Will print simple text value like this:
     * ┌────────────────────┐
     * │Text                │
     * └────────────────────┘
     *
     * @param text text value
     * @return TicketBuilder
     */
    @JvmOverloads
    fun text(text: String?, alignment: TextAlignment? = TextAlignment.LEFT): ReceiptBuilder {
        receipt.text(text!!, alignment!!)
        return this
    }

    @JvmOverloads
    fun textReceiptNumber(
        text: String?,
        alignment: TextAlignment? = TextAlignment.LEFT
    ): ReceiptBuilder {
        receipt.text(text!!, alignment!!)
        return this
    }
    /**
     * Feed some lines
     *
     * @return TicketBuilder
     */
    /**
     * Feed line
     *
     * @return TicketBuilder
     */
    @JvmOverloads
    fun feedLine(count: Int = 1): ReceiptBuilder {
        receipt.feed(count)
        return this
    }

    /**
     * Put a fiscal data
     *
     * @param key   key
     * @param value value
     * @return TicketBuilder
     */
    fun fiscal(key: String?, value: String?): ReceiptBuilder {
        receipt.putFiscalData(key, value)
        return this
    }

    fun fiscalInt(key: String?, value: Int): ReceiptBuilder {
        return fiscal(key, value.toString())
    }

    @JvmOverloads
    fun fiscalDouble(key: String?, value: Double, size: Int = 32): ReceiptBuilder {
        // TODO format string!
        return fiscal(key, value.toString())
    }

    /**
     * Build a ticket
     *
     * @return Ticket
     */
    fun build(): Receipt {
        return receipt
    }

    init {
        charsOnLine = 48
    }
}