package pl.jermey.compromisedjca

import android.content.res.ColorStateList
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import pl.jermey.compromisedjca.databinding.MainActivityBinding
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import javax.security.auth.x500.X500Principal
import kotlin.random.Random

class MainActivity : AppCompatActivity() {


    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        val androidKeyStoreJCACheck = AndroidKeyStoreJCACheck()
        val result = androidKeyStoreJCACheck.check()
        if (result.RSAKeyGenCompromised) {
            binding.statusText.text = getString(R.string.jca_compromised)
            binding.statusIcon.setImageResource(R.drawable.ic_warning)
            binding.statusIcon.imageTintList = ColorStateList.valueOf(getColor(R.color.alert))
        } else {
            binding.statusText.text = getString(R.string.jca_ok)
            binding.statusIcon.setImageResource(R.drawable.ic_check)
            binding.statusIcon.imageTintList = ColorStateList.valueOf(getColor(R.color.ok))
        }
        binding.hardwareKeystoreSupport.text = getString(
            R.string.hardware_keystore_support,
            result.hardwareKeyStorageSupported.toString()
        )
    }
}
