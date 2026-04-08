package com.example.alzheimerapp.data

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.json.JSONArray

val Context.dataStore by preferencesDataStore(name = "images")

data class RewardImage(
    val uri: String,
    val order: Int
)

object ImageStorage {

    private val OBJECTS_KEY = stringPreferencesKey("object_images")
    private val REWARDS_KEY = stringPreferencesKey("reward_images")

    // ---------- SAVE ----------

    suspend fun saveImages(
        context: Context,
        objects: List<String>,
        rewards: List<RewardImage>
    ) {
        context.dataStore.edit {
            it[OBJECTS_KEY] = listToJson(objects)
            it[REWARDS_KEY] = rewardsToJson(rewards)
        }
    }

    // ---------- LOAD ----------

    suspend fun loadObjects(context: Context): List<String> {
        return loadList(context, OBJECTS_KEY)
    }

    suspend fun loadRewards(context: Context): List<RewardImage> {
        return try {
            val prefs = context.dataStore.data.first()
            val json = prefs[REWARDS_KEY] ?: return emptyList()
            val list = jsonToRewards(json)

            list.filter { isValidUri(context, it.uri) }

        } catch (e: Exception) {
            emptyList()
        }
    }

    // ---------- INTERNAL ----------

    private suspend fun loadList(
        context: Context,
        key: Preferences.Key<String>
    ): List<String> {
        return try {
            val prefs = context.dataStore.data.first()
            val json = prefs[key] ?: return emptyList()
            val list = jsonToList(json)

            // 🔹 Filtrar URIs inválidas
            list.filter { isValidUri(context, it) }

        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun listToJson(list: List<String>): String {
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    private fun jsonToList(json: String): List<String> {
        val jsonArray = JSONArray(json)
        return List(jsonArray.length()) { i ->
            jsonArray.getString(i)
        }
    }

    private fun rewardsToJson(list: List<RewardImage>): String {
        val jsonArray = JSONArray()
        list.forEach {
            val obj = org.json.JSONObject()
            obj.put("uri", it.uri)
            obj.put("order", it.order)
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    private fun jsonToRewards(json: String): List<RewardImage> {
        return try {
            val jsonArray = JSONArray(json)

            // Detectar si es formato antiguo
            if (jsonArray.length() > 0 && jsonArray.get(0) is String) {
                List(jsonArray.length()) { i ->
                    RewardImage(
                        uri = jsonArray.getString(i),
                        order = i
                    )
                }
            } else {
                List(jsonArray.length()) { i ->
                    val obj = jsonArray.getJSONObject(i)
                    RewardImage(
                        uri = obj.getString("uri"),
                        order = obj.getInt("order")
                    )
                }
            }

        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun isValidUri(context: Context, uriString: String): Boolean {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}