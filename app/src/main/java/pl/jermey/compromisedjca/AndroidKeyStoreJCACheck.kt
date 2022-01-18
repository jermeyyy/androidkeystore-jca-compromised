package pl.jermey.compromisedjca

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import java.security.*
import javax.security.auth.x500.X500Principal
import kotlin.random.Random

class AndroidKeyStoreJCACheck {

    companion object {
        const val KEY_STORE_PROVIDER = "AndroidKeyStore"
        const val ISSUER = "Example Issuer"

        const val KEY_SIZE: Int = 2048
    }

    private val keyStore: KeyStore = KeyStore.getInstance(KEY_STORE_PROVIDER)

    init {
        keyStore.load(null)
        val providers = Security.getProviders()
        providers.forEach {
            logD("Provider: ${it.name} Version:${it.version} Info: ${it.info}")
        }
    }

    fun check(): CheckResult {
        val checkRSAKeyGenSignature =
            checkRSAKeyGen(KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
        val checkRSAKeyGenEncryption =
            checkRSAKeyGen(KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
        return CheckResult(
            RSAKeyGenCompromised = checkRSAKeyGenSignature || checkRSAKeyGenEncryption,
            hardwareKeyStorageSupported = isKeyStoreHardwareBacked()
        )
    }

    private fun checkRSAKeyGen(purpose: Int): Boolean {
        val random = Random(System.currentTimeMillis())
        val alias1 = "alias" + random.nextInt()
        val alias2 = "alias" + random.nextInt()
        listOf(alias1, alias2)
            .filter(keyStore::containsAlias)
            .forEach(keyStore::deleteEntry)
        val keyPair1 =
            generateRSAKey(alias1, purpose)
        val keyPair2 =
            generateRSAKey(alias2, purpose)
        return checkRSAKeysSimilarity(keyPair1, keyPair2)
    }

    private fun checkRSAKeysSimilarity(keyPair1: KeyPair, keyPair2: KeyPair): Boolean {
        val keyPair1PublicBytes = keyPair1.public.encoded
        val keyPair2PublicBytes = keyPair2.public.encoded
        val publicKeysSame = keyPair1PublicBytes.contentEquals(keyPair2PublicBytes)
        logD("KeyPair1: ${keyPair1PublicBytes.contentToString()}")
        logD("KeyPair2: ${keyPair2PublicBytes.contentToString()}")
        return publicKeysSame
    }

    private fun isKeyStoreHardwareBacked(): Boolean {
        val random = Random(System.currentTimeMillis())
        val alias = "alias" + random.nextInt()
        alias.takeIf(keyStore::containsAlias)?.let(keyStore::deleteEntry)
        val keyPair = generateRSAKey(
            alias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        )
        val keyFactory = KeyFactory.getInstance(
            keyPair.private.algorithm,
            KEY_STORE_PROVIDER
        )
        val keyInfo: KeyInfo =
            keyFactory.getKeySpec(keyPair.private, KeyInfo::class.java) as KeyInfo
        return keyInfo.isInsideSecureHardware
    }

    private fun generateRSAKey(alias: String, purpose: Int): KeyPair {
        val principal = X500Principal("CN=${ISSUER}")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias, purpose)
            .apply {
                setDigests(KeyProperties.DIGEST_SHA256)
                setCertificateSubject(principal)
                setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                setKeySize(KEY_SIZE)
            }.build()

        val keyGenerator =
            KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, keyStore.provider)
        keyGenerator.initialize(keyGenParameterSpec)
        return keyGenerator.genKeyPair() ?: throw RuntimeException("Could not generate RSA key")
    }
}