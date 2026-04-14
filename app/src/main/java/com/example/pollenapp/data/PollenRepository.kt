package com.example.pollenapp.data

import android.content.Context
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Hiking
import androidx.compose.material.icons.outlined.NaturePeople
import androidx.compose.material.icons.outlined.SentimentDissatisfied
import androidx.compose.material.icons.outlined.SentimentNeutral
import androidx.compose.material.icons.outlined.SentimentSatisfiedAlt
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pollenapp.R
import com.example.pollenapp.api.RetroFitInstance
import com.example.pollenapp.elements.AllergenItem
import com.example.pollenapp.elements.Forecast
import com.example.pollenapp.elements.SensitivityAlert
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class PollenRepository(private val context: Context) {

    private val retrofitInstance = RetroFitInstance()
    private val openMeteoApi = retrofitInstance.openMeteoApi
    private val customApi = retrofitInstance.customApi
    private val auth = FirebaseAuth.getInstance()

    private data class AllergenConfig(
        val name: String,
        val values: List<Float?>
    )

    data class PollenUpdateRequest(
        val pollenLevels: List<Float>,
        val discomfortRating: Float
    )

    data class PollenLevelsRequest(
        val pollenLevels: List<Float>
    )

    suspend fun updateAndGetDiscomfortPrediction(allergens: List<AllergenItem>, userRating: Float): SensitivityAlert? {
        return try {
            // Get current Firebase Auth Token
            val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: return null

            // Map current pollen levels to request model
            val requestBody = PollenUpdateRequest(
                pollenLevels = allergens.map { it.score },
                discomfortRating = userRating
            )

            // Call API
            val response = customApi.updateAndGetDiscomfortScore("Bearer $token", requestBody)
            Log.d("API", "Got Prediction-Update Response: $response")

            if (response.isSuccessful) {
                val predictionScore = response.body()?.prediction ?: 0f

                SensitivityAlert(
                    rating = sensitivityRating(predictionScore),
                    message = sensitivityMessage(predictionScore),
                    colour = pollenColor(predictionScore),
                    icon = pollenIcon(predictionScore)
                )
            } else null
        } catch (e: Exception) {
            Log.e("API", "Failed to get prediction", e)
            null
        }
    }

    suspend fun getDiscomfortPrediction(allergens: List<AllergenItem>): SensitivityAlert? {
        return try {
            val token = auth.currentUser?.getIdToken(false)?.await()?.token ?: return null

            val requestBody = PollenLevelsRequest(
                pollenLevels = allergens.map { it.score }
            )

            val response = customApi.getDiscomfortScore("Bearer $token", requestBody)
            Log.d("API", "Got Prediction-Only Response: $response")

            if (response.isSuccessful) {
                val predictionScore = response.body()?.prediction ?: 0f

                SensitivityAlert(
                    rating = sensitivityRating(predictionScore),
                    message = sensitivityMessage(predictionScore),
                    colour = pollenColor(predictionScore),
                    icon = sensitivityIcon(predictionScore)
                )
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
                AllergenConfig(context.getString(R.string.alder), hourly.alderPollen),
                AllergenConfig(context.getString(R.string.birch), hourly.birchPollen),
                AllergenConfig(context.getString(R.string.grass), hourly.grassPollen),
                AllergenConfig(context.getString(R.string.mugwort), hourly.mugwortPollen),
                AllergenConfig(context.getString(R.string.olive), hourly.olivePollen),
                AllergenConfig(context.getString(R.string.ragweed), hourly.ragweedPollen)
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

                    Forecast(
                        dayStr = date.dayOfWeek.name.lowercase()
                            .replaceFirstChar { it.uppercase() },
                        month = date.month.name.lowercase().replaceFirstChar { it.uppercase() },
                        dayInt = date.dayOfMonth,
                        score = overallScore,
                        rating = pollenRating(overallScore),
                        icon = sensitivityIcon(overallScore)
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
                AllergenConfig(context.getString(R.string.alder), hourly.alderPollen),
                AllergenConfig(context.getString(R.string.birch), hourly.birchPollen),
                AllergenConfig(context.getString(R.string.grass), hourly.grassPollen),
                AllergenConfig(context.getString(R.string.mugwort), hourly.mugwortPollen),
                AllergenConfig(context.getString(R.string.olive), hourly.olivePollen),
                AllergenConfig(context.getString(R.string.ragweed), hourly.ragweedPollen)
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
        val normalizedScore = if (score > 10) score / 25 else score

        val clamped = normalizedScore.coerceIn(0f, 10f)
        val fraction = clamped / 10f

        // Define muted start and end colours
        val redStart   = 80
        val redEnd   = 210
        val greenStart = 160
        val greenEnd = 60


        val red   = (redStart   + (redEnd   - redStart)   * fraction).toInt()
        val green = (greenStart + (greenEnd - greenStart)  * fraction).toInt()
        return Color(red, green, 30)
    }

    private fun pollenIcon(score: Float): ImageVector {
        return when {
            score < 50f -> Icons.Outlined.SentimentSatisfiedAlt
            score < 150 -> Icons.Outlined.SentimentNeutral
            else -> Icons.Outlined.SentimentDissatisfied
        }
    }

    private fun sensitivityIcon(score: Float): ImageVector {
        return when {
            score < 3f -> Icons.Outlined.Hiking
            score < 8f -> Icons.Outlined.NaturePeople
            else -> Icons.Outlined.Warning
        }
    }

    private fun sensitivityRating(score : Float): String {
        return when {
            score < 3f -> context.getString(R.string.low)
            score < 8f -> context.getString(R.string.medium)
            else -> context.getString(R.string.high)
        }
    }

    private fun pollenRating(score : Float): String {
        return when {
            score < 50f -> context.getString(R.string.low)
            score < 150f -> context.getString(R.string.medium)
            else -> context.getString(R.string.high)
        }
    }

    private fun sensitivityMessage(score : Float): String {
        return when {
            score < 3f -> context.getString(R.string.predicted_sensitivity_levels_are_low)
            score < 8f -> context.getString(R.string.predicted_sensitivity_levels_are_moderate)
            else -> context.getString(R.string.predicted_sensitivity_levels_are_high)
        }
    }
}