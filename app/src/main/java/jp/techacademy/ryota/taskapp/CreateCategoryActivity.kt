package jp.techacademy.ryota.taskapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_create_category.*
import kotlinx.android.synthetic.main.content_input.*

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

        realm.beginTransaction()
        val categoryRealmResults = realm.where(Category::class.java).findAll()
        val category = Category()

        if (categoryRealmResults.size == 0) {
            category.id = 0
            category.name = ""
            realm.copyToRealmOrUpdate(category)
            realm.commitTransaction()
        }

        val newCategoryName = new_category_name.text.toString()
        val newCategoryId = categoryRealmResults.max("id")!!.toInt() + 1

        category.name = newCategoryName
        category.id = newCategoryId

        realm.copyToRealmOrUpdate(category)
        realm.commitTransaction()

        realm.close()
    }
}
