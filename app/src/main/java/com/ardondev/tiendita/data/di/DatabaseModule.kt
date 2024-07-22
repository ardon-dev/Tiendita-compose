package com.ardondev.tiendita.data.di

import android.content.Context
import androidx.room.Room
import com.ardondev.tiendita.data.source.local.StoreDatabase
import com.ardondev.tiendita.data.source.local.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    /** Database instance **/

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): StoreDatabase {
        return Room.databaseBuilder(
            appContext,
            StoreDatabase::class.java,
            "store_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    /** DAO's **/

    @Provides
    fun provideProductDao(storeDatabase: StoreDatabase): ProductDao {
        return storeDatabase.productDao()
    }

}