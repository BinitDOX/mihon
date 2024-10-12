package eu.kanade.presentation.reader.settings

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import eu.kanade.tachiyomi.ui.reader.setting.ReaderSettingsScreenModel
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.CheckboxItem
import tachiyomi.presentation.core.components.SliderItem
import tachiyomi.presentation.core.components.TextItem
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.collectAsState


@Composable
internal fun ColumnScope.EnhancementSettingsPage(screenModel: ReaderSettingsScreenModel) {
    val enabled by screenModel.enhancementPreferences.enabled().collectAsState()
    val baseURL by screenModel.enhancementPreferences.baseURL().collectAsState()

    // val denoise by screenModel.enhancementPreferences.useDenoiser().collectAsState()
    // val colorize by screenModel.enhancementPreferences.useColorizer().collectAsState()
    // val upscale by screenModel.enhancementPreferences.useUpscaler().collectAsState()
    val denoiseSigmaPref = screenModel.enhancementPreferences.denoiserSigma()
    val denoiseSigma by denoiseSigmaPref.collectAsState()
    // val serverCache by screenModel.enhancementPreferences.useServerCache().collectAsState()

    CheckboxItem(
        label = stringResource(MR.strings.pref_enhancement_enabled),
        pref = screenModel.enhancementPreferences.enabled(),
    )

    if(enabled){
        TextItem(
            value = baseURL,
            onChange = { screenModel.enhancementPreferences.baseURL().set(it) },
            label = stringResource(MR.strings.pref_enhancement_base_url)
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_enhancement_use_denoiser),
            pref = screenModel.enhancementPreferences.useDenoiser(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_enhancement_use_colorizer),
            pref = screenModel.enhancementPreferences.useColorizer(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_enhancement_use_upscaler),
            pref = screenModel.enhancementPreferences.useUpscaler(),
        )

        CheckboxItem(
            label = stringResource(MR.strings.pref_enhancement_use_server_cache),
            pref = screenModel.enhancementPreferences.useServerCache(),
        )

        SliderItem(
            value = denoiseSigma,
            label = stringResource(MR.strings.pref_enhancement_denoiser_sigma),
            valueText = stringResource(MR.strings.pref_enhancement_denoiser_sigma_summary) + ": ${denoiseSigma}",
            onChange = { denoiseSigmaPref.set(it) },
            min = 0,
            max = 150,
        )
    }
}
