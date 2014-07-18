In org.mqnaas.core.impl.dependencies.samples._3_cycle package there is a 
set of Applications that depend one on the other forming a cycle. 

    --------<---------
   /                 /
   --> AF --> B --> DE
         \ --> C-/ /
          \       /
           --->--- 

AppAF depends on IAppB, IAppC and IAppD
IAppB implemented by AppB
IAppC implemented by AppC
IAppD implemented by AppDE

IAppB depends on IAppD
IAppD implemented by AppDE

AppC depends on IAppE
IAppE implemented by AppDE

AppDE depends on IAppF
IAppF implemented by AppAF


The cycle is not noticed looking at interfaces.
