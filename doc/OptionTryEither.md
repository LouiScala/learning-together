# Option

Purpose:
* Denote the fact that there might be "null"
* Error handling in a functional way

Creating an option:

```Scala
val greeting: Option[String] = Some("Hello!")
val greeting: Option[String] = None
val greeting = Option("Hello!")
val greeting = Option(null)
```

..or else?

```Scala
greeting.getOrElse("Herzlich Wilkommen!")
greeting.getOrElse(superComplexDefaultGreeting())
```

..match
```Scala
greeting match {
	case Some(s) => s
	case None => "Dzień Dobry!"
}
```

.. quite popular in Scala API
```Scala
val m = Map(1->11, 2->22, 4->44)
m.get(1)
m.get(3)
m.getOrElse(3, "99")
```

.. for safe side effects
```Scala
m.get(3) foreach {println(_)}
```

..using combinators (functional way?)
```Scala
case class User(name: String, age: Option[Int])
val t = User("Tomek", Some(40))
val j = User("John", None)
val c = User("Craig", None)
val users = Map(1->u, 2->j,3->c)

users.get(1) map (_.name)
users.get(1) map (_.age)
users.get(4) map (_.age)

user.get(1) filter (_name.startsWith("T"))
```

.. for comprehensions
```Scala
for {
	id <- Range(1,10)
	u <- users.get(id)
	age <- u.age
} yield age

val ages = for {
	u <- users.values
	a <- u.age
} yield a
val avgAge = ages.sum / ages.size

val ages = for {
	(_, Some(a)) <- users.values
} yield(a)
```

.. chaining
```Scala
def userConfig(key: String): Option[String] = ...
def globalConfig(key: String): Option[String] = ...
def defaultConfig(key: String): Option[String] = ...

val key = "some_key"
val configValue = userConfig(key) orElse globalConfig(key) orElse defaultConfig(key)
```

# Error handling with Try

Try[A] is a computation that may result in value of type A or in an exception. Present since 2.10, backported to 2.9.3.
* Success[A] or
* Failure[A] (wrapping a Throwable)

```Scala
Try(1/0)
Try{1/0}

import scala.util.Try
import java.net.URL

def parseURL(url: String): Try[URL] = Try(new URL(url))

parseURL("http://www.datastax.com")
parseURL("invalid")
```

Note: We use apply() which expects a "by name" parameter (calculated lazily).

.. check if success

```Scala
parseURL("invalid").isSuccess
````

.. getOrElse
```Scala
parseURL("invalid").getOrElse("www.duckduckgo.com")
```

.. chaining
```Scala
import java.io.InputStream
def inputStreamForURL(url: String): Try[InputStream] = parseURL(url).flatMap { u =>
	Try(u.openConnection()).flatMap(conn => Try(conn.getInputStream))
}
```

.. or better with for comprehensions:

```Scala
import scala.io.Source
def getURLContent(url: String): Try[Iterator[String]] =
for {
      url <- parseURL(url)
      connection <- Try(url.openConnection())
      is <- Try(connection.getInputStream)
      source = Source.fromInputStream(is)
} yield source.getLines()

getURLContent("http://example.org") match {
case Success(lines)
case Failure(ex)
}
```

.. it is possible to recover from failures
```Scala
import java.net.MalformedURLException
import java.io.FileNotFoundException
val content = getURLContent("garbage") recover {
	case e: FileNotFoundException => Iterator("Requested page does not exist")
	case e: MalformedURLException => Iterator("Please make sure to enter a valid URL")
	case _ => Iterator("An unexpected error has occurred. We are so sorry!")
}
```

# Either
```Scala
sealed abstract class Either[+A, +B] extends AnyRef
final case class Left[+A, +B](a: A) extends Either[A, B]...
final case class Right[+A, +B](b: B) extends Either[A, B]...
```

Convention:
* Left denotes failure,
* Right denotes success

..one should use projections
```Scala
val l: Either[String, Int] = Left("flower")
val r: Either[String, Int] = Right(12)
l.left.map(_.size): Either[Int, Int] // Left(6)
r.left.map(_.size): Either[Int, Int] // Right(12)
l.right.map(_.toDouble): Either[String, Double] // Left("flower")
r.right.map(_.toDouble): Either[String, Double] // Right(12.0)
```

..is usefull for returning failures with additional data
```Scala
case class Customer(age: Int)
class Cigarettes
case class UnderAgeFailure(age: Int, required: Int)

def buyCigarettes(customer: Customer): Either[UnderAgeFailure, Cigarettes] =
	if (customer.age < 16) Left(UnderAgeFailure(customer.age, 16))
	else Right(new Cigarettes)
```

..or to work on collections
```Scala
import java.net.URL

type Citizen = String
case class BlackListedResource(url: URL, visitors: Set[Citizen])

val blacklist = List(
	BlackListedResource(new URL("https://google.com"), Set("John Doe", "Johanna Doe")),
	BlackListedResource(new URL("http://yahoo.com"), Set.empty),
	BlackListedResource(new URL("https://maps.google.com"), Set("John Doe")),
	BlackListedResource(new URL("http://plus.google.com"), Set.empty)
)

val checkedBlacklist: List[Either[URL, Set[Citizen]]] =
	blacklist.map(resource =>
		if (resource.visitors.isEmpty) Left(resource.url)
		else Right(resource.visitors))

val suspiciousResources = checkedBlacklist.flatMap(_.left.toOption)
val problemCitizens = checkedBlacklist.flatMap(_.right.toOption).flatten.toSet
```

.. fold[TODO]

# Exceptions in Scala

```Scala
try {
	operation()
} catch {
	case _ => ...
}
```

is almost always wrong, as it would catch fatal errors that need to be propagated. Instead, use the com.twitter.util.NonFatal extractor to handle only nonfatal exceptions.

```Scala
try {
	operation()
} catch {
	case NonFatal(exc) => ...
}
```

# Conclusions

What to use when:
* use 'Option[T]' to denote no value is possible
* use Try[T] to handle unexpected exceptions
* use Either[T] if you know your operation may be failure or success

# Next

Scalaz does it better

# Links

http://danielwestheide.com/blog/2012/12/19/the-neophytes-guide-to-scala-part-5-the-option-type.html
http://danielwestheide.com/blog/2012/12/26/the-neophytes-guide-to-scala-part-6-error-handling-with-try.html
https://tersesystems.com/2012/12/27/error-handling-in-scala/
http://twitter.github.io/effectivescala/#Error%20handling-Handling%20exceptions
https://tonymorris.github.io/blog/posts/scalaoption-cheat-sheet/
