package ru.myproevent.domain.models.repositories.email_hint

import io.reactivex.Single
import ru.myproevent.domain.models.Suggestion

interface IEmailHintRepository {
    fun getEmailHint(part_email: String): Single<List<Suggestion>>
}