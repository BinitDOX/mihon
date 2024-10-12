package eu.kanade.tachiyomi.enhancement

import tachiyomi.core.common.preference.PreferenceStore

class EnhancementPreferences (
    private val preferenceStore: PreferenceStore
) {
    fun enabled() = preferenceStore.getBoolean("pref_enhancement_enabled", false)

    fun baseURL() = preferenceStore.getString("pref_enhancement_base_url", "")

    fun useDenoiser() = preferenceStore.getBoolean("pref_enhancement_use_denoiser", true)
    fun useColorizer() = preferenceStore.getBoolean("pref_enhancement_use_colorizer", true)
    fun useUpscaler() = preferenceStore.getBoolean("pref_enhancement_use_upscaler", false)
    fun denoiserSigma() = preferenceStore.getInt("pref_enhancement_denoiser_sigma", 25)
    fun useServerCache() = preferenceStore.getBoolean("pref_enhancement_use_server_cache", true)
}
