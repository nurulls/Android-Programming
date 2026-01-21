package com.example.todolistquiz.presentation.todo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistquiz.data.model.Todo
import com.example.todolistquiz.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TodoViewModel : ViewModel() {
    private val repository = TodoRepository()
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos = _todos.asStateFlow()

    private val _filter = MutableStateFlow<TodoFilter>(TodoFilter.ALL)
    val filter = _filter.asStateFlow()

    fun observeTodos(userId: String) {
        viewModelScope.launch {
            repository.getTodos(userId).collect { todoList ->
                _todos.value = todoList.sortedWith(
                    compareBy<Todo> { it.isCompleted }
                        .thenBy {
                            when(it.priority) {
                                "HIGH" -> 0
                                "MEDIUM" -> 1
                                "LOW" -> 2
                                else -> 3
                            }
                        }
                        .thenByDescending { it.createdAt }
                )
            }
        }
    }

    fun add(userId: String, title: String, priority: String = "MEDIUM", category: String = "Kerja") =
        viewModelScope.launch {
            repository.addTodo(userId, title, priority, category)
        }

    fun toggle(userId: String, todo: Todo) = viewModelScope.launch {
        repository.updateTodoStatus(userId, todo.id, !todo.isCompleted)
    }

    fun updateTodo(userId: String, todoId: String, newTitle: String, priority: String, category: String) =
        viewModelScope.launch {
            repository.updateTodo(userId, todoId, newTitle, priority, category)
        }

    fun delete(userId: String, todoId: String) = viewModelScope.launch {
        repository.deleteTodo(userId, todoId)
    }

    fun setFilter(newFilter: TodoFilter) {
        _filter.value = newFilter
    }

    fun getFilteredTodos(): List<Todo> {
        return when(_filter.value) {
            TodoFilter.ALL -> _todos.value
            TodoFilter.INCOMPLETE -> _todos.value.filter { !it.isCompleted }
            is TodoFilter.BY_CATEGORY -> _todos.value.filter {
                it.category == (_filter.value as TodoFilter.BY_CATEGORY).category
            }
        }
    }
}

sealed class TodoFilter {
    object ALL : TodoFilter()
    object INCOMPLETE : TodoFilter()
    data class BY_CATEGORY(val category: String) : TodoFilter()
}