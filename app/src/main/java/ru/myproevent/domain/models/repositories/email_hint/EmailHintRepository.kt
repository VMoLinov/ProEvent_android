package ru.myproevent.domain.models.repositories.email_hint

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import ru.myproevent.domain.models.HintRequest
import ru.myproevent.domain.models.IProEventDataSource
import ru.myproevent.domain.models.Suggestion
import javax.inject.Inject

class EmailHintRepository @Inject constructor(private val api: IProEventDataSource):IEmailHintRepository {
    override fun getEmailHint(part_email: String): Single<List<Suggestion>> {
        return api.getEmailHint(HintRequest(part_email)).map { it.suggestions }
            .subscribeOn(Schedulers.io())
    }
}