package avans.avd.model

interface TaskRepository {
    // added suspend.
    // This will allow implementations of the interface methods to start jobs of work on a different Coroutine Dispatcher.
    suspend fun allTasks(): List<Task>
    suspend fun tasksByPriority(priority: Priority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun addTask(task: Task)
    suspend fun removeTask(name: String): Boolean
}