package pl.jermey.compromisedjca

data class CheckResult(
    val RSAKeyGenCompromised: Boolean,
    val hardwareKeyStorageSupported: Boolean
)