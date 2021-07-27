package lars_lion.dev.o_harid.network.response.bestSeller

data class Object(
    val author: String,
    val description: String,
    val `file`: String,
    val foto: String,
    val id: Int,
    val interested: Int,
    val language: String,
    val like: Boolean,
    val name: String,
    val page_size: Int,
    val price: Double
)