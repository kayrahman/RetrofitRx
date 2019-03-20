package neel.com.retrofitrx.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import neel.com.retrofitrx.db.CategoryRepository;
import neel.com.retrofitrx.model.Category;

public class CategoryViewModel extends AndroidViewModel {

    private Context mContext;
    private CategoryRepository mCategoryRepository;
    private LiveData<List<Category>> mListLiveData = new MutableLiveData<>();

    public CategoryViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = new CategoryRepository(application);
        mListLiveData = mCategoryRepository.getListLiveData();

    }



    public void fetchCategoryListAndStore(){
        mCategoryRepository.getCategoryList();
    }


    public LiveData<List<Category>> getListLiveData() {
        return mListLiveData;
    }
}
