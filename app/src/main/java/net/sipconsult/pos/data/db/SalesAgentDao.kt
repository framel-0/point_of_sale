package net.sipconsult.pos.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.sipconsult.pos.data.models.SalesAgentsItem

@Dao
interface SalesAgentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(salesAgents: List<SalesAgentsItem>)

    @Query("SELECT * FROM sales_agent WHERE id = :salesAgentId ")
    fun getSalesAgent(salesAgentId: Int): SalesAgentsItem


    @get:Query("SELECT * FROM sales_agent")
    val getSalesAgents: LiveData<List<SalesAgentsItem>>

    @Query("DELETE FROM sales_agent")
    fun deleteAll()
}