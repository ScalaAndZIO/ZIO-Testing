import java.util.concurrent.TimeUnit

import Test._
import zio._
import zio.console._
import zio.duration.Duration
import zio.test.Assertion._
import zio.test._
import zio.test.environment.{TestClock, TestConsole}

import scala.util.Try

object Test {
  def add(x: Int, y: Int): ZIO[Any, Nothing, Int] = IO.succeed(x + y)

  def sayHello: ZIO[Console, Nothing, Unit] =
    console.putStrLn("Hello, World!")

  def divideXByY(x: Int, y: Int): ZIO[Any,Nothing, Option[Int]] = {
    val DivResult = Try(x / y).toOption match {
      case None => None
      case Some(a) => Some(a)
    }
    IO.succeed(DivResult)
  }
}

object TestOperation
  extends DefaultRunnableSpec(
    suite("TestOperation")(
      testM("Divide by 0 must be None ") {
        for {
          outDiv <- divideXByY(10,0)
        } yield assert(outDiv, isNone)
      },
      testM("Divide 10 by 2 must be 5 "){
        for {
          divRes <- divideXByY(10,2)
        } yield assert(divRes,isSome(equalTo(5)))
      },
      testM ( "hello correctly"){
        for{
          _ <- sayHello
          output <- TestConsole.output
        } yield assert(output, equalTo(Vector("Hello, World!\n")))
      },
      testM("adds get the same value "){
        for {
          outAdd <- add(7,8)
        } yield assert(outAdd,equalTo(15))
      },
      testM("A test for failure "){
        for { // dies with specific type NoSuchElementException
          result <- ZIO.die(new NoSuchElementException("blah")).run
        }yield assert(result,dies(isSubtype[NoSuchElementException](anything)))
      },
      testM("An example of property") { // use check method with generator of 50 elements
        check(Gen.int(0, 50).map(_ * 2)) { int =>
          assert(int, isLessThan(9)) // fails when input is 10
        }
      },
      testM("A test involving the clock ") {
        for {
          p <- Promise.make[Nothing, Int]
          _ <- (clock.sleep(Duration(1,TimeUnit.SECONDS)) *> p.succeed(1)).fork
          _ <- TestClock.adjust(Duration(1,TimeUnit.SECONDS))
          res <- p.await
        } yield assert(res , equalTo(1))
      }
    )
  )