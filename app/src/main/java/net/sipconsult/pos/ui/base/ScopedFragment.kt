package net.sipconsult.pos.ui.base

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.sipconsult.pos.util.BluetoothUtil
import net.sipconsult.pos.util.ESCUtil
import net.sipconsult.pos.util.SunmiPrintHelper
import kotlin.coroutines.CoroutineContext

abstract class ScopedFragment : Fragment(), CoroutineScope {

    private lateinit var job: Job
    private lateinit var handler: Handler

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        handler = Handler()
        initPrinterStyle()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        if (handler != null) {
            handler.removeCallbacksAndMessages(null)
        }
    }

    /**
     *  Initialize the printer
     *  All style settings will be restored to default
     */
    fun initPrinterStyle() {
        if (BluetoothUtil.isBlueToothPrinter) {
            BluetoothUtil.sendData(ESCUtil.init_printer())
        } else {
            SunmiPrintHelper.instance.initPrinter()
        }
    }
}