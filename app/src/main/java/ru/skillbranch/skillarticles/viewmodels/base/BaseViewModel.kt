package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.navigation.NavOptions
import androidx.navigation.Navigator

abstract class BaseViewModel<T : IViewModelState>(
    private val handleState: SavedStateHandle,
    initState: T
) : ViewModel() {
    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val notifications = MutableLiveData<Event<Notify>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val navigation = MutableLiveData<Event<NavigationCommand>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val currentState
        get() = state.value!!

    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    @UiThread
    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    open fun navigate(command: NavigationCommand) {
        navigation.value = Event(command)
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner,
            EventObserver { onNotify(it) })
    }

    fun observeNavigation(owner: LifecycleOwner, onNavigate: (command: NavigationCommand) -> Unit) {
        navigation.observe(owner,
            EventObserver { onNavigate(it) })
    }

    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    fun saveState() {
        currentState.save(handleState)
    }

    @Suppress("UNCHECKED_CAST")
    fun restoreState() {
        state.value = currentState.restore(handleState) as T
    }

}

class Event<out E>(private val content: E) {
    var hasBeenHandled = false

    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content
}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify() {
    abstract val message: String

    data class TextMessage(override val message: String) : Notify()

    data class ActionMessage(
        override val message: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)?
    ) : Notify()

    data class ErrorMessage(
        override val message: String,
        val errLabel: String,
        val errHandler: (() -> Unit)?
    ) : Notify()
}

sealed class NavigationCommand() {
    data class To(
        val destination: Int,
        val args: Bundle? = null,
        val options: NavOptions? = null,
        val extras: Navigator.Extras? = null
    ) : NavigationCommand()

    data class StartLogin(
        val privateDestination: Int? = null
    ) : NavigationCommand()

    data class FinishLogin(
        val privateDestination: Int? = null
    ) : NavigationCommand()
}