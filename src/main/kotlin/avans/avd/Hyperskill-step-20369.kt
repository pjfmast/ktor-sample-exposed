package avans.avd

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Users : IntIdTable() {
    val name: Column<String> = varchar("name", 50)
    val password: Column<String> = varchar("password", 50)
    val amount: Column<Int> = integer("amount")
}
object Cars : IntIdTable() {
    val license: Column<String> = varchar("license", 50).uniqueIndex()
    val model: Column<String> = varchar("model", 50)
    val price: Column<Int> = integer("price")
    val owner = reference("owner", Users, onDelete = ReferenceOption.CASCADE)
}

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        //addLogger(StdOutSqlLogger)
        SchemaUtils.create(Users, Cars)

        Users.insert {
            it[name] = "Alice"
            it[password] = "p455w0rd"
            it[amount] = 500
        }
        Users.insert {
            it[name] = "Bob"
            it[password] = "s3cr3t"
            it[amount] = 10
        }
        Cars.insert { it ->
            it[license] = "22-np-sr"
            it[model] = "Citroen xsara break"
            it[price] = 600
            it[owner] = Users.selectAll().where(Users.name eq "Bob").map { it[Users.id] }.first()
        }

        println("-".repeat(60))
        println("Users")
        var users = Users.selectAll().map { it[Users.name] to it[Users.amount] }
        println("Users: $users")


        // When joining on a foreign key, the more concise innerJoin can be used:
        val carOwners = (Users innerJoin Cars)
            .select(Users.name, Cars.owner, Cars.model)
            .toList()
        println("-".repeat(60))
        println("Cars and owners")
        println(carOwners.joinToString { row -> row[Users.name] + " - " + row[Cars.owner] + " - " + row[Cars.model] })

        // see: https://github.com/JetBrains/Exposed/blob/main/exposed-core/src/main/kotlin/org/jetbrains/exposed/sql/Queries.kt
        val alice = Users.selectAll().where { Users.name eq "Alice" }.firstOrNull() ?: throw Exception("User not found!")
        val bob = Users.selectAll().where { Users.name eq "Bob" }.firstOrNull() ?: throw Exception("User not found!")

        val transfer = 300
        if (alice[Users.amount] < transfer) throw Exception("Insufficient funds!")
        Users.update({ Users.name eq "Alice" }) {
            it[amount] = alice[amount] - transfer
        }
        Users.update({ Users.name eq "Bob" }) {
            it[amount] = bob[amount] + transfer
        }

        Users.deleteWhere { name eq "Alice" }

        users = Users.selectAll().map { it[Users.name] to it[Users.amount] }
        println("Users: $users")
    }
}