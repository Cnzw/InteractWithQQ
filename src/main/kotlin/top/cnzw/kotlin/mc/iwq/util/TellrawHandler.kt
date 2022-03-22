package top.cnzw.kotlin.mc.iwq.util

import taboolib.module.chat.HexColor
import taboolib.module.chat.TellrawJson

object TellrawHandler {

    private val matcher = "<(.+?)>".toRegex()

    fun readContents(contents: String): TellrawJson {

        val raw = contents
        val tellraw = TellrawJson()

        Variables(raw, matcher) { it[1] }.element.forEach { result ->
            if (result.isVariable) {
                val splits = result.value.split("@")
                tellraw.append(HexColor.translate(splits[0]))

                splits.mapNotNull {
                    val keyValue = it.split("=", ":", limit = 2)
                    if (keyValue.size >= 2)
                        keyValue[0] to keyValue[1]
                    else null
                }.forEach {
                    val (type, content) = it
                    when (type.lowercase()) {
                        "hover" -> tellraw.hoverText(content.replace("\\n", "\n"))
                        "suggest" -> tellraw.suggestCommand(content)
                        "command", "execute" -> tellraw.runCommand(content)
                        "url", "open_url" -> tellraw.openURL(content)
                    }
                }
            } else tellraw.append(HexColor.translate(result.value))
        }
        return tellraw
    }
}

class Variables(source: String, regex: Regex, group: (List<String>) -> String = { it[1] }) {

    val element: List<Element> = source.toElements(regex, group)

    companion object {

        private fun String.toElements(regex: Regex, group: (List<String>) -> String): List<Element> {
            val list = mutableListOf<Element>()
            var index = 0
            regex.findAll(this).forEach {
                list.add(Element(substring(index, it.range.first)))
                list.add(Element(group.invoke(it.groupValues), true))
                index = it.range.last + 1
            }
            val last = Element(substring(index, length))
            if (last.value.isNotEmpty()) {
                list.add(last)
            }
            return list
        }

    }

    class Element(var value: String, var isVariable: Boolean = false)


}