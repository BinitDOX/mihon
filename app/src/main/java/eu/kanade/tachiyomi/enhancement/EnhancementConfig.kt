package eu.kanade.tachiyomi.enhancement

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import tachiyomi.core.common.preference.Preference
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class EnhancementConfig(
    private val scope: CoroutineScope,
    enhancementPreferences: EnhancementPreferences = Injekt.get(),
) {

    var enhancementPropertyChangedListener: (() -> Unit)? = null

    var enabled = false
        private set
    var baseURL = ""
        private set
    var useDenoiser = false
        private set
    var useColorizer = false
        private set
    var useUpscaler = false
        private set
    var denoiserSigma = 25
        private set
    var useServerCache = true
        private set

    init {
        enhancementPreferences.enabled()
            .register({ enabled = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.baseURL()
            .register({ baseURL = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.useDenoiser()
            .register({ useDenoiser = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.useColorizer()
            .register({ useColorizer = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.useUpscaler()
            .register({ useUpscaler = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.denoiserSigma()
            .register({ denoiserSigma = it }, { enhancementPropertyChangedListener?.invoke() })

        enhancementPreferences.useServerCache()
            .register({ useServerCache = it }, { enhancementPropertyChangedListener?.invoke() })
    }

    private fun <T> Preference<T>.register(
        valueAssignment: (T) -> Unit,
        onChanged: (T) -> Unit = {}
    ) {
        changes()
            .onEach { valueAssignment(it) }
            .distinctUntilChanged()
            .onEach { onChanged(it) }
            .launchIn(scope)
    }
}

