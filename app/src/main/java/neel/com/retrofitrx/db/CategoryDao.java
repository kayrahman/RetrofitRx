package neel.com.retrofitrx.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import neel.com.retrofitrx.model.Category;


@Dao
public interface CategoryDao {

    @Query("SELECT * FROM category_tbl")
    LiveData<List<Category>> loadAllUser();

    @Insert
    void insertUser(Category... categories);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateTask(Category user);

    @Query("DELETE FROM category_tbl")
    void deleteUserList();


}
