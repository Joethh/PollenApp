package com.example.pollenapp

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pollenapp.elements.AllergenItem
import com.example.pollenapp.elements.Forecast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class PollenRepository {

    private val retrofitInstance = RetroFitInstance()
    private val openMeteoApi = retrofitInstance.openMeteoApi
    private val customApi = retrofitInstance.customApi
    private val auth = FirebaseAuth.getInstance()

    private data class AllergenConfig(
        val name: String,
        val values: List<Float?>
    )

    data class PollenInstanceRequest(
        val pollenLevels: List<Float>,
        val discomfortRating: Float
    )

    suspend fun getDiscomfortPrediction(allergens: List<AllergenItem>): DiscomfortResponse? {
        return try {
            // Get current Firebase Auth Token
            val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: return null
            
            // Map current pollen levels to request model
            val requestBody = PollenInstanceRequest(
                pollenLevels = allergens.map { it.score },
                discomfortRating = 5.0f // or whatever value you intend
            )

            // Call API
            val response = customApi.getDiscomfortScore("Bearer $token", requestBody)
            Log.d("API", "Got Response: " + response.toString())
            
            if (response.isSuccessful) {
                Log.d("API", "Body: ${response.body()}")
                response.body()
            } else null
        } catch (e: Exception) {
            Log.e("API", "Failed to get prediction", e)
            null
        }
    }

    // A forecast of the MAX forecasted value from each day.
    suspend fun getFourDayPollenForecast(lat: Double, lon: Double): List<Forecast> {
        return try {
            val response = openMeteoApi.getHourlyPollen(lat, lon)
            if (!response.isSuccessful) return emptyList()

            val hourly = response.body()?.hourly ?: return emptyList()

            val allergens = listOf(
                AllergenConfig("Alder", hourly.alderPollen),
                AllergenConfig("Birch", hourly.birchPollen),
                AllergenConfig("Grass", hourly.grassPollen),
                AllergenConfig("Mugwort", hourly.mugwortPollen),
                AllergenConfig("Olive", hourly.olivePollen),
                AllergenConfig("Ragweed", hourly.ragweedPollen)
            )

            val dailyGrouped = hourly.time.mapIndexedNotNull { index, timeString ->
                runCatching {
                    val localDateTime = LocalDateTime.parse(timeString)
                    val localDate = localDateTime.toLocalDate()

                    val allergenValues = allergens.map { allergen ->
                        allergen.values.getOrNull(index) ?: 0f
                    }

                    localDate to allergenValues
                }.getOrNull()
            }.groupBy(
                keySelector = { it.first },
                valueTransform = { it.second }
            )

            dailyGrouped
                .toSortedMap()
                .entries
                .take(4)
                .map { (date, dailyReadings) ->
                    val maxAllergenValues = allergens.indices.map { allergenIndex ->
                        dailyReadings.maxOfOrNull { it[allergenIndex] } ?: 0f
                    }

                    val overallScore = maxAllergenValues.maxOrNull() ?: 0f

                    val rating = when {
                        overallScore < 3f -> "Low"
                        overallScore < 7f -> "Medium"
                        else -> "High"
                    }

                    Forecast(
                        dayStr = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                        month = date.month.name.lowercase().replaceFirstChar { it.uppercase() },
                        dayInt = date.dayOfMonth,
                        score = overallScore,
                        rating = rating,
                        icon = pollenIcon(overallScore)
                    )
                }

        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get the pollen levels for the current hour.
    suspend fun getCurrentPollenLevels(lat: Double, lon: Double): List<AllergenItem> {
        return try {
            val response = openMeteoApi.getHourlyPollen(lat, lon)
            if (!response.isSuccessful) return emptyList()

            val hourly = response.body()?.hourly ?: return emptyList()

            val allergens = listOf(
                AllergenConfig("Alder", hourly.alderPollen),
                AllergenConfig("Birch", hourly.birchPollen),
                AllergenConfig("Grass", hourly.grassPollen),
                AllergenConfig("Mugwort", hourly.mugwortPollen),
                AllergenConfig("Olive", hourly.olivePollen),
                AllergenConfig("Ragweed", hourly.ragweedPollen)
            )

            val now = LocalDateTime.now()
            val currentIndex = hourly.time.indexOfLast { timeString ->
                runCatching {
                    val time = LocalDateTime.parse(timeString)
                    !time.isAfter(now)
                }.getOrDefault(false)
            }.coerceAtLeast(0)

            allergens.map { allergen ->
                val score = allergen.values.getOrNull(currentIndex) ?: 0f

                AllergenItem(
                    name = allergen.name,
                    score = score,
                    color = pollenColor(score),
                    icon = pollenIcon(score)
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Get the European AQI for the current hour
    suspend fun getCurrentAqi(lat: Double, lon: Double): Int {
        return try {
            val response = openMeteoApi.getHourlyPollen(lat, lon)
            if (!response.isSuccessful) return 0

            val current = response.body()?.current ?: return 0

            current.europeanAqi ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private fun pollenColor(score: Float): Color {
        val clamped = score.coerceIn(0f, 10f)
        val fraction = clamped / 10f
        val red = (255 * fraction).toInt()
        val green = (255 * (1f - fraction)).toInt()
        return Color(red, green, 0)
    }

    private fun pollenIcon(score: Float): ImageVector {
        return when {
            score < 3f -> Icons.Outlined.SentimentSatisfiedAlt
            score < 7f -> Icons.Outlined.SentimentNeutral
            else -> Icons.Outlined.SentimentVeryDissatisfied
        }
    }
}
