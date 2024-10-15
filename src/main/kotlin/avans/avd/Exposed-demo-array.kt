package avans.avd

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TestTable : IntIdTable() {
    val files = array<String>("files")
}

class TestEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<TestEntity>(TestTable)

    var files by TestTable.files
}

