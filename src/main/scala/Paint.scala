import termbox.termbox
import termbox._

import scala.annotation.tailrec
import scala.scalanative.native
import scala.scalanative.native._

@native.extern
object libc {
  def memcpy[T](dest: Ptr[T], src: Ptr[T], n: CSize): Ptr[T] = extern
}

object Paint {

  import termbox.Ops._

  var bbw: Int = 0
  var bbh: Int = 0

  var curCol: Int = 0
  var curRune: Int = 0

  var backbuf: Ptr[Cell] = _

  val runes = Array(
    0x20, //   ' '
    0x2591, // '░'
    0x2592, // '▒'
    0x2593, // '▓'
    0x2588 //  '█'
  )

  val colors = Array(
    color.BLACK,
    color.RED,
    color.GREEN,
    color.YELLOW,
    color.BLUE,
    color.MAGENTA,
    color.CYAN,
    color.WHITE
  )

  def reallocBackBuffer(w: Int, h: Int): Unit = {
    bbw = w
    bbh = h
    if (backbuf != null) {
      stdlib.free(backbuf.cast[Ptr[Byte]])
    }
    backbuf = stdlib.calloc(w * h, sizeof[Cell]).cast[Ptr[Cell]]
  }

  type AttrFunc = (Int) => (UInt, UShort, UShort)

  val runeAttrFunc: AttrFunc = i => {
    (runes(i).toUShort, color.DEFAULT.toUShort, color.DEFAULT.toUShort)
  }

  val colorAttrFunc: AttrFunc = i => {
    (' '.toUShort, color.DEFAULT.toUShort, colors(i).toUShort)
  }

  def updateAndRedrawAll(mx: Int, my: Int): Unit = {
    termbox.clear()
    if (mx != -1 && my != -1) {
      val cell = backbuf + (bbw * my + mx)
      cell.ch = runes(curRune).toUShort
      cell.fg = colors(curCol).toUShort
    }
    libc.memcpy(termbox.cell_buffer(), backbuf, sizeof[Cell] * bbw * bbh)
    val h = termbox.heigth()

    curRune = updateAndDrawButtons(curRune, 0, 0, mx, my, runes.length, runeAttrFunc)
    curCol = updateAndDrawButtons(curCol, 0, h - 3, mx, my, colors.length, colorAttrFunc)

    termbox.present()
  }

  def updateAndDrawButtons(current: Int, x: Int, y: Int, mx: Int, my: Int, n: Int, attrFunc: AttrFunc): Int = {
    var lx = x
    var ly = y

    var cur = current

    (0 until n).foreach { i =>
      if (lx <= mx && mx <= lx + 3 && ly <= my && my <= ly + 1) {
        cur = i
      }

      val (r, fg, bg) = attrFunc(i)

      change_cell(lx + 0, ly + 0, r, fg, bg)
      change_cell(lx + 1, ly + 0, r, fg, bg)
      change_cell(lx + 2, ly + 0, r, fg, bg)
      change_cell(lx + 3, ly + 0, r, fg, bg)
      change_cell(lx + 0, ly + 1, r, fg, bg)
      change_cell(lx + 1, ly + 1, r, fg, bg)
      change_cell(lx + 2, ly + 1, r, fg, bg)
      change_cell(lx + 3, ly + 1, r, fg, bg)

      lx += 4
    }

    lx = x
    ly = y

    (0 until n).foreach { i =>
      if (cur == i) {
        val fg = (color.RED | attr.BOLD).toUShort
        val bg = color.DEFAULT.toUShort
        val caret = '^'.toUInt

        change_cell(lx + 0, ly + 2, caret, fg, bg)
        change_cell(lx + 1, ly + 2, caret, fg, bg)
        change_cell(lx + 2, ly + 2, caret, fg, bg)
        change_cell(lx + 3, ly + 2, caret, fg, bg)
      }
      lx += 4
    }

    cur
  }

  @tailrec
  def loop(mx: Int = -1, my: Int = -1): Unit = {
    updateAndRedrawAll(mx, my)

    val event = native.stackalloc[Event]
    val event_type = termbox.poll_event(event)
    if (event_type == -1) {
      return
    }

    event_type match {
      case termbox.event.KEY =>
        if (event.key != termbox.key.ESC.toUShort) {
          loop()
        }
      case termbox.event.MOUSE =>
        if (event.key == termbox.key.MOUSE_LEFT.toUShort) {
          loop(event.x, event.y)
        } else {
          loop()
        }
      case termbox.event.RESIZE =>
        reallocBackBuffer(event.w, event.h)
        loop()
    }
  }

  def main(args: Array[String]): Unit = {
    val code = termbox.init()
    if (code < 0) {
      stdio.fprintf(stdio.stderr, c"termbox init failed, code: %d\n", code)
      return
    }

    termbox.select_input_mode(termbox.input.ESC | termbox.input.MOUSE)

    val w = termbox.width()
    val h = termbox.heigth()
    reallocBackBuffer(w, h)

    loop()

    termbox.shutdown()
  }
}
