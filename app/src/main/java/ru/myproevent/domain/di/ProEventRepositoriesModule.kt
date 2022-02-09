package ru.myproevent.domain.di

import dagger.Binds
import dagger.Module
import ru.myproevent.domain.models.repositories.contacts.IProEventContactsRepository
import ru.myproevent.domain.models.repositories.contacts.ProEventContactsRepository
import ru.myproevent.domain.models.repositories.email_hint.EmailHintRepository
import ru.myproevent.domain.models.repositories.email_hint.IEmailHintRepository
import ru.myproevent.domain.models.repositories.events.IProEventEventsRepository
import ru.myproevent.domain.models.repositories.events.ProEventEventsRepository
import ru.myproevent.domain.models.repositories.images.IImagesRepository
import ru.myproevent.domain.models.repositories.images.ImagesRepository
import ru.myproevent.domain.models.repositories.internet_access_info.IInternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.internet_access_info.InternetAccessInfoRepository
import ru.myproevent.domain.models.repositories.local_proevent_user_token.ITokenLocalRepository
import ru.myproevent.domain.models.repositories.local_proevent_user_token.TokenLocalRepository
import ru.myproevent.domain.models.repositories.proevent_login.IProEventLoginRepository
import ru.myproevent.domain.models.repositories.proevent_login.ProEventLoginRepository
import ru.myproevent.domain.models.repositories.profiles.IProEventProfilesRepository
import ru.myproevent.domain.models.repositories.profiles.ProEventProfilesRepository
import ru.myproevent.domain.utils.GlideLoader
import javax.inject.Singleton

@Module
interface ProEventRepositoriesModule {
    @Singleton
    @Binds
    fun bindLoginRepository(proEventLoginRepository: ProEventLoginRepository): IProEventLoginRepository

    @Singleton
    @Binds
    fun bindInternetAccessInfoRepository(internetAccessInfoRepository: InternetAccessInfoRepository): IInternetAccessInfoRepository

    @Singleton
    @Binds
    fun bindProfilesRepository(proEventProfilesRepository: ProEventProfilesRepository): IProEventProfilesRepository

    @Singleton
    @Binds
    fun bindTokenLocalRepository(tokenLocalRepository: TokenLocalRepository): ITokenLocalRepository

    @Singleton
    @Binds
    fun bindContactsRepository(contactsRepository: ProEventContactsRepository): IProEventContactsRepository

    @Singleton
    @Binds
    fun bindEventsRepository(eventsRepository: ProEventEventsRepository): IProEventEventsRepository

    @Singleton
    @Binds
    fun bindImagesRepository(imagesRepository: ImagesRepository): IImagesRepository

    @Singleton
    @Binds
    fun bindEmailHintRepository(emailHintRepository: EmailHintRepository): IEmailHintRepository
}