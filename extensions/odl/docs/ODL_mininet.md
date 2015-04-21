This guide exposes how to download, configure and deploy [OpenDaylight](http://www.opendaylight.org/) Helium acting as an OpenFlow controller of [Mininet](http://mininet.org/) switches.

Mininet
=======

[Mininet ](http://mininet.org/) can create a virtual network composed by [OpenFlow](https://www.opennetworking.org/sdn-resources/openflow) compatible switches. It is possible to install Mininet or download a VM with the software pre-installed. Follow [these instructions](http://mininet.org/download/). For Debian based Linux distros (Ubuntu, Mint, etc.) it is possible installing it with this command:

  ```
  sudo apt-get install mininet
  ```

For this example, creating a predefined topology is the easiest option. Use this command:

  ```
  sudo mn --controller=remote,ip=127.0.0.1 --topo tree,2
  ```

It instantiates 3 switches in a tree topology, all controlled by an OpenFlow controller in the same machine.

OpenDaylight Helium
===================

OpenDaylight would be OpenFlow controller for previously instantiated switches. In order to execute OpenDaylight, the easiest choice is downloading latest stable binary release from [downloads page](http://www.opendaylight.org/software/downloads), in this case, [Helium SR3](https://nexus.opendaylight.org/content/groups/public/org/opendaylight/integration/distribution-karaf/0.2.3-Helium-SR3/distribution-karaf-0.2.3-Helium-SR3.tar.gz). Once downloaded, extract it in any user's folder. Execute it invoking `bin/karaf` located in the extracted folder.

Now, it is necessary installing minimum features to allow desired functionalities. To do it, execute this command in the OpenDaylight console:

  ```
  feature:install odl-restconf odl-l2switch-switch odl-mdsal-apidocs odl-dlux-core
  ```

Now, access this URL: `http://localhost:8181/dlux/index.html#/topology` using _admin_ as user and password to see the switches topology.
