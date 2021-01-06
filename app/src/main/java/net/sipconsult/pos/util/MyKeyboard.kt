package net.sipconsult.pos.util

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import net.sipconsult.pos.R

class MyKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var button5: Button
    private lateinit var button6: Button
    private lateinit var button7: Button
    private lateinit var button8: Button
    private lateinit var button9: Button
    private lateinit var button0: Button
    private lateinit var buttonDelete: Button
    private lateinit var buttonEnter: Button
    private val keyValues = SparseArray<String>()
    private var inputConnection: InputConnection? = null
    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {
        LayoutInflater.from(context).inflate(R.layout.keyboard, this, true)
        button1 = findViewById<View>(R.id.button_1) as Button
        button1.setOnClickListener(this)
        button2 = findViewById<View>(R.id.button_2) as Button
        button2.setOnClickListener(this)
        button3 = findViewById<View>(R.id.button_3) as Button
        button3.setOnClickListener(this)
        button4 = findViewById<View>(R.id.button_4) as Button
        button4.setOnClickListener(this)
        button5 = findViewById<View>(R.id.button_5) as Button
        button5.setOnClickListener(this)
        button6 = findViewById<View>(R.id.button_6) as Button
        button6.setOnClickListener(this)
        button7 = findViewById<View>(R.id.button_7) as Button
        button7.setOnClickListener(this)
        button8 = findViewById<View>(R.id.button_8) as Button
        button8.setOnClickListener(this)
        button9 = findViewById<View>(R.id.button_9) as Button
        button9.setOnClickListener(this)
        button0 = findViewById<View>(R.id.button_0) as Button
        button0.setOnClickListener(this)
        buttonDelete = findViewById<View>(R.id.button_delete) as Button
        buttonDelete.setOnClickListener(this)
        buttonEnter = findViewById<View>(R.id.button_decimal_place) as Button
        buttonEnter.setOnClickListener(this)
        keyValues.put(R.id.button_1, "1")
        keyValues.put(R.id.button_2, "2")
        keyValues.put(R.id.button_3, "3")
        keyValues.put(R.id.button_4, "4")
        keyValues.put(R.id.button_5, "5")
        keyValues.put(R.id.button_6, "6")
        keyValues.put(R.id.button_7, "7")
        keyValues.put(R.id.button_8, "8")
        keyValues.put(R.id.button_9, "9")
        keyValues.put(R.id.button_0, "0")
        keyValues.put(R.id.button_decimal_place, ".")
    }

    override fun onClick(view: View) {
        if (inputConnection == null) return
        if (view.id == R.id.button_delete) {
            val selectedText = inputConnection!!.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection!!.deleteSurroundingText(1, 0)
            } else {
                inputConnection!!.commitText("", 1)
            }
        } else {
            val value = keyValues[view.id]
            inputConnection!!.commitText(value, 1)
        }
    }

    fun setInputConnection(ic: InputConnection?) {
        inputConnection = ic
    }

    init {
        init(context, attrs)
    }
}