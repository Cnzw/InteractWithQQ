package top.cnzw.kotlin.mc.iwq.util

import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigFile

object FileHandler {

    @Config("settings.yml", autoReload = true)
    lateinit var settings: ConfigFile
        private set

    @Config("format.yml", autoReload = true)
    lateinit var format: ConfigFile
        private set
}