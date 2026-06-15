package de.felixnuesse.extract.updates.workmanager

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import ca.pkay.rcloneexplorer.BuildConfig
import ca.pkay.rcloneexplorer.R
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import de.felixnuesse.extract.extensions.tag
import de.felixnuesse.extract.notifications.AppUpdateNotification
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateWorker (private var mContext: Context, workerParams: WorkerParameters): Worker(mContext, workerParams) {

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(mContext)

    private var checkForUpdates = preferenceManager.getBoolean(mContext.getString(R.string.pref_key_app_updates), false)
    private val ignoredVersion = preferenceManager.getString(mContext.getString(R.string.pref_key_app_update_dismiss_current_update), "")
    private val lastFoundVersion = preferenceManager.getString(mContext.getString(R.string.pref_key_app_updates_found_update_for_version), BuildConfig.VERSION_NAME)?:BuildConfig.VERSION_NAME


    override fun doWork(): Result {

        Log.e(tag(), "Try to check updates...")

        // this is supposed to only run on startup and once a week.
        if(!checkForUpdates) {
            return Result.success()
        }

        // if we have a new version stored in the preference, only show a notification
        if(BuildConfig.VERSION_NAME != lastFoundVersion) {
            notifyIfRequired()
            // If the last found version is ignored, still do the check
            if (ignoredVersion != lastFoundVersion) {
                return Result.success()
            }
        }

        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("https://api.github.com/repos/newhinton/Round-Sync/releases/latest")
            .header("User-Agent", "Round-Sync-App")
            .build()

        CoroutineScope(Dispatchers.IO).launch( CoroutineExceptionHandler { _, throwable ->
            Log.e(tag(), "Error checking updates: ${throwable.message}")
        }) {
            try {
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.e(tag(), "Failed to check update: HTTP ${response.code}")
                        return@launch
                    }
                    val bodyStr = response.body?.string() ?: return@launch
                    val json = JSONObject(bodyStr)
                    val newVersion = json.getString("tag_name")
                    val releaseNotes = json.optString("body", "")

                    val currentVersion = BuildConfig.VERSION_NAME
                    if (isNewerVersion(currentVersion, newVersion)) {
                        Log.e(tag(), "Update found : $newVersion")
                        setFoundVersion(newVersion)
                        setChangelog(releaseNotes)
                        notifyIfRequired()
                    } else {
                        setFoundVersion(currentVersion)
                    }
                }
            } catch (e: Exception) {
                Log.e(tag(), "Exception during update check: ${e.message}")
            }
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }



    /**
     * Does not notify the user when the user skipped this update.
     */
    private fun notifyIfRequired(){
        if (ignoredVersion != lastFoundVersion){
            AppUpdateNotification(mContext).showNotification(lastFoundVersion)
        } else {
            Log.e(tag(), "Hide this version, because it is ignored.")
        }
    }

    private fun setChangelog(changelog: String){
        val key = mContext.getString(R.string.pref_key_app_updates_changelog)
        preferenceManager.edit().putString(key, changelog).apply()
    }

    fun getChangelog(): String{
        return preferenceManager.getString(mContext.getString(R.string.pref_key_app_updates_changelog), "") ?: ""
    }

    private fun setFoundVersion(version: String){
        val key = mContext.getString(R.string.pref_key_app_updates_found_update_for_version)
        preferenceManager.edit().putString(key, version).apply()
    }

    private fun isNewerVersion(current: String, new: String): Boolean {
        val currClean = current.trim().removePrefix("v").substringBefore('-')
        val newClean = new.trim().removePrefix("v").substringBefore('-')
        if (currClean == newClean) return false
        val currParts = currClean.split('.').mapNotNull { it.toIntOrNull() }
        val newParts = newClean.split('.').mapNotNull { it.toIntOrNull() }
        val maxParts = maxOf(currParts.size, newParts.size)
        for (i in 0 until maxParts) {
            val currPart = currParts.getOrElse(i) { 0 }
            val newPart = newParts.getOrElse(i) { 0 }
            if (newPart > currPart) return true
            if (newPart < currPart) return false
        }
        return false
    }
}
