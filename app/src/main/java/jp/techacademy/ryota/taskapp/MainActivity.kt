package jp.techacademy.ryota.taskapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_main.*

const val EXTRA_TASK = "jp.techacademy.ryota.taskapp.TASK"

class MainActivity : AppCompatActivity() {
    private var categoryId = 0
    private lateinit var mRealm: Realm
    private val mRealmListener = object : RealmChangeListener<Realm> {
        override fun onChange(element: Realm) {
            reloadListView(categoryId)
            reloadCategory()
        }
    }
    private lateinit var mTaskAdapter: TaskAdapter
    private lateinit var mCategoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(intent)
        }

        // Realmの設定
        mRealm = Realm.getDefaultInstance()
        mRealm.addChangeListener(mRealmListener)

        // ListViewの設定
        mTaskAdapter = TaskAdapter(this@MainActivity)

        // ListViewをタップしたときの処理
        listView1.setOnItemClickListener { parent, _, position, _ ->
            // 入力・編集する画面に遷移させる
            val task = parent.adapter.getItem(position) as Task
            val intent = Intent(this@MainActivity, InputActivity::class.java)
            intent.putExtra(EXTRA_TASK, task.id)
            startActivity(intent)
        }

        // ListViewを長押ししたときの処理
        listView1.setOnItemLongClickListener { parent, _, position, _ ->
            // タスクを削除する
            val task = parent.adapter.getItem(position) as Task

            // ダイアログを表示する
            val builder = AlertDialog.Builder(this@MainActivity)

            builder.setTitle("削除")
            builder.setMessage(task.title + "を削除しますか")

            builder.setPositiveButton("OK") { _, _ ->
                val results = mRealm.where(Task::class.java).equalTo("id", task.id).findAll()

                mRealm.beginTransaction()
                results.deleteAllFromRealm()
                mRealm.commitTransaction()

                val resultIntent = Intent(applicationContext, TaskAlarmReceiver::class.java)
                val resultPendingIntent = PendingIntent.getBroadcast(
                    this@MainActivity,
                    task.id,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(resultPendingIntent)

                reloadListView(categoryId)
            }

            builder.setNegativeButton("CANCEL", null)

            val dialog = builder.create()
            dialog.show()

            true
        }

        // カテゴリ検索
        category_search_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    categoryId = id.toInt()
                    reloadListView(categoryId)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

        reloadCategory()
        reloadListView(categoryId)
    }

    private fun reloadListView(categoryId: Int) {
        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        val taskRealmResults =
            if (categoryId == 0) {
                mRealm.where(Task::class.java).findAll().sort("date", Sort.DESCENDING)
            } else {
                mRealm.where(Task::class.java).equalTo("categoryId", categoryId).findAll()
                    .sort("date", Sort.DESCENDING)
            }

        // 上記の結果を、TaskListとしてセットする
        mTaskAdapter.taskList = mRealm.copyFromRealm(taskRealmResults)

        // TaskのListView用のアダプタに渡す
        listView1.adapter = mTaskAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mTaskAdapter.notifyDataSetChanged()
    }

    private fun reloadCategory() {
        // Realmの設定
        var realm = Realm.getDefaultInstance()

        mCategoryAdapter = CategoryAdapter(this@MainActivity)

        val categoryRealmResults =
            realm.where(Category::class.java).findAll().sort("id", Sort.ASCENDING)


        if (categoryRealmResults.size == 0) {
            val category = Category()
            category.id = 0
            category.name = ""

            realm.beginTransaction()
            realm.copyToRealmOrUpdate(category)
            realm.commitTransaction()
            realm.close()
        }

        // 上記の結果を、categoryListとしてセットする
        mCategoryAdapter.categoryList = realm.copyFromRealm(categoryRealmResults)

        // CategoryのListView用のアダプタに渡す
        category_search_spinner.adapter = mCategoryAdapter

        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mCategoryAdapter.notifyDataSetChanged()

    }

    override fun onDestroy() {
        super.onDestroy()

        mRealm.close()
    }
}
