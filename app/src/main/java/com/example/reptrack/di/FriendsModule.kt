package com.example.reptrack.di

import com.example.reptrack.data.friends.FriendRepositoryImpl
import com.example.reptrack.data.friends.FirebaseFriendsDataSource
import com.example.reptrack.domain.auth.AuthRepository
import com.example.reptrack.domain.friends.FriendRepository
import com.example.reptrack.domain.friends.usecases.AddFriendUseCase
import com.example.reptrack.domain.friends.usecases.DeleteFriendUseCase
import com.example.reptrack.domain.friends.usecases.GetFriendsUseCase
import org.koin.dsl.module

val friendsModule = module {

    single<FriendRepository> {
        FriendRepositoryImpl(
            friendDao = get(),
            firebaseFriendsDataSource = get()
        )
    }

    single {
        FirebaseFriendsDataSource(
            firestore = get()
        )
    }

    factory {
        GetFriendsUseCase(
            authRepository = get(),
            friendRepository = get()
        )
    }

    factory {
        AddFriendUseCase(
            authRepository = get(),
            friendRepository = get()
        )
    }

    factory {
        DeleteFriendUseCase(
            authRepository = get(),
            friendRepository = get()
        )
    }
}
