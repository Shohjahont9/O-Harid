package lars_lion.dev.o_harid.model

data class Comment(
    val id:Int,
    val user_id:Int?,
    val name:String,
    val avatarId: Int?,
    val text:String,
    val date:String,
    val rate:Double,
    val count:Int
)