# Source RCON - Java

This is a basic library written in Java using Netty for handle Source RCON protocol.

Usage is easy, check below:

* First create an RconServer instance

```Java
final RconServer rconServer = new RconServer("127.0.0.1", 27015, "RCON_PASSWORD", false);
```

* Then add any class which implements RconEvent to it:

```Java
rconServer.addRconEvent(new YourRconEvent());
```

* And then... Start the Source RCON server!

```Java
rconServer.startServer();
```

* Don't forget to call `rconServer.close()` when you don't need it anymore!

===

Licensed under GPLv3
