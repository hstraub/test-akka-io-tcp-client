Testing Akka TCP IO Client and connect to localhost port 25 (sendmail).

# Run Application

To run the test application:

```
sbt run
```

# Create a Akka Microkernel tar

1. edit built.sbt and change mainClass... from .ClientTest to .ClientKernel
2. sbt ```universal:package-zip-tarball```

