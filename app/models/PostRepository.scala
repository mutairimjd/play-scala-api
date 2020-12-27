package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}

@Singleton
class PostRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(
    implicit ec: ExecutionContext
) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  private class PostTable(tag: Tag) extends Table[Post](tag, "posts") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def nickname = column[String]("nickname")

    def post = column[String]("post")

    def * = (id, nickname, post) <> ((Post.apply _).tupled, Post.unapply)
  }

  private val posts = TableQuery[PostTable]

  def create(nickname: String, post: String): Future[Post] = db.run {
    (posts.map(p => (p.nickname, p.post))
      returning posts.map(_.id)

      into ((nicknamePost, id) => Post(id, nicknamePost._1, nicknamePost._2))) += (nickname, post)
  }

  def list(): Future[Seq[Post]] = db.run {
    posts.result
  }
}
