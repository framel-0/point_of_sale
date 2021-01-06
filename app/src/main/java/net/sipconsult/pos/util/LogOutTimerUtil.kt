package net.sipconsult.pos.util

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import android.os.AsyncTask
import java.util.*
import java.util.concurrent.ExecutionException

object LogOutTimerUtil {

    var longTimer: Timer? = null
    private const val LOGOUT_TIME: Long =
        1800000 // delay in milliseconds i.e. 5 min = 300000 ms or use timeout argument

    @Synchronized
    fun startLogoutTimer(context: Context?, logOutListener: LogOutListener) {
        if (longTimer != null) {
            longTimer!!.cancel()
            longTimer = null
        }
        if (longTimer == null) {
            longTimer = Timer()
            longTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    cancel()
                    longTimer = null
                    try {
                        val foreGround = ForegroundCheckTask().execute(context).get()
                        if (foreGround) {
                            logOutListener.doLogout()
                        }
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    }
                }
            }, LOGOUT_TIME)
        }
    }

    @Synchronized
    fun stopLogoutTimer() {
        if (longTimer != null) {
            longTimer!!.cancel()
            longTimer = null
        }
    }

    interface LogOutListener {
        fun doLogout()
    }

    internal class ForegroundCheckTask : AsyncTask<Context?, Void?, Boolean>() {
        override fun doInBackground(vararg params: Context?): Boolean {
            val context = params[0]!!.applicationContext
            return isAppOnForeground(context)
        }

        private fun isAppOnForeground(context: Context): Boolean {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val appProcesses = activityManager.runningAppProcesses ?: return false
            val packageName = context.packageName
            for (appProcess in appProcesses) {
                if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName) {
                    return true
                }
            }
            return false
        }
//
//        override fun doInBackground(vararg p0: Context?): Boolean {
//            TODO("Not yet implemented")
//        }
    }
}