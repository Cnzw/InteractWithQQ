package top.cnzw.kotlin.mc.iwq.util


import taboolib.common.platform.function.console
import taboolib.module.database.ColumnOptionSQL
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.Table
import taboolib.module.database.getHost
import taboolib.module.lang.sendLang
import top.cnzw.kotlin.mc.iwq.util.FileHandler.settings

object DataBaseHandler {

    private val databaseSqlHost = settings.getHost("database")

//    Sqlite相关
//    val databaseFile = newFile(getDataFolder(),"data/sqlite.db",true,false)
//    databaseFile.getHost()

    private val databaseLink = this.databaseSqlHost.createDataSource(true)

//    Sqlite相关
//    console().sendLang("Database-Init-Sqlite", "Sqlite",this.databaseSqlHost.connectionUrl.toString())
//    this.databaseSqlHost.createDataSource(true)



    private val table = Table(settings.getString("database.table", "iwq_player")!!, this.databaseSqlHost) {
        add("player") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.NOTNULL)
            }
        }
        add("uuid") {
            type(ColumnTypeSQL.CHAR, 36) {
                options(ColumnOptionSQL.NOTNULL, ColumnOptionSQL.PRIMARY_KEY)
            }
        }
        add("ip") {
            type(ColumnTypeSQL.VARCHAR, 16)
        }
        add("qq") {
            type(ColumnTypeSQL.VARCHAR, 20)
        }
        add("verifyCode") {
            type(ColumnTypeSQL.INT, 4)
        }
    }



    fun init() {
        console().sendLang("Database-Init")
        table.workspace(this.databaseLink) {
            createTable(true)
        }.run()
    }
    fun connect() {
        console().sendLang("Database-Connect", "Mysql",this.databaseSqlHost.connectionUrl)
    }

    fun getUUIDByQQ(player: String): String {
        return table.select(this.databaseLink) {
            rows("uuid")
            where {
                "player" eq player
            }
        }.firstOrNull {
            getString("uuid")
        } ?: ""
    }


    fun getUUIDByPlayer(qq: String): String {
        return table.select(this.databaseLink) {
            rows("uuid")
            where {
                "qq" eq qq
            }
        }.firstOrNull {
            getString("uuid")
        } ?: ""
    }

    fun getUUIDByCode(code: Int): String {
        return table.select(this.databaseLink) {
            rows("uuid")
            where {
                "verifyCode" eq code
            }
        }.firstOrNull {
            getString("uuid")
        } ?: ""
    }

    fun getPlayerByUUID(uuid: String): String {
        return table.select(this.databaseLink) {
            rows("player")
            where {
                "uuid" eq uuid
            }
        }.firstOrNull {
            getString("player")
        } ?: ""
    }

    fun getPlayerByQQ(qq: String): String {
        return table.select(this.databaseLink) {
            rows("player")
            where {
                "qq" eq qq
            }
        }.firstOrNull {
            getString("player")
        } ?: ""
    }

    fun getPlayerByCode(code: Int): String {
        return table.select(this.databaseLink) {
            rows("player")
            where {
                "verifyCode" eq code
            }
        }.firstOrNull {
            getString("player")
        } ?: ""
    }

    fun insertPlayer(player: String,uuid: String, ip: String, qq: String = "",verifyCode: Int = 0): Int {
        return table.insert(databaseLink) {
            value(player, uuid, ip, qq, verifyCode)
        }
    }

    fun getQQByUUID(uuid: String): String {
        return table.select(databaseLink) {
            rows("qq")
            where {
                "uuid" eq uuid
            }
        }.firstOrNull {
            getString("qq")
        } ?: ""
    }

    fun getQQByPlayer(player: String): String {
        return table.select(databaseLink) {
            rows("qq")
            where {
                "player" eq player
            }
        }.firstOrNull {
            getString("qq")
        } ?: ""
    }

    fun setQQByUUID(uuid: String, qq: String): Int {
        return table.update(databaseLink) {
            where {
                "uuid" eq uuid
            }
            set("qq", qq)
        }
    }

    fun getCodeByUUID(uuid: String): Int {
        return table.select(databaseLink) {
            rows("verifyCode")
            where {
                "uuid" eq uuid
            }
        }.firstOrNull {
            getInt("verifyCode")
        } ?: 0
    }

    fun getCodeByPlayer(player: String): Int {
        return table.select(databaseLink) {
            rows("verifyCode")
            where {
                "player" eq player
            }
        }.firstOrNull {
            getInt("verifyCode")
        } ?: 0
    }

    fun setCode(uuid: String, code: Int): Int {
        return table.update(databaseLink) {
            where {
                "uuid" eq uuid
            }
            set("verifyCode", code)
        }
    }
}