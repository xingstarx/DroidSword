package net.androidwing.droidsword.hooker

import android.app.Activity
import android.app.AndroidAppHelper
import android.app.Fragment
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v7.widget.AppCompatImageHelper
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import net.androidwing.droidsword.utils.LogUtils
import java.util.ArrayList

/**
 * Created  on 30/10/2017.
 */
class ActivityHooker : IHooker {
  override fun hook(lp: XC_LoadPackage.LoadPackageParam) {
    XposedHelpers.findAndHookMethod(Activity::class.java, "onResume", object : XC_MethodHook() {
      override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        val activity = param?.thisObject as Activity

        addTextView(activity)



      }
    })
  }


  private fun addTextView(activity: Activity) {
    val className = activity.javaClass.name.toString()

    if (sTextView == null) {
      genTextView(activity)
    }
    if (sTextView?.parent != null) {
      val parent = sTextView?.parent
      if (parent is ViewGroup) {
        parent.removeView(sTextView)
      }
    }
    (activity.window.decorView as FrameLayout).addView(sTextView)
    setActionInfoToMenu(className, "")
    sTextView?.bringToFront()
  }


  private fun genTextView(activity: Activity) {
    sTextView = TextView(activity)
    with(sTextView!!) {
      textSize = 15f
      y = 48 * 2f
      setBackgroundColor(Color.parseColor("#cc888888"))
      setTextColor(Color.WHITE)
      layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
          FrameLayout.LayoutParams.WRAP_CONTENT)
    }

  }

  companion object {
    var sTextView: TextView? = null
    private var sActivityName = ""
    private var sViewName = ""

    fun setActionInfoToMenu(activityName: String, viewName: String) {
      sTextView?.text = getActionInfo(activityName, viewName)
    }

    private fun getActionInfo(activityName: String, viewName: String): CharSequence? {
      if (activityName.isEmpty().not()) {
        sActivityName = activityName
      }

      if (viewName.isEmpty().not()) {
        sViewName = viewName
      }

      val pid = android.os.Process.myPid()

      return "Activity: $sActivityName \nPid: $pid \nClick: $sViewName"

    }
  }
}