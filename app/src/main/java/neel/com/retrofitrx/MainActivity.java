package neel.com.retrofitrx;

//1.TODO: post to github
//2.TODO:handle error response
//3.TODO:handle successful response
//4.TODO:date picker dialog fragment
//5.TODO:Composite disposable
//6.TODO:read the response body and get token.

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.File;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.ResourceSubscriber;
import neel.com.retrofitrx.Utils.FileUtils;
import neel.com.retrofitrx.api.Service;
import neel.com.retrofitrx.model.Category;
import neel.com.retrofitrx.model.ErrorResponse;
import neel.com.retrofitrx.model.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.reactivex.Observable.create;

public class MainActivity extends AppCompatActivity {

    private static int GALLERY_REQUEST=1;

    private EditText mEmailEt,mUserNameEt,mPasswordEt,mFullnameEt;
    private ImageButton mUserImageIb;
    private Button mSubmitBtn;
    private Spinner mCategoriesSpinner;
    private Uri mImageUri;
    public static final String TAG = MainActivity.class.getSimpleName();

    private Subscription mSubscription;


    private List<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        categories = new ArrayList<>();

       // makeNetworkRequest();

        initViews();


        //POPULATING DATA ON SPINNERS


        fetchCategoryListFromDb();

       // populateCategoriesSpinner();


    }

    private void initViews() {

        mEmailEt = findViewById(R.id.et_ac_main_user_email);
        mPasswordEt = findViewById(R.id.et_ac_main_user_password);
        mUserNameEt = findViewById(R.id.et_ac_main_user_username);
        mFullnameEt = findViewById(R.id.et_ac_main_user_full_name);
        mUserImageIb = findViewById(R.id.ib_ac_main_user_image);
        mSubmitBtn = findViewById(R.id.btn_ac_main_submit_btn);
        mCategoriesSpinner = findViewById(R.id.spinner_ac_main_categories);
        mUserImageIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);

            }
        });




        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isStoragePermissionGranted()) {

                   // uploadDataWithImage();

                    uploadWithRxJavaRetrofit();
                }
            }
        });


    }

    private void uploadWithRxJavaRetrofit(){

        int category_position =  mCategoriesSpinner.getSelectedItemPosition();
        String category_id = categories.get(category_position).get_id();


        String username = mUserNameEt.getText().toString();
        String email=mEmailEt.getText().toString();
        String full_name=mFullnameEt.getText().toString();
        final String password=mPasswordEt.getText().toString();
        String gender="m";
        String account = "5beb4486ec74373f70029e36";
        String birthday="2000-01-01T00:00:00.000Z";
        // String[] categories={"5c4577686ae5e088d98adbbc"};
        String[] categories={category_id};
        String image = "1552894269838";

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(full_name) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(category_id)

        ) {

            final Map<String, RequestBody> partmap = new HashMap<>();
            partmap.put("username", createPartFromString(username));
            partmap.put("email", createPartFromString(email));
            partmap.put("gender", createPartFromString(gender));
            partmap.put("password", createPartFromString(password));
            partmap.put("account", createPartFromString(account));
            partmap.put("full_name", createPartFromString(full_name));
            partmap.put("birthday", createPartFromString(birthday));
            partmap.put("categories", createPartFromString(categories[0]));
            partmap.put("image", createPartFromString(image));



         Service.ApiWithRx().uploadDataWithRx(partmap)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {

                            Log.d("ON_RESPONSE", responseBody.toString());
                        }

                        @Override
                        public void onError(Throwable e) {

                            Log.d("ON_RESPONSE","ERROR");
                            Log.d("ON_RESPONSE",e.getMessage());

                        }

                        @Override
                        public void onComplete() {

                            Log.d("ON_RESPONSE", "COMPLETED");
                        }
                    });




        }else{

            Toast.makeText(this, "Fields empty", Toast.LENGTH_SHORT).show();
        }



        }


    private void fetchCategoryListFromDb() {

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

    }

    private void populateCategoriesSpinner(String[] category_names) {

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                category_names);

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategoriesSpinner.setAdapter(myAdapter);

    }

    private void uploadDataWithImage() {


        int category_position =  mCategoriesSpinner.getSelectedItemPosition();
        String category_id = categories.get(category_position).get_id();


        String username = mUserNameEt.getText().toString();
        String email=mEmailEt.getText().toString();
        String full_name=mFullnameEt.getText().toString();
        String password=mPasswordEt.getText().toString();
        String gender="m";
        String account = "5beb4486ec74373f70029e36";
        String birthday="2000-01-01T00:00:00.000Z";
       // String[] categories={"5c4577686ae5e088d98adbbc"};
        String[] categories={category_id};
        String image = "1552894269838";

        if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(full_name) &&
                !TextUtils.isEmpty(password) && !TextUtils.isEmpty(category_id)

        ) {

            Map<String, RequestBody> partmap = new HashMap<>();
            partmap.put("username", createPartFromString(username));
            partmap.put("email", createPartFromString(email));
            partmap.put("gender", createPartFromString(gender));
            partmap.put("password", createPartFromString(password));
            partmap.put("account", createPartFromString(account));
            partmap.put("full_name", createPartFromString(full_name));
            partmap.put("birthday", createPartFromString(birthday));
            partmap.put("categories", createPartFromString(categories[0]));
            partmap.put("image", createPartFromString(image));


       /* Call<ResponseBody> call = Service.postUserApi().uploadFileWithPartMap(
                partmap,
                prepareFilePart("image",mImageUri)
                );

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.isSuccessful()) {
                    Log.d("ON_RESPONSE", response.body().toString());
                }


                if(response.code() == 500) {

                    Log.d("ON_RESPONSE:","500");
                    Log.d("ON_RESPONSE:",response.message());
                }



            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });*/


            Call<ResponseBody> call = Service.postUserApi().uploadDataWithPartMap(
                    partmap
            );

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    if (response.isSuccessful()) {
                        Log.d("ON_RESPONSE", response.body().toString());
                    } else {

                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());

                            Log.d("ON_RESPONSE:", jObjError.getString("status"));
                            Log.d("ON_RESPONSE:", jObjError.getString("message"));

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }


                    if (response.code() == 500) {


                        Log.d("ON_RESPONSE:", "500");
                        Log.d("ON_RESPONSE:", response.errorBody().toString());


                    }


                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });

        }else{

            Toast.makeText(this, "Fields Empty", Toast.LENGTH_SHORT).show();

        }


    }

    private void makeNetworkRequest() {

        String username = "test002";
        String email="testkay002@gmail.com";
        String gender="m";
        String password="123456";
        String account = "5beb4486ec74373f70029e36";
        String full_name="Mr.test 102";
        String birthday="2000-01-01T00:00:00.000Z";
        String[] categories={"5c4577686ae5e088d98adbbc"};


        User user = new User(username,email,gender,password,account,full_name,birthday,categories);

        Service.postUserApi().postUserData(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {


                if(response.isSuccessful()) {
                    Log.d("ON_RESPONSE", response.body().getToken());
                }


                if(response.code() == 500) {

                    Log.d("ON_RESPONSE:","500");
                    Log.d("ON_RESPONSE:",response.message());
                }






                // String token = response.body().getToken();
              //  Log.d("ON_RESPONSE:",response.body().toString());

            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                Log.d("ON_RESPONSE:","Failure");

            }
        });


    }




    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = FileUtils.getFile(this, fileUri);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse(getContentResolver().getType(fileUri)),
                        file
                );

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);

    }


    @NonNull
    private RequestBody createPartFromString(String desc){

      return RequestBody.create(MultipartBody.FORM,desc);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK ) {

            if (data == null) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
            } else {
                mImageUri = data.getData();

            }
        }
            }




    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted");
            return true;
        }
    }
}
