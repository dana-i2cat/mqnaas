In org.mqnaas.core.impl.dependencies.samples._5_miniicycle package there is a 
set of Applications that depend one on the other forming a cycle :

     A <--> B
      

AppA depends on IAppB
IAppB is implemented by AppB

AppB depends on IAppA and IAppC
IAppA is implemented by AppA

