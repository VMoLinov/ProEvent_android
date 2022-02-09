package ru.myproevent.domain.models

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface IEmailHintDataSource {
    @POST("email")
    fun getEmailHint(@Body hintRequest: HintRequest): Single<HintResponse>
}

data class ProfileIdListDto(val profileIds: List<Long>)
data class HintRequest(val query: String)
data class HintResponse(val suggestions: List<Suggestion>)
data class Suggestion(val value: String)