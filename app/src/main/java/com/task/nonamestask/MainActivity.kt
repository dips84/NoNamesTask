package com.task.nonamestask

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.task.nonamestask.Adapters.MyRepoAdapter
import com.task.nonamestask.Models.Data
import com.task.nonamestask.ViewModel.DataViewModel
import com.task.nonamestask.repo.Repo
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.CopyOnWriteArrayList

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    var LIMIT:Int = 5

    var mainProgress:ProgressBar?= null


    var myRepoAdapter: MyRepoAdapter? = null

    var mainRecycler: RecyclerView? = null

    var actionbar: AppBarLayout? = null


    var isset: Boolean = false

    var observer: Observer<List<DataViewModel>>? = null

    var editusername: EditText? = null

    var toolbar: Toolbar? = null

    var menu_icon: ImageView? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    lateinit var loadingProgress:ProgressBar

    private val dataViewModel : DataViewModel by viewModels()

    var dataList:ArrayList<Data> = ArrayList()

    var isMainFetch = false




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val view = layoutInflater.inflate(R.layout.single_item_repo, null, false)

        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        view.measure(widthMeasureSpec, heightMeasureSpec)

        val measuredHeight = view.measuredHeight+10
        val measuredWidth = view.measuredWidth


        Utils.getNoOfItems(applicationContext,measuredWidth,measuredHeight)
        Log.d("MainActivity", "Measured height: $measuredHeight")



        //dataViewModel = ViewModelProvider(this).get(DataViewModel::class.java)
        Repo.buildDB(this@MainActivity)


        loadingProgress = findViewById<ProgressBar>(R.id.loading_progress)
        mainProgress = findViewById(R.id.mainProgress)

        loadingProgress.visibility = View.GONE
        mainProgress!!.visibility = View.GONE

        mainRecycler = findViewById<View>(R.id.main_recycler) as RecyclerView
        mainRecycler!!.layoutManager = GridLayoutManager(this, 2)
        myRepoAdapter = MyRepoAdapter(this)

        mainRecycler!!.adapter = myRepoAdapter


        toolbar = findViewById<View>(R.id.main_toolbar) as Toolbar
        setSupportActionBar(toolbar)


        editusername = findViewById<View>(R.id.edit_username) as EditText

        editusername!!.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val inputText:String = editusername!!.text.toString()

                if (inputText.equals("")) {


                } else {


                    dataViewModel!!.refresh()
                    dataList.clear()
                    myRepoAdapter!!.setData(dataList)
                    mainRecycler!!.scrollToPosition(0)

                    try {

                        dataViewModel!!.getAllData(inputText)
                        loadingProgress.visibility = View.VISIBLE

                    } catch (ex:Exception) {

                        loadingProgress.visibility = View.GONE
                        Toast.makeText(this@MainActivity
                            ,"Failed to load data"
                            ,Toast.LENGTH_SHORT)


                    }

                    hideKeyboard()


                }

                true
            } else {
                false
            }
        }

        setupObserver()

        mainRecycler!!.addOnScrollListener(object : RecyclerView.OnScrollListener(){

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy>0
                    && !dataViewModel!!.isFetching
                    && !dataViewModel!!.getIsEnd()) {

                    val layoutManager: LinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager

                    val totalItems = myRepoAdapter!!.itemCount
                    val visibleItems = layoutManager.childCount

                    Log.d(MainActivity::class.java.name, "onScrolled: " + totalItems)

                    val lastItemVisiblePosition = layoutManager.findLastVisibleItemPosition()

                    Log.d(MainActivity::class.java.name, "onScrolled: " + lastItemVisiblePosition)

                    if (lastItemVisiblePosition+1 == totalItems) {


                        loadingProgress.visibility = View.VISIBLE

                        dataViewModel!!.getData()



                    }


                }


            }


        })



    }

    override fun onResume() {
        super.onResume()
    }

    private fun setupObserver() {

        dataViewModel!!.allMutableLiveData.observe(this , object : Observer<CopyOnWriteArrayList<Data>>{
            override fun onChanged(value: CopyOnWriteArrayList<Data>) {



                loadingProgress.visibility = View.GONE


                dataList.addAll(value)
                myRepoAdapter!!.setData(dataList)

                dataViewModel!!.isFetching = false


            }


        })

        dataViewModel.errorLiveData.observe(this,object :Observer<String>{
            override fun onChanged(value: String) {

                Toast.makeText(this@MainActivity,value,Toast.LENGTH_SHORT).show()

                loadingProgress.visibility = View.GONE

                dataViewModel.isFetching = false
            }


        })

    }


//    private fun getRepos(s: String) {
//        myOwnRepos.clear()
//
//        val retroFitClient: RetroFitClient = RetroFitClient.getInstance()
//        val api: Api = retroFitClient.getApi()
//
//        api.getRepository(s).enqueue(object : Callback<JsonArray?> {
//            override fun onResponse(call: Call<JsonArray?>, response: Response<JsonArray?>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_SHORT).show()
//
//                    val jsonArray = response.body()
//
//                    for (i in 0..<jsonArray!!.size()) {
//                        val jsonObject = jsonArray[i].asJsonObject
//                        Log.d(
//                            MainActivity::class.java.name,
//                            "onResponse: $jsonObject"
//                        )
//                        val myOwnRepo: MyOwnRepo = Gson().fromJson<MyOwnRepo>(
//                            jsonObject.toString(),
//                            MyOwnRepo::class.java
//                        )
//                        myOwnRepos.add(myOwnRepo)
//                    }
//
//                    myRepoAdapter.notifyDataSetChanged()
//                } else {
//                    Toast.makeText(this@MainActivity, "Failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<JsonArray?>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "Error:" + t.message, Toast.LENGTH_SHORT).show()
//            }
//        })
//    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)


        //        myGithubViewModel.getMyRepos().removeObserver(observer);
//
//        observer = new Observer<List<MyOwnRepo>>() {
//            @Override
//            public void onChanged(List<MyOwnRepo> myOwnRepoList) {
//
//                myRepoAdapter.setData(myOwnRepoList);
//
//            }
//        };
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }



}
