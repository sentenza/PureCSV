package purecsv

import java.io._

package object util {
  /**
   * Utility function to serialize an object and immediately deserialize it back.
   *
   * @param obj an object of type T, should be Serializable
   * @return the object of type T after being serialized and deserialized back
   */
  def serializeAndDeserialize[T](obj: T): T = {
    val baos = new ByteArrayOutputStream()
    val oos = new ObjectOutputStream(baos)
    oos.writeObject(obj)

    new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray))
      .readObject()
      .asInstanceOf[T]
  }
}
