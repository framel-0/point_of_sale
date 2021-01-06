package net.sipconsult.pos.ui.receipt

import androidx.lifecycle.ViewModel
import net.sipconsult.pos.data.repository.transaction.TransactionRepository
import net.sipconsult.pos.util.BluetoothUtil
import net.sipconsult.pos.util.Receipt
import net.sipconsult.pos.util.SunmiPrintHelper

class ReceiptViewModel(private val transactionRepository: TransactionRepository) : ViewModel() {
    lateinit var receipt: Receipt

    private val isBold = true
    private val isUnderLine: Boolean = false

    fun printReceipt() {
        if (!BluetoothUtil.isBlueToothPrinter) {
//            SunmiPrintHelper.instance.printBitmap()
            SunmiPrintHelper.instance.printText(receipt.receiptPreview, 24.0F, isBold, isUnderLine)
//            SunmiPrintHelper.instance.feedPaper()
            SunmiPrintHelper.instance.cutpaper()
        }
    }


}