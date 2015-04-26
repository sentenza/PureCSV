# PureCSV #

PureCSV is a Scala library for conversion from and to the CSV format. The library gets rid of most of the boilerplate
required to work with the CSV format

```scala
scala> import purecsv.unsafe._
scala> case class Event(ts: Long, msg: String)
scala> val records = CSVReader[Event].readCSVFromString("1,foo\n2,bar")
records: List[Event] = List(Event(1,foo), Event(2,bar))
scala> records.toCSV()
res0: String =
1,"foo"
2,"bar
```

In this example, a case class called `Event` is defined and immidiately used to read to and write from CSV.


## Writing to CSV ##

The simplest way possible to its CSV representation is to call `toCSV`

```scala
scala> import purecsv.safe._
scala> class Interval(val start: Long, val end: Long)
scala> new Interval(10,20).toCSV()
res1: String = 1,10
scala> Seq(new Interval(1,10),new Interval(11,20)).toCSV("|")
res2: String =
1|10
11|20
```

An utility method to write to file is also provided

```scala
scala> import purecsv.unsafe._
scala> class Interval(val start: Long, val end: Long) { override def toString: String = s"Interval($start,$end)" }
scala> Seq(new Interval(1,10),new Interval(11,20)).writeCSVToFile("/tmp/example.csv")
scala> scala.io.Source.fromFile("/tmp/example.csv").getLines.toList
res0: List[String] = List(1,10, 11,20)
scala> CSVReader[Interval].readCSVFromFile("/tmp/example.csv")
res2: List[Interval] = List(Interval(1,10), Interval(11,20)
```


## Reading from CSV ##

Reading from CSV is a bit different from writing to CSV. There are two different ways to read from CSV Strings, one
safe and the other unsafe. See next paragraph for the difference. Other than that, a `CSVReader` instance should be
used to read data

```scala
scala> import purecsv.unsafe._
scala> class Interval(val start: Long, val end: Long) { override def toString: String = s"Interval($start,$end)" }
scala> CSVReader[Interval].readCSVFromFile("/tmp/example.csv")
res2: List[Interval] = List(Interval(1,10), Interval(11,20)
```

If the source has a header, it is possible to skip it.

```scala
scala> import purecsv.unsafe._
scala> case class Address(name: String, address: String)
scala> Seq(Address("alice","wonderland")).writeCSVToFileName("/tmp/example.csv", header=Some(Seq("name","address")))
scala> scala.io.Source.fromFile("/tmp/example.csv").getLines.toList
res2: List[String] = List(name,address, "alice","wonderland"
scala> CSVReader[Address].readCSVFromFileName("/tmp/example.csv", skipHeader=true)
res1: List[Address] = List(Address(alice,wonderland)
```


## Reading from CSV: Safe vs Unsafe ##

When reading values from String, the library comes in to flavors: one safe and one unsafe. Only one should be imported.
The safe flavor captures errors

```scala
scala> import purecsv.safe._
scala> case class Person(name: String, age: Int)
scala> CSVReader[Person].readCSVFromString("alice,1")
res0: List[scala.util.Try[Person]] = List(Success(Person(alice,1)))
scala> CSVReader[Person].readCSVFromString("alice,nan")
res1: List[scala.util.Try[Person]] = List(Failure(java.lang.NumberFormatException: For input string: "nan"))
```

The unsafe flavor, instead, ignores errors

```scala
scala> import purecsv.safe._
scala> case class Person(name: String, age: Int)
scala> CSVReader[Person].readCSVFromString("alice,1")
res0: List[Person] = List(Person(alice,1))
scala> scala> CSVReader[Person].readCSVFromString("alice,nan")
java.lang.NumberFormatException: For input string: "nan"
  at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
  at java.lang.Integer.parseInt(Integer.java:492)
  ...
```

The two flavors solve different problems: the safe one helps dealing with inputs that could potentially be not well
formatted or event erroneous while the unsafe flavor reads data that is well formatted, for instance the one created
by this library.

The safe flavor has also another characteristic: it helps the user understanding which records gave error by collecting
them

```scala
scala> val result = CSVReader[Person].readCSVFromString("alice,1\nbob,nan\ncharlie,2\ndelta,\n")
result: List[scala.util.Try[Person]] = List(Success(Person(alice,1)), Failure(java.lang.NumberFormatException: For input string: "nan"), Success(Person(charlie,2)), Failure(java.lang.NumberFormatException: For input string: ""))
scala> import purecsv.safe.tryutil._
scala> val (successes,failures) = result.getSuccessesAndFailures
successes: List[(Int, Person)] = List((1,Person(alice,1)), (3,Person(charlie,2)))
failures: List[(Int, Throwable)] = List((2,java.lang.NumberFormatException: For input string: "nan"), (4,java.lang.NumberFormatException: For input string: ""))
```

The method `getSuccessesAndFailures` returns a pair where the first element are the successes and the second element
are the failures with associated the record number. From the information above we know that record 1 and 3 were read
successfully while record 2 and 4 were not.


## How it works ##

The library is based on (Shapeless)[https://github.com/milessabin/shapeless] Generic system. Everything that has a
Generic instance can be used with PureCSV. A Generic instance defines a representation for a given type in terms of
[heterogenous lists](https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#heterogenous-lists)
and function to convert from/to that representation

```scala
scala> import shapeless._
scala> case class Foo(i: Int)
scala> :t Generic[Foo]
shapeless.Generic[Foo]{type Repr = shapeless.::[Int,shapeless.HNil]}
scala> Generic[Foo].to(Foo(1))
res4: shapeless.::[Int,shapeless.HNil] = 1 :: HNil
```

Note that also structures that don't have an automatically generated `Generic` instance can be used by manually define
the `Generic`. So given a type

```scala
class Event2(val ts: Long, var msg: String) {
  override def equals(o: Any): Boolean = o match {
    case other:Event2 => (this.ts ## other.ts && this.msg ## other.msg)
    case _ => false
  }
  override def toString: String = s"Event($ts, $msg)"
}
```

One can define the `Generic` instance as

```scala
implicit val fooGeneric = new Generic[Event2] {
  override type Repr = Long :: String :: HNil
  override def from(r: Repr): Event2 = {
    val ts :: msg :: HNil = r
    new Event2(ts, msg)
  }
  override def to(t: Event2): Repr = t.ts :: t.msg :: HNil
}
```

And then use that structure with PureCSV

```scala
scala> val conv = RawFieldsConverter[Event2]
scala> conv.to(new Event2(1,"foo")) should contain theSameElementsInOrderAs(Seq("1","\"foo\""))
```


## Special Thanks ##

To the [Shapeless](https://github.com/milessabin/shapeless) developers for their amazing library.
