package by.dashkevichpavel.osteopath.helpers.jobs

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class FlowJobController(
    private val scope: CoroutineScope,
    private val jobBody: suspend () -> Unit
) {
    private val operationsChannel = Channel<Operation>(BUFFER_SIZE)
    private var job: Job? = null

    init {
        scope.launch {
            while (isActive) {
                when (operationsChannel.receive()) {
                    Operation.StartJob -> {
                        Log.d("OsteoApp", "FlowJobController: received operation Start")
                        if (job == null || job?.isCompleted == true) {
                            job = scope.launch { jobBody() }
                        }
                    }
                    Operation.StopJob -> {
                        Log.d("OsteoApp", "FlowJobController: received operation Stop")
                        if (job?.isCompleted == false) {
                            job?.cancelAndJoin()
                        }
                    }
                }
            }
        }
    }

    fun start() {
        scope.launch {
            Log.d("OsteoApp", "FlowJobController: start()")
            operationsChannel.send(Operation.StartJob)
        }
    }

    fun stop() {
        scope.launch {
            Log.d("OsteoApp", "FlowJobController: stop()")
            operationsChannel.send(Operation.StopJob)
        }
    }

    companion object {
        private const val BUFFER_SIZE = 3
    }

    private sealed class Operation {
        object StartJob : Operation()
        object StopJob : Operation()
    }
}