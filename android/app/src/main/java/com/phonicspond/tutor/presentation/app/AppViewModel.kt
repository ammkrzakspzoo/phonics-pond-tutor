package com.phonicspond.tutor.presentation.app

import androidx.lifecycle.ViewModel
import com.phonicspond.tutor.core.navigation.Screen
import com.phonicspond.tutor.domain.model.ActivityKind
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    private val backStack = mutableListOf<Screen>()

    fun navigateTo(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash && current != target) {
            backStack.add(current)
        }
        _screen.value = target
        syncBack()
    }

    fun showMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        syncBack()
    }

    fun startActivity(kind: ActivityKind) {
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = screenFor(kind)
        syncBack()
    }

    fun showResult(result: Screen.GameOver) {
        backStack.clear()
        backStack.add(Screen.Menu)
        _screen.value = result
        syncBack()
    }

    fun back(): Boolean {
        if (backStack.isEmpty()) return false
        val previous = backStack.removeAt(backStack.lastIndex)
        _screen.value = previous
        syncBack()
        return true
    }

    private fun syncBack() {
        _canGoBack.value = backStack.isNotEmpty()
    }

    private fun screenFor(kind: ActivityKind): Screen = when (kind) {
        ActivityKind.SOUND_HOP -> Screen.Game
        ActivityKind.BLEND -> Screen.Blend
        ActivityKind.TRACE -> Screen.Trace
    }
}
