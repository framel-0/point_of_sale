package net.sipconsult.pos.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import net.sipconsult.pos.data.models.*
import net.sipconsult.pos.util.DateTypeConverter

@Database(
    entities = [
        ProductItem::class, ClientItem::class, LoggedInUser::class, DiscountTypesItem::class, PaymentMethodItem::class, LocationsItem::class, SalesAgentsItem::class],
    version = 1
)
@TypeConverters(DateTypeConverter::class)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productsDao(): ProductsDao
    abstract fun clientsDao(): ClientsDao
    abstract fun discountTypeDao(): DiscountTypeDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun locationDao(): LocationDao
    abstract fun salesAgentDao(): SalesAgentDao

    companion object {
        private var instance: ApplicationDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                ApplicationDatabase::class.java, "application.db"
            ).allowMainThreadQueries()
                .build()
    }
}