package com.ptmr3.fluxx

import com.ptmr3.fluxx.Fluxx.Companion.ACTION
import com.ptmr3.fluxx.Fluxx.Companion.CLASS
import com.ptmr3.fluxx.Fluxx.Companion.METHOD
import java.lang.reflect.Method
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking

abstract class FluxxActionCreator {
    private val mFluxxLog = FluxxLog.instance

    /**
     * This is the preferred method for publishing actions
     * @param actionId
     * @param data
     */
    protected fun publishAction(actionId: String, vararg data: Any) {
        require(data.size % 2 == 0) { "Data must be a valid list of key,value pairs" }
        val dataHashMap = HashMap<String, Any>()
        var i = 0
        while (i < data.size) {
            dataHashMap[data[i++] as String] = data[i++]
        }
        runBlocking {
            Fluxx.instance.getActionSubscriberMethods(FluxxAction(actionId, dataHashMap))
                .flowOn(Dispatchers.IO)
                .collect { hashMap ->
                    val method = hashMap[METHOD] as Method
                    method.isAccessible = true
                    try {
                        method.invoke(hashMap[CLASS], hashMap[ACTION])
                        mFluxxLog.print("ACTION: $actionId, ${data.toList()} -> ${hashMap[CLASS]?.javaClass?.simpleName}, ${hashMap[ACTION]}")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
}