package ru.myproevent.ui.presenters.events.event.participant

import android.os.Bundle
import com.github.terrakok.cicerone.Router
import ru.myproevent.domain.models.entities.Profile
import ru.myproevent.domain.utils.PARTICIPANT_ID_KEY
import ru.myproevent.domain.utils.PARTICIPANT_TO_REMOVE_ID_RESULT_KEY
import ru.myproevent.ui.presenters.BaseMvpPresenter

class EventParticipantPresenter(localRouter: Router) :
    BaseMvpPresenter<EventParticipantView>(localRouter) {
    fun openChat(userId: Long) {
        viewState.openChat(userId)
    }

    fun openProfile(profile: Profile) {
        // TODO: null передаётся как заглушка, так как на экране contact поле status пока не используется.
        //             Но если это поле будет использованно в дальнейшем, то здесь будет необходимо
        //             передавать действительное значение стутуса, инчае экран с контактом будет
        //             крашится с NullPointerException
        //             (это сделано для того чтобы можно было найти этот комментарий и переделать этот вызов)
        //             В дальнейшем сервер возможно будет отрефакторен так что profileDto и contactDto
        //             будут объеденены в одну сущность (так как они отличаются тольно наличием email и status)
        localRouter.navigateTo(screens.contact(profile.id))
    }

    fun removeParticipant(id: Long) {
        viewState.setResult(
            PARTICIPANT_TO_REMOVE_ID_RESULT_KEY,
            Bundle().apply { putLong(PARTICIPANT_ID_KEY, id) }
        )
        onBackPressed()
    }
}