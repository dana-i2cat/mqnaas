In org.mqnaas.core.impl.dependencies.samples._6_multicycle package there is a 
set of Applications that depend one on the other forming a cycle :

      #####        #####          #####
      # A # -----> # B # -------> # C #
      ##### \      ##### <--\ /-- #####
             \               X
              \    ##### <--/ \-- #####
               \-> # D # -------> # E #
                   #####          #####


A depends on B and D
B depends on C 
C depends on D 
D depends on E
E depends on B
