package com.fingerprintjs.android.fingerprint.signal_providers.os_build


import com.fingerprintjs.android.fingerprint.info_providers.CodecInfoProvider
import com.fingerprintjs.android.fingerprint.info_providers.DeviceSecurityInfoProvider
import com.fingerprintjs.android.fingerprint.info_providers.OsBuildInfoProvider
import com.fingerprintjs.android.fingerprint.signal_providers.SignalGroupProvider
import com.fingerprintjs.android.fingerprint.tools.hashers.Hasher


class OsBuildSignalGroupProvider(
    osBuildInfoProvider: OsBuildInfoProvider,
    codecInfoProvider: CodecInfoProvider?,
    deviceSecurityInfoProvider: DeviceSecurityInfoProvider,
    private val hasher: Hasher,
    version: Int
) : SignalGroupProvider<OsBuildRawData>(version) {

    private val rawData =
        OsBuildRawData(
            osBuildInfoProvider.fingerprint(),
            osBuildInfoProvider.androidVersion(),
            osBuildInfoProvider.sdkVersion(),
            osBuildInfoProvider.kernelVersion(),
            codecInfoProvider?.codecsList() ?: emptyList(),
            deviceSecurityInfoProvider.encryptionStatus(),
            deviceSecurityInfoProvider.securityProvidersData()
        )

    override fun fingerprint(): String {
        return hasher.hash(
            when (version) {
                1 -> v1()
                2 -> v2()
                else -> v2()
            }
        )

    }

    override fun rawData() = rawData

    private fun v1(): String {
        val osBuildInfoSB = StringBuilder()

        osBuildInfoSB.append(rawData.fingerprint)

        return osBuildInfoSB.toString()
    }

    private fun v2(): String {
        val osBuildInfoSB = StringBuilder()

        osBuildInfoSB.append(rawData.androidVersion)
        osBuildInfoSB.append(rawData.sdkVersion)
        osBuildInfoSB.append(rawData.kernelVersion)
        osBuildInfoSB.append(rawData.encryptionStatus)

        rawData.securityProvidersData.forEach {
            osBuildInfoSB
                .append(it.first)
                .append(it.second)
        }

        rawData.codecList.forEach {
            osBuildInfoSB.append(it.name)
            it.capabilities.forEach { capability ->
                osBuildInfoSB.append(capability)
            }
        }

        return osBuildInfoSB.toString()
    }
}