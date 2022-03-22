package top.cnzw.kotlin.mc.iwq.util

import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.random


object EventHandler {

    @SubscribeEvent
    fun onChat(e: AsyncPlayerChatEvent) {
        for (item in FileHandler.format.getLongList("format_qun")) {
            if (e.message.startsWith(FileHandler.format.getString("$item.keywords") ?: "")) {
                val json = """{"syncId":-1,"command":"sendGroupMessage","subCommand":null,"content":{"target":788509185,"messageChain":[{"type":"Plain","text":"?0"}]}}"""
                // TODO 多群发送
                val output = FileHandler.format.getString("$item.qun_format")?.replace("?0", e.player.displayName)
                    ?.replace("?1", e.message)
                    ?: (
                            "<"
                            +  e.player.displayName
                            + ">: "
                            + e.message
                            )
                WebSocketClient.sendRaw(json.replace("?0", output))
            }
        }
    }

    @SubscribeEvent
    fun onLogin(e: AsyncPlayerPreLoginEvent) {
        if (DataBaseHandler.getUUIDByPlayer(e.name) == "") {
            val code = random(1000, 9999)
            DataBaseHandler.insertPlayer(e.name, e.uniqueId.toString(), e.address.toString(), verifyCode = code)
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "校验码: $code") // TODO 在lang中定义
        } else {
            if (DataBaseHandler.getQQByUUID(e.uniqueId.toString()) == "") {
                val code = DataBaseHandler.getCodeByUUID(e.uniqueId.toString())
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "校验码: $code")
            }
        }
    }

}