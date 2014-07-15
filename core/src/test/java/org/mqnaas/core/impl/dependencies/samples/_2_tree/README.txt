In org.mqnaas.core.ompl.dependencies.samples._2_tree package there is a 
set of Applications that depend between each other forming a dependency tree
as follows:

       ------
     /       \
    A --> B --> C
     \       /
       --> D --> E

AppA depends on IAppB, IAppC and IAppD
IAppB is implemented by AppB
IAppC is implemented by AppC
IAppD is implemented by AppD

AppB depends on IAppC
IAppC is implemented by AppC

AppC has no dependencies

AppD depends on IAppE
IAppE is implemented by AppE

AppE has no dependencies