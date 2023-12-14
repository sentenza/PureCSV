package purecsv.config

sealed trait Trimming {
  def trim(s: String): String
}

object Trimming {
  object NoAction extends Trimming {
    override def trim(s: String): String = s
  }

  object TrimEmpty extends Trimming {
    override def trim(s: String): String = if (s.matches("\\s+")) s.trim else s
  }

  object TrimAll extends Trimming {
    override def trim(s: String): String = s.trim
  }
}
