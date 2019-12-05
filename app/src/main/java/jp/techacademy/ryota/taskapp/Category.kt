package jp.techacademy.ryota.taskapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.io.Serializable

open class Category : RealmObject(), Serializable {
    var name: String = ""

    // id をプライマリーキーとして設定
    @PrimaryKey
    var id: Int = 0
}