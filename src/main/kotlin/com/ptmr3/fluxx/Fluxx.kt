package com.ptmr3.fluxx

import com.ptmr3.fluxx.annotation.Action
import com.ptmr3.fluxx.annotation.FailureReaction
import com.ptmr3.fluxx.annotation.Reaction
import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

class Fluxx {
    private val mActionSubscribers = ConcurrentHashMap<Any, Set<Method>>()
    private val mReactionSubscribers = ConcurrentHashMap<Any, Set<Method>>()
    private val mFailureReactionSubscribers = ConcurrentHashMap<Any, Set<Method>>()

    fun getActionSubscriberMethods(action: FluxxAction): Flow<HashMap<String, Any>> =
        flow {
            mActionSubscribers.keys.map { parentClass ->
                mActionSubscribers[parentClass].orEmpty().map {
                    if (action.type == it.getAnnotation(Action::class.java).actionType) {
                        val map = HashMap<String, Any>()
                        map[METHOD] = it
                        map[CLASS] = parentClass
                        map[ACTION] = action
                        emit(map)
                    }
                }
            }
        }

    fun getReactionSubscriberMethods(reaction: FluxxReaction): Flow<HashMap<String, Any>> =
        flow {
            mReactionSubscribers.keys.map { parentClass ->
                mReactionSubscribers[parentClass].orEmpty().map {
                    if (reaction.type == it.getAnnotation(Reaction::class.java).reactionType) {
                        val map = HashMap<String, Any>()
                        map[METHOD] = it
                        map[CLASS] = parentClass
                        map[REACTION] = reaction
                        emit(map)
                    }
                }
            }
        }

    fun getFailureReactionSubscriberMethods(failureReaction: FluxxFailureReaction): Flow<HashMap<String, Any>> =
        flow {
            mFailureReactionSubscribers.keys.map { parentClass ->
                mFailureReactionSubscribers[parentClass].orEmpty().map {
                    if (failureReaction.type == it.getAnnotation(FailureReaction::class.java).failureReactionType) {
                        val map = HashMap<String, Any>()
                        map[METHOD] = it
                        map[CLASS] = parentClass
                        map[FAILURE_REACTION] = failureReaction
                        emit(map)
                    }
                }
            }
        }

    private fun methodsWithActionAnnotation(parentClass: Any) {
        if (!mActionSubscribers.containsKey(parentClass)) {
            val classMethods = HashSet<Method>()
            parentClass.javaClass.declaredMethods.map {
                val paramTypes = it.parameterTypes
                if (it.isAnnotationPresent(Action::class.java) && paramTypes.size == 1 && paramTypes[0] == FluxxAction::class.java) {
                    classMethods.add(it)
                }
            }
            mActionSubscribers[parentClass] = classMethods
        }
    }

    private fun methodsWithReactionAnnotation(parentClass: Any) {
        if (!mReactionSubscribers.containsKey(parentClass)) {
            val classMethods = HashSet<Method>()
            parentClass.javaClass.declaredMethods.map {
                if (it.isAnnotationPresent(Reaction::class.java)) {
                    classMethods.add(it)
                }
            }
            mReactionSubscribers[parentClass] = classMethods
        }
    }

    private fun methodsWithFailureReactionAnnotation(parentClass: Any) {
        if (!mFailureReactionSubscribers.containsKey(parentClass)) {
            val classMethods = HashSet<Method>()
            parentClass.javaClass.declaredMethods.map {
                if (it.isAnnotationPresent(FailureReaction::class.java)) {
                    classMethods.add(it)
                }
            }
            mFailureReactionSubscribers[parentClass] = classMethods
        }
    }

    fun registerActionSubscriber(logicClass: Any) {
        if (logicClass is FluxxLogic) {
            runBlocking(Dispatchers.Unconfined) {
                methodsWithActionAnnotation(logicClass)
            }
        }
    }

    fun registerReactionSubscriber(initiatorClass: Any) {
        runBlocking(Dispatchers.Unconfined) {
            methodsWithReactionAnnotation(initiatorClass)
        }
        runBlocking(Dispatchers.Unconfined) {
            methodsWithFailureReactionAnnotation(initiatorClass)
        }
    }

    fun unregisterReactionSubscriber(initiator: Any) {
        if (mReactionSubscribers.containsKey(initiator)) {
            mReactionSubscribers.remove(initiator)
        }
        if (mFailureReactionSubscribers.contains(initiator)) {
            mFailureReactionSubscribers.remove(initiator)
        }
    }

    companion object {
        val instance: Fluxx by lazy { Fluxx() }
        const val ACTION = "action"
        const val CLASS = "class"
        const val METHOD = "method"
        const val REACTION = "reaction"
        const val FAILURE_REACTION = "failureReaction"
    }
}