In org.mqnaas.core.impl.dependencies.samples._4_multicycle package there is a 
set of Applications that depend one on the other forming a cycle with different paths:

    --> A --> B --> C
     \              /
      ------<-------

AppA depends on IAppB
IAppB is implemented by AppB

AppB depends on IAppC
IAppC is implemented by AppC

AppC depends on IAppA
IAppA is implemented by AppA