package com.task.nonamestask.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.task.nonamestask.Models.Data
import com.task.nonamestask.Utils
import com.task.nonamestask.repo.Repo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(@ApplicationContext private val c:Context)
    : ViewModel() {

    val allMutableLiveData : LiveData<CopyOnWriteArrayList<Data>> = Repo.mutableLiveDataList
    val errorLiveData:MutableLiveData<String> = MutableLiveData()
    var isFetching:Boolean = false
        get() = field
        set(value) {

            field = value
        }

    var userName:String?=null
    //val searchMutableData : LiveData<HashMap<String,ArrayList<Books>>> = Repo.searchMutableData



     fun getAllData(userName:String) {


         if (!Repo.isEnd) {

             isFetching = true

             viewModelScope.launch(Dispatchers.IO) {

                 try {

                     Repo.getAllData(userName)
                     //isFetching = false
                     this@DataViewModel.userName = userName

                 } catch (ex:Exception) {

                     errorLiveData.postValue("Error fetching data")


                 }




             }


         }

    }

    public fun getData() {

        if (!Repo.isEnd) {

            isFetching = true

            viewModelScope.launch(Dispatchers.IO) {


                    Repo.getItem(Utils.noOfItems+2)
                //isFetching = false


            }


        }


    }

    public fun refresh() {

        Repo.isEnd = false

    }

    public fun getIsEnd():Boolean {

        if (Repo.isEnd) {

            return true
        }

        return false



    }

//    fun getAllSearch(s:String) {
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            Repo.filterMap.clear()
//            Repo.searchBook(s)
//
//        }
//
//    }


}