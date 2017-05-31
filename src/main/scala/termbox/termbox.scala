package termbox

import scala.scalanative.native
import scala.scalanative.native._

/**
  * Created by lenstr on 5/30/17.
  */
@link("termbox")
@extern
object termbox {

  object key {
    val F1 = 0xFFFF - 0
    val F2 = 0xFFFF - 1
    val F3 = 0xFFFF - 2
    val F4 = 0xFFFF - 3
    val F5 = 0xFFFF - 4
    val F6 = 0xFFFF - 5
    val F7 = 0xFFFF - 6
    val F8 = 0xFFFF - 7
    val F9 = 0xFFFF - 8
    val F10 = 0xFFFF - 9
    val F11 = 0xFFFF - 10
    val F12 = 0xFFFF - 11
    val INSERT = 0xFFFF - 12
    val DELETE = 0xFFFF - 13
    val HOME = 0xFFFF - 14
    val END = 0xFFFF - 15
    val PGUP = 0xFFFF - 16
    val PGDN = 0xFFFF - 17
    val ARROW_UP = 0xFFFF - 18
    val ARROW_DOWN = 0xFFFF - 19
    val ARROW_LEFT = 0xFFFF - 20
    val ARROW_RIGHT = 0xFFFF - 21
    val MOUSE_LEFT = 0xFFFF - 22
    val MOUSE_RIGHT = 0xFFFF - 23
    val MOUSE_MIDDLE = 0xFFFF - 24
    val MOUSE_RELEASE = 0xFFFF - 25
    val MOUSE_WHEEL_UP = 0xFFFF - 26
    val MOUSE_WHEEL_DOWN = 0xFFFF - 27

    val CTRL_TILDE = 0x00
    val CTRL_2 = 0x00 /* clash with 'CTRL_TILDE' */
    val CTRL_A = 0x01
    val CTRL_B = 0x02
    val CTRL_C = 0x03
    val CTRL_D = 0x04
    val CTRL_E = 0x05
    val CTRL_F = 0x06
    val CTRL_G = 0x07
    val BACKSPACE = 0x08
    val CTRL_H = 0x08 /* clash with 'CTRL_BACKSPACE' */
    val TAB = 0x09
    val CTRL_I = 0x09 /* clash with 'TAB' */
    val CTRL_J = 0x0A
    val CTRL_K = 0x0B
    val CTRL_L = 0x0C
    val ENTER = 0x0D
    val CTRL_M = 0x0D /* clash with 'ENTER' */
    val CTRL_N = 0x0E
    val CTRL_O = 0x0F
    val CTRL_P = 0x10
    val CTRL_Q = 0x11
    val CTRL_R = 0x12
    val CTRL_S = 0x13
    val CTRL_T = 0x14
    val CTRL_U = 0x15
    val CTRL_V = 0x16
    val CTRL_W = 0x17
    val CTRL_X = 0x18
    val CTRL_Y = 0x19
    val CTRL_Z = 0x1A
    val ESC = 0x1B
    val CTRL_LSQ_BRACKET = 0x1B /* clash with 'ESC' */
    val CTRL_3 = 0x1B /* clash with 'ESC' */
    val CTRL_4 = 0x1C
    val CTRL_BACKSLASH = 0x1C /* clash with 'CTRL_4' */
    val CTRL_5 = 0x1D
    val CTRL_RSQ_BRACKET = 0x1D /* clash with 'CTRL_5' */
    val CTRL_6 = 0x1E
    val CTRL_7 = 0x1F
    val CTRL_SLASH = 0x1F /* clash with 'CTRL_7' */
    val CTRL_UNDERSCORE = 0x1F /* clash with 'CTRL_7' */
    val SPACE = 0x20
    val BACKSPACE2 = 0x7F
    val CTRL_8 = 0x7F /* clash with 'BACKSPACE2' */
  }

  object event {
    val KEY = 1
    val RESIZE = 2
    val MOUSE = 3
  }

  object input {
    val CURRENT: Int = 0
    val ESC: Int = 1
    val INPUT: Int = 2
    val MOUSE: Int = 4
  }

  object attr {
    val BOLD = 0x0100
    val UNDERLINE = 0x0200
    val REVERSE = 0x0400
  }

  object color {
    val DEFAULT = 0x00
    val BLACK = 0x01
    val RED = 0x02
    val GREEN = 0x03
    val YELLOW = 0x04
    val BLUE = 0x05
    val MAGENTA = 0x06
    val CYAN = 0x07
    val WHITE = 0x08
  }

  type Event = CStruct8[
    native.UByte, // type
    native.UByte, // mod /* modifiers to either 'key' or 'ch' below */
    native.UShort, // key /* one of the * constants */
    native.UInt, // ch /* unicode character */
    native.CInt, // w
    native.CInt, // h
    native.CInt, // x
    native.CInt // y
  ]

  type Cell = CStruct3[native.UInt, native.UShort, native.UShort]

  object Ops {
    implicit class EventOps(val self: Ptr[Event]) extends AnyVal {
      def `type` = !self._1
      def mod = !self._2
      def key = !self._3
      def ch = !self._4
      def w = !self._5
      def h = !self._6
      def x = !self._7
      def y = !self._8
    }

    implicit class CellOps(val self: Ptr[Cell]) extends AnyVal {
      def ch = !self._1
      def ch_=(v: native.UInt) = !self._1 = v

      def fg = !self._2
      def fg_=(v: native.UShort) = !self._2 = v

      def bg = !self._3
      def bg_=(v: native.UShort) = !self._3 = v
    }
  }

  @name("tb_init")
  def init(): Int = extern

  @name("tb_shutdown")
  def shutdown(): Unit = extern

  @name("tb_width")
  def width(): Int = extern

  @name("tb_height")
  def heigth(): Int = extern

  @name("tb_clear")
  def clear(): Unit = extern

  @name("tb_present")
  def present(): Unit = extern

  @name("tb_select_input_mode")
  def select_input_mode(mode: Int): Int = extern

  @name("tb_poll_event")
  def poll_event(event: Ptr[Event]): Int = extern

  @name("tb_cell_buffer")
  def cell_buffer(): Ptr[Cell] = extern

  @name("tb_put_cell")
  def put_cell(x: Int, y: Int, cell: Ptr[Cell]): Unit = extern

  @name("tb_change_cell")
  def change_cell(x: Int, y: Int, ch: UInt, fg: UShort, bg: UShort): Unit = extern
}
