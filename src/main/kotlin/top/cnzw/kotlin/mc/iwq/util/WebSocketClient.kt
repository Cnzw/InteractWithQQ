package top.cnzw.kotlin.mc.iwq.util

import com.alibaba.fastjson.JSON
import okhttp3.*
import taboolib.common.env.RuntimeDependencies
import taboolib.common.env.RuntimeDependency
import taboolib.common.platform.function.console
import taboolib.common.platform.function.onlinePlayers
import taboolib.module.lang.Level
import taboolib.module.lang.sendLang
import top.cnzw.kotlin.mc.iwq.util.FileHandler.format
import top.cnzw.kotlin.mc.iwq.util.FileHandler.settings
import java.util.concurrent.TimeUnit


@RuntimeDependencies(
    RuntimeDependency("com.squareup.okhttp3:okhttp:4.9.3"),
    RuntimeDependency("com.alibaba:fastjson:1.2.79")
)
object WebSocketClient {

    // okhttp构建
    private val wsClient = OkHttpClient.Builder().pingInterval(40, TimeUnit.SECONDS).build()
    private val url = settings.getString("url")
    private val request = Request.Builder().url(this.url!!).build()
    private val ws = wsClient.newWebSocket(this.request, WsListener())
    // TODO ws状态变量

    // 处理函数
    class WsListener: WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            super.onOpen(webSocket, response)
            console().sendLang("WebSocket-Open")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            console().sendMessage(text)

            val data = JSON.parseObject(text)



            when(data.getJSONObject("data").getString("type")) {
                "GroupMessage" -> {
                    // 检测对应群
                    val groupId = data.getJSONObject("data").getJSONObject("sender").getJSONObject("group").getLong("id")
                    if (groupId in format.getLongList("format_qun")) {

                        var vaildMsg = 0
                        val messageChain = data.getJSONObject("data").getJSONArray("messageChain")

                        // 关键词检测, 第二个参数必须为#
                        if (
                            messageChain.getJSONObject(1).getString("type") != "Plain"
                            ||
                            !messageChain.getJSONObject(1).getString("text")
                                .startsWith(format.getString("$groupId.keywords") ?: "")
                        ) return

                        // 检测命令词
                        val cmd1 = messageChain.getJSONObject(1).getString("text").split(" ")
                        val cmd = cmd1[0].substring(1)
                        if (cmd == "绑定") {
                            if (cmd1[1].length == 4 && cmd1[1].toIntOrNull() != null && cmd1.size == 2) {
                                val playerUUID = DataBaseHandler.getUUIDByCode(cmd1[1].toInt())
                                if (playerUUID != "") {
                                    DataBaseHandler.setCode(playerUUID, 0)
                                    DataBaseHandler.setQQByUUID(playerUUID, data.getJSONObject("data").getJSONObject("sender").getLong("id").toString())
                                    sendRaw(
                                        """{"syncId":-1,"command":"sendGroupMessage","subCommand":null,"content":{"target":788509185,"messageChain":[{"type":"Plain","text":"?0"}]}}"""
                                            .replace("?0", "已成功绑定!")
                                    )
                                } else {
                                    sendRaw(
                                        """{"syncId":-1,"command":"sendGroupMessage","subCommand":null,"content":{"target":788509185,"messageChain":[{"type":"Plain","text":"?0"}]}}"""
                                            .replace("?0", "校验码不存在")
                                    )
                                }
                            } else {
                                sendRaw(
                                    """{"syncId":-1,"command":"sendGroupMessage","subCommand":null,"content":{"target":788509185,"messageChain":[{"type":"Plain","text":"?0"}]}}"""
                                        .replace("?0", "校验码格式错误")
                                )
                                return
                            }
                        }


                        // 构造tellraw
                        val v1 = format.getString("$groupId.mc_format")
                            ?.replace("?0", data.getJSONObject("data").getJSONObject("sender").getJSONObject("group").getLong("id").toString())
                            ?.replace("?1", data.getJSONObject("data").getJSONObject("sender").getJSONObject("group").getString("name"))
                            ?.replace("?2", data.getJSONObject("data").getJSONObject("sender").getLong("id").toString())
                            ?.replace("?3", data.getJSONObject("data").getJSONObject("sender").getString("memberName"))
                            ?.replace("?4", data.getJSONObject("data").getJSONObject("sender").getString("specialTitle"))
                            ?.replace("?5", data.getJSONObject("data").getJSONObject("sender").getString("permission"))
                            ?: (
                                    "[IWQ]<"
                                            +data.getJSONObject("data").getJSONObject("sender").getJSONObject("group").getLong("id").toString()
                                            + ">"
                                            + data.getJSONObject("data").getJSONObject("sender").getJSONObject("group").getString("name")
                                            + "("
                                            + data.getJSONObject("data").getJSONObject("sender").getLong("id").toString()
                                            + ")"
                                            + data.getJSONObject("data").getJSONObject("sender").getString("memberName")
                                            + ": "
                                    )
                        val output = TellrawHandler.readContents(v1)

                        // 历遍数据
                        for (i in 0 until messageChain.size) {
                            when(messageChain.getJSONObject(i).getString("type")) {
                                "Plain" -> {
                                    vaildMsg++
                                    val var1 = format.getString("$groupId.mc_format_plain")
                                        ?.replace("?0", messageChain.getJSONObject(i).getString("text"))
                                        ?: messageChain.getJSONObject(i).getString("text")
                                    output.append(TellrawHandler.readContents(var1))
                                }
                                "Image" -> {
                                    vaildMsg++
                                    val var1 = format.getString("$groupId.mc_format_image")
                                        ?.replace("?0", messageChain.getJSONObject(i).getString("url"))
                                        ?: (
                                                "[图片: "
                                                +messageChain.getJSONObject(i).getString("url")
                                                +" ]"
                                                )
                                    output.append(TellrawHandler.readContents(var1))
                                }
                                "Face" -> {
                                    vaildMsg++
                                    val var1 = format.getString("$groupId.mc_format_face")
                                        ?.replace("?0", messageChain.getJSONObject(i).getString("name"))
                                        ?: (
                                                "[表情: "
                                                +messageChain.getJSONObject(i).getString("name")
                                                +" ]"
                                                )
                                    output.append(TellrawHandler.readContents(var1))
                                }
                            }
                        }
                        // 校验记录的数量
                        if (vaildMsg > 0) {
                            onlinePlayers().forEach {
                                output.sendTo(it)
                            }
                        }
                    }
                }
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            console().sendLang(Level.ERROR,"WebSocket-Failure", response.toString())
            t.printStackTrace()

            // TODO 更优雅的掉线重连
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)
            console().sendLang(Level.WARN,"WebSocket-Closed", reason)
        }
    }

    fun init() {
        //?
    }

    fun sendRaw(json: String) {
        if (!this.ws.send(json)) {
            console().sendLang(Level.ERROR,"WebSocket-SendFailed", json)
        }
    }

    // TODO sendPlain(qun: Long, text: String)


    fun close() {
        this.ws.close(1000, "shutdown")
        console().sendLang("WebSocket-Shutdown")
    }
}