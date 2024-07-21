package com.ardondev.tiendita.presentation.util

import androidx.lifecycle.Observer

open class SingleEvent<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): T = content

    class SingleEventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<SingleEvent<T>> {

        override fun onChanged(value: SingleEvent<T>) {
            value.getContentIfNotHandled()?.let {
                onEventUnhandledContent(it)
            }
        }

    }

}