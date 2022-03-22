package top.cnzw.kotlin.mc.iwq

import taboolib.common.platform.Plugin
import taboolib.common.platform.function.console
import taboolib.common.platform.function.getDataFolder
import taboolib.module.lang.sendLang
import top.cnzw.kotlin.mc.iwq.util.DataBaseHandler
import top.cnzw.kotlin.mc.iwq.util.WebSocketClient

object InteractWithQQ : Plugin() {

    override fun onLoad() {
        //TODO Metrics
        if (getDataFolder().exists()) console().sendLang("Plugin-OnLoad") else console().sendLang("Plugin-FirstLoad")
    }
    override fun onEnable() {
        console().sendLang("Plugin-OnEnable")
        if (getDataFolder().exists()) DataBaseHandler.connect() else DataBaseHandler.init()
        WebSocketClient.init()
    }
    override fun onDisable() {
        console().sendLang("Plugin-OnDisable")
        WebSocketClient.close()
    }
}