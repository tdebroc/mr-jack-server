package controllers

import com.fasterxml.jackson.databind._
import com.grooptown.mrjack.game.Game
import javax.inject._
import play.api.mvc._
import play.mvc

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def getGame : mvc.Result = {
    val res: String = new ObjectMapper().writeValueAsString(new Game)
    play.mvc.Results.ok(res)
  }
}
