package com.example.kotlinflows

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val countdownFlow = flow {
        val startingValue = 10
        var currentValue = startingValue
        emit(currentValue)

        while (currentValue > 0) {
            delay(1000L)
            currentValue--

            emit(currentValue)
        }
    }

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>(replay = 5)
    val sharedFlow = _sharedFlow.asSharedFlow()


    init {
//        collectFlow()
        squareNumber(3)

        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: received number is $it")
            }
        }

        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: received number is $it")
            }
        }

//        terminalOperators()
//        flatteningOperators()
//        forwardEmissions()
    }

    private fun collectFlow() {
        viewModelScope.launch {
//            countdownFlow.filter { time ->
//                time % 2 == 0
//            }.map { time ->
//                time * time
//            }.onEach { time ->
//                println("The current time by onEach is $time")
//            }.collect { time ->
//                println("The current time is $time")
//            }

            // in case of conflict between emitted values, collectLatest gets only the latest value sent
//            countdownFlow.collectLatest { latestTime ->
//                delay(1500L)
//                println("The current latest time is $latestTime")
//            }
        }

        // equivalent to the method above
//        countdownFlow.onEach {
//            println(it)
//        }.launchIn(viewModelScope)
    }

    private fun terminalOperators() {
        viewModelScope.launch {
//            val count = countdownFlow.count { time ->
//                time % 2 == 0
//            }
//
//            println("The count is $count")

//            val reduceResult = countdownFlow.reduce { accumulator, value ->
//                println("acc $accumulator - val $value")
//                accumulator + value
//            }
//
//            println("Reduce result is $reduceResult")

            // similar to reduce but we set the initial value for accumulator instead of starting with the first value of the sequence
//            val foldResult = countdownFlow.fold(100) { accumulator, value ->
//                println("acc $accumulator - val $value")
//                accumulator + value
//            }
        }
    }

    private fun flatteningOperators() {
        val flow1 = (1..5).asFlow()

        viewModelScope.launch {
            flow1.flatMapConcat { id ->
                getRecipeById(id)
            }.collect { value ->
                println("02032023 - Collected value is $value")
            }
        }
    }

    private fun getRecipeById(id: Int) = flow {
        emit("Recipe $id")
    }

    private fun forwardEmissions() {
        val flow = flow {
            delay(250L)
            emit("Appetizer")
            delay(1000L)
            emit("Main dish")
            delay(100L)
            emit("Dessert")
        }

        viewModelScope.launch {
            flow.onEach {
                println("FLOW: $it is delivered")
            }
                // .buffer() // runs onEach block and collect block in a different thread
                // .conflate() // if more than 1 emission was made, consume the last
                // .collectLatest { ... } // if an emission came before the collected was finished, it skips the current "collection" and go to the next
                .collect {
                    println("FLOW: Now eating $it")
                    delay(1500L)
                    println("FLOW: Finished eating $it")
                }
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }
}
