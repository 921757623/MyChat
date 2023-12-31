package com.hust.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hust.database.tables.User

@Dao
abstract class UsersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(user: User)

    @Query("select * from users where user_name =:userName and password =:password")
    abstract fun queryByLoginIn(userName: String, password: String): User?

    @Query("select * from users where user_name =:userName")
    abstract fun queryByName(userName: String): User?

    @Query("select * from users where id =:id")
    abstract fun queryById(id: Int): User?

    @Query("select * from users")
    abstract fun queryAll(): List<User>

}