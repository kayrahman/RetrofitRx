package neel.com.retrofitrx.db;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;
import neel.com.retrofitrx.api.Service;
import neel.com.retrofitrx.model.Category;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PartMap;


public class CategoryRepository {

    private static final String TAG = CategoryRepository.class.getSimpleName();

    private Context mContext;
    private AppDatabase mDatabase;
    private LiveData<List<Category>> mCategoryList = new MutableLiveData<>();

    public CategoryRepository(Context context) {
        mContext = context;
        mDatabase = AppDatabase.getInstance(context);
        getCategoryList();

    }

    public void getCategoryList(Map<String, RequestBody> partMap) {

        Service.getApi().getCategories()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                        if(response.isSuccessful()){

                            categories = response.body();

                            List<String> category_names = new ArrayList<>();

                            for(int i=0;i<categories.size();i++){

                                category_names.add(categories.get(i).getName());
                            }

                            String[] category_array = new String[category_names.size()];
                            category_array=category_names.toArray(category_array);

                            populateCategoriesSpinner(category_array);


                            Log.d("ON_RESPONSE:", String.valueOf(categories.size()));



                        }else{

                            Log.d("ON_RESPONSE:",response.errorBody().toString());
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                    }
                });



       /* Service.getApiResponse().getApiResponse()
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if(response.code() == HttpURLConnection.HTTP_OK) {

                            //Log.i(TAG, "ON_RESPONSE" + response.body().toString());
                            Log.i(TAG, "ON_RESPONSE" + "HTTP_OK");

                            String api_response = response.body();

                            insertUserDetailIntoRoom(api_response);



                        }else{
                            Log.i(TAG, "ON_RESPONSE" + "SOME ERROR");

                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        Log.i(TAG, "ON_RESPONSE" + "FAILURE");

                    }
                });*/


    }

    private void insertUserDetailIntoRoom(final String api_response) {


        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {

                try {
                    JSONObject json_users =new JSONObject(api_response);

                    Log.i(TAG, "ON_RESPONSE" + json_users.length());

                    mDatabase.categoryDao().deleteUserList();


                    Iterator<String> iter=json_users.keys();
                    while(iter.hasNext()) {
                        String key = iter.next();

                        //retrieve user object from jsonobject
                        JSONObject json_user = json_users.getJSONObject(key);

                     //   fetchUserDetailFromJson(json_user);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }





            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {

                Toast.makeText(mContext, "Room Insert Successful", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable e) {

                Toast.makeText(mContext, "Room Insert Error", Toast.LENGTH_SHORT).show();
            }
        });



    }


    public LiveData<List<Category>> getListLiveData(){

        mCategoryList = mDatabase.categoryDao().loadAllUser();

        return mCategoryList;
    }

}
