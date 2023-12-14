package purecsv.config

sealed trait Headers

object Headers {
  object ReadAndIgnore extends Headers
  object None          extends Headers
  object ParseHeaders  extends Headers
}
