package ua.leonidius.beatinspector.infrastructure

import android.content.Context
import android.content.pm.PackageManager

// todo: maybe have it return flow if there's a lib for that?
fun Context.isPackageInstalled(packageName: String): Boolean {
    return try {
        packageManager.getPackageInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}