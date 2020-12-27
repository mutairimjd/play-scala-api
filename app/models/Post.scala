package models

import play.api.libs.json._

case class Post(id: Long, nickname: String, post: String)

object Post {
  implicit val postFormat = Json.format[Post]
}
