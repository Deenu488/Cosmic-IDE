package org.cosmicide.rewrite

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.google.android.material.color.DynamicColors
import com.itsaky.androidide.config.JavacConfigProvider
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cosmicide.rewrite.util.FileUtil
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.File
import java.io.FileNotFoundException

class App : Application() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var indexFile: File

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        FileUtil.init(this)
        indexFile = File(FileUtil.dataDir, INDEX_FILE_NAME)
        scope.launch {
            try {
                disableModules()
                loadTextmateTheme()
                extractFiles()
            } catch (e: Throwable) {
                Log.e("App", "Initialization failed: $e")
            }
        }
    }

    private suspend fun extractFiles() = withContext(Dispatchers.IO) {
        extractAsset(INDEX_FILE_NAME, indexFile)
        extractAsset(ANDROID_JAR, File(FileUtil.classpathDir, ANDROID_JAR))
        extractAsset(KOTLIN_STDLIB, File(FileUtil.classpathDir, KOTLIN_STDLIB))
    }

    private suspend fun extractAsset(assetName: String, outputFile: File) = withContext(Dispatchers.IO) {
        assets.open(assetName).use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    private suspend fun disableModules() = withContext(Dispatchers.IO) {
        JavacConfigProvider.disableModules()
    }

    private suspend fun loadTextmateTheme() = withContext(Dispatchers.IO) {
        val fileProvider = AssetsFileResolver(assets)
        FileProviderRegistry.getInstance().addFileProvider(fileProvider)
        GrammarRegistry.getInstance().loadGrammars(LANGUAGES_FILE_PATH)

        val themeRegistry = ThemeRegistry.getInstance()
        themeRegistry.loadTheme(loadTheme(DARCULA_THEME_FILE_NAME, DARCULA_THEME_NAME))
        themeRegistry.loadTheme(loadTheme(QUIET_LIGHT_THEME_FILE_NAME, QUIET_LIGHT_THEME_NAME))

        applyThemeBasedOnConfiguration(resources.configuration)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applyThemeBasedOnConfiguration(newConfig)
    }

    private fun applyThemeBasedOnConfiguration(config: Configuration) {
        val themeName =
            if (config.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                DARCULA_THEME_NAME
            } else {
                QUIET_LIGHT_THEME_NAME
            }
        ThemeRegistry.getInstance().setTheme(themeName)
    }

    private fun loadTheme(fileName: String, themeName: String): ThemeModel {
        val inputStream = FileProviderRegistry.getInstance().tryGetInputStream("$TEXTMATE_DIR/$fileName")
            ?: throw FileNotFoundException("Theme file not found: $fileName")
        val source = IThemeSource.fromInputStream(inputStream, fileName, null)
        return ThemeModel(source, themeName)
    }

    companion object {
        private const val INDEX_FILE_NAME = "index.json"
        private const val ANDROID_JAR = "android.jar"
        private const val KOTLIN_STDLIB = "kotlin-stdlib-1.8.0.jar"
        private const val LANGUAGES_FILE_PATH = "textmate/languages.json"
        private const val TEXTMATE_DIR = "textmate"
        private const val DARCULA_THEME_FILE_NAME = "darcula.json"
        private const val DARCULA_THEME_NAME = "darcula"
        private const val QUIET_LIGHT_THEME_FILE_NAME = "QuietLight.tmTheme"
        private const val QUIET_LIGHT_THEME_NAME = "QuietLight"
    }
}