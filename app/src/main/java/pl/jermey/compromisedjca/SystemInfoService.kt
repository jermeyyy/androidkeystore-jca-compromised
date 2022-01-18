package pl.jermey.compromisedjca

import android.os.Build

class SystemInfoService {
    fun getSystemInfo(): SystemInfo {
        return SystemInfo(
            osVersion = Build.VERSION.RELEASE,
            securityPatch = Build.VERSION.SECURITY_PATCH,
            deviceModel = Build.MODEL,
            deviceManufacturer = Build.MANUFACTURER
        )
    }
}

data class SystemInfo(
    val osVersion: String,
    val securityPatch:String,
    val deviceModel: String,
    val deviceManufacturer: String
)