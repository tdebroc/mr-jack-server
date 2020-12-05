import {Component, OnInit} from '@angular/core';
import {DataServiceService} from "../dataService/data-service.service";
import {Position} from "../model/position";
import {DetectiveAndPosition} from "../model/detectiveAndPosition";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
// Link helper:
// https://github.com/tdebroc/ia-server-robotturtles/blob/master/src/main/webapp/app/home/home.controller.js
export class HomeComponent implements OnInit {

  currentAction: string | undefined = undefined
  actionToImageName: Record<string, string> = {
    "JokerAction$": "JOKER",
    "MoveSherlockAction$": "SHERLOCK",
    "MoveTobbyAction$": "TOBBY",
    "MoveWatsonAction$": "WATSON",
    "SwapDistrictAction$": "SWAP_DISTRICT",
    "RotateAction$": "ROTATE",
    "AlibiCardAction$": "ALIBI"
  }
  orientations = ["NORTH", "WEST", "SOUTH", "EAST"]
  detectivePositions: Position[] = [
    new Position(0, 1), new Position(0, 2), new Position(0, 3), // TOP
    new Position(1, 4), new Position(2, 4), new Position(3, 4), // RIGHT
    new Position(4, 3), new Position(4, 2), new Position(4, 1), // BOTTOM
    new Position(3, 0), new Position(2, 0), new Position(1, 0) // LEFT
  ]
  detectiveMovePossiblePosition: DetectiveAndPosition[] | undefined = undefined;


  constructor(private dataService: DataServiceService) {
    this.dataService.loadGames(this.selectFirstGame.bind(this));
    this.refreshUI()
  }

  ngOnInit() {
  }

  createGame() {
    this.dataService.createGame();
  }

  getGames() {
    return this.dataService.getGames()
  }

  getCurrentActionIndex() {
    let currentGame = this.getCurrentGame()
    return currentGame.actionTokens
      .filter(action => !action.used)
      .findIndex(action => action.currentAction.actionName == this.currentAction)
  }

  isCurrentGameDefined() {
    return this.dataService.currentGame;
  }

  getCurrentGame() {
    if (!this.dataService.currentGame) throw 'Game can\'t be null';
    return this.dataService.currentGame;
  }

  getCurrentGameId() {
    if (!this.dataService.currentGameId) throw 'currentGameId can\'t be null';
    return this.dataService.currentGameId;
  }

  // ===================================================================================================
  // = Game Register
  // ===================================================================================================

  isGameStarted() {
    return this.getCurrentGame().mrJackRegistered && this.getCurrentGame().detectiveRegistered
  }

  registerPlayer(isMrJack: boolean) {
    this.dataService.registerPlayer("Player", isMrJack, this.getCurrentGameId())
  }


  amIMrJack() {
    return this.dataService.currentPlayers[this.dataService.getPlayerKey(this.getCurrentGameId(), true)]
  }

  amIDetective() {
    return this.dataService.currentPlayers[this.dataService.getPlayerKey(this.getCurrentGameId(), false)]
  }

  amIamCurrentPlayer() {
    return this.amIMrJack() && !this.isTurnOfDetective() || this.amIDetective() && this.isTurnOfDetective()
  }

  isTurnOfDetective() {
    return this.getCurrentGame().detectiveCurrentPlayer
  }

  getSecret(isMrJack: string) {
    return this.dataService.secrets[isMrJack]
  }

  // ===================================================================================================
  // = Display
  // ===================================================================================================

  refreshUI() {
    if (this.isCurrentGameDefined()) {
      this.dataService.refreshCurrentGame(this.getCurrentGameId())
    }
    setTimeout(this.refreshUI.bind(this), 1500)
  }

  isCurrentGameId(game: any) {
    return this.dataService.currentGameId && game.key === this.getCurrentGameId()
  }

  getDistrictImageName(district: District) {
    return (district.recto ? district.alibiNameAsString : "empty")
      + (district.cross ? "-cross" : "")
      + "-district";
  }

  getTokenImageName(actionName: string) {
    return this.actionToImageName[actionName]
  }

  getPlayableTokens() {
    return this.getCurrentGame().actionTokens.filter(t => !t.used);
  }

  // ===================================================================================================
  // = Events
  // ===================================================================================================

  clickToken(actionName: string) {
    this.currentAction = actionName;
    console.log("Playing: " + this.currentAction)
    if (this.isMovingDetectiveAction()) {
      this.clickDetectiveAction()
    } else if (this.isAlibiCardAction()) {
      this.playAlibiCard();
    }
  }

  selectGame(gameKey: string) {
    this.dataService.refreshCurrentGame(gameKey)
  }

  selectFirstGame() {
    let firstKey = Object.keys(this.dataService.getGames())[Object.keys(this.dataService.getGames()).length - 1]
    this.selectGame(firstKey)
  }

  // ===================================================================================================
  // = Cell Utils
  // ===================================================================================================

  clickOnDistrict(lineIndex: number, colIndex: number) {
    if (this.isSwapDistrictAction()) {
      this.selectDistrictToSwap(lineIndex, colIndex)
    } else if (this.isRotateAction()) {
      this.selectDistrictToRotate(lineIndex, colIndex)
    }
  }

  isDistrictPointer() {
    return this.isSwapDistrictAction() || this.isRotateAction();
  }

  // ===================================================================================================
  // = Swap District
  // ===================================================================================================
  positionDistrictSelect1: Position | undefined = undefined
  positionDistrictSelect2: Position | undefined = undefined

  isSwapDistrictAction() {
    return this.currentAction === "SwapDistrictAction$"
  }

  isSelectedDistrict(lineIndex: number, colIndex: number) {
    return this.areEquals(new Position(lineIndex, colIndex), this.positionDistrictSelect1)
      || this.areEquals(new Position(lineIndex, colIndex), this.positionDistrictSelect2)
  }

  private selectDistrictToSwap(lineIndex: number, colIndex: number) {
    let clickedDistrict = new Position(lineIndex, colIndex)
    if (this.areEquals(this.positionDistrictSelect1, clickedDistrict)) {
      this.positionDistrictSelect1 = undefined;
    } else if (this.areEquals(this.positionDistrictSelect2, clickedDistrict)) {
      this.positionDistrictSelect2 = undefined;
    } else if (!this.positionDistrictSelect1) {
      this.positionDistrictSelect1 = clickedDistrict
    } else if (!this.positionDistrictSelect2) {
      this.positionDistrictSelect2 = clickedDistrict
    }
  }

  playSwapDistrict() {
    if (!this.positionDistrictSelect1 || !this.positionDistrictSelect2) {
      alert("You need to select 2 districts before.")
      return;
    }
    let moveDetails = this.getCurrentActionIndex() + "_" +
      this.positionToDistrictIndex(this.positionDistrictSelect1) + "-" +
      this.positionToDistrictIndex(this.positionDistrictSelect2);
    this.sendAction(moveDetails)
  }

  positionToDistrictIndex(position: Position) {
    return 3 * (position.line - 1) + (position.col - 1);
  }

  // ===================================================================================================
  // = Rotate District Action
  // ===================================================================================================
  rotateDistrictPosition: Position | undefined = undefined
  rotateOrientation: string = ""

  isRotateAction() {
    return this.currentAction && this.currentAction.includes("RotateAction$")
  }

  private selectDistrictToRotate(lineIndex: number, colIndex: number) {
    let newPos = new Position(lineIndex, colIndex)
    if (!this.areEquals(newPos, this.rotateDistrictPosition)) {
      this.rotateDistrictPosition = newPos
      this.rotateOrientation = this.findDistrictOrientation(lineIndex, colIndex)
    }
    let orientationIndex = this.orientations.indexOf(this.rotateOrientation)
    this.rotateOrientation = this.orientations[(orientationIndex + 4 - 1) % 4]

    if (this.rotateOrientation === this.findDistrictOrientation(lineIndex, colIndex)) {
      this.rotateDistrictPosition = undefined;
    }
  }

  isRotating(orientation: string, lineIndex: number, colIndex: number) {
    return this.isDistrictBeingRotated(lineIndex, colIndex) && orientation === this.rotateOrientation
  }

  findDistrictOrientation(lineIndex: number, colIndex: number) {
    let orientation = this.getCurrentGame().board.cells[lineIndex][colIndex].districts.orientationAsString;
    if (!orientation) throw Error("Game is wrong")
    return orientation
  }

  isDistrictBeingRotated(lineIndex: number, colIndex: number) {
    return this.areEquals(this.rotateDistrictPosition, new Position(lineIndex, colIndex));
  }

  playRotateDistrict() {
    if (!this.rotateDistrictPosition) {
      alert("You should select a district.")
      return;
    }
    let moveDetails = this.getCurrentActionIndex() + "_" +
      this.positionToDistrictIndex(this.rotateDistrictPosition) + "-" + this.rotateOrientation;
    this.sendAction(moveDetails)
  }

  // ===================================================================================================
  // = Alibi Card
  // ===================================================================================================

  playAlibiCard() {
    this.sendAction(this.getCurrentActionIndex() + "_",
      (data) => {
        console.log(data)
      })
  }

  isAlibiCardAction() {
    return this.currentAction === "AlibiCardAction$"
  }


  // ===================================================================================================
  // = Move Detective
  // ===================================================================================================

  clickDetectiveAction() {
    this.detectiveMovePossiblePosition = []
    if (this.currentAction && this.currentAction.includes("Joker")) {
      this.addDetectivePossibleMove("SHERLOCK", 1)
      this.addDetectivePossibleMove("WATSON", 1)
      this.addDetectivePossibleMove("TOBBY", 1)
    } else {
      let detectiveName = this.currentAction ? this.currentAction
        .replace("Move", "")
        .replace("Action$", "") : undefined
      this.addDetectivePossibleMove(detectiveName, 2)
    }
    console.log(this.detectiveMovePossiblePosition)
  }

  addDetectivePossibleMove(detectiveName: string | undefined, numberOfDetective: number) {
    if (!detectiveName) throw Error("detectiveName shouldn't be undefined.")
    let detectivePosition = this.getDetectivePosition(detectiveName);
    let positionIndex = this.getDetectivePositionIndex(detectivePosition);
    if (typeof positionIndex === "undefined") throw Error("position should have been found")
    for (let i = 1; i <= numberOfDetective; i++) {
      let nextIndex = positionIndex + i;
      let pos = this.detectivePositions[(nextIndex) % this.detectivePositions.length];
      if (this.detectiveMovePossiblePosition) {
        this.detectiveMovePossiblePosition.push(
          new DetectiveAndPosition(pos.line, pos.col, detectiveName, i)
        );
      }
    }
  }

  getDetectivePosition(detectiveName: string) {
    let positionFound = undefined
    this.getCurrentGame().board.cells.forEach((line, lineIndex) => {
      line.forEach((cell, colIndex) => {
        if (cell.detectives && cell.detectives.filter(d => d.name.toUpperCase() === detectiveName.toUpperCase()).length > 0) {
          positionFound = new Position(lineIndex, colIndex);
        }
      });
    });
    if (!positionFound) throw Error()
    return positionFound;
  }

  getDetectivePositionIndex(position: Position) {
    let indexFound = undefined;
    this.detectivePositions.forEach((pos, index) => {
      if (pos.line === position.line && position.col === pos.col) {
        indexFound = index
      }
    })
    return indexFound;
  }

  areEquals(object1: any, object2: any) {
    return JSON.stringify(object1) === JSON.stringify(object2)
  }

  isItJokerAction() {
    return this.currentAction === "JokerAction$"
  }

  isMovingDetectiveAction() {
    return this.currentAction ? ["MoveSherlockAction$", "MoveTobbyAction$", "MoveWatsonAction$", "JokerAction$"].includes(this.currentAction) : false;
  }

  getBonusDetective(lineIndex: number, colIndex: number) {
    if (!this.isMovingDetectiveAction() || !this.detectiveMovePossiblePosition) return undefined;
    let founds = this.detectiveMovePossiblePosition.filter(p => p.line === lineIndex && p.col === colIndex)
    return founds.length > 0 ? founds[0] : undefined;
  }

  playMoveDetective(bonusDetective: DetectiveAndPosition) {
    console.log("Playing " + JSON.stringify(bonusDetective));
    let actionDetails = this.getCurrentActionIndex() + "_"
    actionDetails += this.isItJokerAction() ? bonusDetective.name.toUpperCase() : bonusDetective.moveCount
    this.sendAction(actionDetails)
  }

  sendAction(actionDetails: string, callback: undefined | ((data: MessageResponse) => void) = undefined) {
    this.dataService.playAction(this.getCurrentGameId(),
      !this.getCurrentGame().detectiveCurrentPlayer,
      actionDetails,
      (data) => {
        this.currentAction = undefined;
        this.positionDistrictSelect1 = undefined
        this.positionDistrictSelect2 = undefined
        this.rotateDistrictPosition = undefined
        if (callback) callback(data)
      })
  }


  isEnded() {
    return this.getCurrentGame() && this.getCurrentGame().winner
  }

  getHourGlasses() {
    return this.arrayOne(this.getCurrentGame().mrJack.turnTokenCount)
  }

  arrayOne(n: number | undefined): any[] {
    return Array(n);
  }


  countMrJackHourGlasses() {
    let countHourGlass = 0;
    if (this.getSecret('true') && this.getSecret('true').alibiCards.length > 0) {
      countHourGlass += this.getSecret('true').alibiCards.map(a => a.hourGlass).reduce((a, b) => a + b)
    }
    return this.getHourGlasses().length + countHourGlass
  }


}

