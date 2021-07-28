package lars_lion.dev.o_harid.network.response.comments

data class Comment(
    val date: String,
    val evaluate: Double,
    val id: Int,
    val text: String,
    val user: User
)