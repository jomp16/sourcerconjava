# Source RCON - Java

This is a basic library written in Java using Netty for handle Source RCON protocol.

Usage is easy, check below:

* First create an RconServer instance

```Java
final RconServer rconServer = new RconServer("127.0.0.1", 27015, "RCON_PASSWORD", false);
```

Where 127.0.0.1 is the host which the server will bind to;

27015 is the port which the server will bind to; By the way, it's the default port for Valve's Source RCON.

RCON_PASSWORD is the Source RCON password;

The false boolean is to not run the server in epoll mode. Set to true if you want it to run in epoll mode.

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
