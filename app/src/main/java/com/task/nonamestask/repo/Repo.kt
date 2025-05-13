package com.task.nonamestask.repo

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.gson.annotations.Expose
import com.task.nonamestask.Api.Api
import com.task.nonamestask.Constants
import com.task.nonamestask.Models.Data
import com.task.nonamestask.Utils
import kotlinx.coroutines.delay


import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.CopyOnWriteArrayList

import kotlin.math.log


object Repo {

    var isEnd:Boolean = false

    var  errorLiveData:MutableLiveData<String> = MutableLiveData()

    private var LIMIT:Int = 0

    private var INDEX = 0;

    private val TAG = Repo::class.simpleName

    var newList:ArrayList<Data>?=null

    var dataList:CopyOnWriteArrayList<Data>? = CopyOnWriteArrayList()

    var mutableLiveDataList:MutableLiveData<CopyOnWriteArrayList<Data>> = MutableLiveData()

    lateinit var r: Retrofit

    fun buildDB(c: Context) {

        synchronized(this) {

            if (!Repo::r.isInitialized) {

                r = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            }

        }
    }


    suspend fun getAllData(userName: String) {

        newList?.let {

            newList!!.clear()
            dataList!!.clear()
        }



        LIMIT = 0
        INDEX = 0




        r?.apply {

            val response:Response<ArrayList<Data>>? = r.create(Api::class.java).getRepo(userName)

            if (response!=null) {

                if (response.isSuccessful && response.code() == 200) {

                    if (response.body()!=null) {

                        newList = response.body()?.let { it1 -> ArrayList(it1) }

                        if (!newList!!.isEmpty()) {

                            addItem(Utils.noOfItems+2)


                        } else {

                            throw Exception()

                        }

                    } else {

                        throw Exception()


                    }
                } else {

                    throw Exception()
                }
            }

        }

    }

    private suspend fun addItem(count:Int) {

        if (INDEX<newList!!.size) {

            dataList!!.clear()

            LIMIT+=count

            Log.d(TAG, "addItem: " + LIMIT + " " + INDEX)

            newList?.let {

                for (i in INDEX until LIMIT) {

                    try {

                        delay(300)

                        INDEX+=1

                        dataList!!.add(newList!!.get(i))


                    } catch (ex:IndexOutOfBoundsException) {

                        break


                    }


                }

            }

            if (INDEX >= newList!!.size) {

                Log.d(TAG, "addItem: ExpTrue")

                isEnd = true

            }

            mutableLiveDataList.postValue(dataList)


        }



    }

    public suspend fun getItem(count: Int) {

        addItem(count)

    }


}



