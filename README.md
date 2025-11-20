# Android KeyStore JCA Compromised Demo

This repository contains a proof-of-concept (PoC) Android application that demonstrates a critical security vulnerability found in the Java Cryptography Architecture (JCA) implementation on certain Android devices, specifically observed on "Hammer" branded devices (and potentially other devices from similar ODMs).

## The Vulnerability

The Android KeyStore system is designed to provide a secure container for cryptographic keys. When an application requests the generation of a new key pair (e.g., RSA), the system is expected to use a Cryptographically Secure Pseudo-Random Number Generator (CSPRNG) to ensure the new key is unique and unpredictable.

**The Issue:**
On affected devices, the `AndroidKeyStore` provider fails to generate unique keys. Instead, repeated calls to `KeyPairGenerator.generateKeyPair()` result in the **exact same key pair** being returned, even when different aliases are used.

**Implications:**
This is a catastrophic failure of the cryptographic system.
*   **Predictability:** If the keys are static or generated from a static seed, an attacker who obtains a key from one device (or reverse-engineers the static generation logic) could potentially derive keys for all other devices of the same model.
*   **Loss of Confidentiality & Integrity:** Encryption schemes relying on unique private keys are broken. Digital signatures provide no non-repudiation if the private key is not unique to the user/device.
*   **Hardware Backing False Sense of Security:** Even if the device reports that keys are stored in secure hardware (TEE/SE), the security is nullified if the key generation process itself is compromised.

## How the Demo App Works

The application performs a simple but effective check:

1.  **Initialization**: It initializes the `AndroidKeyStore` provider.
2.  **Key Generation**: It attempts to generate two distinct RSA-2048 key pairs using random aliases.
3.  **Comparison**: It extracts the public key from both generated pairs.
4.  **Verification**: It compares the encoded bytes of the two public keys.
    *   **Expected Behavior**: The keys should be different.
    *   **Compromised Behavior**: The keys are identical.

The core logic is located in `app/src/main/java/pl/jermey/compromisedjca/AndroidKeyStoreJCACheck.kt`.

```kotlin
private fun checkRSAKeysSimilarity(keyPair1: KeyPair, keyPair2: KeyPair): Boolean {
    val keyPair1PublicBytes = keyPair1.public.encoded
    val keyPair2PublicBytes = keyPair2.public.encoded
    // If public keys are the same, the generator is compromised
    return keyPair1PublicBytes.contentEquals(keyPair2PublicBytes)
}
```

## Affected Devices

This behavior has been observed on devices manufactured by certain Chinese ODMs, often rebranded under local names (e.g., Hammer).

*   **Observed on:** Hammer devices (specific models may vary).
*   **Potential Impact:** Other budget rugged phones or devices sharing the same compromised BSP (Board Support Package) or ROM.

## Running the Check

1.  Clone the repository.
2.  Open the project in Android Studio.
3.  Build and run the application on a target device.
4.  The app will display a large status icon:
    *   **Green Check**: JCA appears to be working correctly (keys are unique).
    *   **Red Warning**: JCA is compromised (keys are identical).

The app also displays system information (OS Version, Security Patch, Model, Manufacturer) to help identify affected units.

## Disclaimer

This software is for educational and security research purposes only. It is intended to help developers and security researchers identify compromised devices. The author is not responsible for any misuse of this information.
