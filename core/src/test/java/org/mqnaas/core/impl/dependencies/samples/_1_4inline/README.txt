In org.mqnaas.core.ompl.dependencies.samples._1_4_inline package there is a 
set of Applications that depend one on the other forming a line: 

    D --> C --> B --> A

AppD depends on IAppC
IAppC is implemented by AppC

AppC depends on IAppB
IAppB is implemented by AppB

AppB depends on IAppA
IAppA is implemented by AppA

AppA has no dependencies