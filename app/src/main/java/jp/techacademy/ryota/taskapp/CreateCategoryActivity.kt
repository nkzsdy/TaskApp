package jp.techacademy.ryota.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_category.*

class CreateCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        done_create_category.setOnClickListener {
            createCategory()
            finish()
        }
    }

    private fun createCategory() {
        val realm = Realm.getDefaultInstance()

        val categoryRealmResults = realm.where(Category::class.java).findAll()
        val category = Category()

        val newCategoryName = new_category_name.text.toString()
        val newCategoryId = categoryRealmResults.max("id")!!.toInt() + 1

        category.name = newCategoryName
        category.id = newCategoryId

        val isNotUniqueName = realm.where(Category::class.java).equalTo("name", category.name).findAll().isNotEmpty()
        var message = ""
        val duration = Toast.LENGTH_SHORT

        when {
            category.name.isBlank() -> {
                message = "カテゴリ名を入力してください"
                Toast.makeText(applicationContext, message, duration).show()
            }
            isNotUniqueName -> {
                message = "すでに存在するカテゴリ名です"
                Toast.makeText(applicationContext, message, duration).show()
            }
            else -> {
                realm.beginTransaction()
                realm.copyToRealmOrUpdate(category)
                realm.commitTransaction()
                realm.close()
            }
        }
    }
}
