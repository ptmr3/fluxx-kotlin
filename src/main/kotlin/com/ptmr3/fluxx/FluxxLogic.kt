package com.ptmr3.fluxx

import com.ptmr3.fluxx.Fluxx.Companion.CLASS
import com.ptmr3.fluxx.Fluxx.Companion.FAILURE_REACTION
import com.ptmr3.fluxx.Fluxx.Companion.REACTION
import java.lang.reflect.Method
import javax.xml.transform.OutputKeys.METHOD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

abstract class FluxxLogic {
    private val mFluxxLog = FluxxLog.instance

    init {
        registerActionSubscriber()
    }

    private fun registerActionSubscriber() {
        Fluxx.instance.registerActionSubscriber(this)
    }

    protected fun publishReaction(reactionId: String, vararg data: Any) {
        require(data.size % 2 == 0) { "Data must be a valid list of key,value pairs" }
        val dataHashMap = HashMap<String, Any>()
        var i = 0
        while (i < data.size) {
            dataHashMap[data[i++] as String] = data[i++]
        }

        runBlocking {
            Fluxx.instance.getReactionSubscriberMethods(FluxxReaction(reactionId, dataHashMap))
                .collect { hashMap ->
                    val method = hashMap[METHOD] as Method
                    method.isAccessible = true
                    try {
                        if (method.genericParameterTypes.isEmpty()) {
                            method.invoke(hashMap[CLASS])
                            mFluxxLog.print("publishReaction: $reactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}")
                        } else {
                            method.invoke(hashMap[CLASS], hashMap[REACTION])
                            mFluxxLog.print("REACTION: $reactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}, ${hashMap[REACTION]}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    protected fun publishParallelReaction(reactionId: String, vararg data: Any) {
        require(data.size % 2 == 0) { "Data must be a valid list of key,value pairs" }
        val dataHashMap = HashMap<String, Any>()
        var i = 0
        while (i < data.size) {
            dataHashMap[data[i++] as String] = data[i++]
        }

        runBlocking {
            Fluxx.instance.getReactionSubscriberMethods(FluxxReaction(reactionId, dataHashMap))
                .flowOn(Dispatchers.IO)
                .collect { hashMap ->
                    val method = hashMap[METHOD] as Method
                    method.isAccessible = true
                    try {
                        if (method.genericParameterTypes.isEmpty()) {
                            method.invoke(hashMap[CLASS])
                            mFluxxLog.print("publishReaction: $reactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}")
                        } else {
                            method.invoke(hashMap[CLASS], hashMap[REACTION])
                            mFluxxLog.print("REACTION: $reactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}, ${hashMap[REACTION]}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    protected fun publishFailureReaction(failureReactionId: String, vararg data: Any) {
        require(data.size % 2 == 0) { "Data must be a valid list of key,value pairs" }
        val dataHashMap = HashMap<String, Any>()
        var i = 0
        while (i < data.size) {
            dataHashMap[data[i++] as String] = data[i++]
        }

        runBlocking {
            Fluxx.instance.getFailureReactionSubscriberMethods(FluxxFailureReaction(failureReactionId, dataHashMap))
                .collect { hashMap ->
                    val method = hashMap[METHOD] as Method
                    method.isAccessible = true
                    try {
                        if (method.genericParameterTypes.isEmpty()) {
                            method.invoke(hashMap[CLASS])
                            mFluxxLog.print("publishFailureReaction: $failureReactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}")
                        } else {
                            method.invoke(hashMap[CLASS], hashMap[FAILURE_REACTION])
                            mFluxxLog.print("FAILURE REACTION: $failureReactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}, ${hashMap[FAILURE_REACTION]}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }

    protected fun publishParallelFailureReaction(failureReactionId: String, vararg data: Any) {
        require(data.size % 2 == 0) { "Data must be a valid list of key,value pairs" }
        val dataHashMap = HashMap<String, Any>()
        var i = 0
        while (i < data.size) {
            dataHashMap[data[i++] as String] = data[i++]
        }

        runBlocking {
            Fluxx.instance.getFailureReactionSubscriberMethods(FluxxFailureReaction(failureReactionId, dataHashMap))
                .flowOn(Dispatchers.IO)
                .collect { hashMap ->
                    val method = hashMap[METHOD] as Method
                    method.isAccessible = true
                    try {
                        if (method.genericParameterTypes.isEmpty()) {
                            method.invoke(hashMap[CLASS])
                            mFluxxLog.print("publishFailureReaction: $failureReactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}")
                        } else {
                            method.invoke(hashMap[CLASS], hashMap[FAILURE_REACTION])
                            mFluxxLog.print("FAILURE REACTION: $failureReactionId, ${data.asList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}, ${hashMap[FAILURE_REACTION]}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}