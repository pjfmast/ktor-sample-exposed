package avans.avd.database

import avans.avd.model.Priority
import avans.avd.model.Task
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

object TaskTable : IntIdTable("task") {
    val name: Column<String> = varchar("name", 50)
    val description: Column<String> = varchar("description", 50)
    val priority: Column<Priority> = enumeration<Priority>("priority")
}

// one row of the table is represented by an Entity<Id>
class TaskDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TaskDAO>(TaskTable)

    var name: String by TaskTable.name
    var description: String by TaskTable.description
    var priority: Priority by TaskTable.priority
}

suspend fun <T> suspendTransaction(block: Transaction.() -> T): T =
    newSuspendedTransaction(Dispatchers.IO, statement = block)

fun daoToModel(dao: TaskDAO) = Task(
    dao.name,
    dao.description,
    dao.priority,
)