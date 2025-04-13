package dev.bswanson.hackernews.model

data class Submission(
    val by: String = "",
    val id: ID = -1,
    val parent: ID? = null,
    val text: String = "",
    /** Unix timestamp in seconds */
    val time: Long = -1,
    val title: String = "",
    /** "job", "story", "comment", "poll", or "pollopt" */
    val type: String = "",
    /** The total comment count (present if [type] is "story" or "poll") */
    val descendants: Int? = -1,
    /** Comments (if [type] is "story" or "poll") or replies (if [type] is "comment") */
    val kids: List<ID>? = null,
    /** Poll options, if [type] == "poll" */
    val parts: List<ID>? = null,
    val url: String? = null,
    val score: Int? = null,
    /** The item's parent poll if [type] == "pollopt" */
    val poll: ID? = null
)