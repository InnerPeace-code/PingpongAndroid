package com.pingpong.app.core.data

import com.pingpong.app.core.common.IoDispatcher
import com.pingpong.app.core.model.ApiResponse
import com.pingpong.app.core.network.api.CampusApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonElement

@Singleton
class CampusRepository @Inject constructor(
    private val campusApi: CampusApi,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun fetchSchoolOptions(): ApiResponse<JsonElement> = withContext(ioDispatcher) {
        campusApi.getSchoolOptions()
    }
}
