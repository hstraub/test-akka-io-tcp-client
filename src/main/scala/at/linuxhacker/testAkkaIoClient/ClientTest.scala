package at.linuxhacker.testAkkaIoClient

import akka.actor.{ Actor, ActorSystem, Props }
import akka.kernel.Bootable
import akka.actor.{ Actor, ActorRef, Props }
import akka.io.{ IO, Tcp }
import akka.util.ByteString
import java.net.InetSocketAddress

class Listener extends Actor { 
  def receive = { 
    case x: ByteString => println (x.decodeString("UTF-8") )
  }
}

class Client(remote: InetSocketAddress, listener: ActorRef) extends Actor {
 
  import Tcp._
  import context.system
 
  IO(Tcp) ! Connect(remote)
 
  def receive = {
    case CommandFailed(_: Connect) ⇒
      listener ! "failed"
      context stop self
 
    case c @ Connected(remote, local) ⇒
      listener ! c
      val connection = sender
      connection ! Register(self)
      context become {
        case data: ByteString        ⇒ connection ! Write(data)
        case CommandFailed(w: Write) ⇒ // O/S buffer was full
        case Received(data)          ⇒ listener ! data
        case "close"                 ⇒ connection ! Close
        case _: ConnectionClosed     ⇒ context stop self
      }
  }
}

object ClientTest extends App {

  val system = ActorSystem( "hellokernel" )
  val listener = system.actorOf( Props[Listener], "Listener" )
  val remote = new InetSocketAddress( "localhost", 25 )
  val props = Props( classOf[Client], remote, listener )
  val client = system.actorOf( props, "Client" )

  Thread.sleep( 1000 )
  client ! ByteString( "help\n" )
  client ! ByteString( "quit\n" )
  Thread.sleep( 2000 )
  system.shutdown( )
}

class ClientKernel extends Bootable {

  val system = ActorSystem( "hellokernel" )

  def startup = {
    val listener = system.actorOf( Props[Listener], "Listener" )
    val remote = new InetSocketAddress( "localhost", 25 )
    val props = Props( classOf[Client], remote, listener )
    val client = system.actorOf( props, "Client" )

    Thread.sleep( 1000 )
    client ! ByteString( "help\n" )
    client ! ByteString( "quit\n" )
  }

  def shutdown = {
    system.shutdown
  }
}