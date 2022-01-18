package pl.jermey.compromisedjca

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import pl.jermey.compromisedjca.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        val androidKeyStoreJCACheck = AndroidKeyStoreJCACheck()
        val result = androidKeyStoreJCACheck.check()
        if (result.RSAKeyGenCompromised) {
            binding.statusText.text = getString(R.string.jca_compromised)
            binding.statusText.setTextColor(getColor(R.color.alert))
            binding.statusIcon.setImageResource(R.drawable.ic_warning)
            binding.statusIcon.imageTintList = ColorStateList.valueOf(getColor(R.color.alert))
        } else {
            binding.statusText.text = getString(R.string.jca_ok)
            binding.statusText.setTextColor(getColor(R.color.ok))
            binding.statusIcon.setImageResource(R.drawable.ic_check)
            binding.statusIcon.imageTintList = ColorStateList.valueOf(getColor(R.color.ok))
        }
        binding.hardwareKeystoreSupport.text = getString(
            R.string.hardware_keystore_support,
            result.hardwareKeyStorageSupported.toString()
        )
        val systemInfoService = SystemInfoService()
        val (osVersion, securityPatch, deviceModel, deviceManufacturer) = systemInfoService.getSystemInfo()
        binding.systemInfo.text =
            getString(
                R.string.system_info,
                osVersion,
                securityPatch,
                deviceManufacturer,
                deviceModel
            )
    }
}
