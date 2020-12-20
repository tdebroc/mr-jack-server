import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Game} from "../model/game";
import {SecretResponse} from "../model/SecretResponse";
import {PlayerSecret} from "../model/PlayerSecret";

@Injectable({
  providedIn: 'root'
})
export class DataServiceService {
  private CURRENT_PLAYERS_LS_KEY: string = "currentPlayers";
  games: any
  currentGame: Game | undefined = undefined
  currentGameId: string | undefined = undefined
  currentPlayers: Record<string, string> = {}
  secrets: Record<string, PlayerSecret> = {}

  constructor(private http: HttpClient) {
    this.loadCurrentPlayers();
  }


  getUrlPrefix() {
    return window.location.host.includes("localhost") ?
      "http://localhost:9000" : ""
  }

  loadGames(callback: undefined | (() => void) = undefined) {
    this.http.get(this.getUrlPrefix() + '/games')
      .subscribe((data) => {
        this.games = data;
        this.games.sort().reverse()
        if (callback) callback()
      });
  }

  createGame() {
    this.http.post(this.getUrlPrefix() + '/game', {})
      .subscribe((gameId: any) => {
        console.log("Game created : " + gameId)
        this.refreshCurrentGame(gameId)
        this.loadGames()
      });
  }

  refreshCurrentGame(gameId: string,
                     callback: undefined | (() => void) = undefined) {
    this.http.get(this.getUrlPrefix() + '/game/' + gameId)
      .subscribe((data: any) => {
        if (this.areEquals(this.currentGame, data) && gameId === this.currentGameId) return;

        this.currentGame = data;
        this.currentGameId = gameId;
        this.refreshSecrets(gameId)
        console.log("Current Game is ", this.currentGame)
        if (callback) callback()
      });
  }

  areEquals(obj1: any, obj2: any) {
    return JSON.stringify(obj1) == JSON.stringify(obj2)
  }

  getSecretId(gameId: string, isMrJack: boolean) {
    let secretId = this.currentPlayers[this.getPlayerKey(gameId, isMrJack)]
    if (!secretId) {
      alert("This is not your turn. Be patient, your turn will come !")
      return false;
    }
    return secretId
  }

  refreshSecrets(gameId: string) {
    this.secrets = {}
    this.getCurrentPlayersOfGame(gameId)
      .forEach(secretId => {
        this.getSecret(gameId, secretId, (secret: PlayerSecret) => {
          this.secrets["" + secret.mrJack] = secret;
        })
      })
  }

  playAction(gameId: string, isMrJackPlaying: boolean, action: string, callback?: (data: MessageResponse) => void) {
    console.log("Sending action : " + action)
    let secretId = this.getSecretId(gameId, isMrJackPlaying)
    if (!secretId) return;
    this.http
      .post(this.getUrlPrefix() + '/playAction?secretId=' + secretId, {gameId, action})
      .subscribe((data: any) => {
        this.refreshCurrentGame(gameId)
        if (callback) callback(data)
      });
  }


  getGames() {
    return this.games
  }

  registerPlayer(pseudo: string, isMrJack: boolean, currentGameId: string, callback?: (data: SecretResponse) => void) {
    this.http
      .post(this.getUrlPrefix() + '/register',
        {pseudo, isMrJack, gameId: currentGameId})
      .subscribe((data: any) => {
        let key = this.getPlayerKey(currentGameId, isMrJack);
        this.currentPlayers[key] = data.secretId;
        localStorage.setItem(this.CURRENT_PLAYERS_LS_KEY, JSON.stringify(this.currentPlayers));
        this.refreshCurrentGame(currentGameId);
        if (callback) callback(data)
      });
  }

  registerAIPlayer(aiLevel: string, isMrJack: boolean, currentGameId: string) {
    this.http
      .post(this.getUrlPrefix() + '/registerAI',
        {aiLevel, isMrJack, gameId: currentGameId})
      .subscribe(() => {
        this.refreshCurrentGame(currentGameId);
      });
  }

  getSecret(gameId: string, secretId: string, callback: undefined | ((data: any) => void) = undefined) {
    this.http
      .get(this.getUrlPrefix() + '/game/' + gameId + "/secret?secretId=" + secretId)
      .subscribe((data: any) => {
        if (callback) callback(data)
      });
  }

  // ================================================================================================
  // = Current Players
  // ================================================================================================

  loadCurrentPlayers() {
    let currentPlayerString = localStorage.getItem(this.CURRENT_PLAYERS_LS_KEY);
    if (!currentPlayerString) {
      this.currentPlayers = {};
    } else {
      this.currentPlayers = JSON.parse(currentPlayerString);
    }
  }

  getPlayerKey(idGame: string, isMrJack: boolean) {
    return idGame + "#" + isMrJack;
  }

  getCurrentPlayersOfGame(gameId: string) {
    return Object.keys(this.currentPlayers)
      .filter(key => key.startsWith(gameId))
      .map(key => this.currentPlayers[key])
  }


}
