export class DetectiveAndPosition {
  constructor(line: number, col: number, name: string, moveCount: number) {
    this.line = line;
    this.col = col;
    this.name = name
    this.moveCount = moveCount
  }

  line: number = 0
  col: number = 0
  name: string = ""
  moveCount: number = 0
}
