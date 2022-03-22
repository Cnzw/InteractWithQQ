package top.cnzw.kotlin.mc.iwq.util

import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.module.lang.sendLang

@CommandHeader("iwq", description = "InteractWithQQ 主命令", permission = "iwq.use")
object CommandHandler {

    @CommandBody(optional = true)
    val link = subCommand {
        execute<ProxyCommandSender> { sender , _, _ ->
            sender.sendLang("Command-link")
            //TODO 开关
        }
    }

    @CommandBody
    val help = subCommand {
        execute<ProxyCommandSender> { sender , _, _ ->
            sender.sendLang("Command-help")
        }
    }

    @CommandBody
    val main = mainCommand {
        execute<ProxyCommandSender> { sender , _, _ ->
            sender.sendLang("Command-main")
        }
    }
}