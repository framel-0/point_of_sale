package net.sipconsult.pos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_scanning.*


class ScanningActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanning)

//        editTextBarcode.isFocusableInTouchMode = true
//        editTextBarcode.isFocusable = true
        editTextBarcode.requestFocus()
//        editTextBarcode.inputType = InputType.TYPE_NULL

        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editTextBarcode, InputMethodManager.SHOW_IMPLICIT)

        buttonBarcodeEnter.setOnClickListener {
            val barcode = editTextBarcode.text.toString()
            doneWith(barcode)
        }

        editTextBarcode.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val barcode = editTextBarcode.text.toString().trim()
                doneWith(barcode)
                return@OnKeyListener true
            }
            false
        })


    }

    private fun showKeyboard() {
        editTextBarcode.inputType = InputType.TYPE_CLASS_TEXT
        editTextBarcode.requestFocus()
        val mgr: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        mgr.showSoftInput(editTextBarcode, InputMethodManager.SHOW_FORCED)
    }

    private fun doneWith(barcode: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("product_barcode", barcode)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}