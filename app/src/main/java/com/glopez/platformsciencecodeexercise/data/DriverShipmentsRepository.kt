package com.glopez.platformsciencecodeexercise.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.HashMap

object DriverShipmentsRepository {
    private val drivers: List<Driver> = arrayListOf(
        Driver(1, "Everardo Welch"),
        Driver(2, "Orval Mayert"),
        Driver(3,"Howard Emmerich"),
        Driver(4,"Izaiah Lowe"),
        Driver(5, "Monica Hermann"),
        Driver(6, "Ellis Wisozk"),
        Driver(7, "Noemie Murphy"),
        Driver(8,"Cleve Durgan"),
        Driver(9, "Murphy Mosciski"),
        Driver(10,"Kaiser Sose")
    )

    private val shipments: List<Shipment> = arrayListOf(
        Shipment(1, "215 Osinski Manors"),
        Shipment(2, "9856 Marvin Stravenue"),
        Shipment(3, "7127 Kathlyn Ferry"),
        Shipment(4, "987 Champlin Lake"),
        Shipment(5, "63187 Volkman Garden Suite 447"),
        Shipment(6, "75855 Dessie Lights"),
        Shipment(7, "1797 Adolf Island Apt. 744"),
        Shipment(8, "2431 Lindgren Corners"),
        Shipment(9, "8725 Aufderhar River Suite 859"),
        Shipment(10, "79035 Shanna Light Apt. 322")
    )

    private val permutationResult: MutableList<List<Int>> = mutableListOf()


    fun getShipments() : List<Shipment> {
        return shipments
    }

    suspend fun getDrivers() : List<Driver> {
        return withContext(Dispatchers.IO){
            drivers
        }
    }

    private fun getPermutationRange() : List<Int> {
        val permutationRange: MutableList<Int> = mutableListOf()
        for (i in 0 until shipments.size) {
            permutationRange.add(i)
        }
        return permutationRange
    }

    private fun calculatePermutations() : List<List<Int>> {
        val permutationRange = getPermutationRange()
        return getPermutations(permutationRange)
    }

    private fun calculateScore(shipment: Shipment) {
        val driverScores: MutableList<DriverScore> = mutableListOf()
        for (driver in this.drivers) {
            val score = getSuitabilityScore(driver.name, shipment.streetName)
            val driverScore = DriverScore(driver.id, shipment.id, score)
            driverScores.add(driverScore)
        }
        shipment.updateScores(driverScores)
    }

    suspend fun calculateAllShipmentScores() {
        return withContext(Dispatchers.Default) {
            val driverScores: MutableList<List<DriverScore>> = mutableListOf()

            // Build driverScore matrix. This will be a list of lists (2d array).
            // The matrix will have shipments on the y-axis and scores for each driver on the
            // x-axis. So it's a list of driver scores, with each index representing a shipment.
            // And the value for each index is a list of driver scores for that shipment.
            for (shipment in shipments) {
                calculateScore(shipment)
            }
            for (shipment in shipments) {
                driverScores.add(shipment.getScores())
            }
            // Used to calculate all possible combinations of driver assignments
            val permutations = calculatePermutations()

            var maxSSScore = 0.0
            var maxSSIndex = 0
            var currentSSScore = 0.0

            // Permutation range will be used to determine the combinations of indexes.
            // These will be used later to identify the max scores
            val permutationRange = getPermutationRange()


            // The double for loops are used to calculate the SS score for each permutation.
            // The permutation matrix is used as a reference to the indices we will look up in
            // the score matrix. This combination of scores will be totaled up and compared to
            // the recorded max score. What we're looking for is the max score possible, per driver,
            // given all the possible permutations
            for (i in permutations.indices) {
                for (j in permutationRange.indices) {
                    currentSSScore += driverScores[j][permutations[i][j]].score
                }
                if (currentSSScore > maxSSScore) {
                    maxSSScore = currentSSScore
                    maxSSIndex = i
                }
            }

            // Now that we've got the max scores, we will use this list to look up the index
            // in the score matrix. The index represents the driver (index 0 is driver 1). The value
            // at that index represents the shipment (value of 0 is shipment 1). We will retrieve
            // the score at that position in the score matrix.
            val maxScores = permutations[maxSSIndex]

            for (i in maxScores.indices) {

                // The i-th index in maxScores corresponds to the shipment with shipmentId == i
                val maxSSIndexForDiver = maxScores[i]

                val driverScoresForShipment = driverScores[i]
                val driverToAssign = driverScoresForShipment[maxSSIndexForDiver].driverId
                val shipment = shipments[i]
                shipment.assignedDriverId = driverToAssign
            }
        }
    }

    private val isEven: (Int) -> Boolean = { it % 2 == 0 }

    private fun getNumberOfVowels(input: String) : Int {
        val inputWithNoDigits = input.filter { it.isLetter() }
        var count = 0
        for (char in inputWithNoDigits.toLowerCase().toCharArray()) {
            when (char) {
                'a', 'e', 'i', 'o', 'u' -> count++
            }
        }
        return count
    }

    private fun getNumberOfConsonants(input: String) : Int {
        val inputWithNoDigits = input.filter { it.isLetter() }
        val numVowels = getNumberOfVowels(inputWithNoDigits)
        return inputWithNoDigits.length - numVowels
    }

    private fun getFactors(input: Int) : MutableList<Int> {
        val factors = mutableListOf<Int>()
        if (input < 0)
            return factors

        for (value in 1..input) {
            if (input % value == 0) {
                factors.add(value)
            }
        }
        return factors
    }

    private fun hasCommonFactors (driverNameFactors: MutableList<Int>,
                                  shipmentAddressFactors: MutableList<Int>) : Int {
        var numberOfFactors = 0
        val commonFactorsCount = HashMap<Int, Int>()
        val commonFactors = driverNameFactors
        commonFactors.addAll(shipmentAddressFactors)
        commonFactors.sort()

        for (value: Int in commonFactors) {
            if (commonFactorsCount.containsKey(value)) {
                val count = commonFactorsCount[value]?.plus(1) ?: 1
                commonFactorsCount[value] = count
            } else {
                commonFactorsCount[value] = 1
            }
        }

        commonFactorsCount.values.forEach { count ->
            if (count > 1)
                numberOfFactors++
        }

        return numberOfFactors
    }

    private fun getSuitabilityScore(driverName: String, shipmentAddress: String) : Double {
        var baseSS = 0.0
        val length = shipmentAddress.length
        baseSS = if (isEven(length)) {
            getNumberOfVowels(driverName) * 1.5
        } else {
            getNumberOfConsonants(driverName) * 1.0
        }
        val factors = hasCommonFactors(getFactors(driverName.length),
            getFactors(shipmentAddress.length))
        if (factors > 1) {
            baseSS *= 1.5
        }
        return baseSS
    }

    private fun getPermutations(list: List<Int>) : List<List<Int>> {
        generatePermutations(list.count(), list.toList())
        return permutationResult
    }

    /**
     * In my search for an algorithm to generate all possible permutations,
     * I came upon methods using Heap's algorithm
     * (https://en.wikipedia.org/wiki/Heap%27s_algorithm). This will generate
     * all of the possible permutations of k objects. I also referenced solutions
     * from the following:
     * https://www.geeksforgeeks.org/heaps-algorithm-for-generating-permutations/
     * https://gist.github.com/dmdrummond/4b1d8a4f024183375f334a5f0a984718
     */
    private fun generatePermutations(k: Int, list: List<Int>) {
        if (k == 1) {
            permutationResult.add(list.toList())
        } else {
            generatePermutations(k - 1, list)
            for (i in 0 until k) {
                if (isEven(k)) {
                    Collections.swap(list, i, k - 1)
                } else {
                    Collections.swap(list, 0, k - 1)
                }
            }
        }
    }
}