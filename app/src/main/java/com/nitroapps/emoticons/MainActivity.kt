package com.nitroapps.emoticons

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Toast
import com.nitroapps.emoticons.events.CategorySelectedEvent
import com.nitroapps.emoticons.events.DBCreatedEvent
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import android.util.DisplayMetrics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.nitroapps.emoticons.animation.AnimationUtil


class MainActivity : AppCompatActivity(), EmoticonsAdapter.FavoriteStatusListener {
    private var emoticonsDB: EmoticonsDatabase? = null
    private lateinit var dbWorkerThread: DBWorkerThread
    private val mUiHandler = Handler()

    private var returnToCategories: Boolean = false
    private var screenWidth: Int = 0
    private var currentTab: Int = 3

    private var lastVisibleRecyclerView: RecyclerView? = null

    var allEmoticons: ArrayList<EmoticonEntity> = ArrayList()
    var favoriteEmoticons: ArrayList<EmoticonEntity> = ArrayList()
    var emoticonsByCategoryEmoticons: ArrayList<EmoticonEntity> = ArrayList()

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.favourites -> {
                currentTab = 0
                returnToCategories = false
                getFavoritesFromDb()
                return@OnNavigationItemSelectedListener true
            }
            R.id.categories -> {
                currentTab = 1
                returnToCategories = false
                generateCategories()
                return@OnNavigationItemSelectedListener true
            }
            R.id.all -> {
                currentTab = 3
                returnToCategories = false
                AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, allEmoticonsRecyclerView, true, 300)
                lastVisibleRecyclerView = allEmoticonsRecyclerView
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        setupRecyclerViews()
        lastVisibleRecyclerView = allEmoticonsRecyclerView

        dbWorkerThread = DBWorkerThread("dbWorkerThread")
        dbWorkerThread.start()
        emoticonsDB = EmoticonsDatabase.getInstance(this)

        navigation.selectedItemId = R.id.all

        //EventBus.getDefault().post(DBCreatedEvent(true))
        getAllDataFromDb(DBCreatedEvent(true))

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels
    }

    private fun setupRecyclerViews() {
        favoriteEmoticonsRecyclerView.adapter = EmoticonsAdapter(this, favoriteEmoticons, this)
        favoriteEmoticonsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val categories = resources.getStringArray(R.array.categories_array).toList()
        categoryRecyclerView.adapter = CategoriesAdapter(this, categories)
        categoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        emoticonsByCategoryRecyclerView.adapter = EmoticonsAdapter(this, emoticonsByCategoryEmoticons, this)
        emoticonsByCategoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        allEmoticonsRecyclerView.adapter = EmoticonsAdapter(this, allEmoticons, this)
        allEmoticonsRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onBackPressed() {
        if (returnToCategories) {
            generateCategories(true)
            returnToCategories = false
        } else {
            super.onBackPressed()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getAllDataFromDb(event: DBCreatedEvent) {
        val task = Runnable {
            val emoticonsData = emoticonsDB?.emoticonDAO()?.getAll()
            mUiHandler.post {
                if (emoticonsData == null || emoticonsData.size == 0) {
                    if (!event.isFirstTime) {
                        Toast.makeText(this@MainActivity, "No data in Database", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    allEmoticons.addAll(emoticonsData)
                    allEmoticonsRecyclerView.adapter?.notifyDataSetChanged()
                    if(lastVisibleRecyclerView == allEmoticonsRecyclerView) {
                        AnimationUtil.animateViewChangeScaleFade(loaderProgressbar, allEmoticonsRecyclerView, true, 300)
                    } else {
                        AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, allEmoticonsRecyclerView, true, 300)
                    }
                    lastVisibleRecyclerView = allEmoticonsRecyclerView

                }
            }
        }
        dbWorkerThread.postTask(task)
    }

    private fun getFavoritesFromDb() {
        val task = Runnable {
            val emoticonsData = emoticonsDB?.emoticonDAO()?.getFavorites()
            mUiHandler.post {
                if (emoticonsData == null || emoticonsData.size == 0) {
                    //AnimationUtil.hideViewChangeScaleFade(loaderProgressbar,true, 250)
                    Toast.makeText(this@MainActivity, "No favourites yet :(", Toast.LENGTH_SHORT).show()
                    AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, favoriteEmoticonsRecyclerView, true, 300)
                    lastVisibleRecyclerView = favoriteEmoticonsRecyclerView
                } else {
                    favoriteEmoticons.clear()
                    favoriteEmoticons.addAll(emoticonsData)
                    favoriteEmoticonsRecyclerView.adapter?.notifyDataSetChanged()
                    AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, favoriteEmoticonsRecyclerView, true, 300)
                    lastVisibleRecyclerView = favoriteEmoticonsRecyclerView
                }
            }
        }
        dbWorkerThread.postTask(task)
    }

    override fun favoriteStatusChanged(emoticon: EmoticonEntity, position: Int) {
        if (currentTab == 0) {
            favoriteEmoticons.remove(emoticon)
            favoriteEmoticonsRecyclerView.adapter!!.notifyItemRemoved(position)
            val allPosition = (allEmoticonsRecyclerView.adapter as EmoticonsAdapter).getEmoticonPositionByValue(emoticon.value)
            allEmoticons[allPosition].isFavorite = emoticon.isFavorite
            allEmoticonsRecyclerView.adapter!!.notifyItemChanged(allPosition)
        }

        if(emoticon.isFavorite) {
            Toast.makeText(this, "${emoticon.value} was added to your favourites list", Toast.LENGTH_SHORT).show()
        }

        val task = Runnable {
            emoticonsDB?.emoticonDAO()?.changeFavoriteStatus(emoticon.uid!!, emoticon.isFavorite)
        }
        dbWorkerThread.postTask(task)
    }

    private fun generateCategories(shouldAnimateSlide: Boolean = false) {
        if (!shouldAnimateSlide) {
            loaderProgressbar.visibility = View.VISIBLE
            allEmoticonsRecyclerView.visibility = View.INVISIBLE
        }
        categoryRecyclerView.adapter!!.notifyDataSetChanged()
        if (shouldAnimateSlide) {
            AnimationUtil.animateViewChangeScaleFade(emoticonsByCategoryRecyclerView, categoryRecyclerView, true, 300)
        } else {
            AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, categoryRecyclerView, true, 300)
        }
        lastVisibleRecyclerView = categoryRecyclerView
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun getEmoticonsFromDbByCategory(event: CategorySelectedEvent) {
        currentTab = 2
        returnToCategories = true
        val task = Runnable {
            val emoticonsData = emoticonsDB?.emoticonDAO()?.getByCategory(event.catId)
            mUiHandler.post {
                if (emoticonsData == null || emoticonsData.size == 0) {
                    Toast.makeText(this@MainActivity, "Something went wrong :(", Toast.LENGTH_SHORT).show()
                } else {
                    emoticonsByCategoryEmoticons.clear()
                    emoticonsByCategoryEmoticons.addAll(emoticonsData)
                    emoticonsByCategoryRecyclerView.adapter?.notifyDataSetChanged()
                    emoticonsByCategoryRecyclerView.scrollToPosition(0)
                    AnimationUtil.animateViewChangeScaleFade(lastVisibleRecyclerView!!, emoticonsByCategoryRecyclerView, true, 300)
                    lastVisibleRecyclerView = emoticonsByCategoryRecyclerView
                }
            }
        }
        dbWorkerThread.postTask(task)
    }
}
