package purecsv.util

import java.io._
import java.nio.charset.StandardCharsets

object FileUtil {
  def createReader(f: File): Reader = {
    new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))
  }
}
