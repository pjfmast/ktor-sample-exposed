package avans.avd

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object User : IntIdTable() {
    val name: Column<String> = varchar("name", 50)
    val password: Column<String> = varchar("password", 50)
    val amount: Column<Int> = integer("amount")
}

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(User)

        User.insert {
            it[name] = "Alice"
            it[password] = "p455w0rd"
            it[amount] = 500
        }
        User.insert {
            it[name] = "Bob"
            it[password] = "s3cr3t"
            it[amount] = 10
        }

        var users = User.selectAll().map { it[User.name] to it[User.amount] }
        println("Users: $users")

        // see: https://github.com/JetBrains/Exposed/blob/main/exposed-core/src/main/kotlin/org/jetbrains/exposed/sql/Queries.kt
        val alice = User.selectAll().where { User.name eq "Alice" }.firstOrNull() ?: throw Exception("User not found!")
        val bob = User.selectAll().where { User.name eq "Bob" }.firstOrNull() ?: throw Exception("User not found!")

        val transfer = 300
        if (alice[User.amount] < transfer) throw Exception("Insufficient funds!")
        User.update({ User.name eq "Alice" }) {
            it[amount] = alice[amount] - transfer
        }
        User.update({ User.name eq "Bob" }) {
            it[amount] = bob[amount] + transfer
        }

        User.deleteWhere { User.name eq "Alice" }

        users = User.selectAll().map { it[User.name] to it[User.amount] }
        println("Users: $users")
    }
}